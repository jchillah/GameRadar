package de.syntax_institut.androidabschlussprojekt.utils

import android.app.*
import android.content.*
import android.os.*
import androidx.lifecycle.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.*
import kotlinx.coroutines.flow.*

/**
 * Singleton-Manager für App Open Ads (AdMob). Zeigt ein App Open Ad beim Starten und beim Wechsel
 * in den Vordergrund, sofern Opt-In für Werbung und keine Pro-Version aktiv ist.
 *
 * - Initialisierung im Application-Objekt (App.kt) erforderlich.
 * - Berücksichtigt Opt-In/Pro-Logik und sendet Analytics-Events.
 * - KDoc und Clean Code Best Practices.
 */
object AppOpenAdManager : DefaultLifecycleObserver {
    private var appOpenAd: AppOpenAd? = null
    private var isLoading = false
    private var isShowing = false
    private lateinit var application: Application
    private var adUnitId: String =
        "ca-app-pub-7269049262039376/1234567890" // Echte AppOpenAd-ID eintragen
    private var adsEnabled: Boolean = false
    private var isProUser: Boolean = false
    private var analyticsEnabled: Boolean = false
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Initialisiert den Manager und registriert sich als LifecycleObserver. */
    fun init(
        app: Application,
        adUnitId: String = "ca-app-pub-7269049262039376/1234567890",
        adsEnabled: Boolean = false,
        isProUser: Boolean = false,
        analyticsEnabled: Boolean = false,
    ) {
        this.application = app
        this.adUnitId = adUnitId
        this.adsEnabled = adsEnabled
        this.isProUser = isProUser
        this.analyticsEnabled = analyticsEnabled
        MobileAds.initialize(app)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadAd(app)
    }

    /** Wird beim Starten oder Wechseln in den Vordergrund aufgerufen. */
    override fun onStart(owner: LifecycleOwner) {
        showAdIfAvailable()
    }

    /** Lädt ein App Open Ad, falls noch keines geladen ist. */
    fun loadAd(context: Context) {
        if (isLoading || appOpenAd != null || !adsEnabled || isProUser) return
        isLoading = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adUnitId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoading = false
                    if (analyticsEnabled) AppAnalytics.trackAdEvent(context, "app_open", "loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    appOpenAd = null
                    isLoading = false
                    _error.value = error.message
                    if (analyticsEnabled) AppAnalytics.trackAdEvent(context, "app_open", "error")
                }
            }
        )
    }

    /** Zeigt das App Open Ad, falls verfügbar und erlaubt. */
    fun showAdIfAvailable() {
        if (!adsEnabled || isProUser || isShowing) return
        val activity = getCurrentActivity() ?: return
        if (appOpenAd == null) {
            loadAd(activity)
            return
        }
        isShowing = true
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                if (analyticsEnabled) AppAnalytics.trackAdEvent(activity, "app_open", "impression")
            }

            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                appOpenAd = null
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                isShowing = false
                appOpenAd = null
                _error.value = error.message
                loadAd(activity)
                if (analyticsEnabled) AppAnalytics.trackAdEvent(activity, "app_open", "error")
            }

            override fun onAdClicked() {
                if (analyticsEnabled) AppAnalytics.trackAdEvent(activity, "app_open", "click")
            }
        }
        appOpenAd?.show(activity)
    }

    /** Setzt die Opt-In/Pro-Logik dynamisch (z. B. nach Settings-Änderung). */
    fun updateSettings(adsEnabled: Boolean, isProUser: Boolean, analyticsEnabled: Boolean) {
        this.adsEnabled = adsEnabled
        this.isProUser = isProUser
        this.analyticsEnabled = analyticsEnabled
    }

    /** Gibt die aktuelle Activity zurück (Workaround für AppOpenAd). */
    private fun getCurrentActivity(): Activity? {
        // Workaround: Handler postDelayed, um Activity zu bekommen
        // In echten Projekten ggf. ActivityLifecycleCallbacks nutzen
        var activity: Activity? = null
        Handler(Looper.getMainLooper()).post {
            activity = application.currentActivity
        }
        return activity ?: application.currentActivity
    }
}

/**
 * Extension-Property für Application, um die aktuelle Activity zu bekommen.
 * In echten Projekten sollte ein ActivityLifecycleCallbacks-Tracker verwendet werden.
 */
val Application.currentActivity: Activity?
    get() = try {
        val field = Application::class.java.getDeclaredField("mLoadedApk")
        field.isAccessible = true
        val loadedApk = field.get(this)
        val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
        activityThreadField.isAccessible = true
        val activityThread = activityThreadField.get(loadedApk)
        val activitiesField = activityThread.javaClass.getDeclaredField("mActivities")
        activitiesField.isAccessible = true
        val activities = activitiesField.get(activityThread) as? Map<*, *>
        val activityRecord = activities?.values?.firstOrNull()
        val activityField = activityRecord?.javaClass?.getDeclaredField("activity")
        activityField?.isAccessible = true
        activityField?.get(activityRecord) as? Activity
    } catch (_: Exception) {
        null
    } 
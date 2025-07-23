package de.syntax_institut.androidabschlussprojekt.ui.components.common.models

import android.os.*
import androidx.compose.ui.graphics.vector.*
import java.io.*

/**
 * Datenklasse für Dropdown-Optionen mit Icon und Trennlinien-Support.
 *
 * @property value Der Wert der Option (z.B. "en")
 * @property label Der anzuzeigende Name der Option (z.B. "English")
 * @property icon Optionales Icon für die Option (wird nicht serialisiert)
 * @property showDividerBefore Wenn true, wird vor dieser Option eine Trennlinie angezeigt
 */
data class DropdownOption(
    val value: String,
    val label: String,
    val icon: ImageVector? = null,
    val showDividerBefore: Boolean = false,
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        value = parcel.readString() ?: "",
        label = parcel.readString() ?: "",
        icon = null,
        showDividerBefore = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
        parcel.writeString(label)
        parcel.writeByte(if (showDividerBefore) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DropdownOption> {
        override fun createFromParcel(parcel: Parcel): DropdownOption {
            return DropdownOption(parcel)
        }

        override fun newArray(size: Int): Array<DropdownOption?> {
            return arrayOfNulls(size)
        }
    }
}
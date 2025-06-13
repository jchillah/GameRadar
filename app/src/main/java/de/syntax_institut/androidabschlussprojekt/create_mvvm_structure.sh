import os

structure = {
    "": [
        "data/local",
        "data/remote",
        "data/repositories",
        "di",
        "navigation",
        "ui/components",
        "ui/screens",
        "ui/theme",
        "ui/viewmodels",
        "utils",
        "services"
    ]
}

def create_structure(base_dir, structure_dict):
    for base, folders in structure_dict.items():
        for folder in folders:
            path = os.path.join(base_dir, base, folder)
            os.makedirs(path, exist_ok=True)
            
            # Erstelle placeholder.kt Datei in jedem Package
            placeholder_file = os.path.join(path, "placeholder.kt")
            with open(placeholder_file, "w") as f:
                f.write("// Diese Datei dient als Platzhalter für das Package\n")

if __name__ == "__main__":
    create_structure(".", structure)
    print("✅ Projektstruktur mit Placeholder-Dateien erfolgreich erstellt!")

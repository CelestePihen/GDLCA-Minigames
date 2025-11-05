#!/bin/bash

# Crée le dossier build commun s'il n'existe pas
mkdir -p build

# Parcours tous les dossiers dans le dossier courant
for plugin_dir in */; do
    # Vérifie qu'il y a un build.gradle
    if [[ -f "$plugin_dir/build.gradle" || -f "$plugin_dir/build.gradle.kts" ]]; then
        echo "Building $plugin_dir..."
        # Va dans le dossier et build
        (cd "$plugin_dir" && ./gradlew build)
        # Copie les fichiers JAR dans le build commun
        cp "$plugin_dir/build/libs/"*.jar build/ 2>/dev/null
    fi
done

echo "Build terminé. Tous les JAR sont dans le dossier 'build'."
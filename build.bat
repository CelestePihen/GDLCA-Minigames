@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

REM Crée le dossier build commun s'il n'existe pas
if not exist build (
    mkdir build
)

REM Parcours tous les dossiers dans le dossier courant
for /D %%D in (*) do (
    if exist "%%D\build.gradle" (
        echo Building %%D...
        pushd %%D
        
        popd
        copy "%%D\build\libs\*.jar" build\
    ) else if exist "%%D\build.gradle.kts" (
        echo Building %%D...
        pushd %%D
        
        popd
        copy "%%D\build\libs\*.jar" build\
    )
)

echo Build terminé. Tous les JAR sont dans le dossier "build".
pause
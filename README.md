# Halo Visuals (HV) — Fabric 1.21.4, client-side

Wszystkie funkcje są darmowe i odblokowane. ClickGUI: domyślnie klawisz `I` (zmiana: Client → Click GUI → Key).
Prawy klik na module = rozwiń ustawienia, lewy = włącz/wyłącz. Konfiguracja: `.minecraft/config/halovisuals.json`.

## Jak dostać gotowy .jar (bez IDE)

### Sposób A — GitHub Actions (nic nie instalujesz)
1. Załóż darmowe konto na github.com i utwórz nowe repozytorium (np. `HaloVisuals`).
2. Wgraj całą zawartość tego folderu (Add file → Upload files), razem z ukrytym folderem `.github`.
3. Zakładka **Actions** → workflow `build` → poczekaj ~2–4 min na zielony znaczek.
4. Wejdź w ten przebieg → sekcja **Artifacts** → pobierz `HaloVisuals-jar` (zip z plikiem `halovisuals-1.0.0.jar`).

### Sposób B — lokalnie
Wymagane: JDK 21 i Gradle 8.12+ (https://gradle.org/install). W folderze projektu: `gradle build`
Plik: `build/libs/halovisuals-1.0.0.jar`

## Instalacja
Zainstaluj Fabric Loader dla 1.21.4, wrzuć do `mods` plik Fabric API (1.21.4) oraz `halovisuals-1.0.0.jar`.

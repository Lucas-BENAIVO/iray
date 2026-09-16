# Iray

Application Android citoyenne pour Madagascar - **fangatahana** (démarches administratives) et **tatitra** (signalements locaux), avec profil et faritra (zone), en **offline-first**.

- **Package** : `mg.iray.app`
- **UI** : malagasy
- **Firebase** : projet `iray-mobile`

## Stack

| Couche | Techno |
|--------|--------|
| Langage / UI | Kotlin 2.1, Jetpack Compose, Material 3 |
| Navigation | Navigation Compose |
| Local | Room (`app_db`) |
| Sync | Firebase Firestore (+ Auth, Storage, Analytics) |
| Background | WorkManager (`SyncWorker`, `MediaUploadWorker`) |
| Carte / GPS | Maps Compose, Play Services Maps & Location |
| Build | AGP 8.7, Gradle 8.9, **JDK 17**, minSdk 24, targetSdk 35 |

Module unique : `:app` (`mg.iray.app`).

## Architecture offline-first

1. Écritures locales Room d’abord (`isSynced` / `pendingOperation`)
2. Sync : `pushPendingChanges()` puis `pullRemoteChanges()` via `FirestoreRemoteSync`
3. Travailleurs périodiques + sync immédiate au démarrage / après écritures importantes
4. Référentiel **territories** (région → district → commune → arrondissement? → fokontany) : seed assets + pull Firestore
5. Médias signalement : upload Storage en arrière-plan

Point d’entrée : `IrayApp` → `ServiceLocator` → `MainActivity` → `IrayNavHost`.

## Fonctionnalités

- **Onboarding** → **Welcome** (accueil)
- **Profil** : identité → **Faritrao** (listes cascade territoires) → succès
- **Démarches** : catalogue → détail → formulaire → pièces → récap → confirmation → *Mes démarches*
- **Signalements** : catégorie → sous-catégorie → **carte + GPS (lat/lng)** → détails/photos → envoi → *Mes signalements*
- **Mon profil**, notifications

Un profil est considéré **complet** seulement avec identité **et** faritra (commune + fokontany).

## Prérequis

- Android Studio (Ladybug+ recommandé)
- **JDK 17**
- Android SDK 35
- Fichier `app/google-services.json` (projet Firebase `iray-mobile`)
- Compte Google propriétaire du projet Firebase **iray-mobile** (Cloud Console)

## Lancer le projet

```bash
# Windows
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

Ou *Run* depuis Android Studio (configuration debug).

### Signature debug partagée

Toutes les machines utilisent le même keystore pour éviter les conflits d’install :

- Fichier : [`keystore/iray-debug.jks`](keystore/iray-debug.jks)
- Config : `signingConfigs.sharedDebug` dans [`app/build.gradle.kts`](app/build.gradle.kts)
- Alias : `iraydebug`

Après un install d’une autre machine / autre signature : désinstaller l’ancienne app puis réinstaller.

### SHA-1 (Maps / Firebase)

```bash
keytool -list -v -keystore keystore/iray-debug.jks -alias iraydebug
```

SHA-1 attendu (keystore partagé actuel) :

`A6:03:EB:49:BD:41:65:22:59:C3:66:F3:AD:16:12:BE:5E:FC:77:00`

## Carte Google Maps (signalements)

L’écran **Toerana** utilise Google Maps. Si la carte est **grise** (logo Google visible) :

1. Réseau téléphone OK (VPN / DNS souvent en cause)
2. Sur le compte Google du projet **iray-mobile** :
 - activer **Maps SDK for Android**
 - restreindre la clé API (apps Android) :
 - package : `mg.iray.app`
 - SHA-1 : celui du keystore ci-dessus
3. Clé injectée via `MAPS_API_KEY` (manifest `com.google.android.geo.API_KEY`) 
 Surcharge possible dans `gradle.properties` :

```properties
MAPS_API_KEY=votre_cle
```

Sans surcharge, le build utilise la clé présente dans la config Firebase / fallback Gradle.

## Structure utile

```
iray/
├── app/
│ ├── google-services.json
│ ├── src/main/java/mg/iray/app/
│ │ ├── auth/ # session, Firebase Auth
│ │ ├── dao/ entity/ db/
│ │ ├── di/ # ServiceLocator
│ │ ├── location/ # GPS / géocodage
│ │ ├── repository/ sync/ worker/
│ │ └── ui/ # screens, navigation, theme
│ └── src/main/assets/territories_seed.json
├── keystore/iray-debug.jks
└── gradle/
```

## Tests

```bash
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

## Notes

- Langue UI : **malagasy** (`app/src/main/res/values/strings.xml`)
- Room version 4 ; migrations destructives en debug (`fallbackToDestructiveMigration`)
- Ne pas committer de secrets hors du flux prévu (préférer `local.properties` / `gradle.properties` local pour une clé Maps dédiée)

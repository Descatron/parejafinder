<p align="center">
  <img src="Logo 512 x 512.png" alt="ParejaFinder logo" width="140" />
</p>

<h1 align="center">ParejaFinder</h1>

<p align="center">
  Aplicación Android de demostración para descubrimiento de perfiles, gestión de matches y chat entre usuarios.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-App-3DDC84?logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Java-11-007396?logo=openjdk&logoColor=white" alt="Java 11" />
  <img src="https://img.shields.io/badge/Gradle-Build-02303A?logo=gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="GPLv3" />
  <img src="https://img.shields.io/badge/F--Droid-Compatible-orange" alt="F-Droid compatible" />
  <img src="https://img.shields.io/badge/Auth-Backend%20propio-success" alt="Backend propio" />
</p>

Aplicación Android de demostración para descubrimiento de perfiles, gestión de matches y chat entre usuarios, con un panel de administración incluido.

## Funcionalidades principales

- Registro/login de usuarios.
- Descubrimiento de perfiles con filtros (nombre/intereses, ciudad y edad).
- Acciones de interacción: `Like`, `Super Like` y `Descartar`.
- Gestión de matches y chat por match.
- Perfil de usuario editable (incluye foto de perfil).
- Notificaciones de nuevos mensajes.
- Panel de administrador para:
  - bloqueo/desbloqueo de usuarios,
  - cambio de contraseña,
  - eliminación de cuentas,
  - mensajería privada admin-usuario,
  - exportación/importación de usuarios,
  - visualización de logs de auditoría.

## Stack técnico

- Android (Java)
- Gradle
- AndroidX + Material Components
- Room (persistencia local)
- Backend propio (autenticación online por API REST)

## Requisitos

- Android Studio (versión reciente con soporte para AGP 9.x)
- JDK 11
- SDK de Android (mínimo API 24)
- URL de backend configurada en `BuildConfig.AUTH_BASE_URL` (`app/build.gradle`)

## Instalación y ejecución

1. Clona el repositorio:
   - `git clone https://github.com/Descatron/parejafinder.git`
2. Abre el proyecto en Android Studio.
3. Configura tu backend en `app/build.gradle`:
   - `buildConfigField "String", "AUTH_BASE_URL", "\"https://tu-backend.com\""`
4. Sincroniza Gradle.
5. Ejecuta la app en emulador o dispositivo físico.

### Compilar desde terminal

- Debug APK: `./gradlew assembleDebug`
- Ejecutar tests unitarios: `./gradlew test`

En Windows PowerShell puedes usar `.\gradlew.bat` en lugar de `./gradlew`.

## Estructura del proyecto

- `app/src/main/java/com/aplicafran/parejafinder`
  - `ui/`: fragments y adapters de interfaz
  - `data/`: entidades, DAOs y repositorios
  - clases base como `MainActivity`, `SessionManager`, `NotificationHelper`
- `app/src/main/res`: layouts, menús, drawables y strings
- `app/src/main/AndroidManifest.xml`: permisos y configuración de aplicación

## Notas

- El proyecto no depende de Firebase ni de Google Services para autenticación.
- El repositorio contiene licencia en `LICENSE`.

## Capturas de pantalla

Añade tus imágenes en `assets/screenshots/` y usa esta estructura:

![Login](assets/screenshots/login.png)
![Descubrir](assets/screenshots/discover.png)
![Matches](assets/screenshots/matches.png)
![Chat](assets/screenshots/chat.png)
![Perfil](assets/screenshots/profile.png)
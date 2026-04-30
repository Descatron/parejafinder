<p align="center">
  <img src="Logo 512 x 512.png" alt="ParejaFinder logo" width="140" />
</p>

<h1 align="center">ParejaFinder</h1>

<p align="center">
  Aplicacion Android para descubrimiento de perfiles, gestion de matches y chat entre usuarios.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-App-3DDC84?logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Java-11-007396?logo=openjdk&logoColor=white" alt="Java 11" />
  <img src="https://img.shields.io/badge/Gradle-Build-02303A?logo=gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="GPLv3" />
</p>

Aplicacion Android nativa (Java) para descubrir perfiles, hacer match y chatear, con panel de administracion y almacenamiento local.

## Funcionalidades principales

- Registro/login de usuarios con email y contrasena.
- Descubrimiento de perfiles con filtros (nombre/intereses, ciudad y edad maxima).
- Acciones de interaccion: `Like`, `Super Like` y `Descartar`.
- Gestion de matches y chat por match.
- Perfil de usuario editable (incluye foto de perfil).
- Notificaciones de nuevos mensajes.
- Panel de administrador para:
  - bloqueo/desbloqueo de usuarios,
  - cambio de contrasena,
  - eliminacion de cuentas,
  - mensajeria privada admin-usuario,
  - exportacion/importacion de usuarios,
  - visualizacion de logs de auditoria.

## Stack tecnico

- Android (Java 11)
- Gradle (AGP 9.x)
- AndroidX + Material Components
- Room (persistencia local)

## Requisitos

- Android Studio (version reciente con soporte para AGP 9.x)
- JDK 11
- SDK de Android (minimo API 24)

## Instalacion y ejecucion

1. Clona el repositorio:
   - `git clone https://github.com/Descatron/parejafinder.git`
2. Abre el proyecto en Android Studio.
3. Sincroniza Gradle.
4. Ejecuta la app en emulador o dispositivo fisico.

### Compilar desde terminal

- Debug APK: `./gradlew assembleDebug`
- Ejecutar tests unitarios: `./gradlew test`

En Windows PowerShell puedes usar `.\gradlew.bat` en lugar de `./gradlew`.

## Estructura del proyecto

- `app/src/main/java/com/aplicafran/parejafinder`
  - `ui/`: fragments y adapters de interfaz
  - `data/`: entidades, DAOs y repositorio
  - clases base como `MainActivity`, `SessionManager`, `NotificationHelper`
- `app/src/main/res`: layouts, menus, drawables y strings
- `app/src/main/AndroidManifest.xml`: permisos y configuracion de la aplicacion

## Licencia

Este proyecto se distribuye bajo licencia **GNU GPL v3.0**.
Consulta el archivo `LICENSE`.

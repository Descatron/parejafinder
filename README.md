# ParejaFinder

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
- Firebase Authentication
- Google Services
- AdMob

## Requisitos

- Android Studio (versión reciente con soporte para AGP 9.x)
- JDK 11
- SDK de Android (mínimo API 24)
- Archivo de configuración de Firebase: `app/google-services.json`

## Instalación y ejecución

1. Clona el repositorio:
   - `git clone https://github.com/Descatron/parejafinder.git`
2. Abre el proyecto en Android Studio.
3. Verifica que exista `app/google-services.json`.
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

- El proyecto incluye `google-services` y configuración para Firebase.
- El repositorio contiene licencia en `LICENSE`.
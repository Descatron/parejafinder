# Pareja Finder

![Android](https://img.shields.io/badge/Android-App-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-11-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?logo=gradle&logoColor=white)
![License](https://img.shields.io/badge/License-GPLv3-blue.svg)

Aplicacion Android de descubrimiento de perfiles, gestion de matches y chat entre usuarios, con panel de administracion.

## Funcionalidades

- Registro/login con email y contrasena (Firebase Auth).
- Descubrimiento de perfiles con filtros por texto, ciudad y edad.
- Acciones: `Like`, `Super Like` y `Descartar`.
- Lista de matches y chat por match.
- Notificaciones de nuevos mensajes.
- Perfil editable (nombre, edad, ciudad, bio y foto).
- Panel admin con bloqueo, desbloqueo, eliminacion, cambio de contrasena, mensajes privados, backup y logs.

## Stack tecnico

- Android (Java 11)
- AndroidX + Material Components
- Room (persistencia local)
- Firebase Authentication
- Google Mobile Ads (AdMob)
- Gradle / AGP 9.x

## Requisitos

- Android Studio actualizado.
- JDK 11.
- SDK Android (min API 24).
- `app/google-services.json` valido para Firebase.

## Instalacion y ejecucion

```bash
git clone https://github.com/Descatron/parejafinder.git
cd parejafinder
./gradlew assembleDebug
```

En Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

Luego abre el proyecto en Android Studio y ejecuta en emulador o dispositivo.

## Estructura

- `app/src/main/java/com/aplicafran/parejafinder/data`: entidades, DAO y repositorio.
- `app/src/main/java/com/aplicafran/parejafinder/ui`: fragments y adapters.
- `app/src/main/java/com/aplicafran/parejafinder/MainActivity.java`: flujo principal.

## Licencia

Este proyecto se distribuye bajo licencia **GNU GPL v3.0**.
Consulta el archivo `LICENSE`.

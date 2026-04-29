# Pareja Finder

[![Android](https://img.shields.io/badge/Android-minSdk%2024-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Java-11-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Firebase Auth](https://img.shields.io/badge/Firebase-Authentication-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com/products/auth)
[![Room](https://img.shields.io/badge/Database-Room-4CAF50)](https://developer.android.com/training/data-storage/room)

Aplicacion Android nativa desarrollada en Java para descubrir perfiles, hacer match y chatear. Combina autenticacion con Firebase, persistencia local con Room y un panel de administracion para gestion de usuarios.

## Tabla de contenidos

- [Caracteristicas](#caracteristicas)
- [Stack tecnico](#stack-tecnico)
- [Requisitos](#requisitos)
- [Instalacion y ejecucion](#instalacion-y-ejecucion)
- [Demo](#demo)
- [Capturas](#capturas)
- [Cuenta administrador por defecto](#cuenta-administrador-por-defecto)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Roadmap](#roadmap)
- [Contribucion](#contribucion)
- [Licencia](#licencia)

## Caracteristicas

- Registro e inicio de sesion con email y contrasena (Firebase Authentication).
- Descubrimiento de perfiles con filtros por texto, ciudad y edad maxima.
- Sistema de `Like` y `Super Like`.
- Listado de matches y chat por cada match.
- Notificaciones de nuevos mensajes.
- Perfil editable (nombre, edad, ciudad, bio y foto local).
- Panel de administrador para:
  - bloquear/desbloquear usuarios,
  - eliminar cuentas (excepto admin),
  - cambiar contrasenas,
  - enviar mensajes privados,
  - exportar/importar backup de usuarios,
  - consultar logs de auditoria.

## Stack tecnico

- **Lenguaje:** Java 11
- **Android:** minSdk 24, targetSdk 36
- **UI:** AndroidX + Material Design + Fragments + RecyclerView
- **Autenticacion:** Firebase Authentication
- **Persistencia local:** Room Database
- **Monetizacion:** Google Mobile Ads (AdMob)
- **Build system:** Gradle (AGP 9.2.0)

## Requisitos

- Android Studio actualizado.
- JDK 11 configurado.
- SDK de Android instalado.
- Proyecto Firebase con Authentication habilitado.
- Archivo `google-services.json` valido en `app/google-services.json`.

## Instalacion y ejecucion

1. Clona el repositorio:

   ```bash
   git clone https://github.com/Descatron/parejafinder.git
   cd parejafinder
   ```

2. Abre el proyecto en Android Studio.
3. Verifica que `app/google-services.json` corresponde a tu proyecto Firebase.
4. Sincroniza Gradle.
5. Ejecuta en emulador o dispositivo fisico.

Compilacion por terminal:

```bash
./gradlew assembleDebug
```

En Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

## Demo

Puedes añadir aqui un GIF o video corto mostrando el flujo principal:

- Login / registro
- Descubrir perfiles y hacer match
- Chat en tiempo real local
- Panel de administrador

Ejemplo (cuando subas tu archivo):

```md
![Demo Pareja Finder](docs/images/demo.gif)
```

## Capturas

> Sustituye estas rutas por tus imagenes reales en `docs/images/`.

### Login

![Pantalla de login](docs/images/login.png)

### Descubrir perfiles

![Pantalla descubrir](docs/images/discover.png)

### Matches

![Pantalla matches](docs/images/matches.png)

### Chat

![Pantalla chat](docs/images/chat.png)

### Perfil

![Pantalla perfil](docs/images/profile.png)

### Panel admin

![Pantalla admin](docs/images/admin.png)

## Como crear un demo GIF ligero (Windows)

Flujo recomendado para que el GIF se vea bien sin pesar demasiado:

1. **Graba pantalla** en Android Studio Emulator con una herramienta como ScreenToGif, ShareX o la grabadora de Windows.
2. **Duracion ideal:** 15-30 segundos.
3. **Contenido sugerido:**
   - login,
   - descubrir perfiles,
   - match,
   - chat,
   - panel admin.
4. **Exporta con estos parametros aproximados:**
   - ancho: `480px` o `540px`,
   - FPS: `10-12`,
   - colores: `64-128`.
5. Guarda el archivo como: `docs/images/demo.gif`.

Si prefieres video en lugar de GIF (normalmente pesa menos y se ve mejor), puedes usar:

```md
[Ver demo en video](docs/images/demo.mp4)
```

## Cuenta administrador por defecto

La aplicacion crea una cuenta administradora local automaticamente:

- **Email:** `admin@aplicafran.com`
- **Contrasena:** `admin123`

> Recomendacion: cambia esta contrasena al primer acceso en cualquier entorno real.

## Estructura del proyecto

```text
app/
  src/main/java/com/aplicafran/parejafinder/
    data/         # entidades, DAO, Room, repositorio
    ui/           # fragments y adapters
    MainActivity  # login, navegacion y flujo principal
```

## Roadmap

- [ ] Migrar `allowMainThreadQueries()` a operaciones asincronas.
- [ ] Añadir tests de integracion para flujos de autenticacion y chat.
- [ ] Mejorar validaciones y manejo de errores de red/Firebase.
- [ ] Preparar pipeline CI para build y pruebas automaticas.

## Contribucion

Las contribuciones son bienvenidas. Flujo recomendado:

1. Haz un fork del repo.
2. Crea una rama: `feature/mi-mejora`.
3. Realiza cambios y pruebas.
4. Abre un Pull Request con una descripcion clara.

## Licencia

Este proyecto aun no tiene una licencia definida.
Si vas a publicarlo como open source, añade un archivo `LICENSE` (por ejemplo, MIT).

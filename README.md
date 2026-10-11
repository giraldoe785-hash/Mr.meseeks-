# Meeseeks Task Master & Alarm

> **"I'm Mr. Meeseeks, look at me!"**  
> Aplicación Android nativa desarrollada exclusivamente en **Kotlin** con **Jetpack Compose** y arquitectura **MVVM**, inspirada en el universo de *Rick y Morty*.

---

## 1. Descripción del Proyecto

**Meeseeks Task Master & Alarm** es una aplicación móvil nativa diseñada para gestionar tareas contra reloj bajo la filosofía del multiverso de Rick y Morty:  
*Los Mr. Meeseeks son criaturas invocadas con un único propósito. La existencia prolongada es sufrimiento físico y mental para ellos; cuanto más se acerque una tarea a su fecha límite sin ser completada, mayor será la agonía y desesperación del Meeseeks asignado.*

La aplicación implementa:
1. **Creación de tareas** con validación estricta y fecha/hora límite.
2. **Cuentas regresivas en tiempo real** en formato digital `HH:MM:SS`.
3. **Transformación dinámica del Meeseeks** (estados de estrés en tiempo real según el porcentaje de tiempo transcurrido).
4. **Alarmas y notificaciones nativas** persistentes mediante `AlarmManager` y `BroadcastReceiver`.
5. **Pantalla de Crisis** con botones de acción urgente:
   - **`¡HECHO! ¡MÁTALO!`**: Completa la tarea, cancela la alarma, detiene sonido/vibración y libera al Meeseeks.
   - **`DAME 10 MINUTOS`**: Pospone la tarea 10 minutos, reprograma la alarma, recalcula el estrés y detiene los efectos.
6. **Historial de tareas resueltas** y **Ajustes de sonido/vibración/permisos**.

---

## 2. Tecnologías y Arquitectura

- **Lenguaje:** **Kotlin (100% exclusivo)**. Queda estrictamente excluida cualquier lógica en Java, JavaScript, Flutter, React Native o WebViews.
- **Interfaz Gráfica:** **Jetpack Compose con Material 3**. Cero archivos de layouts en XML (`setContentView` no utilizado; toda la UI es declarativa en Kotlin).
- **Arquitectura:** **Model-View-ViewModel (MVVM)** estricto:
  - `Model`: Entidad `Task`, enums `TaskStatus` y `MeeseeksStressLevel`, base de datos Room (`AppDatabase`), DAO (`TaskDao`) y Repositorio (`TaskRepository`).
  - `ViewModel`: `HomeViewModel`, `HistoryViewModel`, `SettingsViewModel`, `CrisisViewModel` exponiendo reactividad con `StateFlow` y `CoroutineScope`.
  - `View`: Pantallas (`SplashScreen`, `HomeScreen`, `CrisisAlarmScreen`, `HistoryScreen`, `SettingsScreen`) y componentes reutilizables (`MeeseeksAvatar`, `TaskCard`, `CountdownTimer`, `CreateTaskDialog`).
- **Persistencia Local:** **Room Database con SQLite**.
- **Servicios Nativos:** `AlarmManager` (con soporte para alarmas exactas), `BroadcastReceiver`, `NotificationManager` (canal de alta prioridad con Full-Screen Intent), `Vibrator` y `MediaPlayer` nativo con audio local PCM WAV (`meeseeks_alarm.wav`).
- **Sistema de Compilación:** Gradle Kotlin DSL (`.gradle.kts`) con catálogo de versiones (`gradle/libs.versions.toml`).

---

## 3. Estructura del Código

```text
app/src/main/java/com/example/meeseekstaskmaster/
├── MainActivity.kt                       # Activity principal con soporte de Intents de crisis
├── MeeseeksApplication.kt                # Inicialización de Singletons y canales de notificación
├── navigation/
│   └── AppNavigation.kt                  # Gestión de rutas y argumentos en Navigation Compose
├── model/
│   ├── Task.kt                           # Entidad Room con cálculos de progreso y estrés
│   ├── TaskStatus.kt                     # PENDING, COMPLETED, OVERDUE
│   └── MeeseeksStressLevel.kt            # NORMAL, STRESSED, CRISIS, OVERDUE y frases
├── data/
│   ├── local/
│   │   ├── TaskDao.kt                    # Métodos de acceso a base de datos
│   │   ├── Converters.kt                 # Conversores de tipos para Room
│   │   └── AppDatabase.kt                # Base de datos Room con pre-población única
│   └── repository/
│       ├── TaskRepository.kt             # Lógica de negocio y orquestación de alarmas
│       └── SettingsRepository.kt         # Almacenamiento local de preferencias
├── viewmodel/
│   ├── HomeViewModel.kt                  # Ticker en tiempo real y tareas activas
│   ├── HistoryViewModel.kt               # Tareas completadas y resueltas
│   ├── SettingsViewModel.kt              # Control de audio, vibración y permisos
│   ├── CrisisViewModel.kt                # Acciones críticas (¡Mátalo! y +10 minutos)
│   └── ViewModelFactory.kt               # Inyección de dependencias para ViewModels
├── view/
│   ├── theme/
│   │   ├── Color.kt                      # Paleta Rick & Morty (Space Dark, Portal Green, Cyan)
│   │   ├── Theme.kt                      # Tema Material 3 oscuro
│   │   └── Type.kt                       # Tipografía espacial y monospace
│   ├── components/
│   │   ├── MeeseeksAvatar.kt             # Dibujo vectorial en Canvas con animaciones dinámicas
│   │   ├── CountdownTimer.kt             # Cronómetro sci-fi digital en formato HH:MM:SS
│   │   ├── TaskCard.kt                   # Tarjeta interactiva de tarea con Meeseeks
│   │   └── CreateTaskDialog.kt           # Diálogo con DatePicker y TimePicker Material 3
│   └── screens/
│       ├── SplashScreen.kt               # Portal verde animado y transición fluida (2.5s)
│       ├── HomeScreen.kt                 # Pantalla principal con lista, FAB y navegación
│       ├── CrisisAlarmScreen.kt          # Pantalla de alarma estroboscópica y desespero
│       ├── HistoryScreen.kt              # Historial de tareas completadas y vencidas
│       └── SettingsScreen.kt             # Configuración de sonido, vibración y permisos
├── service/
│   ├── AlarmScheduler.kt                 # Programación y cancelación con AlarmManager
│   ├── AlarmReceiver.kt                  # BroadcastReceiver para disparo de alarmas y acciones
│   ├── BootReceiver.kt                   # Reprogramación automática tras reinicio del móvil
│   ├── NotificationService.kt            # Notificación de alta prioridad con botones de acción
│   ├── VibrationService.kt               # Vibración háptica en patrón de crisis
│   └── AudioService.kt                   # Reproducción en bucle con MediaPlayer nativo
└── util/
    ├── TimeUtils.kt                      # Formateador HH:MM:SS y cálculo de milisegundos
    └── TaskValidation.kt                 # Validación de títulos y fechas límite
```

---

## 4. Sistema de Estrés del Mr. Meeseeks

El cálculo del progreso temporal se define mediante:

$$\text{Progreso} = \frac{\text{currentTime} - \text{createdAt}}{\text{deadline} - \text{createdAt}}$$

| Nivel | Rango Temporal | Expresión y Animación del Meeseeks | Frase Característica |
|---|---|---|---|
| **NORMAL** | $0\% - 40\%$ | Avatar celeste alegre, ojos brillantes, sonrisa abierta, suave balanceo flotante. | *"I'm Mr. Meeseeks, look at me!"* |
| **ESTRESADO** | $> 40\% - 75\%$ | Cejas caídas, gota de sudor, boca en zigzag, temblor nervioso sutil. | *"¡Esto se está volviendo raro, tengo que cumplir!"* |
| **CRISIS INMINENTE** | $> 75\% - < 100\%$ | Ojos saltones desorbitados con venas rojas, boca abierta gritando, sacudida intensa. | *"¡EL TIEMPO SE AGOTA! ¡EXISTENCIA ES DOLOR!"* |
| **ALARMA VENCIDA** | $\ge 100\%$ | Aura roja pulsante, parpadeo estroboscópico, sonido de sirena y vibración continúa. | *"¡EXISTENCE IS PAIN! ¡MÁTALO YA!"* |

> **Nota:** La fecha de vencimiento (`isOverdue`) tiene prioridad absoluta sobre cualquier cálculo porcentual.

---

## 5. Datos Iniciales de Prueba

La base de datos Room (`AppDatabase`) se inicializa automáticamente una sola vez con dos tareas predeterminadas:
1. **`Entregar el trabajo de programación`** — Fecha límite: momento actual + 3 horas (inicia con progreso visible).
2. **`Estudiar para el examen`** — Fecha límite: momento actual + 24 horas.

---

## 6. Pruebas Automatizadas (Unit Tests)

Ubicadas en `app/src/test/java/com/example/meeseekstaskmaster/`:
- `TimeUtilsTest.kt`: Valida formato `HH:MM:SS`, cálculo de milisegundos restantes y detección de vencimiento.
- `TaskValidationTest.kt`: Valida títulos vacíos, títulos cortos y rechazo de fechas en el pasado.
- `MeeseeksStressLevelTest.kt`: Valida los umbrales de transición ($0-40\%$, $40-75\%$, $>75\%$, vencimiento prioritario).
- `TaskModelTest.kt`: Valida el cálculo de ratios temporales, protección contra división por cero y estado completado.
- `TaskRepositoryTest.kt`: Valida la creación, completado y la función de posponer $+10$ minutos con un Fake DAO en memoria.

---

## 7. Instrucciones para Abrir y Ejecutar en Android Studio

1. **Abrir Android Studio:**
   - Selecciona **File > Open...** (o la opción **Open** de la pantalla de bienvenida).
   - Navega hasta la carpeta del proyecto: `c:\Users\Shalom\Downloads\Sr. meseeks!`.
   - Haz clic en **OK**.

2. **Sincronización de Gradle:**
   - Android Studio detectará automáticamente el archivo `settings.gradle.kts` y el catálogo `libs.versions.toml`.
   - Si no inicia de forma automática, presiona el botón **Sync Project with Gradle Files** (icono del elefante).

3. **Verificación de JDK:**
   - Ve a **Settings / Preferences > Build, Execution, Deployment > Build Tools > Gradle**.
   - Asegúrate de que **Gradle JDK** esté configurado en **Embedded JDK 17** (o cualquier JDK 17+ instalado).

4. **Compilación y Pruebas:**
   - Abre la pestaña **Terminal** integrada en Android Studio y ejecuta:
     ```powershell
     .\gradlew.bat testDebugUnitTest
     ```
   - O compila la APK de depuración:
     ```powershell
     .\gradlew.bat assembleDebug
     ```

5. **Ejecución en Emulador o Dispositivo:**
   - Selecciona el módulo `app` en la barra superior.
   - Selecciona un emulador (API 26+) o un dispositivo físico Android con depuración USB habilitada.
   - Presiona el botón verde **Run 'app'** (`Shift + F10`).

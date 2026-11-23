# FitTrack

Sistema de seguimiento de entrenamiento y nutrición en Java (POO).
Proyecto de Programación II (ISWZ1103) - UDLA. Autor: Axcel Sebastián Cuaical Pazmiño.

## Módulos
1. Perfil y metas (cálculo de calorías y macros)
2. Entrenamiento (rutinas, sesiones, progresión de carga, estancamiento)
3. Nutrición (catálogo de alimentos, registro de comidas, balance diario)
4. Progreso corporal (mediciones y reporte con recomendación)

## Estructura
- `src/modelo`: entidades del problema
- `src/negocio`: reglas y lógica de cada módulo
- `src/interfaz`: interfaz gráfica (Swing) y menús de consola
- `dist/FitTrack.jar`: aplicación ejecutable
- `docs/`: documento PDF/Word y diagramas UML

## Cómo ejecutar (JDK 17 o superior)
Aplicación ejecutable (interfaz gráfica):

    java -jar dist/FitTrack.jar

Desde el código fuente:

    javac -d out src/modelo/*.java src/negocio/*.java src/interfaz/*.java
    java -cp out interfaz.MainGUI      # interfaz gráfica
    java -cp out interfaz.Main         # modo consola

En un IDE: abrir la carpeta, marcar `src` como fuentes y ejecutar `interfaz.MainGUI` o `interfaz.Main`.
Los datos se guardan en `fittrack.dat` (carpeta desde donde se ejecuta).

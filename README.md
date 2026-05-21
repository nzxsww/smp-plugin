# SMPPlugin for Paper 1.21.11

Este es un plugin inicial para **Paper 1.21.11 build 69** (API `1.21.11-R0.1-SNAPSHOT`), que simplemente muestra un mensaje en la consola al activarse.

## Estructura del Proyecto

*   **`src/main/java/com/smpplugin/SMPPlugin.java`**: Código fuente principal del plugin.
*   **`src/main/resources/plugin.yml`**: Archivo de configuración del plugin de Paper/Bukkit.
*   **`build.gradle`**: Configuración de Gradle.
*   **`settings.gradle`**: Configuración del proyecto de Gradle.
*   **`mock-bukkit.jar`** y **`mock-bukkit/`**: Biblioteca mock para permitir la compilación offline ya que el entorno tiene restricciones de red. (Solo se usa en compilación, no se incluye en el JAR final).

## Cómo Compilar

Para compilar el plugin en tu entorno local (que cuenta con JDK 21 instalado en `C:\Users\matias\AppData\Local\jdk-21`), ejecuta el siguiente comando desde la carpeta del proyecto (`smp-plugin`):

```powershell
$env:JAVA_HOME="C:\Users\matias\AppData\Local\jdk-21"; & "C:\Users\matias\.gradle\wrapper\dists\gradle-9.5.0-bin\bvnork1r7n8i6kp5cnkibsc9q\gradle-9.5.0\bin\gradle.bat" build --offline
```

El archivo `.jar` compilado se generará en:
`smp-plugin/build/libs/smp-plugin-1.0.0.jar`

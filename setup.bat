@echo off
REM ========================================================================
REM Script de configuración e inicio para Caribe Vibes
REM Sistema de gestión turística - Spring Boot Application
REM ========================================================================

echo.
echo =====================================
echo     Caribe Vibes - SETUP SCRIPT
echo =====================================
echo.

REM Verificar Java
echo [1/6] Verificando Java...
java -version > nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java no encontrado. Instale Java 17 o superior.
    pause
    exit /b 1
)
echo ✓ Java encontrado

REM Verificar Maven
echo [2/6] Verificando Maven...
call mvn -version > nul 2>&1
if %errorlevel% neq 0 (
    echo ℹ Maven no encontrado globalmente. Usando Maven Wrapper...
    set MAVEN_CMD=mvnw.cmd
) else (
    echo ✓ Maven encontrado
    set MAVEN_CMD=mvn
)

REM Compilar aplicación
echo [3/6] Compilando aplicación...
call %MAVEN_CMD% clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Falló la compilación
    pause
    exit /b 1
)
echo ✓ Compilación exitosa

REM Ejecutar tests
echo [4/6] Ejecutando tests...
call %MAVEN_CMD% test -q
if %errorlevel% neq 0 (
    echo WARNING: Algunos tests fallaron
) else (
    echo ✓ Tests ejecutados exitosamente
)

REM Crear archivo JAR
echo [5/6] Creando aplicación ejecutable...
call %MAVEN_CMD% package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Falló la creación del JAR
    pause
    exit /b 1
)
echo ✓ JAR creado exitosamente

REM Configurar base de datos
echo [6/6] Configuración de base de datos...
echo.
echo Seleccione su base de datos:
echo [1] H2 (En memoria - Desarrollo)
echo [2] MySQL (Desarrollo local)
echo [3] PostgreSQL (Desarrollo local)
echo [4] Configuración personalizada
echo.
set /p DB_CHOICE="Ingrese su opción (1-4): "

if "%DB_CHOICE%"=="1" (
    set SPRING_PROFILES=dev
    echo ✓ Configurado para H2 en memoria
) else if "%DB_CHOICE%"=="2" (
    set SPRING_PROFILES=dev
    echo.
    echo Configuración MySQL:
    set /p DB_URL="URL de base de datos (jdbc:mysql://localhost:3306/caribe_dreams): "
    set /p DB_USER="Usuario de base de datos (caribe_user): "
    set /p DB_PASS="Contraseña de base de datos: "
    if "%DB_URL%"=="" set DB_URL=jdbc:mysql://localhost:3306/caribe_dreams
    if "%DB_USER%"=="" set DB_USER=caribe_user
    echo ✓ Configurado para MySQL
) else if "%DB_CHOICE%"=="3" (
    set SPRING_PROFILES=dev
    echo.
    echo Configuración PostgreSQL:
    set /p DB_URL="URL de base de datos (jdbc:postgresql://localhost:5432/caribe_dreams): "
    set /p DB_USER="Usuario de base de datos (caribe_user): "
    set /p DB_PASS="Contraseña de base de datos: "
    if "%DB_URL%"=="" set DB_URL=jdbc:postgresql://localhost:5432/caribe_dreams
    if "%DB_USER%"=="" set DB_USER=caribe_user
    echo ✓ Configurado para PostgreSQL
) else if "%DB_CHOICE%"=="4" (
    set /p SPRING_PROFILES="Perfil de Spring (dev/test/prod): "
    echo ✓ Configuración personalizada
) else (
    echo Opción inválida. Usando H2 por defecto...
    set SPRING_PROFILES=dev
)

echo.
echo =====================================
echo     CONFIGURACIÓN COMPLETADA
echo =====================================
echo.
echo Aplicación: Caribe Vibes API
echo Perfil: %SPRING_PROFILES%
echo Puerto: 8080
echo.
echo URLs importantes:
echo - API: http://localhost:8080/api
echo - Swagger: http://localhost:8080/swagger-ui.html
echo - Health: http://localhost:8080/actuator/health
echo.

REM Preguntar si iniciar la aplicación
set /p START_APP="¿Desea iniciar la aplicación ahora? (s/n): "
if /i "%START_APP%"=="s" (
    echo.
    echo Iniciando Caribe Vibes...
    echo ========================================
    echo.
    
    if defined DB_URL (
        if defined DB_PASS (
            java -jar target\caribe-vibes-1.0.0.jar ^
                --spring.profiles.active=%SPRING_PROFILES% ^
                --spring.datasource.url=%DB_URL% ^
                --spring.datasource.username=%DB_USER% ^
                --spring.datasource.password=%DB_PASS%
        ) else (
            java -jar target\caribe-vibes-1.0.0.jar ^
                --spring.profiles.active=%SPRING_PROFILES% ^
                --spring.datasource.url=%DB_URL% ^
                --spring.datasource.username=%DB_USER%
        )
    ) else (
        java -jar target\caribe-vibes-1.0.0.jar --spring.profiles.active=%SPRING_PROFILES%
    )
) else (
    echo.
    echo Para iniciar la aplicación manualmente:
    echo.
    if defined DB_URL (
        if defined DB_PASS (
            echo java -jar target\caribe-vibes-1.0.0.jar ^
                --spring.profiles.active=%SPRING_PROFILES% ^
                --spring.datasource.url=%DB_URL% ^
                --spring.datasource.username=%DB_USER% ^
                --spring.datasource.password=%DB_PASS%
        ) else (
            echo java -jar target\caribe-vibes-1.0.0.jar ^
                --spring.profiles.active=%SPRING_PROFILES% ^
                --spring.datasource.url=%DB_URL% ^
                --spring.datasource.username=%DB_USER%
        )
    ) else (
        echo java -jar target\caribe-vibes-1.0.0.jar --spring.profiles.active=%SPRING_PROFILES%
    )
    echo.
)

echo.
echo ¡Configuración completada exitosamente!
pause

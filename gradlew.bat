@echo off
setlocal
set GRADLE_VERSION=9.5.0
if "%GRADLE_USER_HOME%"=="" set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set DIST_HOME=%GRADLE_USER_HOME%\manual-wrapper\gradle-%GRADLE_VERSION%
set GRADLE_BIN=%DIST_HOME%\bin\gradle.bat
if exist "%GRADLE_BIN%" goto run

if not exist "%GRADLE_USER_HOME%\manual-wrapper" mkdir "%GRADLE_USER_HOME%\manual-wrapper"
set ZIP_FILE=%GRADLE_USER_HOME%\manual-wrapper\gradle-%GRADLE_VERSION%-bin.zip
set URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip
 echo Gradle %GRADLE_VERSION% not found. Downloading distribution...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%ZIP_FILE%'"
if errorlevel 1 exit /b 1
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%ZIP_FILE%' '%GRADLE_USER_HOME%\manual-wrapper'"
if errorlevel 1 exit /b 1

:run
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%

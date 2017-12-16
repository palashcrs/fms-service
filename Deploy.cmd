@echo off
title Deploy
@REM color 2F

@REM Print env variables set variables:
echo Java Home=%JAVA_HOME%
echo Maven Home=%M2_HOME%
set APP_NAME=edgepay-fms-service
set APP_VERSION=1.0
set BUILD_DIRECTORY=D:\Workspace-Eclipse-Gets\%APP_NAME%\target
set DEPLOYMENT_DIRECTORY=D:\Deployment
echo:

@REM Deployment:
DEL %APP_NAME%-%APP_VERSION%.jar /q
copy /Y %BUILD_DIRECTORY%\%APP_NAME%-%APP_VERSION%.jar %DEPLOYMENT_DIRECTORY%
java -Dtomcat.threads=100 -jar %APP_NAME%-%APP_VERSION%.jar
echo:

echo Error code: %ERRORLEVEL%
echo:

<nul set /p "=Deployment failed! Press any key to exit..."
 pause >nul
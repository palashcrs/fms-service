@echo off
title Build
@REM color 2F

@REM Print env variables set variables:
echo Java Home=%JAVA_HOME%
echo Maven Home=%M2_HOME%
set BUILD_DIRECTORY=D:\Workspace-Eclipse-Gets\edgepay-fms-service

call mvn -e -f %BUILD_DIRECTORY%\pom.xml clean package -DskipTests=true

if not %ERRORLEVEL% == 0 exit /b
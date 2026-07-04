@echo off
rem JavaFX GUI - needs the SDK in lib\ (see README)
cd /d "%~dp0"
java --module-path "lib\javafx-sdk-24.0.1\lib" --add-modules javafx.controls -cp out app.MainFX

@echo off
rem Console version - plain JDK is enough
cd /d "%~dp0"
java -cp out app.Main
pause

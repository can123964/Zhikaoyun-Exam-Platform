@echo off
title ZhikaoYun Frontend Launcher

:menu
cls
echo ========================================
echo   ZhikaoYun - Frontend Launcher
echo ========================================
echo.
echo   1. Admin    (port 3000, Vite + Vue3)
echo   2. Student  (port 8001, Vue-CLI + Vue2)
echo   3. Start All
echo   0. Exit
echo.
set /p choice=Select [0-3]: 

if "%choice%"=="1" goto admin
if "%choice%"=="2" goto student
if "%choice%"=="3" goto all
if "%choice%"=="0" exit /b 0
echo Invalid choice, please try again.
goto menu

:admin
title ZhikaoYun - Admin Frontend
cls
echo ========================================
echo   Starting Admin Frontend...
echo ========================================
echo.
cd /d "%~dp0frontend-admin"
echo [INFO] Dir: %CD%
echo [INFO] Command: pnpm dev (port 3000)
echo.
call pnpm dev
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed. Check:
    echo   1. pnpm installed?  npm install -g pnpm
    echo   2. deps installed?  cd frontend-admin ^&^& pnpm install
    echo.
    pause
)
goto menu

:student
title ZhikaoYun - Student Frontend
cls
echo ========================================
echo   Starting Student Frontend...
echo ========================================
echo.
cd /d "%~dp0frontend-student"
echo [INFO] Dir: %CD%
echo [INFO] Command: npm run serve (port 8001)
echo.
call npm run serve
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed. Check:
    echo   1. Node.js installed?
    echo   2. deps installed?  cd frontend-student ^&^& npm install
    echo.
    pause
)
goto menu

:all
title ZhikaoYun - Frontend Launcher
echo.
echo [INFO] Starting both frontends...
echo.
start "ZhikaoYun-Admin" cmd /c "cd /d "%~dp0frontend-admin" & pnpm dev"
start "ZhikaoYun-Student" cmd /c "cd /d "%~dp0frontend-student" & npm run serve"
echo [INFO] Launched in separate windows.
echo.
echo   Admin:   http://localhost:3000
echo   Student: http://localhost:8001
echo.
pause
goto menu

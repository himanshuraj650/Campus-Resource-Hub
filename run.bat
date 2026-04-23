@echo off
setlocal enabledelayedexpansion

:: Add JDK to Temporary Path if missing
where javac >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Java not found in loaded PATH. Discovering JDK...
    for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
        set "PATH=%%d\bin;%PATH%"
        goto :ready
    )
    for /d %%d in ("C:\Program Files\Java\jdk*") do (
        set "PATH=%%d\bin;%PATH%"
        goto :ready
    )
    echo ERROR: Cannot find JDK automatically. Please ensure Java is installed.
    pause
    exit /b 1
)

:ready
echo Compiling Java source files...
if not exist out mkdir out

:: Create a temporary file to hold the list of source files
set "sources=%temp%\java_sources.txt"
if exist "%sources%" del "%sources%"

:: Find all .java files in src directory and subdirectories
for /r src %%f in (*.java) do (
    echo %%f>>"%sources%"
)

:: Compile the sources
javac -cp "lib/*" -d out @"%sources%"
if %errorlevel% neq 0 (
    echo Compilation failed.
    del "%sources%"
    exit /b %errorlevel%
)
del "%sources%"

echo Running Campus Resource Hub...
java -Djava.io.tmpdir=. -cp "out;lib/*" campus.Main

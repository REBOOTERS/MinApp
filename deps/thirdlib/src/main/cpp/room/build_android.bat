@echo off
setlocal enabledelayedexpansion

set NDK_PATH=C:\Users\xx\AppData\Local\Android\Sdk\ndk\27.1.12297006
set PROJECT_DIR=%~dp0
set BUILD_DIR=%PROJECT_DIR%build_android

rem 设置输出目录（可根据需要修改）
set JNI_LIBS_DIR=%PROJECT_DIR%jniLibs
rem 或者使用当前目录下的 jniLibs
rem set JNI_LIBS_DIR=%PROJECT_DIR%jniLibs

echo Cleaning build directory...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%JNI_LIBS_DIR%" rmdir /s /q "%JNI_LIBS_DIR%"
mkdir "%BUILD_DIR%"

rem 清理旧的 jniLibs 目录（可选）
rem if exist "%JNI_LIBS_DIR%" rmdir /s /q "%JNI_LIBS_DIR%"

cd "%BUILD_DIR%"

echo Configuring for Android...

rem 支持的ABI列表
rem set ABIS=armeabi-v7a arm64-v8a x86 x86_64
set ABIS=x86_64

for %%A in (%ABIS%) do (
    echo Building for %%A...

    mkdir %%A
    cd %%A

    cmake %PROJECT_DIR% ^
        -G "Ninja" ^
        -DCMAKE_TOOLCHAIN_FILE="%NDK_PATH%/build/cmake/android.toolchain.cmake" ^
        -DANDROID_ABI=%%A ^
        -DANDROID_PLATFORM=android-21 ^
        -DCMAKE_BUILD_TYPE=Release

    if !errorlevel! equ 0 (
        cmake --build .
        echo Successfully built for %%A

        rem 复制 so 文件到 jniLibs 目录
        echo Copying .so files to jniLibs...

        rem 创建对应的 ABI 目录
        set TARGET_DIR=%JNI_LIBS_DIR%\%%A
        if not exist "!TARGET_DIR!" mkdir "!TARGET_DIR!"

        rem 查找并复制所有 .so 文件
        for /r %%F in (*.so) do (
            echo Copying %%F to !TARGET_DIR!
            copy "%%F" "!TARGET_DIR!" > nul
        )

        rem 如果上面找不到文件，尝试在 build 目录下查找
        if not exist "!TARGET_DIR!\*.so" (
            echo Searching in build directory...
            for %%F in (*.so) do (
                if exist "%%F" (
                    echo Copying %%F to !TARGET_DIR!
                    copy "%%F" "!TARGET_DIR!" > nul
                )
            )
        )

        rem 检查是否复制成功
        set "HAS_SO=0"
        for %%F in ("!TARGET_DIR!\*.so") do set "HAS_SO=1"
        if "!HAS_SO!"=="1" (
            echo Successfully copied .so files for %%A
        ) else (
            echo Warning: No .so files found for %%A
        )

    ) else (
        echo Failed to build for %%A
    )

    cd ..
)

rem 显示最终结果
echo.
echo ========================================
echo Build Summary:
echo ========================================
echo Build directory: %BUILD_DIR%
echo jniLibs directory: %JNI_LIBS_DIR%

for %%A in (%ABIS%) do (
    set TARGET_DIR=%JNI_LIBS_DIR%\%%A
    if exist "!TARGET_DIR!" (
        set "COUNT=0"
        for /f %%i in ('dir /b "!TARGET_DIR!\*.so" 2^>nul ^| find /c /v ""') do set COUNT=%%i
        echo %%A: !COUNT! .so file(s)
    )
)
echo ========================================

echo Build completed!
pause
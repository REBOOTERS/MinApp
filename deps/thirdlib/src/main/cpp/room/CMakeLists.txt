cmake_minimum_required(VERSION 4.1.2)
project(demo)

# 添加Android支持
if(ANDROID)
    # 设置Android NDK路径
    set(CMAKE_TOOLCHAIN_FILE
            ${CMAKE_ANDROID_NDK}/build/cmake/android.toolchain.cmake)

    # 设置Android API级别
    set(ANDROID_PLATFORM android-21)
    set(ANDROID_ABI armeabi-v7a)  # 或 arm64-v8a, x86_64等

    # 添加NDK日志库
    find_library(log-lib log)
endif()

add_library(demo SHARED demo.cpp)

# 链接Android日志库
if(ANDROID)
    target_link_libraries(demo ${log-lib})
endif()
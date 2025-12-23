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

# Size-optimization: prefer size-optimized code, remove unused sections, hide symbols
if(ANDROID)
    # Optimize for size and enable section garbage collection
    target_compile_options(demo PRIVATE -Os -ffunction-sections -fdata-sections -fvisibility=hidden)
    # Disable C++ RTTI/exceptions if your code doesn't rely on them (can reduce size)
    target_compile_options(demo PRIVATE -fno-rtti)
    # Enable link-time optimization where supported
    target_compile_options(demo PRIVATE -flto)
    target_link_options(demo PRIVATE -Wl,--gc-sections -flto)

    # Strip unnecessary symbols from the final shared object
    if(CMAKE_STRIP)
        add_custom_command(TARGET demo POST_BUILD
            COMMAND ${CMAKE_STRIP} --strip-unneeded $<TARGET_FILE:demo>
            COMMENT "Stripping unneeded symbols from $<TARGET_FILE:demo>"
        )
    endif()
endif()

# 链接Android日志库
if(ANDROID)
    target_link_libraries(demo ${log-lib})
endif()


add_executable(server demo.cpp)

# Linux/macOS
if(UNIX AND NOT APPLE)
    target_link_libraries(server pthread)
endif()

# Windows
if(WIN32)
    target_link_libraries(server ws2_32 wsock32)
endif()
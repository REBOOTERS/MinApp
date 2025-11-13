
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project(dev_rknn)
include_directories(logcat)
include_directories(yoyoimg)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        dev_rknn

        # Sets the library as a shared library.
        SHARED
        yoyoimg/yolo_image.cc
#        room/*.cpp
        # Provides a relative path to your source file(s).
        dev_rknn.cpp)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        dev_rknn

        # Links the target library to the log library
        # included in the NDK.
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/librknnrt.so
        ${log-lib})

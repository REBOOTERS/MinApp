
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 4.1.2)

# Declares and names the project.

project(thirdlib)
include_directories(logcat)
message("hello cmake")
set(MIN_APP "MIN APP HAHA")
message("MIN_APP=${MIN_APP}")
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

file(GLOB DEMO room/*.cpp)

add_library( # Sets the name of the library.
        thirdlib

        # Sets the library as a shared library.
        SHARED
#        room/*.cpp
        ${DEMO}
        # Provides a relative path to your source file(s).
        thirdlib.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

if (ANDROID)
    message("this is android")

    message("-- ANDROID_ABI=${ANDROID_ABI}")
    message("-- build type=${CMAKE_BUILD_TYPE}")
    message("-- CMAKE_LIBRARY_OUTPUT_DIRECTORY=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}")
    message("PROJECT_SOURCE_DIR=${PROJECT_SOURCE_DIR}")
endif ()

if (MACOS)
    message("this is macOs")
endif ()

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        thirdlib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

# For Android builds prefer flexible/large page sizes when available.
# Gradle already passes -DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON, but
# add safe linker hints here as well. These are best-effort: the NDK/toolchain
# ultimately decides supported page sizes.
if(ANDROID)
    message("ANDROID: enabling flexible page-size support (request 16KB)")
    # Define for source if needed
    add_definitions(-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON)
    # Prefer 16 KB max page size during linking (if linker supports it).
    # This appends a linker option; if the toolchain rejects it the build will fail,
    # so we only add it as a target-level option as a best-effort.
    if (CMAKE_VERSION VERSION_GREATER "3.13")
        target_link_options(thirdlib PRIVATE "-Wl,-z,max-page-size=16384")
    else()
        set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384")
    endif()
endif()


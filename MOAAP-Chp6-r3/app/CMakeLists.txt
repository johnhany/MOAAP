cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)
set(ocvlibs "E:/dev-lib/opencv-contrib-android-sdk/sdk/native/libs")

include_directories(E:/dev-lib/opencv-contrib-android-sdk/sdk/native/jni/include)

add_library(libopencv_java3 SHARED IMPORTED )
set_target_properties(libopencv_java3 PROPERTIES
                      IMPORTED_LOCATION "${ocvlibs}/${ANDROID_ABI}/libopencv_java3.so")

add_library( # Sets the name of the library.
             stitcher

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/stitcher.cpp )

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       stitcher android log libopencv_java3

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
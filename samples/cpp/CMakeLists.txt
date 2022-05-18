
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.18.1)

include(FetchContent)

project("myapplication")

#Fetch SimpleJNI library
FetchContent_Declare(SimpleJNI
        GIT_REPOSITORY git@github.com:gershnik/SimpleJNI.git
        GIT_TAG d0237019cc0353f6c7508ba8a1e6d87ec5d90978
        GIT_SHALLOW TRUE
)
FetchContent_MakeAvailable(SimpleJNI)


add_library(myapplication SHARED
    myapplication.cpp 
)

set_property(TARGET myapplication PROPERTY CXX_STANDARD 17)
set_property(TARGET myapplication PROPERTY CXX_STANDARD_REQUIRED ON)
set_property(TARGET myapplication PROPERTY CXX_VISIBILITY_PRESET hidden)
set_property(TARGET myapplication PROPERTY VISIBILITY_INLINES_HIDDEN ON)
set_property(TARGET myapplication PROPERTY POSITION_INDEPENDENT_CODE ON)


find_library(log-lib log)

target_link_libraries(myapplication 
PRIVATE
    #Declare dependecy on SimpleJNI (the library name is 'smjni' for backward compatibility)
    smjni
    ${log-lib} 
)

target_include_directories(myapplication
PRIVATE
    #Add the directory of JniGen generated files to include path
    ${JNIGEN_GENERATED_DIR}
)


# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.18.1)

project("myapplication")

#Fetch SmJNI library
include(FetchContent)
FetchContent_Declare(smjni
        GIT_REPOSITORY git@github.com:gershnik/smjni.git
        GIT_TAG 5a0f5197
        GIT_SHALLOW TRUE
)
FetchContent_MakeAvailable(smjni)


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
    #Declare dependecy on SmJNI
    smjni
    ${log-lib} 
)

target_include_directories(myapplication
PRIVATE
    #Add the directory of JniGen generated files to include path
    ${JNIGEN_GENERATED_DIR}
)

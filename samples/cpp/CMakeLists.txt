
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.18.1)

include(FetchContent)

project("myapplication")

#Fetch SimpleJNI library
FetchContent_Declare(SimpleJNI
        GIT_REPOSITORY https://github.com/gershnik/SimpleJNI.git
        GIT_TAG 3.11
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
    #Declare dependecy on SimpleJNI
    smjni::smjni
    ${log-lib} 
)

target_include_directories(myapplication
PRIVATE
    #Add the directory of JniGen generated files to include path
    ${JNIGEN_GENERATED_DIR}
)

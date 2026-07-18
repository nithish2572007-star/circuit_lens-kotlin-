cmake_minimum_required(VERSION 3.15)
project(simulation_service CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include the downloaded single-header libraries in include/
include_directories(include)

add_executable(simulation_service src/main.cpp)

# Link threads since cpp-httplib requires thread support
find_package(Threads REQUIRED)
target_link_libraries(simulation_service PRIVATE Threads::Threads)

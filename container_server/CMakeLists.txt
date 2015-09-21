cmake_minimum_required(VERSION 3.3)
project(container_server)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

include_directories(headers)

set(SOURCE_FILES
    main.cpp
    headers/net/Socket.hpp
    src/net/Socket.cpp)

add_executable(container_server ${SOURCE_FILES})

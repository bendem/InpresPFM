cmake_minimum_required(VERSION 3.3)
project(cpp_commons)

# Warnings
if(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -pedantic")
endif()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

include_directories(headers)

set(SOURCE_FILES
    headers/net/Socket.hpp
    src/net/Socket.cpp
    headers/net/IOError.hpp
    headers/protocol/ProtocolHandler.hpp
    headers/protocol/Packet.hpp)

add_library(cpp_commons ${SOURCE_FILES})

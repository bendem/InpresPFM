cmake_minimum_required(VERSION 3.3)
project(cpp_commons)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

include_directories(headers)

set(SOURCE_FILES
    headers/net/Socket.hpp
    src/net/Socket.cpp
    headers/net/IOError.hpp
    headers/protocol/ProtocolHandler.hpp
    src/protocol/Translator.cpp
    headers/protocol/Translator.hpp)

add_library(cpp_commons ${SOURCE_FILES})

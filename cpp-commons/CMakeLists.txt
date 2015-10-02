cmake_minimum_required(VERSION 3.3)
project(cpp_commons)

# Warnings
if(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -pedantic")
endif()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
find_package(Threads)

include_directories(headers)

set(SOURCE_FILES
    headers/net/Socket.hpp
    src/net/Socket.cpp
    headers/net/IOError.hpp
    headers/protocol/ProtocolHandler.hpp
    headers/protocol/Packet.hpp
    headers/protocol/ProtocolError.hpp
    headers/threading/ThreadPool.hpp
    src/threading/ThreadPool.cpp
    headers/net/Selector.hpp
    src/net/Selector.cpp
    headers/utils/Logger.hpp
    src/utils/Logger.cpp
    headers/io/CSVFile.hpp
    src/io/CSVFile.cpp
)

add_library(cpp_commons ${SOURCE_FILES})
target_link_libraries(cpp_commons ${CMAKE_THREAD_LIBS_INIT})

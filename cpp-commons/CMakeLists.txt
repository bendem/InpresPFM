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
    headers/input/InputHelper.hpp
    headers/io/BinaryFile.hpp
    headers/io/CSVFile.hpp
    headers/io/StreamUtils.hpp
    headers/net/IOError.hpp
    headers/net/Selector.hpp
    headers/net/SelectorThread.hpp
    headers/net/Socket.hpp
    headers/protocol/Packet.hpp
    headers/protocol/ProtocolError.hpp
    headers/protocol/ProtocolHandler.hpp
    headers/threading/ThreadPool.hpp
    headers/utils/Logger.hpp
    headers/utils/Sanity.hpp
    src/input/InputHelper.cpp
    src/io/CSVFile.cpp
    src/io/StreamUtils.cpp
    src/net/Selector.cpp
    src/net/Socket.cpp
    src/threading/ThreadPool.cpp
    src/utils/Logger.cpp
    src/utils/Sanity.cpp
    headers/utils/ProgramProperties.hpp src/utils/ProgramProperties.cpp headers/utils/StringUtils.hpp src/utils/StringUtils.cpp)

add_library(cpp_commons ${SOURCE_FILES})
target_link_libraries(cpp_commons ${CMAKE_THREAD_LIBS_INIT})

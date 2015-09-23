cmake_minimum_required(VERSION 3.3)
project(container_server)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

include_directories(headers)
include_directories(../cpp-commons/headers)

set(SOURCE_FILES
    main.cpp
    headers/cmmp/PacketId.hpp
    headers/cmmp/CMMPTranslator.hpp
    headers/cmmp/LoginPacket.hpp
    headers/cmmp/InputTruckPacket.hpp
    headers/cmmp/InputDonePacket.hpp
    headers/cmmp/OutputReadyPacket.hpp
    headers/cmmp/OutputOnePacket.hpp
    headers/cmmp/OutputDonePacket.hpp
    headers/cmmp/LogoutPacket.hpp
    src/cmmp/LoginPacket.cpp
    src/cmmp/InputDonePacket.cpp
    src/cmmp/InputTruckPacket.cpp
    src/cmmp/OutputDonePacket.cpp
    src/cmmp/OutputOnePacket.cpp
    src/cmmp/OutputReadyPacket.cpp
    src/cmmp/LogoutPacket.cpp)

add_subdirectory(../cpp-commons build)

add_executable(container_server ${SOURCE_FILES})
target_link_libraries(container_server cpp_commons)

cmake_minimum_required(VERSION 3.3)
project(container_server)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

# Warnings
if(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -pedantic")
endif()

include_directories(headers)
include_directories(../cpp-commons/headers)

set(SOURCE_FILES
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
    src/cmmp/LogoutPacket.cpp
    headers/cmmp/LoginResponsePacket.hpp
    headers/cmmp/InputTruckResponsePacket.hpp
    headers/cmmp/InputDoneResponsePacket.hpp
    headers/cmmp/OutputReadyResponsePacket.hpp
    headers/cmmp/OutputOneResponsePacket.hpp
    headers/cmmp/OutputDoneResponsePacket.hpp
    headers/cmmp/LogoutResponsePacket.hpp
    src/cmmp/LoginResponsePacket.cpp
    src/cmmp/InputDoneResponsePacket.cpp
    src/cmmp/InputTruckResponsePacket.cpp
    src/cmmp/OutputDoneResponsePacket.cpp
    src/cmmp/OutputOneResponsePacket.cpp
    src/cmmp/OutputReadyResponsePacket.cpp
    src/cmmp/LogoutResponsePacket.cpp
    headers/Container.hpp
    src/cmmp/CMMPTranslator.cpp
    headers/ContainerServer.hpp
    src/ContainerServer.cpp)

add_subdirectory(../cpp-commons build)

add_executable(container_server main.cpp ${SOURCE_FILES})
target_link_libraries(container_server cpp_commons)

add_executable(container_server_test_application client_test.cpp ${SOURCE_FILES})
target_link_libraries(container_server_test_application cpp_commons)

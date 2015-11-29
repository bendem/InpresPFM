cmake_minimum_required(VERSION 3.3)
project(container_server)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

# Warnings
if(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -pedantic")
endif()

include_directories(headers)
include_directories(../cpp-commons/headers)

set(COMMON_FILES
    headers/cmmp/Translator.hpp
    headers/cmmp/InputDonePacket.hpp
    headers/cmmp/InputDoneResponsePacket.hpp
    headers/cmmp/InputTruckPacket.hpp
    headers/cmmp/InputTruckResponsePacket.hpp
    headers/cmmp/LoginPacket.hpp
    headers/cmmp/LoginResponsePacket.hpp
    headers/cmmp/LogoutPacket.hpp
    headers/cmmp/LogoutResponsePacket.hpp
    headers/cmmp/OutputDonePacket.hpp
    headers/cmmp/OutputDoneResponsePacket.hpp
    headers/cmmp/OutputOnePacket.hpp
    headers/cmmp/OutputOneResponsePacket.hpp
    headers/cmmp/OutputReadyPacket.hpp
    headers/cmmp/OutputReadyResponsePacket.hpp
    headers/cmmp/PacketId.hpp
    headers/Container.hpp
    headers/ParcLocation.hpp
    src/cmmp/Translator.cpp
    src/cmmp/InputDonePacket.cpp
    src/cmmp/InputDoneResponsePacket.cpp
    src/cmmp/InputTruckPacket.cpp
    src/cmmp/InputTruckResponsePacket.cpp
    src/cmmp/LoginPacket.cpp
    src/cmmp/LoginResponsePacket.cpp
    src/cmmp/LogoutPacket.cpp
    src/cmmp/LogoutResponsePacket.cpp
    src/cmmp/OutputDonePacket.cpp
    src/cmmp/OutputDoneResponsePacket.cpp
    src/cmmp/OutputOnePacket.cpp
    src/cmmp/OutputOneResponsePacket.cpp
    src/cmmp/OutputReadyPacket.cpp
    src/cmmp/OutputReadyResponsePacket.cpp
    src/ParcLocation.cpp
    src/Container.cpp
    src/admin/Admin.cpp
    headers/csa/Translator.hpp)
add_library(cmmp_protocol ${COMMON_FILES})
target_link_libraries(cmmp_protocol cpp_commons)

file(COPY ${CMAKE_CURRENT_SOURCE_DIR}/data/users.csv DESTINATION ${CMAKE_CURRENT_BINARY_DIR}/data)

add_subdirectory(../cpp-commons build)

add_executable(container_server
    headers/server/ContainerServer.hpp
    src/server/ContainerServer.cpp
    server.cpp
    headers/admin/Admin.hpp
    src/admin/Admin.cpp
    headers/csa/PacketId.hpp
    headers/csa/LoginPacket.hpp
    headers/csa/LoginResponsePacket.hpp
    headers/csa/ListPacket.hpp
    headers/csa/ListResponsePacket.hpp
    headers/csa/PausePacket.hpp
    headers/csa/PauseResponsePacket.hpp
    headers/csa/StopPacket.hpp
    headers/csa/StopResponsePacket.hpp
    src/csa/LoginPacket.cpp
    src/csa/ListResponsePacket.cpp
    src/csa/Translator.cpp)
target_link_libraries(container_server cpp_commons)
target_link_libraries(container_server cmmp_protocol)

add_executable(container_app
    headers/client/ContainerClient.hpp
    src/client/ContainerClient.cpp
    client.cpp
)
target_link_libraries(container_app cpp_commons)
target_link_libraries(container_app cmmp_protocol)

add_executable(parc_debugger parc_debugger.cpp)
target_link_libraries(parc_debugger cpp_commons)
target_link_libraries(parc_debugger cmmp_protocol)

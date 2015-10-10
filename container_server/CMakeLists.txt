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
    headers/cmmp/CMMPTranslator.hpp
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
    src/cmmp/CMMPTranslator.cpp
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
    headers/ParkLocation.hpp
    src/ParkLocation.cpp
)

file(COPY ${CMAKE_CURRENT_SOURCE_DIR}/data/users.csv DESTINATION ${CMAKE_CURRENT_BINARY_DIR}/data)

add_subdirectory(../cpp-commons build)

add_executable(container_server
    headers/server/ContainerServer.hpp
    src/server/ContainerServer.cpp
    server.cpp
    ${COMMON_FILES}
)
target_link_libraries(container_server cpp_commons)

add_executable(container_app
    client.cpp
    headers/client/ContainerClient.hpp
    src/client/ContainerClient.cpp
    ${COMMON_FILES}
)
target_link_libraries(container_app cpp_commons)

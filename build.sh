#!/bin/bash

function run_gradle() {
    local path=${1:0:(-1)}
    cd $path
    echo Running gradle in $(pwd)
    gradle build &> ../$path.log
}

function run_cmake() {
    local path=${1:0:(-1)}
    cd $path
    if [ ! -d "build" ]; then
        mkdir build
    fi
    echo Running cmake in $(pwd)
    cd build
    cmake .. &> ../../$path.log
    make &>> ../../$path.log
}

for f in */; do
    if [ -f "$f/build.gradle" ]; then
        run_gradle $f&
    else
        if [ -f "$f/CMakeLists.txt" ]; then
            run_cmake $f&
        fi
    fi
done

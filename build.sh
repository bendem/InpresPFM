set -e

function build() {
    echo
    echo "Building $*"
    cd "$*"
    grep "id 'maven'" build.gradle >/dev/null && gradle clean install || gradle clean build
    cd ..
}

build java-commons

build accounting_db
build trafficdb

build iobrep
build pfmcop
build tramap
build boomap

build boat_server
build chat_client_java
build chat_server
build demo_jdbc
build reservation_app
build reservation_servlet
build traffic_application
build traffic_server

# BoatApp

# cpp-commons
# container_server

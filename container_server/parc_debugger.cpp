#include <iostream>
#include <cassert>

#include "ParcLocation.hpp"
#include "io/BinaryFile.hpp"

using namespace std;

string e(ParkLocationFlag::ParkLocationFlag flag) {
    switch(flag) {
        case ParkLocationFlag::Free:     return "Free";
        case ParkLocationFlag::Reserved: return "Reserved";
        case ParkLocationFlag::Storing:  return "Storing";
        case ParkLocationFlag::Leaving:  return "Leaving";
        case ParkLocationFlag::Taken:    return "Taken";
    }
    assert(false);
}

string e(MeanOfTransportation::MeanOfTransportation meanOfTransportation) {
    switch(meanOfTransportation) {
        case MeanOfTransportation::Boat:  return "Boat";
        case MeanOfTransportation::Train: return "Train";
    }
    assert(false);
}

int main(int argc, char** argv) {
    if(argc < 2) {
        cout << "No file provided" << endl;
        return 0;
    }
    cout << endl;

    BinaryFile<ParcLocation> file(argv[1]);

    vector<ParcLocation> locations = file.load();
    for(const ParcLocation& location : locations) {
        cout
            << " | x: " << location.x << endl
            << " | y: " << location.y << endl
            << " | containerId: " << location.containerId << endl
            << " | flag: " << e(location.flag) << endl
            << " | reservationDate: " << location.reservationDate << endl
            << " | arrivalDate: " << location.arrivalDate << endl
            << " | weight: " << location.weight << endl
            << " | destination: " << location.destination << endl
            << " | meanOfTranspartation: " << e(location.meanOfTranspartation) << endl
            << endl;
    }
}

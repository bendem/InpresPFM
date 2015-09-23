#ifndef CONTAINER_SERVER_INPUTDONEPACKET_HPP
#define CONTAINER_SERVER_INPUTDONEPACKET_HPP

class InputDonePacket {

public:
    InputDonePacket(bool ok, float weight)
            : ok(ok), weight(weight) {}

    bool isOk() const { return ok; }
    float getWeight() const { return weight; }

private:
    bool ok;
    float weight;

};

#endif //CONTAINER_SERVER_INPUTDONEPACKET_HPP

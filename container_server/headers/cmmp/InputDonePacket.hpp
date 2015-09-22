#ifndef CONTAINER_SERVER_INPUTDONEPACKET_HPP
#define CONTAINER_SERVER_INPUTDONEPACKET_HPP

class InputDonePacket {

public:
    InputDonePacket(bool _iDontRemember, float weight)
            : _iDontRemember(_iDontRemember), weight(weight) {}

    bool is_iDontRemember() const { return _iDontRemember; }
    float getWeight() const { return weight; }

private:
    bool _iDontRemember;
    float weight;

};

#endif //CONTAINER_SERVER_INPUTDONEPACKET_HPP

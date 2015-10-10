#include "client/ContainerClient.hpp"

ContainerClient::ContainerClient(std::shared_ptr<Socket> ptr, ProtocolHandler<CMMPTranslator, PacketId>& proto)
    : socket(ptr),
      proto(proto),
      closed(false),
      loggedIn(false) {}

ContainerClient& ContainerClient::init() {
    return *this;
}

ContainerClient& ContainerClient::mainLoop() {
    while(!this->closed) {
        if(this->loggedIn) {
            this->menu();
        } else {
            this->loginMenu();
        }
    }

    return *this;
}

void ContainerClient::loginMenu() {
    std::cout << " Create a new user? ";
    this->login(InputHelper::readBool());
}

void ContainerClient::login(bool newUser) {
    std::cout << " Username: ";
    std::string username = InputHelper::readNonEmtpyString();
    std::cout << " Password: ";
    std::string password = InputHelper::readNonEmtpyString(); // TODO Hiding thing?

    this->proto.write(this->socket, LoginPacket(username, password, newUser));
    LoginResponsePacket response = this->proto.readSpecificPacket<LoginResponsePacket>(this->socket);
    if(response.isOk()) {
        this->loggedIn = true;
        std::cout << " > Login successful" << std::endl;
    } else {
        std::cout << " > Login failed: " << response.getReason() << std::endl;
    }
}

template<class P>
std::string packetResult(const P& p) {
    return std::string(typeid(P).name()) + " went " + (p.isOk() ? "" : "not ") + "ok " + (p.isOk() ? "" : ": " + p.getReason());
}

void ContainerClient::menu() {
    std::string bah;
    std::cout << " > Press a key to continue <" << std::endl;
    getline(std::cin, bah);
    std::cout << "\033[2J\033[;H";



    unsigned long input;
    std::cout
        << std::endl
        << "  Menu" << std::endl
        << " ------" << std::endl << std::endl
        << "    1. Send a InputTruck packet" << std::endl
        << "    2. Send a InputDone packet" << std::endl
        << "    3. Send a OutputReady packet" << std::endl
        << "    4. Send a OutputOne packet" << std::endl
        << "    5. Send a OutputDone packet" << std::endl
        << "    6. Send a Logout packet" << std::endl
        << std::endl << " Your choice: ";

    while(true) {
        input = InputHelper::readUnsignedInt();
        if(input > 0 && input < 7) {
            break;
        }
        std::cout << " > Invalid choice, try again: ";
    }
    std::cout << std::endl;

    switch(input) {
        case 1: {
            this->proto.write(this->socket, InputTruckPacket("license", {
                { "id", "destination", 0, 0 }
            }));
            std::cout << packetResult(
                this->proto.readSpecificPacket<InputTruckResponsePacket>(this->socket)
            ) << std::endl;
            break;
        }
        case 2: {
            this->proto.write(this->socket, InputDonePacket(true, 12.9));
            std::cout << packetResult(
                this->proto.readSpecificPacket<InputDoneResponsePacket>(this->socket)
            ) << std::endl;
            break;
        }
        case 3: {
            this->proto.write(this->socket, OutputReadyPacket("license", "destination", 2));
            std::cout << packetResult(
                this->proto.readSpecificPacket<OutputReadyResponsePacket>(this->socket)
            ) << std::endl;
            break;
        }
        case 4: {
            this->proto.write(this->socket, OutputOnePacket("id"));
            std::cout << packetResult(
                this->proto.readSpecificPacket<OutputOneResponsePacket>(this->socket)
            ) << std::endl;
            break;
        }
        case 5: {
            this->proto.write(this->socket, OutputDonePacket("license", 2));
            std::cout << packetResult(
                this->proto.readSpecificPacket<OutputDoneResponsePacket>(this->socket)
            ) << std::endl;
            break;
        }
        case 6: {
            this->proto.write(this->socket, LogoutPacket("", ""));
            try {
                std::cout << packetResult(
                    this->proto.readSpecificPacket<LogoutResponsePacket>(this->socket)
                ) << std::endl;
            } catch(IOError e) {
                std::cout << e.what() << std::endl;
                this->closed = true;
            }
            this->loggedIn = false;
            break;
        }
        default:
            assert(false);
            break;
    }
}

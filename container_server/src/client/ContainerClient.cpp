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

        std::string bah;
        std::cout << " > Press a key to continue <" << std::endl;
        getline(std::cin, bah);
        std::cout << "\033[2J\033[;H";
    }

    return *this;
}

void ContainerClient::loginMenu() {
    std::cout
        << " Login" << std::endl
        << " -----" << std::endl << std::endl
        << " Create a new user? ";
    bool newUser = InputHelper::readBool();
    std::cout << " Username: ";
    std::string username = InputHelper::readNonEmtpyString();
    std::cout << " Password: ";
    std::string password = InputHelper::readPassword();

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
void packetResult(const P& p) {
    LOG << Logger::Debug << std::string(typeid(P).name()) + " went " + (p.isOk() ? "" : "not ") + "ok" + (p.isOk() ? "" : ": " + p.getReason());
}

void ContainerClient::menu() {
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
        << "    7. Quit" << std::endl
        << std::endl << " Your choice: ";

    while(true) {
        input = InputHelper::readUnsignedInt();
        if(input > 0 && input < 8) {
            break;
        }
        std::cout << " > Invalid choice, try again: ";
    }
    std::cout << std::endl;

    switch(input) {
        case 1: {
            this->proto.write(this->socket, InputTruckPacket("license", {
                { "id" + std::to_string(std::rand()), "destination", 0, 0 }
            }));
            InputTruckResponsePacket p = this->proto.readSpecificPacket<InputTruckResponsePacket>(this->socket);
            packetResult(p);
            if(p.isOk()) {
                std::cout << " > " << p.getContainers().size() << " containers:" << std::endl;
                for(const Container& container : p.getContainers()) {
                    std::cout << " >> " << container.id << " to " << container.x << ':' << container.y << std::endl;
                }
            }
            break;
        }
        case 2: {
            this->proto.write(this->socket, InputDonePacket(true, 12.9));
            InputDoneResponsePacket p = this->proto.readSpecificPacket<InputDoneResponsePacket>(this->socket);
            packetResult(p);
            break;
        }
        case 3: {
            this->proto.write(this->socket, OutputReadyPacket("license", "destination", 2));
            OutputReadyResponsePacket p = this->proto.readSpecificPacket<OutputReadyResponsePacket>(this->socket);
            packetResult(p);
            break;
        }
        case 4: {
            this->proto.write(this->socket, OutputOnePacket("id"));
            OutputOneResponsePacket p = this->proto.readSpecificPacket<OutputOneResponsePacket>(this->socket);
            packetResult(p);
            break;
        }
        case 5: {
            this->proto.write(this->socket, OutputDonePacket("license", 2));
            OutputDoneResponsePacket p = this->proto.readSpecificPacket<OutputDoneResponsePacket>(this->socket);
            packetResult(p);
            break;
        }
        case 6: {
            this->proto.write(this->socket, LogoutPacket("", ""));
            try {
                LogoutResponsePacket p = this->proto.readSpecificPacket<LogoutResponsePacket>(this->socket);
                packetResult(p);
            } catch(IOError e) {
                std::cout << e.what() << std::endl;
                this->closed = true;
            }
            this->loggedIn = false;
            break;
        }
        case 7: {
            this->proto.write(this->socket, LogoutPacket("", ""));
            try {
                LogoutResponsePacket p = this->proto.readSpecificPacket<LogoutResponsePacket>(this->socket);
                packetResult(p);
            } catch(IOError e) {
                std::cout << e.what() << std::endl;
            }
            this->closed = true;
            this->loggedIn = false;
            break;
        }
        default:
            assert(false);
            break;
    }
}

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
    std::cout << "wanna create a user bitch? ";
    this->login(InputHelper::readBool());
}

void ContainerClient::login(bool newUser) {
    std::cout << "username plz: ";
    std::string username = InputHelper::readNonEmtpyString();
    std::cout << "password plz: ";
    std::string password = InputHelper::readNonEmtpyString(); // TODO Hiding thing?

    this->proto.write(this->socket, LoginPacket(username, password, newUser));
    LoginResponsePacket response = this->proto.readSpecificPacket<LoginResponsePacket>(this->socket);
    if(response.isOk()) {
        this->loggedIn = true;
        std::cout << "> Login successful" << std::endl;
    } else {
        std::cout << "> Login failed: " << response.getReason() << std::endl;
    }
}

void ContainerClient::menu() {
    unsigned long input;
    std::cout
        << "1. Send a InputTruck packet" << std::endl
        << "2. Send a InputDone packet" << std::endl
        << "3. Send a OutputReady packet" << std::endl
        << "4. Send a OutputOne packet" << std::endl
        << "5. Send a OutputDone packet" << std::endl
        << "6. Send a Logout packet" << std::endl
        << std::endl << "You choice: ";

    while(true) {
        input = InputHelper::readUnsignedInt();
        if(input > 0 || input < 7) {
            break;
        }
        std::cout << "Invalid choice, try again: ";
    }

    switch(input) {
        case 1: {
            this->proto.write(this->socket, InputTruckPacket("license", {
                { "id", "destination", 0, 0 }
            }));
            InputTruckResponsePacket p = this->proto.readSpecificPacket<InputTruckResponsePacket>(
                this->socket);
            std::cout << "InputTruck went " << (p.isOk() ? "" : "not") << " ok" << std::endl;
            break;
        }
        case 2: {
            this->proto.write(this->socket, InputDonePacket(true, 12.9));
            InputDoneResponsePacket p = this->proto.readSpecificPacket<InputDoneResponsePacket>(
                this->socket);
            std::cout << "InputDone went " << (p.isOk() ? "" : "not (" + p.getReason() + ")") << " ok" << std::endl;
            break;
        }
        case 3: {
            this->proto.write(this->socket, OutputReadyPacket("license", "destination", 2));
            OutputReadyResponsePacket p = this->proto.readSpecificPacket<OutputReadyResponsePacket>(
                this->socket);
            std::cout << "OutputReady went " << (p.isOk() ? "" : "not (" + p.getReason() + ")") << " ok" << std::endl;
            break;
        }
        case 4: {
            this->proto.write(this->socket, OutputOnePacket("id"));
            OutputOneResponsePacket p = this->proto.readSpecificPacket<OutputOneResponsePacket>(
                this->socket);
            std::cout << "OutputOne went " << (p.isOk() ? "" : "not (" + p.getReason() + ")") << " ok" << std::endl;
            break;
        }
        case 5: {
            this->proto.write(this->socket, OutputDonePacket("license", 2));
            OutputDoneResponsePacket p = this->proto.readSpecificPacket<OutputDoneResponsePacket>(
                this->socket);
            std::cout << "OutputDone went " << (p.isOk() ? "" : "not (" + p.getReason() + ")") << " ok" << std::endl;
            break;
        }
        case 6: {
            this->proto.write(this->socket, LogoutPacket("", ""));
            LogoutResponsePacket p = this->proto.readSpecificPacket<LogoutResponsePacket>(
                this->socket);
            std::cout << "Logout went " << (p.isOk() ? "" : "not (" + p.getReason() + ")") << " ok" << std::endl;
            break;
        }
        default:
            assert(false);
            break;
    }
}

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
    std::string input;
    std::getline(std::cin, input);
    this->login(input == "yeah bro");
}

void ContainerClient::login(bool newUser) {
    std::cout << "username plz: ";
    std::string username, password;
    std::getline(std::cin, username);
    std::cout << "password plz: ";
    std::getline(std::cin, password);

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
    std::string input;
    std::getline(std::cin, input);
    //switch(input) {
    //    //case "input":
    //    //case "output":
    //    //case "logout":
    //    //case "quit":
    //        this->proto.write(this->socket, LogoutPacket("", ""));
    //        return;
    //    default:
    //        break;
    //}
}

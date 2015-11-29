#include "client/ContainerClient.hpp"

ContainerClient::ContainerClient(std::shared_ptr<Socket> ptr, ProtocolHandler<Translator, PacketId>& proto)
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

        string bah;
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
    bool newUser = InputHelper::readBool(" > Invalid boolean, enter a valid boolean: ");
    std::cout << " Username: ";
    string username = InputHelper::readString([](cstring_ref username) {
        return !username.empty() && username.find(';') == string::npos;
    }, " > Invalid username, enter your real one: ");
    std::cout << " Password: ";
    string password = InputHelper::readPassword([](cstring_ref password) {
        return !password.empty();
    }, " > You password can't be empty, enter a password: ");

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

    input = InputHelper::readUnsignedInt([](unsigned int i) {
        return i > 0 && i < 8;
    }, " > Invalid choice, try again: ");
    std::cout << std::endl;

    switch(input) {
        // InputTruck
        case 1: {
            std::cout << " Enter your license: ";
            string license = InputHelper::readString([](cstring_ref license) {
                return !license.empty();
            }, " > Invalid license, enter a new one: ");

            std::cout << " Enter the number of containers to input: ";
            unsigned int count = InputHelper::readUnsignedInt();
            std::vector<Container> containers;
            containers.reserve(count);

            string container_id;
            string destination;
            for(unsigned int i = 0; i < count; ++i) {
                std::cout
                    << " = Container " << i + 1 << " =" << std::endl
                    << " Enter the container id: ";
                container_id = InputHelper::readString([](cstring_ref container_id) {
                    return !container_id.empty();
                }, " > Container id can't be empty, enter a new one: ");
                std::cout << " Enter the container destination: ";
                destination = InputHelper::readString([](cstring_ref destination) {
                    return !destination.empty();
                }, " > Destination can't be empty, enter a new one: ");
                containers.push_back({ container_id, destination, 0, 0 });
            }

            this->proto.write(this->socket, InputTruckPacket("license", containers));
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
        // InputTruckDone
        case 2: {
            std::cout << " Enter the weight of all these containers: ";
            this->proto.write(this->socket, InputDonePacket(true, InputHelper::readFloat([](float f) {
                return f > 0;
            }, " > Invalid weight, enter a valid one: ")));
            InputDoneResponsePacket p = this->proto.readSpecificPacket<InputDoneResponsePacket>(this->socket);
            packetResult(p);
            break;
        }
        // OutputTruck
        case 3: {
            std::cout << " Insert the license: ";
            string license = InputHelper::readString([](cstring_ref license) {
                return !license.empty();
            }, " > Type in a license please: ");

            std::cout << " Insert the destination: ";
            string destination = InputHelper::readString([](cstring_ref destination) {
                return !destination.empty();
            }, " > Type in a destination please: ");

            std::cout << " Insert the capacity of your mean of transport: ";
            unsigned int count = InputHelper::readUnsignedInt();

            this->proto.write(this->socket, OutputReadyPacket(license, destination, count));
            OutputReadyResponsePacket p = this->proto.readSpecificPacket<OutputReadyResponsePacket>(this->socket);
            packetResult(p);
            if(p.isOk()) {
                for(const Container& container : p.getContainers()) {
                    std::cout
                        << " => container '" << container.id
                        << "' for " << container.destination
                        << ' ' << container.x << ':' << container.y << std::endl;
                }
            }
            break;
        }
        case 4: {
            std::cout << " Enter the container id you just loaded: ";
            string id = InputHelper::readString([](cstring_ref id) {
                return !id.empty();
            }, " > The container id can't be empty, enter a new one: ");

            this->proto.write(this->socket, OutputOnePacket(id));
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

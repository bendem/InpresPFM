#include "input/InputHelper.hpp"

const std::map<std::string, bool> InputHelper::BOOLEANS {
    { "yes",    true },
    { "y",      true },
    { "oui",    true },
    { "o",      true },
    { "yeah",   true },
    { "yep",    true },
    { "sure",   true },
    { "ok",     true },
    { "da",     true },
    { "true",   true },
    { "no",     false },
    { "non",    false },
    { "n",      false },
    { "nope",   false },
    { "no way", false },
    { "niet",   false },
    { "nada",   false },
    { "nein",   false },
    { "false",  false },
};

bool InputHelper::readBool() {
    std::string input;

    for(;;) {
        std::getline(std::cin, input);
        std::transform(input.cbegin(), input.cend(), input.begin(), ::tolower);
        auto it = BOOLEANS.find(input);
        if(it != BOOLEANS.end()) {
            return it->second;
        }
        std::cout << " > Invalid boolean, try again: ";
    }
}

int InputHelper::readInt() {
    std::string input;

    for(;;) {
        std::getline(std::cin, input);

        try {
            return std::stoi(input);
        } catch(std::invalid_argument e) {
            std::cout << "> x Input is not a valid int: " << e.what() << std::endl;
        } catch(std::out_of_range e) {
            std::cout << "> x Entered value is too big: " << e.what() << std::endl;
        }
    }
}

unsigned long InputHelper::readUnsignedInt() {
    std::string input;

    for(;;) {
        std::getline(std::cin, input);

        try {
            return std::stoul(input);
        } catch(std::invalid_argument e) {
            std::cout << "> x Input is not a valid int: " << e.what() << std::endl;
        } catch(std::out_of_range e) {
            std::cout << "> x Entered value is too big: " << e.what() << std::endl;
        }
    }
}

std::string InputHelper::readNonEmtpyString() {
    std::string input;

    for(;;) {
        std::getline(std::cin, input);

        if(input.empty()) {
            std::cout << "> x Please provide a value: ";
        } else {
            return input;
        }
    }
}

std::string InputHelper::readPassword() {
    std::string input;

    for(;;) {
        echoInput(false);
        std::getline(std::cin, input);
        echoInput(true);
        std::cout << std::endl;

        if(input.empty()) {
            std::cout << "> x Please provide a value: ";
        } else {
            return input;
        }
    }
}

void InputHelper::echoInput(bool yes) {
    struct termios tty;
    tcgetattr(STDIN_FILENO, &tty);
    if(yes) {
        tty.c_lflag |= ECHO;
    } else {
        tty.c_lflag &= ~ECHO;
    }
    tcsetattr(STDIN_FILENO, TCSANOW, &tty);
}

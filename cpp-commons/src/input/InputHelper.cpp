#include "input/InputHelper.hpp"

const std::map<std::string, bool> InputHelper::BOOLEANS {
    { "yes",    true },
    { "y",      true },
    { "1",      true },
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
    { "0",      false },
    { "nope",   false },
    { "no way", false },
    { "niet",   false },
    { "nada",   false },
    { "nein",   false },
    { "false",  false },
};

bool InputHelper::readBool(const std::string& error) {
    std::string input;

    for(;;) {
        Sanity::streamness(std::cin, "cin got closed");
        std::getline(std::cin, input);
        std::transform(input.cbegin(), input.cend(), input.begin(), ::tolower);
        auto it = BOOLEANS.find(input);
        if(it != BOOLEANS.end()) {
            return it->second;
        }
        std::cout << error;
    }
}

int InputHelper::readInt(predicate<int> predicate, const std::string& error) {
    std::string input;

    for(;;) {
        Sanity::streamness(std::cin, "cin got closed");
        std::getline(std::cin, input);

        try {
            int i = std::stoi(input);
            if(!predicate(i)) {
                std::cout << error;
                continue;
            }
            return i;
        } catch(std::invalid_argument e) {
            std::cout << "> x Input is not a valid int, enter a valid one: ";
        } catch(std::out_of_range e) {
            std::cout << "> x Entered value is too big, enter a smaller one: ";
        }
    }
}

unsigned int InputHelper::readUnsignedInt(predicate<unsigned int> predicate, const std::string& error) {
    std::string input;

    for(;;) {
        Sanity::streamness(std::cin, "cin got closed");
        std::getline(std::cin, input);

        try {
            unsigned int i = static_cast<unsigned int>(std::stoi(input));
            if(!predicate(i)) {
                std::cout << error;
                continue;
            }
            return i;
        } catch(std::invalid_argument e) {
            std::cout << " > Input is not a valid int, enter a valid integer: ";
        } catch(std::out_of_range e) {
            std::cout << " > Entered value is too big, enter a smaller integer: ";
        }
    }
}

float InputHelper::readFloat(predicate<float> predicate, const std::string& error) {
    std::string input;

    for(;;) {
        Sanity::streamness(std::cin, "cin got closed");
        std::getline(std::cin, input);

        try {
            float i = std::stof(input);
            if(!predicate(i)) {
                std::cout << error;
                continue;
            }
            return i;
        } catch(std::invalid_argument e) {
            std::cout << "> x Input is not a valid float, enter a valid one: ";
        } catch(std::out_of_range e) {
            std::cout << "> x Entered value is too big, enter a smaller one: ";
        }
    }
}

std::string InputHelper::readString(predicate<std::string> predicate, const std::string& error) {
    std::string input;

    for(;;) {
        Sanity::streamness(std::cin, "cin got closed");
        std::getline(std::cin, input);

        if(!predicate(input)) {
            std::cout << error;
            continue;
        }
        return input;
    }
}

std::string InputHelper::readPassword(predicate<std::string> predicate, const std::string& error) {
    std::string input;

    for(;;) {
        Sanity::streamness(std::cin, "cin got closed");
        echoInput(false);
        std::getline(std::cin, input);
        echoInput(true);
        std::cout << std::endl;

        if(!predicate(input)) {
            std::cout << error;
            continue;
        }
        return input;
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

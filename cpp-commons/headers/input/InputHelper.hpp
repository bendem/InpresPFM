#ifndef CPP_COMMONS_INPUTHELPER_HPP
#define CPP_COMMONS_INPUTHELPER_HPP

#include <algorithm>
#include <iostream>
#include <map>
#include <string>

#include <termios.h>
#include <unistd.h>

class InputHelper {

public:
    static bool readBool();
    static int readInt();
    static unsigned long readUnsignedInt();
    static std::string readNonEmtpyString();
    static std::string readPassword();
    static void echoInput(bool);

private:
    static const std::map<std::string, bool> BOOLEANS;

};

#endif

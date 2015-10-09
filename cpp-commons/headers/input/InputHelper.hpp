#ifndef CPP_COMMONS_INPUTHELPER_HPP
#define CPP_COMMONS_INPUTHELPER_HPP

#include <algorithm>
#include <iostream>
#include <map>
#include <string>

class InputHelper {

public:
    static bool readBool();
    static int readInt();
    static unsigned long readUnsignedInt();
    static std::string readNonEmtpyString();

private:
    static const std::map<std::string, bool> BOOLEANS;

};

#endif

#ifndef CPP_COMMONS_INPUTHELPER_HPP
#define CPP_COMMONS_INPUTHELPER_HPP

#include <algorithm>
#include <functional>
#include <iostream>
#include <map>
#include <string>

#include <termios.h>
#include <unistd.h>

template<class T>
bool f(const T&) {
    return true;
}

class InputHelper {

public:
    template<class T> using predicate = std::function<bool(const T&)>;

    static bool readBool(const std::string&);
    static int readInt(predicate<int> = f<int>, const std::string& = "");
    static unsigned int readUnsignedInt(predicate<unsigned int> = f<unsigned int>, const std::string& = "");
    static float readFloat(predicate<float> = f<float>, const std::string& = "");
    static std::string readString(predicate<std::string> = f<std::string>, const std::string& = "");
    static std::string readPassword(predicate<std::string> = f<std::string>, const std::string& = "");
    static void echoInput(bool);

private:
    static const std::map<std::string, bool> BOOLEANS;

};

#endif

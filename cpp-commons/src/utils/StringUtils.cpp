#include "utils/StringUtils.hpp"

bool StringUtils::startsWith(const std::string& string, const std::string& search) {
    if(search.length() > string.length()) {
        return false;
    }

    return memcmp(string.data(), search.data(), search.length()) == 0;
}

bool StringUtils::endsWith(const std::string& string, const std::string& search) {
    if(search.length() > string.length()) {
        return false;
    }

    return memcmp(&string[string.length() - search.length()], search.data(), search.length()) == 0;
}

std::string StringUtils::rtrim(const std::string& string, char c) {
    if(string.empty()) {
        return string;
    }

    unsigned long i;
    for(i = string.length() - 1; i < string.length(); --i) {
        if(string[i] != c) {
            break;
        }
    }

    return string.substr(0, i + 1);
}

std::string StringUtils::ltrim(const std::string& string, char c) {
    if(string.empty()) {
        return string;
    }

    unsigned long i;
    for(i = 0; i < string.length(); ++i) {
        if(string[i] != c) {
            break;
        }
    }

    return string.substr(i);
}

std::string StringUtils::trim(const std::string& string, char c) {
    return ltrim(rtrim(string, c), c);
}

std::vector<std::string> StringUtils::split(const std::string& string, char c) {
    if(string.empty()) {
        return {};
    }

    std::vector<std::string> result;
    std::istringstream ss(string);
    std::string item;
    while(std::getline(ss, item, c)) {
        result.push_back(item);
    }
    return result;
}

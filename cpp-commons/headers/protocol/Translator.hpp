#ifndef CPP_COMMONS_TRANSLATOR_HPP
#define CPP_COMMONS_TRANSLATOR_HPP

#include <cstdint>
#include <string>
#include <vector>

class Translator {

public:
    template<class T>
    static T readPrimitive(std::vector<char>::const_iterator&);
    static std::string readString(std::vector<char>::const_iterator&);

    template<class T>
    static void writePrimitive(std::vector<char>&, T);
    static void writeString(std::vector<char>&, std::string&);

};

template<class T>
T Translator::readPrimitive(std::vector<char>::const_iterator& it) {
    const T* p = reinterpret_cast<const T*>(&*it);
    it += sizeof(T);
    return *p;
}

std::string Translator::readString(std::vector<char>::const_iterator& it) {
    uint32_t len = readPrimitive<uint32_t>(it);
    std::string res;
    for (uint32_t i = 0; i < len; ++i) {
        res += *it;
        ++it;
    }
    return res;
}

template<class T>
void Translator::writePrimitive(std::vector<char>& v, T p) {
    const char* c = reinterpret_cast<const char*>(&p);

    for (unsigned int i = 0; i < sizeof(T); ++i) {
        v.push_back(c[i]);
    }
}

void Translator::writeString(std::vector<char>& v, std::string& str) {
    writePrimitive<uint32_t>(v, str.size());
    for(char c : str) {
        v.push_back(c);
    }
}

#endif //CPP_COMMONS_TRANSLATOR_HPP

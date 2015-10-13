#ifndef CPP_COMMONS_PROGRAMPROPERTIES_HPP
#define CPP_COMMONS_PROGRAMPROPERTIES_HPP

#include <fstream>
#include <map>
#include <string>

#include "utils/Logger.hpp"
#include "utils/Sanity.hpp"
#include "utils/StringUtils.hpp"

class ProgramProperties {

public:
    ProgramProperties(int argc, char** argv);

    const std::string& getProgramName() const { return programName; }
    bool has(const std::string& key) const { return props.find(key) != props.end(); }

    const std::string& get(const std::string&) const;
    const std::string& get(const std::string&, const std::string&) const;
    int getAsInt(const std::string&, int) const;
    short getAsShort(const std::string&, short) const;
    unsigned long getAsUnsigned(const std::string&, unsigned) const;
    unsigned short getAsUnsignedShort(const std::string&, unsigned short) const;
    std::map<std::string, std::string> getProperties() const { return props; }

private:
    std::string programName;
    std::map<std::string, std::string> props;

    void loadFile(std::istream&);

};

#endif

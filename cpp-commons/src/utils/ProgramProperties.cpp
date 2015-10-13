#include "utils/ProgramProperties.hpp"

ProgramProperties::ProgramProperties(int argc, char** argv) : programName(argv[0]) {
    Sanity::truthness(argc > 0, "argc must be greater than 0");
    Sanity::nullness(argv, "argv can't be null");

    std::string prev;
    std::string current;
    for(int i = 1; i < argc; ++i) {
        current = argv[i];
        if(current.empty()) {
            continue;
        }

        if(StringUtils::startsWith(current, "--")) {
            std::vector<std::string> parts(StringUtils::split(current.substr(2), '='));
            std::string val;
            if(parts.size() > 1) {
                val = StringUtils::join(parts.begin() + 1, parts.end(), "=");
            }
            if(!props.insert({ parts[0], val }).second) {
                props.find(parts[0])->second = val;
            }

        } else if(StringUtils::startsWith(current, "-")) {
            prev = StringUtils::ltrim(current, '-');
            props.insert({ prev, "" });

        } else {
            if(!prev.empty()) {
                if(!props.insert({ prev, current }).second) {
                    props.find(prev)->second = current;
                }
            } else {
                throw std::runtime_error("Parameter value without name");
            }
        }
    }

    std::string properties = get("property-file", StringUtils::split(programName, '/').back() + ".properties");
    std::ifstream is(properties);
    if(!is.fail()) {
        LOG << Logger::Debug << "Property file found (" << properties << ")";
        this->loadFile(is);
    }
}

const std::string& ProgramProperties::get(const std::string& key) const {
    if(!has(key)) {
        throw std::runtime_error("No property '" + key + "'");
    }
    return props.find(key)->second;
}

const std::string& ProgramProperties::get(const std::string& key, const std::string& def) const {
    auto value = props.find(key);
    if(value == props.end()) {
        return def;
    }
    return value->second;
}

int ProgramProperties::getAsInt(const std::string& key, int def) const {
    return has(key) ? std::stoi(get(key)) : def;
}

short ProgramProperties::getAsShort(const std::string& key, short def) const {
    return static_cast<short>(getAsInt(key, def));
}

unsigned long ProgramProperties::getAsUnsigned(const std::string& key, unsigned int def) const {
    return has(key) ? std::stoul(get(key)) : def;
}

unsigned short ProgramProperties::getAsUnsignedShort(const std::string& key, unsigned short def) const {
    return static_cast<unsigned short>(getAsInt(key, def));
}

void ProgramProperties::loadFile(std::istream& is) {
    std::string line;
    while(std::getline(is, line)) {
        if(line.empty() || line[0] == '#' || line[0] == ';') {
            continue;
        }

        std::vector<std::string> parts = StringUtils::split(line, ':');
        if(parts.empty() || !get(parts[0], "").empty()) {
            continue;
        }

        props.insert({ parts[0], StringUtils::join(parts.begin() + 1, parts.end(), ":") });
    }
}

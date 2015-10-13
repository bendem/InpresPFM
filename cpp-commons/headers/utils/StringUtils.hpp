#ifndef CPP_COMMONS_STRINGUTILS_HPP
#define CPP_COMMONS_STRINGUTILS_HPP

#include <algorithm>
#include <cstring>
#include <sstream>
#include <string>

class StringUtils {

public:
    static bool startsWith(const std::string&, const std::string&);
    static bool endsWith(const std::string&, const std::string&);
    static std::string rtrim(const std::string&, char = ' ');
    static std::string ltrim(const std::string&, char = ' ');
    static std::string trim(const std::string&, char = ' ');
    static std::vector<std::string> split(const std::string&, char = ' ');
    template<class IterStart, class IterEnd>
    static std::string join(IterStart, IterEnd, const std::string&, const std::string& = "", const std::string& = "");

};

template<class IterStart, class IterEnd>
std::string StringUtils::join(IterStart start, IterEnd end, const std::string& delim, const std::string& before, const std::string& after) {
    if(start == end) {
        return before + after;
    }

    return before + std::accumulate(
        start + 1,
        end,
        *start,
        [&delim](const std::string& a, const std::string& b) {
            return a + delim + b;
        }
    ) + after;
}

#endif

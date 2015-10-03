#ifndef CPP_COMMONS_CSVFILE_HPP
#define CPP_COMMONS_CSVFILE_HPP

#include <algorithm>
#include <istream>
#include <map>
#include <sstream>
#include <vector>

#include "utils/Sanity.hpp"

class CSVFile {

public:
    CSVFile(std::istream&, char);

    std::string get(const std::string&, long) const;
    std::map<std::string, std::string> find(const std::string&, const std::string&) const;
    unsigned long size() const { return data.size(); }
    unsigned long columnCount() const { return columns.size(); }

private:
    std::vector<std::vector<std::string>> data;
    std::map<std::string, long> columns;

    void parseHeader(std::istream&, unsigned long, char);
    void parseData(std::istream&, char);
    unsigned getColumn(const std::string&) const;

};

#endif

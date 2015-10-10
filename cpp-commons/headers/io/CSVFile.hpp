#ifndef CPP_COMMONS_CSVFILE_HPP
#define CPP_COMMONS_CSVFILE_HPP

#include <algorithm>
#include <fstream>
#include <map>
#include <sstream>
#include <vector>

#include "utils/Sanity.hpp"

class CSVFile {

public:
    CSVFile(const std::string&, char);

    std::string get(const std::string&, long) const;
    std::map<std::string, std::string> find(const std::string&, const std::string&) const;
    CSVFile& insert(const std::vector<std::string>&);
    CSVFile& save();
    unsigned long size() const { return data.size(); }
    unsigned long columnCount() const { return columns.size(); }

private:
    const std::string file;
    const char sep;
    std::vector<std::vector<std::string>> data;
    std::vector<std::string> columns;

    void parseHeader(std::istream&, unsigned long, char);
    void parseData(std::istream&, char);
    unsigned getColumn(const std::string&) const;
    std::string join(const std::vector<std::string>&, char);

};

#endif

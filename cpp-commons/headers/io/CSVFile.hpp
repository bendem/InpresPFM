#ifndef CPP_COMMONS_CSVFILE_HPP
#define CPP_COMMONS_CSVFILE_HPP

#include <algorithm>
#include <istream>
#include <map>
#include <sstream>
#include <vector>

class CSVFile {

public:
    CSVFile(std::istream&, char);

    std::string get(const std::string&, unsigned) const;
    std::map<std::string, std::string> find(const std::string&, const std::string&) const;
    unsigned size() const { return data.size(); }
    unsigned columnCount() const { return columns.size(); }

private:
    std::vector<std::vector<std::string>> data;
    std::map<std::string, unsigned> columns;

    void parseHeader(std::istream&, unsigned, char);
    void parseData(std::istream&, char);
    unsigned getColumn(const std::string&) const;

};

#endif

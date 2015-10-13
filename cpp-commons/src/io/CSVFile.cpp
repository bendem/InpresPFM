#include <utils/StringUtils.hpp>
#include "io/CSVFile.hpp"

CSVFile::CSVFile(const std::string& file, char sep) : file(file), sep(sep) {
    std::ifstream is(file);
    Sanity::streamness(is, "Invalid stream provided to CSVFile");
    std::string line;
    getline(is, line);
    unsigned long count = std::count(line.begin(), line.end(), sep);

    std::istringstream header_is(line);
    this->parseHeader(header_is, count, sep);
    this->parseData(is, sep);
}

std::string CSVFile::get(const std::string& column, long line) const {
    return this->data[line][this->getColumn(column)];
}

std::map<std::string, std::string> CSVFile::find(const std::string& column, const std::string& value) const {
    unsigned column_index = this->getColumn(column);
    for(unsigned i = 0; i < this->data.size(); ++i) {
        if(this->data[i][column_index] == value) {
            std::map<std::string, std::string> result;
            for(unsigned col = 0; col < this->columns.size(); ++col) {
                result.insert({ this->columns[col], this->data[i][col] });
            }
            return result;
        }
    }

    return {};
}

CSVFile& CSVFile::insert(const std::vector<std::string>& values) {
    Sanity::truthness(values.size() == this->columns.size(), "Invalid number of values to insert");

    this->data.emplace_back(std::vector<std::string> { values });
    return *this;
}

CSVFile& CSVFile::save() {
    std::ofstream os(file);
    Sanity::streamness(os, "Invalid stream passed to CSVFile::save");

    if(this->data.empty()) {
        return *this;
    }

    os << join(this->columns, sep) << std::endl;
    for(auto& line : this->data) {
        os << join(line, sep) << std::endl;
    }

    return *this;
}

void CSVFile::parseHeader(std::istream& is, unsigned long count, char sep) {
    std::string part;
    for(unsigned long i = 0; i < count; ++i) {
        getline(is, part, sep);
        this->columns.push_back(part);
    }
    // Read last one
    is >> part;
    this->columns.push_back(part);
}

void CSVFile::parseData(std::istream& is, char sep) {
    std::string part;
    while(is.peek() != EOF) {
        std::vector<std::string> parts;
        for(unsigned long i = 0; i < this->columns.size() - 1; ++i) {
            getline(is, part, sep);
            parts.push_back(part);
        }
        getline(is, part);
        parts.push_back(part);
        data.emplace_back(parts);
    }
}

unsigned CSVFile::getColumn(const std::string& column) const {
    for(unsigned i = 0; i < this->columns.size(); ++i) {
        if(this->columns[i] == column) {
            return i;
        }
    }
    throw std::runtime_error("File doesn't contain '" + column + "'");
}

std::string CSVFile::join(const std::vector<std::string>& vector, char sep) {
    if(vector.size() == 0) {
        return "";
    }
    if(vector.size() == 1) {
        return vector[0];
    }

    return StringUtils::join(vector.begin(), vector.end(), std::string(1, sep));
}

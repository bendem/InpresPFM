#include "utils/CSVFile.hpp"

CSVFile::CSVFile(std::istream& is, char sep) {
    // TODO Check stream
    std::string line;
    getline(is, line);
    unsigned count = std::count(line.begin(), line.end(), sep);

    std::istringstream header_is(line);
    this->parseHeader(header_is, count, sep);
    this->parseData(is, sep);
}

std::string CSVFile::get(const std::string& column, unsigned line) const {
    return this->data[line][this->getColumn(column)];
}

std::map<std::string, std::string> CSVFile::search(const std::string& column, const std::string& value) const {
    unsigned col = this->getColumn(column);
    for(unsigned i = 0; i < this->data.size(); ++i) {
        if(this->data[i][col] == value) {
            std::map<std::string, std::string> result;
            for(auto& item : this->columns) {
                result.insert({ item.first, this->data[i][item.second] });
            }
            return result;
        }
    }

    return {};
}

void CSVFile::parseHeader(std::istream& is, unsigned count, char sep) {
    std::string part;
    for(unsigned i = 0; i < count; ++i) {
        getline(is, part, sep);
        this->columns.insert({part, i});
    }
    // Read last one
    is >> part;
    this->columns.insert({part, count});
}

void CSVFile::parseData(std::istream& is, char sep) {
    std::string part;
    while(is.peek() != EOF) {
        std::vector<std::string> parts;
        for(unsigned i = 0; i < this->columns.size() - 1; ++i) {
            getline(is, part, sep);
            parts.push_back(part);
        }
        getline(is, part);
        parts.push_back(part);
        data.emplace_back(parts);
    }
}

unsigned CSVFile::getColumn(const std::string& column) const {
    auto it = this->columns.find(column);
    if(it == this->columns.end()) {
        throw std::runtime_error("File doesn't contain '" + column + "'");
    }
    return it->second;
}

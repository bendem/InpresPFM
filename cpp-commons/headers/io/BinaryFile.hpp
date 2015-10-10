#ifndef CPP_COMMONS_BINARYFILE_HPP
#define CPP_COMMONS_BINARYFILE_HPP

#include <fstream>
#include <mutex>
#include <string>
#include <vector>

#include "io/StreamUtils.hpp"
#include "utils/Sanity.hpp"

/**
 * Thread safe implementation of an utility to save an iterable
 * containing an overload for the operator<<(ostream&, const T&)
 * and operator>>(istream&, T&) to and from a file.
 */
template<class T>
class BinaryFile {

public:
    BinaryFile(const std::string& file) : file(file) {}

    std::vector<T> load();

    template<class ItFirst, class ItLast>
    void save(ItFirst, ItLast);

private:
    const std::string file;
    std::mutex mutex;

};

template<class T>
std::vector<T> BinaryFile<T>::load() {
    std::lock_guard<std::mutex> lk(mutex);
    std::ifstream is(file);
    if(!is.good()) {
        return {};
    }

    Sanity::streamness(is, "Could not read file: " + file);

    // Read count
    uint64_t size = StreamUtils::read<uint64_t>(is), i = 0;

    std::vector<T> result;
    result.reserve(size);
    T t;
    while(i++ < size) {
        is >> t;
        result.push_back(t);
    }

    return result;
}

template<class T>
template<class ItFirst, class ItLast>
void BinaryFile<T>::save(ItFirst first, ItLast last) {
    std::lock_guard<std::mutex> lk(mutex);
    std::ofstream os(file);
    Sanity::streamness(os, "Could not write to file: " + file);

    os.seekp(sizeof(uint64_t));
    uint64_t size;
    for(size = 0; first != last; ++first, ++size) {
        os << *first;
    }
    os.seekp(0);
    StreamUtils::write(os, size);
}


#endif

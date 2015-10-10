#ifndef CPP_COMMONS_BINARYFILE_HPP
#define CPP_COMMONS_BINARYFILE_HPP

#include <fstream>
#include <mutex>
#include <string>
#include <vector>

#include "io/StreamUtils.hpp"
#include "utils/Sanity.hpp"

using std::ios;

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

    bool update(const T&, std::function<bool(const T&)>);
    void append(const T&);

private:
    const std::string file;
    std::mutex mutex;

    std::vector<T> collect(std::istream&, uint64_t, uint64_t);

};

template<class T>
std::vector<T> BinaryFile<T>::load() {
    std::lock_guard<std::mutex> lk(mutex);
    std::ifstream is(file, ios::in | ios::binary);
    if(!is.good()) {
        return {};
    }

    Sanity::streamness(is, "Could not open " + file + " to read");

    return this->collect(is, 0, StreamUtils::read<uint64_t>(is));
}

template<class T>
template<class ItFirst, class ItLast>
void BinaryFile<T>::save(ItFirst first, ItLast last) {
    std::lock_guard<std::mutex> lk(mutex);
    std::ofstream os(file, ios::trunc | ios::out | ios::binary);
    Sanity::streamness(os, "Could not open " + file + " to write");

    os.seekp(sizeof(uint64_t));
    uint64_t size;
    for(size = 0; first != last; ++first, ++size) {
        os << *first;
    }
    os.seekp(0);
    StreamUtils::write(os, size);
}

template<class T>
bool BinaryFile<T>::update(const T& t, std::function<bool(const T&)> predicate) {
    std::lock_guard<std::mutex> lk(mutex);

    std::fstream ios(file, ios::out | ios::in | ios::binary);
    Sanity::streamness(ios, "Could not open " + file + " to update");

    T tmp;
    ios.seekg(0);
    uint64_t size = StreamUtils::read<uint64_t>(ios);
    for(uint64_t i = 0; i < size; ++i) {
        std::ios::pos_type fpos = ios.tellg();
        ios >> tmp;

        if(predicate(tmp)) {
            std::vector<T> collected(this->collect(ios, i + 1, size));
            ios.seekp(fpos);
            ios << t;
            for(auto item : collected) {
                ios << item;
            }
            return true;
        }
    }

    return false;
}

template<class T>
void BinaryFile<T>::append(const T& t) {
    std::lock_guard<std::mutex> lk(mutex);
    std::ofstream os(file, ios::app | ios::out | ios::binary);
    Sanity::streamness(os, "Could not open " + file + " to append");
    os << t;
}

template<class T>
std::vector<T> BinaryFile<T>::collect(std::istream& is, uint64_t i, uint64_t count) {
    std::vector<T> res;
    res.reserve(count - i);

    T t;
    for(; i < count; ++i) {
        is >> t;
        res.emplace_back(t);
    }

    return res;
}

#endif

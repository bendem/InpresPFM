#ifndef CPP_COMMONS_LOGGER_HPP
#define CPP_COMMONS_LOGGER_HPP

#include <algorithm>
#include <ctime>
#include <fstream>
#include <functional>
#include <iomanip>
#include <iostream>
#include <map>
#include <memory>
#include <mutex>
#include <sstream>
#include <string>
#include <thread>
#include <vector>

#include "utils/StringUtils.hpp"

#define LOG LoggerStream(Logger::instance, Logger::Info, __FILE__, __LINE__, __FUNCTION__)

class FileHandler;

class Logger {

public:
    enum Level : uint8_t {
        Debug   = 0x1,
        Info    = 0x2,
        Warning = 0x4,
        Error   = 0x8,
    };

    using Handler = std::function<void(Level, const std::string&)>;
    using Formatter = std::function<std::string(Level, const std::string&, const std::string&, int, const std::string&, const std::tm*)>;

    Logger() : formatter(&Logger::defaultFormatter), handlers(1, Logger::consoleHandler()) {}

    void log(Level, const std::string&, const std::string& = "n/a", int = 0, const std::string& = "n/a") const;

    Logger& setFormatter(Formatter);

    Logger& addHandler(Handler);
    Logger& clearHandlers();

    static Logger instance;

    static std::string levelToName(Level);
    static Handler consoleHandler(uint8_t levels = -1);

    static Handler fileHandler(const std::string& file, uint8_t levels = -1);

private:
    Formatter formatter;
    std::vector<Handler> handlers;

    static std::string defaultFormatter(Level, const std::string&, const std::string&, int, const std::string&, const std::tm*);

};

class LoggerStream {

public:
    LoggerStream(const Logger& logger, Logger::Level, const std::string&, int, const std::string&);
    ~LoggerStream();

    template<class T>
    LoggerStream& operator<<(const T&);
    LoggerStream& operator<<(const Logger::Level&);

private:
    const Logger& logger;
    Logger::Level level;
    std::string file;
    int line;
    std::string function;
    std::ostringstream input;

    static std::string fileToName(std::string);

};

template<class T>
LoggerStream& LoggerStream::operator<<(const T& t) {
    input << t;
    return *this;
}

std::ostream& operator<<(std::ostream&, const std::vector<std::string>&);
std::ostream& operator<<(std::ostream&, const std::map<std::string, std::string>&);

#endif

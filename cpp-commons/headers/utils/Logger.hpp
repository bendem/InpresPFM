#ifndef CPP_COMMONS_LOGGER_HPP
#define CPP_COMMONS_LOGGER_HPP

#include <ctime>
#include <functional>
#include <iomanip>
#include <iostream>
#include <memory>
#include <mutex>
#include <sstream>
#include <string>
#include <thread>
#include <vector>

#define LOG LoggerStream(Logger::instance, Logger::Info, __FILE__, __LINE__)

class Logger {

public:
    enum Level {
        Debug, Info, Warning, Error
    };

    using Handler = std::function<void(Level, const std::string&, const std::string&, int, std::tm*)>;

    void log(Level, const std::string&, const std::string& = "n/a", int = 0) const;

    void addHandler(Handler);
    Logger& clearHandlers();

    static Logger instance;

    static std::string levelToName(Level);
    static void consoleHandler(Level, const std::string&, const std::string&, int, std::tm*);

private:
    std::vector<Handler> handlers;

};

class LoggerStream {

public:
    LoggerStream(const Logger& logger, Logger::Level, const std::string&, int);
    ~LoggerStream();

    template<class T>
    LoggerStream& operator<<(const T&);
    LoggerStream& operator<<(const Logger::Level&);

private:
    const Logger& logger;
    Logger::Level level;
    std::string file;
    int line;
    std::ostringstream input;

    static std::string fileToName(std::string);

};

template<class T>
LoggerStream& LoggerStream::operator<<(const T& string) {
    input << string;
    return *this;
}

#endif

#include "utils/Logger.hpp"

void Logger::log(Logger::Level level, const std::string& msg, const std::string& file, int line) const {
    std::time_t time = std::time(nullptr);
    std::tm* now = std::localtime(&time);

    for(auto handler : this->handlers) {
        handler(level, msg, file, line, now);
    }
}

void Logger::addHandler(Handler handler) {
    this->handlers.push_back(handler);
}

Logger& Logger::clearHandlers() {
    this->handlers.clear();
    return *this;
}

std::string Logger::levelToName(Level level) {
    switch(level) {
        case Debug:
            return "debug";
        case Info:
            return "info";
        case Warning:
            return "warning";
        case Error:
            return "error";
    }
    throw std::logic_error("Invalid log level");
}

void Logger::consoleHandler(Level level, const std::string& msg, const std::string& file, int line, std::tm* time) {
    (level > Logger::Info ? std::cerr : std::cout)
        << '[' << std::put_time(time, "%H:%M:%S") << "] ["
        << file << ':' << line << "] ["
        << Logger::levelToName(level) << "] "
        << msg << std::endl;
}

LoggerStream::LoggerStream(const Logger& logger, Logger::Level lvl, const std::string& file, int line)
    : level(lvl),
      file(fileToName(file)),
      line(line),
      input(new std::ostringstream, [this, &logger](std::ostringstream* os) {
          logger.log(this->level, os->str(), this->file, this->line);
          delete os;
      }) {}

LoggerStream& LoggerStream::operator<<(const Logger::Level& lvl) {
    this->level = lvl;
    return *this;
}

std::string LoggerStream::fileToName(std::string file) {
    unsigned long pos;

    // Remove start of path
    pos = file.find_last_of('/');
    if(pos != std::string::npos) {
        file = file.substr(pos + 1);
    }

    // Remove extension
    pos = file.find_last_of('.');
    if(pos != std::string::npos) {
        file = file.substr(0, pos);
    }

    return file;
}

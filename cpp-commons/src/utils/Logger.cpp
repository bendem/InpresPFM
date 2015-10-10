#include "utils/Logger.hpp"

Logger Logger::instance;

void Logger::log(Logger::Level level, const std::string& msg, const std::string& file, int line) const {
    std::time_t time = std::time(nullptr);
    std::tm* now = std::localtime(&time);

    std::string log_line(this->formatter(level, msg, file, line, now));
    for(Handler handler : this->handlers) {
        handler(level, log_line);
    }
}

Logger& Logger::setFormatter(Formatter formatter) {
    this->formatter = formatter;
    return *this;
}

Logger& Logger::addHandler(Handler handler) {
    this->handlers.push_back(handler);
    return *this;
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
            return "info ";
        case Warning:
            return "warn ";
        case Error:
            return "error";
    }
    throw std::logic_error("Invalid log level");
}

void Logger::consoleHandler(Level level, const std::string& log) {
    static std::mutex mutex;
    std::lock_guard<std::mutex> lk(mutex);
    (level > Logger::Info ? std::cerr : std::cout) << log;
}

std::string Logger::defaultFormatter(Level level, const std::string& msg, const std::string& file, int line, const std::tm* time) {
    std::ostringstream os;
    os
        << '[' << std::put_time(time, "%H:%M:%S") << "] ["
        << Logger::levelToName(level) << "] ["
        << std::hex << std::this_thread::get_id() << std::dec << "] ["
        << file << ':' << line << "] "
        << msg << std::endl;
    return os.str();
}


LoggerStream::LoggerStream(const Logger& logger, Logger::Level lvl, const std::string& file, int line)
    : logger(logger),
      level(lvl),
      file(fileToName(file)),
      line(line) {
}

LoggerStream::~LoggerStream() {
    this->logger.log(this->level, this->input.str(), this->file, this->line);
}

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

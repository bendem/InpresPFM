#ifndef CPP_COMMONS_THREADPOOL_HPP
#define CPP_COMMONS_THREADPOOL_HPP

#include <atomic>
#include <condition_variable>
#include <queue>
#include <thread>
#include <vector>

#include "utils/Logger.hpp"

class ThreadPool {

public:
    typedef std::function<void()> Task;
    ThreadPool(unsigned int count, Task startup = []{});
    ThreadPool(const ThreadPool&) = delete;
    ThreadPool(ThreadPool&&) = delete;
    ~ThreadPool() { this->close(); }

    ThreadPool& submit(Task);
    void close();

    ThreadPool& operator=(const ThreadPool&) = delete;

private:
    std::vector<std::thread> threads;
    std::queue<Task> tasks;
    std::atomic_bool closed;
    std::condition_variable tasksCondVar;
    std::mutex tasksMutex;

};

#endif

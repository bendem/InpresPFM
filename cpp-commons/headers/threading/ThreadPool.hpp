#ifndef CONTAINER_SERVER_THREADPOOL_HPP
#define CONTAINER_SERVER_THREADPOOL_HPP

#include <atomic>
#include <condition_variable>
#include <queue>
#include <thread>
#include <vector>

#include "utils/Logger.hpp"

class ThreadPool {

public:
    ThreadPool(unsigned int count, std::function<void()> startup = []{});
    ThreadPool(const ThreadPool&) = delete;
    ThreadPool(ThreadPool&&) = delete;
    ~ThreadPool() { this->close(); }

    ThreadPool& submit(std::function<void()>);
    void close();

    ThreadPool& operator=(const ThreadPool&) = delete;

private:
    std::vector<std::thread> threads;
    std::queue<std::function<void()>> tasks;
    std::atomic_bool closed;
    std::condition_variable tasksCondVar;
    std::mutex tasksMutex;

};

#endif //CONTAINER_SERVER_THREADPOOL_HPP

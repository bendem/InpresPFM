#ifndef CONTAINER_SERVER_THREADPOOL_HPP
#define CONTAINER_SERVER_THREADPOOL_HPP

#include <atomic>
#include <condition_variable>
#include <queue>
#include <thread>
#include <vector>

class ThreadPool {

public:
    ThreadPool(unsigned int count);
    ~ThreadPool() { this->close(); }

    ThreadPool& submit(std::function<void()>);
    void close();

private:
    std::vector<std::thread> threads;
    std::queue<std::function<void()>> tasks;
    std::atomic<bool> closed;
    std::condition_variable tasksCondVar;
    std::mutex tasksMutex;

};

#endif //CONTAINER_SERVER_THREADPOOL_HPP

#include "threading/ThreadPool.hpp"

ThreadPool::ThreadPool(unsigned int count, Task startup) : threads(count), closed(false) {
    for(unsigned int i = 0; i < count; ++i) {
        this->threads[i] = std::thread([this, &startup] {
            startup();
            while(!this->closed) {
                Task task;
                {
                    std::unique_lock<std::mutex> lock(this->tasksMutex);
                    if(this->tasks.empty()) {
                        this->tasksCondVar.wait(lock);
                    }

                    if(this->tasks.empty()) {
                        // Closing the pool
                        continue;
                    }

                    task = this->tasks.front();
                    this->tasks.pop();
                }

                task();
            }
            LOG << Logger::Debug << "Thread from thread pool closed";
        });
    }
}

ThreadPool& ThreadPool::submit(Task task) {
    if(this->closed) {
        return *this;
    }

    {
        std::lock_guard<std::mutex> lock(this->tasksMutex);
        this->tasks.push(task);
    }

    this->tasksCondVar.notify_one();
    return *this;
}

void ThreadPool::close() {
    if(this->closed.exchange(true)) {
        return;
    }

    this->tasksCondVar.notify_all();

    for(std::thread& thread : this->threads) {
        thread.join();
    }
}

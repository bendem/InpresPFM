#include "threading/ThreadPool.hpp"

ThreadPool::ThreadPool(unsigned int count) : closed(false) {
    LOG << "Starting thread pool of " << count << " threads";
    for(unsigned int i = 0; i < count; ++i) {
        this->threads.emplace_back(std::thread([this] {
            while(!this->closed) {
                std::unique_lock<std::mutex> lock(this->tasksMutex);
                this->tasksCondVar.wait(lock);

                if(this->tasks.empty()) {
                    continue;
                }

                std::function<void()> task = this->tasks.front();
                this->tasks.pop();
                lock.unlock();

                task();
            }
        }));
    }
}

ThreadPool& ThreadPool::submit(std::function<void()> task) {
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

#ifndef CONTAINER_SERVER_SELECTORTHREAD_HPP
#define CONTAINER_SERVER_SELECTORTHREAD_HPP

#include "net/Selector.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolHandler.hpp"
#include "threading/ThreadPool.hpp"

template<class Translator, class Id>
class SelectorThread {

public:
    SelectorThread(Selector&, ThreadPool&, ProtocolHandler<Translator, Id>&);

    void operator()();

    void close();

private:
    Selector& selector;
    ThreadPool& pool;
    ProtocolHandler<Translator, Id>& proto;
    std::atomic_bool closed;
    std::thread thread;

};

template<class Translator, class Id>
SelectorThread<Translator, Id>::SelectorThread(Selector& selector, ThreadPool& pool, ProtocolHandler<Translator, Id>& proto)
        : selector(selector),
          pool(pool),
          proto(proto),
          closed(false),
          thread(&SelectorThread::operator(), this) {}

template<class Translator, class Id>
void SelectorThread<Translator, Id>::operator()() {
    LOG << "Starting polling thread";
    while(!this->closed) {
        for(Socket& socket : this->selector.select()) {
            this->pool.submit([this, socket] () mutable {
                LOG << Logger::Debug << "reading on socket " << socket.getHandle();
                this->proto.read(socket);
                if(!socket.isClosed()) {
                    LOG << Logger::Debug << "socket not closed, readding to selector";
                    this->selector.addSocket(socket);
                }
            });
        }
    }

}

template<class Translator, class Id>
void SelectorThread<Translator, Id>::close() {
    if(this->closed.exchange(true)) {
        return;
    }
    this->selector.interrupt();
    this->thread.join();
}

#endif

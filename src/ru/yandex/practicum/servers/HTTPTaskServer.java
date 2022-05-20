package ru.yandex.practicum.servers;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.managerscollection.Handler;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HTTPTaskServer {
    final int PORT = 8080;
    private TaskManager manager;
    private HttpServer server;

    public HTTPTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new Handler(manager));
    }

    public HTTPTaskServer() {

    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}
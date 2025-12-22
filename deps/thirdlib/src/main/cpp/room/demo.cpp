//
// Created by rookie on 2025/11/11.
//
#include<vector>
#include <iostream>
#include "httplib.h"

int entrance() {
    std::vector<int> v1;
    std::vector<float> v2(2);
    std::vector<std::vector<float>> v3;

    v1.emplace_back(1);
    std::cout << v1.size() << std::endl;
    std::cout << "v1 size " << v1.size() << std::endl;
    return 0;
}

int network() {
    httplib::Server svr;

    // 定义监听地址和端口
    std::string listen_address = "0.0.0.0";
    int port = 8080;

    // 添加更多路由处理
    svr.Get("/", [](const httplib::Request &req, httplib::Response &res) {
        res.set_content("Hello World!", "text/plain");
    });

    svr.Get("/health", [](const httplib::Request &req, httplib::Response &res) {
        res.status = 200;
        res.set_content("OK", "text/plain");
    });

    svr.Get("/api/data", [](const httplib::Request &req, httplib::Response &res) {
        // 可以处理查询参数
        std::string name = req.has_param("name") ? req.get_param_value("name") : "Guest";
        res.set_content("Hello " + name, "text/plain");
    });

    svr.Post("/api/submit", [](const httplib::Request &req, httplib::Response &res) {
        // 处理 POST 请求
        std::string body = req.body;
        res.set_content("Received: " + body, "text/plain");
    });

    std::cout << "Server listening on " << listen_address << ":" << port << std::endl;

    if (!svr.listen(listen_address.c_str(), port)) {
        std::cerr << "Failed to start server!" << std::endl;
        return -1;
    }

    return 0;
}


int main(int argc, char **argv) {
    std::cout << "main fun " << argc << argv << std::endl;
    entrance();
    network();
    return EXIT_SUCCESS;
}

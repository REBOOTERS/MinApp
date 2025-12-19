//
// Created by rookie on 2025/11/11.
//
#include<vector>
#include <iostream>

//#include "httplib.h"
//
//int port = 8081;
//std::string listen_address = "127.0.0.1";
//int http_server() {
//    httplib::Server svr;
//    svr.Get("/health", [](const httplib::Request &, httplib::Response &res) {
//        res.status = 200;
//    });
//    std::cout << "Server listening on " << listen_address << ":" << port
//              << std::endl;
//    svr.listen(listen_address.c_str(), port);
//}

int entrance() {
    std::vector<int> v1;
    std::vector<float> v2(2);
    std::vector<std::vector<float>> v3;

    v1.emplace_back(1);
    std::cout << v1.size() << std::endl;
    std::cout << "v1 size " << v1.size() << std::endl;
    return 0;
}


int main(int argc, char **argv) {
    std::cout << "main fun " << argc << argv << std::endl;
    entrance();
}

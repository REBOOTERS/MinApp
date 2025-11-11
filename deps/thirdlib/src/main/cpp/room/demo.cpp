//
// Created by rookie on 2025/11/11.
//
#include<vector>
#include <iostream>
#include "../logcat/androidlog.h"

int entrance() {
    std::vector<int> v1;
    std::vector<float> v2(2);
    std::vector<std::vector<float>> v3;

    v1.emplace_back(1);
    std::cout << v1.size() << std::endl;
    LOGV("v1 size %zu", v1.size());
    return 0;
}
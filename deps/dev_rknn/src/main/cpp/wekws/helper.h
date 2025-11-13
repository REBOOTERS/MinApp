//
// Created by  on 2025/11/13.
//
#include <iostream>
#include <vector>
#include <random>
#include <cmath>
#ifndef MINAPP_MOCK_DATA_H
#define MINAPP_MOCK_DATA_H

void FillRandomFeats(std::vector<std::vector<float>>& feats, int rows, int cols);
bool IsVectorAlmostEqual(const std::vector<float>& a, const std::vector<float>& b, float eps);
#endif //MINAPP_MOCK_DATA_H

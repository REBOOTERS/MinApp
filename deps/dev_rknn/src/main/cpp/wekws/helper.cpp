//
// Created by  on 2025/11/13.
//


#include "helper.h"

// 封装函数：填充随机 float
void FillRandomFeats(std::vector<std::vector<float>>& feats, int rows, int cols) {
    // 重新调整大小
    feats.assign(rows, std::vector<float>(cols));

    // 随机数生成器
    static std::random_device rd;
    static std::mt19937 gen(rd());
    std::uniform_real_distribution<float> dis(0.0f, 1.0f); // [0,1)

    // 填充数据
    for (int i = 0; i < rows; ++i) {
        for (int j = 0; j < cols; ++j) {
            feats[i][j] = dis(gen);
        }
    }
}

bool IsVectorAlmostEqual(const std::vector<float>& a, const std::vector<float>& b, float eps = 1e-6f) {
    if (a.size() != b.size()) return false;
    for (size_t i = 0; i < a.size(); ++i) {
        if (std::fabs(a[i] - b[i]) > eps) return false;
    }
    return true;
}

int entrance() {
    std::vector<std::vector<float>> feats;

    FillRandomFeats(feats, 80, 40);  // 生成 80×40 随机浮点数据

    // 验证输出前几项
    for (int i = 0; i < 3; ++i) {
        for (int j = 0; j < 5; ++j)
            std::cout << feats[i][j] << " ";
        std::cout << "...\n";
    }

    return 0;
}

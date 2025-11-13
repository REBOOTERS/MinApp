//
// Created by  on 2025/11/13.
//

#ifndef MINAPP_WEKWS_H
#define MINAPP_WEKWS_H

#include "../rknn_api.h"
#include "../logcat/androidlog.h"
#include <string>
#include<vector>

namespace wekws {

    class WeKws {
    public:
        WeKws();

        ~WeKws() {}


        int create(const std::string &model_path);



        void destroy(const std::string &msg);

        void infer();

    private:
        int init_model(const std::string &model_path);

        int query_input_output();

        int run(const std::vector<std::vector<float>> &feats,
                std::vector<std::vector<float>> *prob);

        rknn_context ctx = 0;

        uint32_t n_input = 1;
        uint32_t n_output = 3;

        std::vector<float> cache_;
        std::vector<float> cache_new;
        int cache_len_ = 105;
        int cache_dim = 64;

        int out_len_ = 80; // 根据你模型 output 长度调整
    };


}


#endif //MINAPP_WEKWS_H

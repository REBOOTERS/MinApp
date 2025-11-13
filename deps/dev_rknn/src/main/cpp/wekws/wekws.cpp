//
// Created by  on 2025/11/13.
//
#include "wekws.h"
#include "helper.h"

wekws::WeKws::WeKws() {}

std::vector<std::vector<float>> feats;
std::vector<std::vector<float>> prob;

int wekws::WeKws::create(const std::string &model_path) {
    LOGI("create model path %s", model_path.c_str());
    int init = init_model(model_path);
    if (init == 0) {
        return query_input_output();
    }
    return init;
}

int wekws::WeKws::init_model(const std::string &model_path) {
    FILE *fp = fopen(model_path.c_str(), "rb");
    if (fp == NULL) {
        LOGE("fopen %s fail!!", model_path.c_str());
        return -1;
    }
    fseek(fp, 0, SEEK_END);
    uint32_t model_len = ftell(fp);
    void *model = malloc(model_len);
    fseek(fp, 0, SEEK_SET);
    if (model_len != fread(model, 1, model_len, fp)) {
        LOGE("fread  %s fail ", model_path.c_str());
        free(model);
        fclose(fp);
        return -1;
    }
    fclose(fp);

    // init model
    int ret = rknn_init(&ctx, model, model_len, 0, nullptr);
    free(model);
    if (ret < 0) {
        LOGE("rknn init fail ,ret = %d", ret);
        return -1;
    }
    LOGI("rknn_init_success ");
    return 0;
}

int wekws::WeKws::query_input_output() {
    rknn_input_output_num io_num;
    rknn_query_cmd cmd = RKNN_QUERY_IN_OUT_NUM;
    int size = sizeof(io_num);
    LOGI("io_num size = %d", size);
    int ret = rknn_query(ctx, cmd, &io_num, size);

    n_input = io_num.n_input;
    n_output = io_num.n_output;

    LOGI("n_input = %d, n_output = %d", n_input, n_output);

    return 0;

}

void wekws::WeKws::infer() {
    FillRandomFeats(feats, 80, 40);
    run(feats, &prob);
}

int wekws::WeKws::run(const std::vector<std::vector<float>> &feats,
                      std::vector<std::vector<float>> *prob) {
    if (!prob) {
        LOGI("prob pointer is null");
        return -1;
    }
    prob->clear();
    LOGI("********************************************************************* ");
    int T = static_cast<int>(feats.size());
    if (T <= 0) {
        LOGE("feats is empty");
        return -1;
    }
    LOGD("Forward: feats.size()     = %zu", feats.size());
    LOGD("Forward: feats.feature()  = %zu", feats[0].size());

    // 2) 展平成连续 buffer: row-major [T, 40]
    std::vector<float> feat_flat;
    feat_flat.reserve(T * 40);
    for (int t = 0; t < T; ++t) {
        // copy 40 floats
        const std::vector<float> &frame = feats[t];
        feat_flat.insert(feat_flat.end(), frame.begin(), frame.end());
    }

    std::vector<float> output_scores(out_len_, 0.0f);
    std::vector<float> new_cache(cache_len_, 0.0f);

    int input0_elems = T * 40;
    rknn_input inputs[2];
    memset(inputs, 0, sizeof(inputs));


    inputs[0].index = 0; // 假设 model 的第0 input 为 feature input
    inputs[0].type = RKNN_TENSOR_FLOAT32;
    inputs[0].fmt = RKNN_TENSOR_NHWC;
    inputs[0].size = input0_elems * sizeof(float);
    inputs[0].buf = const_cast<float *>(feat_flat.data());


    std::vector<float> cache_buf;
    if(cache_.empty()) {
        LOGI("cache_ is empty");
    } else {
        LOGI("cache_ size %zu", cache_.size());
    }
    cache_buf.assign(1 * 64 * 105, 0.0f);
    inputs[1].index = 1; // 假设 model 的第1 input 为 cache
    inputs[1].type = RKNN_TENSOR_FLOAT32;
    inputs[1].fmt = RKNN_TENSOR_NHWC;
    inputs[1].size = static_cast<int>(cache_buf.size() * 64 * sizeof(float));
    inputs[1].buf = cache_buf.data();

    int ret = rknn_inputs_set(ctx, 2, inputs);
    if (ret != 0) {
        LOGE("rknn_inputs_set failed %d", ret);
        return -1;
    }

    ret = rknn_run(ctx, nullptr);
    if (ret != 0) {
        LOGE("rknn_run failed %d", ret);
        return false;
    }

    std::vector<rknn_output> outputs(2);
    for (int i = 0; i < 2; ++i) {
        memset(&outputs[i], 0, sizeof(rknn_output));
        outputs[i].want_float = 1;
    }

    ret = rknn_outputs_get(ctx, 2, outputs.data(), nullptr);
    if (ret != 0) {
        LOGE("rknn_outputs_get failed %d", ret);
        return false;
    }

    LOGI("outputs[0]: %d", outputs[0].size);
    LOGI("outputs[1]: %d", outputs[1].size);

    int want = 2;
    int out_len = 80;
    if (want >= 1 && outputs[0].buf && out_len > 0) {
        int elems = outputs[0].size / sizeof(float);
        int cp = std::min(elems, out_len);
        LOGD("cp = %d ,elems = %d", cp, elems);
        memcpy(output_scores.data(), outputs[0].buf, cp * sizeof(float));
        float max = 0.0f;
        for (int i = 0; i < output_scores.size(); i++) {
//            LOGD("score %d = %f", i, output_scores[i]);
            if(output_scores[i] > max) {
                max = output_scores[i];
            }
        }
        LOGD("max = %f",max);
    }

    if (want >= 1 && outputs[1].buf && cache_len_ > 0) {
        int elems = outputs[1].size / sizeof(float);
        int cp = std::min(elems, cache_len_ * cache_dim);
        LOGD("cp1 = %d ,elems = %d", cp, elems);
        if (cache_.empty()) {
            cache_.resize(cp);
        }
        if (cache_new.empty()) {
            cache_new.resize(cp);
        }

        memcpy(cache_new.data(), outputs[1].buf, cp * sizeof(float));
        bool same = IsVectorAlmostEqual(cache_,cache_new,1e-6f);
        LOGE("cache is same %d",same);
        cache_.swap(cache_new);
        float max = 0.0f;
        for (int i = 0; i < cache_.size(); i++) {
//            LOGD("score %d = %f", i, output_scores[i]);
            if(cache_[i] > max) {
                max = cache_[i];
            }
        }
        LOGD("max1 = %f",max);
    }

    rknn_outputs_release(ctx, 2, outputs.data());

    return 0;
}

void wekws::WeKws::destroy(const std::string &msg) {
    LOGI("destroy from %s", msg.c_str());
    LOGI("release rknn res");
    rknn_destroy(ctx);

}
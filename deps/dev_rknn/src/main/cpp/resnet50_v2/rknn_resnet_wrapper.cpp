#include "rknn_resnet_wrapper.h"
#include <cstring>
#include <cmath>
#include <algorithm>
#include <android/log.h>
#include <vector>   // Add this
#include <utility>  // Add this for std::pair


#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "RKNN_WRAPPER", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "RKNN_WRAPPER", __VA_ARGS__)

resnet50::RKNNResNetWrapper::RKNNResNetWrapper()
        : rknn_ctx_(nullptr),
          output_count_(0),
          num_classes_(1000),
          input_w_(224),
          input_h_(224) {
}

resnet50::RKNNResNetWrapper::~RKNNResNetWrapper() {
    release();
}

void resnet50::RKNNResNetWrapper::setModelInputSize(int w, int h) {
    std::lock_guard<std::mutex> lk(mtx_);
    input_w_ = w;
    input_h_ = h;
}

int resnet50::RKNNResNetWrapper::initModelFromBytes(const std::string &model_path) {

    std::lock_guard<std::mutex> lk(mtx_);
    if (rknn_ctx_) {
        destroyRknn();
    }
    if (initRknn(model_path)) {
        return 0;
    }
    return -1;
}

bool resnet50::RKNNResNetWrapper::initRknn(const std::string &model_path) {
    FILE *fp = fopen(model_path.c_str(), "rb");
    if (fp == NULL) {
        LOGE("fopen %s fail!!", model_path.c_str());
        return false;
    }
    fseek(fp, 0, SEEK_END);
    uint32_t model_len = ftell(fp);
    void *model = malloc(model_len);
    fseek(fp, 0, SEEK_SET);
    if (model_len != fread(model, 1, model_len, fp)) {
        LOGE("fread  %s fail ", model_path.c_str());
        free(model);
        fclose(fp);
        return false;
    }
    fclose(fp);

    // init model
    int ret = rknn_init((rknn_context *) &rknn_ctx_, model, model_len, 0, nullptr);
    free(model);
    if (ret < 0) {
        LOGE("rknn init fail ,ret = %d", ret);
        return false;
    }
    LOGI("rknn_init_success ");


    rknn_input_output_num io_num;
    ret = rknn_query((rknn_context) rknn_ctx_, RKNN_QUERY_IN_OUT_NUM, &io_num, sizeof(io_num));
    if (ret < 0) {
        LOGE("rknn_query IO num err=%d", ret);
        return false;
    }
    if (io_num.n_input != 1) {
        LOGE("model requires %d inputs, but this wrapper only supports 1 input", io_num.n_input);
        return false;
    }

    rknn_tensor_attr input_attrs[1];
    memset(input_attrs, 0, sizeof(input_attrs));
    input_attrs[0].index = 0;
    ret = rknn_query((rknn_context) rknn_ctx_, RKNN_QUERY_INPUT_ATTR, input_attrs, sizeof(input_attrs));
    if (ret < 0) {
        LOGE("rknn_query input attr err=%d", ret);
        return false;
    }

    // 输出详细输入信息
    LOGI("model input index=%d, name=%s, n_dims=%d, dims=[%d,%d,%d,%d], n_elems=%d, size=%d, fmt=%d, type=%d, qnt_type=%d, qnt_zp=%d, qnt_scale=%f",
        input_attrs[0].index,
        input_attrs[0].name,
        input_attrs[0].n_dims,
        input_attrs[0].dims[0], input_attrs[0].dims[1], input_attrs[0].dims[2], input_attrs[0].dims[3],
        input_attrs[0].n_elems,
        input_attrs[0].size,
        input_attrs[0].fmt,
        input_attrs[0].type,
        input_attrs[0].qnt_type,
        input_attrs[0].zp,
        input_attrs[0].scale);

    // 检查输入类型和格式
    if (input_attrs[0].fmt != RKNN_TENSOR_NHWC || input_attrs[0].type != RKNN_TENSOR_INT8) {
        LOGE("Unsupported model: this wrapper requires NHWC INT8 input. Model has fmt=%d, type=%d",
             input_attrs[0].fmt, input_attrs[0].type);
        return false;
    }

    output_count_ = io_num.n_output;
    num_classes_ = 1000; // Default value, can be adjusted based on model output shape query
    return true;
}

void resnet50::RKNNResNetWrapper::release() {
    std::lock_guard<std::mutex> lk(mtx_);
    destroyRknn();
}

void resnet50::RKNNResNetWrapper::destroyRknn() {
    if (rknn_ctx_) {
        rknn_destroy((rknn_context) rknn_ctx_);
        rknn_ctx_ = nullptr;
        LOGI("rknn destroyed");
    }
}

// ---------- image helpers ----------

static inline uint8_t clamp_u8(int v) {
    if (v < 0) return 0;
    if (v > 255) return 255;
    return (uint8_t) v;
}

void
resnet50::RKNNResNetWrapper::nv21_to_rgb(const uint8_t *nv21, int w, int h, uint8_t *outRgb) const {
    int frameSize = w * h;
    const uint8_t *yPlane = nv21;
    const uint8_t *vuPlane = nv21 + frameSize;

    for (int j = 0; j < h; ++j) {
        int pY = j * w;
        int pUV = (j / 2) * w;
        for (int i = 0; i < w; ++i) {
            int y = yPlane[pY + i] & 0xFF;
            int v = vuPlane[pUV + (i & ~1)] & 0xFF;
            int u = vuPlane[pUV + (i & ~1) + 1] & 0xFF;

            int c = y - 16;
            int d = u - 128;
            int e = v - 128;
            int r = (298 * c + 409 * e + 128) >> 8;
            int g = (298 * c - 100 * d - 208 * e + 128) >> 8;
            int b = (298 * c + 516 * d + 128) >> 8;

            int outIdx = (pY + i) * 3;
            outRgb[outIdx + 0] = clamp_u8(r);
            outRgb[outIdx + 1] = clamp_u8(g);
            outRgb[outIdx + 2] = clamp_u8(b);
        }
    }
}

void resnet50::RKNNResNetWrapper::argb_to_rgb(const uint8_t *argb, int w, int h, uint8_t *outRgb) const {
    int pix = w * h;
    for (int i = 0; i < pix; ++i) {
        // A R G B
        outRgb[i * 3 + 0] = argb[i * 4 + 1];
        outRgb[i * 3 + 1] = argb[i * 4 + 2];
        outRgb[i * 3 + 2] = argb[i * 4 + 3];
    }
}

// 新增: float 版本 ARGB -> RGB
void resnet50::RKNNResNetWrapper::argb_to_rgb_float(const float *argb, int w, int h, float *outRgb) const {
    int pix = w * h;
    for (int i = 0; i < pix; ++i) {
        // A R G B
        outRgb[i * 3 + 0] = argb[i * 4 + 1];
        outRgb[i * 3 + 1] = argb[i * 4 + 2];
        outRgb[i * 3 + 2] = argb[i * 4 + 3];
    }
}

void resnet50::RKNNResNetWrapper::resize_bilinear_rgb(const uint8_t *in, int w, int h, uint8_t *out,
                                                      int ow,
                                                      int oh) const {
    float x_ratio = (float) w / ow;
    float y_ratio = (float) h / oh;

    for (int j = 0; j < oh; ++j) {
        float sy = (j + 0.5f) * y_ratio - 0.5f;
        if (sy < 0) sy = 0;
        int y0 = (int) floor(sy);
        int y1 = std::min(y0 + 1, h - 1);
        float fy = sy - y0;
        for (int i = 0; i < ow; ++i) {
            float sx = (i + 0.5f) * x_ratio - 0.5f;
            if (sx < 0) sx = 0;
            int x0 = (int) floor(sx);
            int x1 = std::min(x0 + 1, w - 1);
            float fx = sx - x0;
            for (int c = 0; c < 3; ++c) {
                float v00 = in[(y0 * w + x0) * 3 + c];
                float v01 = in[(y0 * w + x1) * 3 + c];
                float v10 = in[(y1 * w + x0) * 3 + c];
                float v11 = in[(y1 * w + x1) * 3 + c];
                float v0 = v00 * (1 - fx) + v01 * fx;
                float v1 = v10 * (1 - fx) + v11 * fx;
                float v = v0 * (1 - fy) + v1 * fy;
                out[(j * ow + i) * 3 + c] = clamp_u8((int) roundf(v));
            }
        }
    }
}

void resnet50::RKNNResNetWrapper::softmax_inplace(float *data, int len) const {
    if (len <= 0) return;
    float maxv = data[0];
    for (int i = 1; i < len; ++i) if (data[i] > maxv) maxv = data[i];
    double sum = 0.0;
    for (int i = 0; i < len; ++i) {
        data[i] = expf(data[i] - maxv);
        sum += data[i];
    }
    for (int i = 0; i < len; ++i) data[i] = (float) (data[i] / sum);
}


std::vector<float>
resnet50::RKNNResNetWrapper::inferFromBuffer(const std::vector<uint8_t> &image_bytes,
                                             int width, int height, ImageFormat fmt){
    LOGI("inferFromBuffer ,w = %d,h=%d,fmt=%d", width, height, fmt);
    std::lock_guard<std::mutex> lk(mtx_);
    std::vector<float> empty;

    if (!rknn_ctx_) {
        LOGE("infer called but rknn not initialized");
        return empty;
    }
    if (image_bytes.empty() || width <= 0 || height <= 0) {
        LOGE("invalid image input");
        return empty;
    }

    // 判断输入是否 float ARGB
    bool is_float_argb = false;
    if (fmt == FORMAT_ARGB && image_bytes.size() == width * height * 4 * sizeof(float)) {
        is_float_argb = true;
    }


    int ow = input_w_;
    int oh = input_h_;
    if (ow <= 0 || oh <= 0) {
        LOGE("Invalid model input dimensions: %dx%d. Set positive dimensions with setModelInputSize.", ow, oh);
        return empty;
    }


    // 获取模型量化参数（scale, zp）
    float input_scale = 1.0f;
    int input_zp = 0;
    {
        rknn_tensor_attr input_attr;
        memset(&input_attr, 0, sizeof(input_attr));
        input_attr.index = 0;
        int ret = rknn_query((rknn_context)rknn_ctx_, RKNN_QUERY_INPUT_ATTR, &input_attr, sizeof(input_attr));
        if (ret == 0) {
            input_scale = input_attr.scale;
            input_zp = input_attr.zp;
        }
    }

    // 始终生成 int8_t NHWC 格式输入（量化）
    std::vector<int8_t> input_nhwc(ow * oh * 3);
    const float mean_vals[3] = {0.485f, 0.456f, 0.406f};
    const float std_vals[3] = {0.229f, 0.224f, 0.225f};
    if (is_float_argb) {
        // float ARGB -> float RGB
        const float* argb_ptr = reinterpret_cast<const float*>(image_bytes.data());
        std::vector<float> rgb_src(width * height * 3);
        argb_to_rgb_float(argb_ptr, width, height, rgb_src.data());
        // resize 最近邻
        std::vector<float> rgb_resized(ow * oh * 3);
        float x_ratio = (float)width / ow;
        float y_ratio = (float)height / oh;
        for (int j = 0; j < oh; ++j) {
            int sy = std::min((int)(j * y_ratio), height - 1);
            for (int i = 0; i < ow; ++i) {
                int sx = std::min((int)(i * x_ratio), width - 1);
                for (int c = 0; c < 3; ++c) {
                    float v = rgb_src[(sy * width + sx) * 3 + c];
                    // 归一化
                    float norm = (v / 255.0f - mean_vals[c]) / std_vals[c];
                    // 量化
                    int q = static_cast<int>(std::round(norm / input_scale + input_zp));
                    if (q < -128) q = -128;
                    if (q > 127) q = 127;
                    input_nhwc[(j * ow + i) * 3 + c] = static_cast<int8_t>(q);
                }
            }
        }
    } else {
        // 原有 uint8_t 流程
        std::vector<uint8_t> rgb_src(width * height * 3);
        if (fmt == FORMAT_NV21) {
            nv21_to_rgb(image_bytes.data(), width, height, rgb_src.data());
        } else {
            argb_to_rgb(image_bytes.data(), width, height, rgb_src.data());
        }
        std::vector<uint8_t> rgb_resized(ow * oh * 3);
        resize_bilinear_rgb(rgb_src.data(), width, height, rgb_resized.data(), ow, oh);
        for (int j = 0; j < oh; ++j) {
            for (int i = 0; i < ow; ++i) {
                int idx = (j * ow + i) * 3;
                for (int c = 0; c < 3; ++c) {
                    float v = rgb_resized[idx + c];
                    float norm = (v / 255.0f - mean_vals[c]) / std_vals[c];
                    int q = static_cast<int>(std::round(norm / input_scale + input_zp));
                    if (q < -128) q = -128;
                    if (q > 127) q = 127;
                    input_nhwc[idx + c] = static_cast<int8_t>(q);
                }
            }
        }
    }

    // 4) RKNN 输入、推理、输出获取
    rknn_input inputs[1];
    memset(inputs, 0, sizeof(inputs));
    inputs[0].index = 0;
    inputs[0].buf = input_nhwc.data();
    inputs[0].size = input_nhwc.size() * sizeof(int8_t);
    inputs[0].pass_through = 0;
    inputs[0].type = RKNN_TENSOR_INT8;
    inputs[0].fmt = RKNN_TENSOR_NHWC;

    if (rknn_ctx_ == nullptr) {
        LOGE("rknn_ctx_ is null");
        return empty;
    }

    int ret = rknn_inputs_set((rknn_context) rknn_ctx_, 1, inputs);
    if (ret != RKNN_SUCC) {
        LOGE("rknn_inputs_set fail %d", ret);
        return empty;
    }

    ret = rknn_run((rknn_context) rknn_ctx_, nullptr);
    if (ret != RKNN_SUCC) {
        LOGE("rknn_run fail %d", ret);
        return empty;
    }

    rknn_output outputs[1];
    memset(outputs, 0, sizeof(outputs));
    outputs[0].want_float = 1;
    ret = rknn_outputs_get((rknn_context) rknn_ctx_, 1, outputs, NULL);
    if (ret != RKNN_SUCC) {
        LOGE("rknn_outputs_get fail %d", ret);
        return empty;
    }

    int out_elems = outputs[0].size / sizeof(float);
    float *raw = (float *) outputs[0].buf;
    std::vector<float> probs(out_elems);
    for (int i = 0; i < out_elems; ++i) probs[i] = raw[i];

    // softmax
    softmax_inplace(probs.data(), out_elems);

    // Find top 5 probabilities and their indices
    std::vector<std::pair<float, int>> top_probs;
    for (int i = 0; i < out_elems; ++i) {
        top_probs.push_back({probs[i], i});
    }

    // Sort in descending order of probability
    std::sort(top_probs.begin(), top_probs.end(), [](const std::pair<float, int>& a, const std::pair<float, int>& b) {
        return a.first > b.first;
    });

    // Log top 5
    LOGI("Top 5 probabilities:");
    for (int i = 0; i < std::min((int)top_probs.size(), 5); ++i) {
        LOGI("  [%d]: %.4f (index %d)", i + 1, top_probs[i].first, top_probs[i].second);
    }

    // release
    rknn_outputs_release((rknn_context) rknn_ctx_, 1, outputs);

    return probs;
}

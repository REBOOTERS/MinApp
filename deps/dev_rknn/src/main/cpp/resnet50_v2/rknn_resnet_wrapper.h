#ifndef RKNN_RESNET_WRAPPER_H
#define RKNN_RESNET_WRAPPER_H

#include <vector>
#include <cstdint>
#include <mutex>

// 如果你的 SDK 有 rknn_api.h，包含它；否则把具体声明替换为对应 SDK 的
#include "../rknn_api.h"

namespace resnet50 {

    class RKNNResNetWrapper {
    public:
        enum ImageFormat {
            FORMAT_ARGB = 0, FORMAT_NV21 = 1
        };

        RKNNResNetWrapper();

        ~RKNNResNetWrapper();

        // 初始化模型（传入 model bytes)
        // 返回 true 表示成功
        int initModelFromBytes(const std::string &model_path);

        // 释放资源
        void release();

        // 推理接口：传入图像原始 bytes（ARGB 或 NV21），原始宽高和格式。
        // 返回：float vector（概率或原始输出，取决于模型）；调用端检查返回长度
        std::vector<float> inferFromBuffer(const std::vector<uint8_t> &image_bytes,
                                           int width, int height, ImageFormat fmt);

        // 可配置模型输入尺寸（默认 224x224）
        void setModelInputSize(int w, int h);

    private:
        // 不可拷贝
        RKNNResNetWrapper(const RKNNResNetWrapper &) = delete;

        RKNNResNetWrapper &operator=(const RKNNResNetWrapper &) = delete;

        // 私有实现细节
        bool initRknn(const std::string &model_path);

        void destroyRknn();

        // 图像处理函数（成员版本）
        void nv21_to_rgb(const uint8_t *nv21, int w, int h, uint8_t *outRgb) const;

        void argb_to_rgb(const uint8_t *argb, int w, int h, uint8_t *outRgb) const;

        void
        resize_bilinear_rgb(const uint8_t *in, int w, int h, uint8_t *out, int ow, int oh) const;

        void softmax_inplace(float *data, int len) const;

    private:
        // RKNN 上下文等
        void *rknn_ctx_; // 使用 void* 以便兼容不同 SDK，实际类型为 rknn_context
        int output_count_;
        int num_classes_;
        int input_w_;
        int input_h_;

        // 线程安全
        mutable std::mutex mtx_;
    };
}
#endif // RKNN_RESNET_WRAPPER_H

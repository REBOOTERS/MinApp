//
// Created by on 2025/11/19.
//

#include "image_helper.h"

static inline uint8_t clamp_u8(int v) {
    if (v < 0) return 0;
    if (v > 255) return 255;
    return (uint8_t) v;
}

void
nv21_to_rgb(const uint8_t *nv21, int w, int h, uint8_t *outRgb)  {
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

void
argb_to_rgb(const uint8_t *argb, int w, int h, uint8_t *outRgb)  {
    int pix = w * h;
    for (int i = 0; i < pix; ++i) {
        // A R G B
        outRgb[i * 3 + 0] = argb[i * 4 + 1];
        outRgb[i * 3 + 1] = argb[i * 4 + 2];
        outRgb[i * 3 + 2] = argb[i * 4 + 3];
    }
}

// 新增: float 版本 ARGB -> RGB
void argb_to_rgb_float(const float *argb, int w, int h,
                                                    float *outRgb)  {
    int pix = w * h;
    for (int i = 0; i < pix; ++i) {
        // A R G B
        outRgb[i * 3 + 0] = argb[i * 4 + 1];
        outRgb[i * 3 + 1] = argb[i * 4 + 2];
        outRgb[i * 3 + 2] = argb[i * 4 + 3];
    }
}

void resize_bilinear_rgb(const uint8_t *in, int w, int h, uint8_t *out,
                                                      int ow,
                                                      int oh)  {
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

void softmax_inplace(float *data, int len)  {
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
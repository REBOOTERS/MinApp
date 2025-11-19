//
// Created by zyq on 2025/11/19.
//

#ifndef MINAPP_IMAGE_HELPER_H
#define MINAPP_IMAGE_HELPER_H

#include <cstdint>
#include <cmath>
#include <cstdint>
#include <algorithm>

void
nv21_to_rgb(const uint8_t *nv21, int w, int h, uint8_t *outRgb);

void
argb_to_rgb(const uint8_t *argb, int w, int h, uint8_t *outRgb);

void argb_to_rgb_float(const float *argb, int w, int h,
                       float *outRgb);

void resize_bilinear_rgb(const uint8_t *in, int w, int h, uint8_t *out,
                         int ow,
                         int oh);

void softmax_inplace(float *data, int len);

#endif //MINAPP_IMAGE_HELPER_H

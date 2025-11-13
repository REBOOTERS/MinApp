/**
  * @ClassName yolo_image
  * @Description inference code for yolo
  * @Author raul.rao
  * @Date 2022/5/23 11:10
  * @Version 1.0
  */

#include <cstdarg>
#include <cstdio>
#include <cstdlib>
#include <fstream>
#include <iostream>
#include <memory>
#include <sstream>
#include <string>
#include <vector>
#include <ctime>

#include <cstdint>

#include "yolo_image.h"

// #define DEBUG_DUMP
//#define EVAL_TIME
#define DO_NOT_FLIP -1

int g_inf_count = 0;

int g_post_count = 0;

rknn_context ctx = 0;

bool created = false;

int img_width = 0;
int img_height = 0;

int m_in_width = 0;   // the width of the RKNN model input
int m_in_height = 0;  // the height of the RKNN model input
int m_in_channel = 0; // the channel of the RKNN model input

float scale_w = 0.0;
float scale_h = 0.0;

uint32_t n_input = 1;
uint32_t n_output = 3;

rknn_tensor_attr input_attrs[1];
rknn_tensor_attr output_attrs[3];

rknn_tensor_mem *input_mems[1];
rknn_tensor_mem *output_mems[3];

std::vector<float> out_scales;
std::vector<int32_t> out_zps;

static double __get_us(struct timeval t) { return (t.tv_sec * 1000000 + t.tv_usec); }


int create(int im_height, int im_width, int im_channel, char *model_path) {
    img_height = im_height;
    img_width = im_width;

    LOGI("try rknn_init!");

    LOGI("img width: %d, height: %d", im_width, im_height);

    // 1. Load model
    FILE *fp = fopen(model_path, "rb");
    if (fp == NULL) {
        LOGE("fopen %s fail!\n", model_path);
        return -1;
    }
    fseek(fp, 0, SEEK_END);
    uint32_t model_len = ftell(fp);
    void *model = malloc(model_len);
    fseek(fp, 0, SEEK_SET);
    if (model_len != fread(model, 1, model_len, fp)) {
        LOGE("fread %s fail!\n", model_path);
        free(model);
        fclose(fp);
        return -1;
    }

    fclose(fp);

    // 2. Init RKNN model
    int ret = rknn_init(&ctx, model, model_len, 0, nullptr);
    free(model);

    if (ret < 0) {
        LOGE("rknn_init fail! ret=%d\n", ret);
        return -1;
    }

    // 3. Query input/output attr.
    rknn_input_output_num io_num;
    rknn_query_cmd cmd = RKNN_QUERY_IN_OUT_NUM;
    // 3.1 Query input/output num.
    ret = rknn_query(ctx, cmd, &io_num, sizeof(io_num));

    n_input = io_num.n_input;
    n_output = io_num.n_output;

    LOGI("n_input = %d, n_output = %d", n_input, n_output);

    // 3.2 Query input attributes
    memset(input_attrs, 0, n_input * sizeof(rknn_tensor_attr));
    for (int i = 0; i < n_input; ++i) {
        input_attrs[i].index = i;
        cmd = RKNN_QUERY_INPUT_ATTR;
        ret = rknn_query(ctx, cmd, &(input_attrs[i]), sizeof(rknn_tensor_attr));
        if (ret < 0) {
            LOGE("rknn_query input_attrs[%d] fail!ret=%d\n", i, ret);
            return -1;
        }
    }
    LOGD("input attrs size %d",input_attrs->size);
    for (int i = 0; i < 1; i++) {
        LOGE("input %d attrs %u", i, input_attrs[i].n_dims);
        LOGE("input %d attrs %u", i, input_attrs[i].n_elems);
        LOGE("input %d attrs %u", i, input_attrs[i].size);
    }

    // 3.3 Update global model input shape.
    if (RKNN_TENSOR_NHWC == input_attrs[0].fmt) {
        m_in_height = input_attrs[0].dims[1];
        m_in_width = input_attrs[0].dims[2];
        m_in_channel = input_attrs[0].dims[3];
    } else if (RKNN_TENSOR_NCHW == input_attrs[0].fmt) {
        m_in_height = input_attrs[0].dims[2];
        m_in_width = input_attrs[0].dims[3];
        m_in_channel = input_attrs[0].dims[1];
    } else {
        LOGE("Unsupported model input layout: %d!\n", input_attrs[0].fmt);
        return -1;
    }

    // 3.4 Query output attributes
    memset(output_attrs, 0, n_output * sizeof(rknn_tensor_attr));
    for (int i = 0; i < n_output; ++i) {
        output_attrs[i].index = i;
        cmd = RKNN_QUERY_OUTPUT_ATTR;
        ret = rknn_query(ctx, cmd, &(output_attrs[i]), sizeof(rknn_tensor_attr));
        if (ret < 0) {
            LOGE("rknn_query output_attrs[%d] fail!ret=%d\n", i, ret);
            return -1;
        }
        // set out_scales/out_zps for post_process
        out_scales.push_back(output_attrs[i].scale);
        out_zps.push_back(output_attrs[i].zp);
    }
    LOGD("output attrs size %d",output_attrs->size);
    for (int i = 0; i < 1; i++) {
        LOGE("output %d attrs %u", i, output_attrs[i].n_dims);
        LOGE("output %d attrs %u", i, output_attrs[i].n_elems);
        LOGE("output %d attrs %u", i, output_attrs[i].size);
    }

    // 4.1 Update input attrs
    input_attrs[0].index = 0;
    input_attrs[0].type = RKNN_TENSOR_UINT8;
    input_attrs[0].size = m_in_height * m_in_width * m_in_channel * sizeof(char);
    input_attrs[0].fmt = RKNN_TENSOR_NHWC;
    // TODO -- The efficiency of pass through will be higher, we need adjust the layout of input to
    //         meet the use condition of pass through.
    input_attrs[0].pass_through = 0;

    // 4.2. Set outputs memory
    for (int i = 0; i < n_output; ++i) {
        // 4.2.1 Create output tensor memory, output data type is int8, post_process need int8 data.
        output_mems[i] = rknn_create_mem(ctx, output_attrs[i].n_elems * sizeof(unsigned char));
        memset(output_mems[i]->virt_addr, 0, output_attrs[i].n_elems * sizeof(unsigned char));
        // 4.2.2 Update input attrs
        output_attrs[i].type = RKNN_TENSOR_INT8;
        // 4.2.3 Set output buffer
        rknn_set_io_mem(ctx, output_mems[i], &(output_attrs[i]));
    }

    created = true;

    LOGI("rknn_init success!");

    return 0;
}


void destroy() {
    LOGI("release related rknn res");
    rknn_destroy(ctx);
}


bool
run_yolo(long npu_buf_handle, int camera_width, int camera_height, char *y0, char *y1, char *y2) {
    int ret;
    bool status = false;
    if (!created) {
        LOGE("run_yolo: init yolo hasn't successful!");
        return false;
    }

#ifdef EVAL_TIME
    struct timeval start_time, stop_time;
#endif

    auto p_img_npu_buf = reinterpret_cast<img_npu_buffer *>(npu_buf_handle);
    ret = rknn_set_io_mem(ctx, p_img_npu_buf->p_npu_buf, &p_img_npu_buf->in_attrs);

#ifdef DEBUG_DUMP
    // save resized image
    if (g_inf_count == 9) {
        char out_img_name[1024];
        memset(out_img_name, 0, sizeof(out_img_name));
        sprintf(out_img_name, "/data/user/0/com.rockchip.gpadc.yolodemo/cache/resized_img_%d.rgb", g_inf_count);
        FILE *fp = fopen(out_img_name, "w");
//        LOGI("n_elems: %d", input_attrs[0].n_elems);
//        fwrite(input_mems[0]->virt_addr, 1, input_attrs[0].n_elems * sizeof(unsigned char), fp);
//        fflush(fp);
        for (int i = 0; i < input_attrs[0].n_elems; ++i) {
            fprintf(fp, "%d\n", *((uint8_t *)(p_img_npu_buf->p_npu_buf->virt_addr) + i));
        }
        fclose(fp);
    }
#endif

    img_width = camera_width;    // the width of the actual input image
    img_height = camera_height;   // the height of the actual input image

    // set scale_w, scale_h for post process
    scale_w = (float) m_in_width / img_width;
    scale_h = (float) m_in_height / img_height;

#ifdef EVAL_TIME
    gettimeofday(&start_time, NULL);
#endif
    ret = rknn_run(ctx, nullptr);
    if (ret < 0) {
        LOGE("rknn_run fail! ret=%d\n", ret);
        return false;
    }
#ifdef EVAL_TIME
    gettimeofday(&stop_time, NULL);
    LOGI("inference use %f ms\n", (__get_us(stop_time) - __get_us(start_time)) / 1000);

    // outputs format are all NCHW.
    gettimeofday(&start_time, NULL);
#endif

    memcpy(y0, output_mems[0]->virt_addr, output_attrs[0].n_elems * sizeof(char));
    memcpy(y1, output_mems[1]->virt_addr, output_attrs[1].n_elems * sizeof(char));
    memcpy(y2, output_mems[2]->virt_addr, output_attrs[2].n_elems * sizeof(char));

#ifdef EVAL_TIME
    gettimeofday(&stop_time, NULL);
    LOGI("copy output use %f ms\n", (__get_us(stop_time) - __get_us(start_time)) / 1000);
#endif

#ifdef DEBUG_DUMP
    if (g_inf_count == 5) {
        for (int i = 0; i < n_output; ++i) {
            char out_path[1024];
            memset(out_path, 0, sizeof(out_path));
            sprintf(out_path, "/data/user/0/com.rockchip.gpadc.yolodemo/cache/out_%d.tensor", i);
            FILE *fp = fopen(out_path, "w");
            for (int j = 0; j < output_attrs[i].n_elems; ++j) {
                fprintf(fp, "%d\n", *((int8_t *)(output_mems[i]->virt_addr) + i));
            }
            fclose(fp);
        }
    }
    if (g_inf_count < 100) {
        g_inf_count++;
    }
#endif

    status = true;

//    LOGI("run_yolo: end\n");

    return status;
}

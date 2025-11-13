/**
  * @ClassName yolo_image
  * @Description TODO
  * @Author raul.rao
  * @Date 2022/5/23 11:43
  * @Version 1.0
  */
#ifndef RK_YOLOV5_DEMO_YOLO_IMAGE_H
#define RK_YOLOV5_DEMO_YOLO_IMAGE_H

#include "../rknn_api.h"
#include "../logcat/androidlog.h"

//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "rkyolo4j", ##__VA_ARGS__);
//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "rkyolo4j", ##__VA_ARGS__);

typedef struct img_npu_buffer_t
{
  rknn_tensor_mem *p_npu_buf;

  rknn_tensor_attr in_attrs;
}img_npu_buffer;


int create(int im_height, int im_width, int im_channel, char *model_path);
void destroy();
bool run_yolo(long npu_buf_handle, int camera_width, int camera_height, char *y0, char *y1, char *y2);

#endif //RK_YOLOV5_DEMO_YOLO_IMAGE_H

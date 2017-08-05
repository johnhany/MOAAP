#include <jni.h>
#include <vector>
#include <android/log.h>

#include "opencv2/opencv.hpp"

using namespace cv;
using namespace std;

char filepath1[100] = "/storage/emulated/0/Download/MOAAP/Chapter6/panorama_stitched.jpg";

#define  LOG_TAG    "MOAAP-CHP6"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
{
JNIEXPORT jint JNICALL
Java_net_johnhany_moaap_1chp6_1r3_MainActivity_StitchPanorama(JNIEnv *env, jobject,
                                                                   jlongArray imageAddressArray,
                                                                   jlong outputAddress) {
    jsize a_len = env->GetArrayLength(imageAddressArray);
    jlong *imgAddressArr = env->GetLongArrayElements(imageAddressArray,0);
    vector<Mat> imgVec;
    for(int	k=0; k<a_len; k++)
    {
        Mat	&curimage = *(Mat*)imgAddressArr[k];
        Mat	newimage;
        curimage.copyTo(newimage);
        float scale = 500.0f / curimage.rows;
        resize(newimage, newimage, Size((int)(scale*curimage.cols), (int)(scale*curimage.rows)));
        LOGD("Image height %d width %d", newimage.rows, newimage.cols);
        imgVec.push_back(newimage);
    }
    Mat	&result = *(Mat*)outputAddress;

    Stitcher stitcher = Stitcher::createDefault();
    Stitcher::Status status = stitcher.stitch(imgVec, result);

    LOGD("Result height %d width %d", result.rows, result.cols);

    imwrite(filepath1, result);

    return status;
}
}

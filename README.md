# MOAAP

Updated source codes for book [Mastering OpenCV Android Application Programming](https://www.amazon.cn/Mastering-OpenCV-Android-Application-Programming-Kapur-Salil/dp/B012GGB7O8) and it's Chinese edition [《深入OpenCV Android应用开发》](http://item.jd.com/11958048.html).

The book is written by Salil Kapur and Nisarg Thakkar, and translated into Chinese by [John Hany](https://github.com/johnhany). The english edition was first published by Packt Publishin in July, 2015, and the Chinese edition in June, 2016. Many pieces of codes in the book have been deprecated over the time. This repository aims to provide  source codes for the latest developing and building tools (Android Studio, Android SDK, NDK and OpenCV) and to achieve the exact functionalities of original codes in the book.

## Requirements

* JDK 8u141
* Android Studio 2.3.3
* Android 7.0 (API 24)
* Android SDK Tools 26.0.1
* Android NDK r15b
* OpenCV Android SDK 3.2.0
* OpenCV 3.2.0 (including opencv_contrib 3.2.0)

All tests are passed on Windows 10 and Ubuntu 16.04.

For Chp3 and Chp6, configuring native build with CMake in Android Studio is required.

For Chp3, compiling OpenCV Android SDK with opencv_contrib modules on Ubuntu is required.

For Chp7, [MNIST](http://yann.lecun.com/exdb/mnist/) dataset is required. A pre-trained SVM classifier is available [here](http://pan.baidu.com/s/1nuS3M9F). Please copy `train-images.idx3-ubyte`, `train-labels.idx1-ubyte` and `SVM_MNIST.xml` files to root directory of sd-card in your Android device. C++ codes for training and testing SVM and KNN on PC are provided [here](https://gist.github.com/johnhany/ff6835b2191e58f96699645b2d36c1a1) and [here](https://gist.github.com/johnhany/a48487dcacdb4c2108e919b421d19cfb).

## Projects

* MOAAP-Chp1

  Image filters, including mean blur, Gaussian blur, median blur, dilation, erosion, thresholding and adaptive thresholding.

* MOAAP-Chp2

  Feature detection, including Difference of Gaussian, Canny Edge detector, Sobel operator, Harris Corner detection, Hough line transformation and Hough circle transfermation.

* MOAAP-Chp3

  Object detection and image matching, including SIFT, SURF, ORB, BRISK and FREAK.

* MOAAP-Chp4

  Cascade classifiers for face detection, including Haar and LBP.

* MOAAP-Chp5

  Object tracking, including Optical Flow, Gaussian pyramids, Laplacian pyramids and KLT tracker.

* MOAAP-Chp6

  Image alignment and stitching for panorama image.

* MOAAP-Chp7

  Basic machine learnings for digit recognition, including SVM and KNN.

## Related Posts

* [在Android Studio上进行OpenCV 3.1开发](http://johnhany.net/2016/01/opencv-3-development-in-android-studio/)
* [Android Studio 2.3利用CMAKE进行OpenCV 3.2的NDK开发](http://johnhany.net/2017/07/opencv-ndk-dev-with-cmake-on-android-studio/)
* [Ubuntu 16.04下为Android编译OpenCV 3.2.0 Manager](http://johnhany.net/2016/07/build-opencv-manager-for-android-on-ubuntu/)
* [《深入OpenCV Android应用开发 中文版 – 第一章代码更新》](http://johnhany.net/2016/07/moaap-chapter-1-codes/)
* [《深入OpenCV Android应用开发 中文版 – 第二章代码更新》](http://johnhany.net/2016/07/moaap-chapter-2-codes/)
* [《深入OpenCV Android应用开发 中文版 – 第三章代码更新》](http://johnhany.net/2016/07/moaap-chapter-3-codes/)
* [《深入OpenCV Android应用开发 中文版 – 第四章代码更新》](http://johnhany.net/2016/07/moaap-chapter-4-codes/)
* [《深入OpenCV Android应用开发 中文版 – 第五章代码更新》](http://johnhany.net/2016/07/moaap-chapter-5-codes/)
* [《深入OpenCV Android应用开发 中文版 – 第六章代码更新》](http://johnhany.net/2016/07/moaap-chapter-6-codes/)
* [《深入OpenCV Android应用开发 中文版 – 第七章代码更新》](http://johnhany.net/2016/07/moaap-chapter-7-codes/)

## License

The MOAAP updated codes are released under the MIT license.

Copyrights of English edition of MOAAP book including the original source codes belong to Packt Publishing. Copyrights of the Chinese edition belong to Publishing House of Electronics Industry. As translator for the Chinese edition, under the contract with Publishin House of Electronics Industry, I do not own the copyrights of any material in the Chinese edition. I do believe, however, I'm entitled rights to release updated source codes (aka this repository) under an open-source license, since the source codes in this repository do not resemble the original ones in many aspects and I'm not profitting from this project in any form.
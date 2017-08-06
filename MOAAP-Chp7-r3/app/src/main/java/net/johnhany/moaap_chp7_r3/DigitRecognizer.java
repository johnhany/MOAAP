package net.johnhany.moaap_chp7_r3;

import android.os.Environment;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.SVM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.opencv.ml.Ml.ROW_SAMPLE;

public class DigitRecognizer {

    private String images_path = "";
    private String labels_path = "";

    private int total_images = 0;
    private int width = 0;
    private int height = 0;

    private byte[] labels_data;
    private KNearest knn = null;
    private SVM svm = null;

    DigitRecognizer(String images, String labels)
    {
        images_path = images;
        labels_path = labels;

        try{
            ReadMNISTData();
        }
        catch (IOException e)
        {
            Log.i("Read error:", "" + e.getMessage());
        }
    }

    private void ReadMNISTData() throws FileNotFoundException {

        File external_storage = Environment.getExternalStorageDirectory(); //Get SD Card's path

        //Read images
        Mat training_images;
        File mnist_images_file = new File(external_storage, images_path);
        FileInputStream images_reader = new FileInputStream(mnist_images_file);

        try{
            //Read the file headers which contain the total number of images and dimensions. First 16 bytes hold the header

            //byte 0 -3 : Magic Number (Not to be used)
            //byte 4 - 7: Total number of images in the dataset
            //byte 8 - 11: width of each image in the dataset
            //byte 12 - 15: height of each image in the dataset


            byte [] header = new byte[16];
            images_reader.read(header, 0, 16);

            //Combining the bytes to form an integer
            ByteBuffer temp = ByteBuffer.wrap(header, 4, 12);
            total_images = temp.getInt();
            width = temp.getInt();
            height = temp.getInt();

            //Total number of pixels in each image
            int px_count = width * height;
            training_images = new Mat(total_images, px_count, CvType.CV_8U);

            //images_data = new byte[total_images][px_count];
            //Read each image and store it in an array.

            for (int i = 0 ; i < total_images ; i++)
            {
                byte[] image = new byte[px_count];
                images_reader.read(image, 0, px_count);
                training_images.put(i,0,image);
            }
            training_images.convertTo(training_images, CvType.CV_32FC1);
            images_reader.close();
        }
        catch (IOException e)
        {
            Log.i("MNIST Read Error:", "" + e.getMessage());
        }

        //Read Labels
        Mat training_labels;

        labels_data = new byte[total_images];
        File mnist_labels_file = new File(external_storage, labels_path);
        FileInputStream labels_reader = new FileInputStream(mnist_labels_file);

        try{

            training_labels = new Mat(total_images, 1, CvType.CV_8U);
            Mat temp_labels = new Mat(1, total_images, CvType.CV_8U);
            byte[] header = new byte[8];
            //Read the header
            labels_reader.read(header, 0, 8);
            //Read all the labels at once
            labels_reader.read(labels_data,0,total_images);
            temp_labels.put(0,0, labels_data);

            //Take a transpose of the image
            Core.transpose(temp_labels, training_labels);
            training_labels.convertTo(training_labels, CvType.CV_32S);
            labels_reader.close();
        }
        catch (IOException e)
        {
            Log.i("MNIST Read Error:", "" + e.getMessage());
        }


        //K-NN Classifier
        //knn = KNearest.create();
        //knn.setDefaultK(10);
        //knn.train(training_images, ROW_SAMPLE, training_labels);

        //SVM Classifier
        //svm = SVM.create();
        //svm.train(training_images, ROW_SAMPLE, training_labels);
        File mnist_svm_file = new File(external_storage, "SVM_MNIST.xml");
        svm = SVM.load(mnist_svm_file.getAbsolutePath());

        Log.d("MNIST SVM:", "var_count: " + svm.getVarCount());
    }


    int FindMatch(Mat test_image)
    {

        //Dilate the image
        Imgproc.dilate(test_image, test_image, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(3,3)));
        //Resize the image to match it with the sample image size
        Imgproc.resize(test_image, test_image, new Size(width, height));
        //Convert the image to grayscale
        Imgproc.cvtColor(test_image, test_image, Imgproc.COLOR_RGB2GRAY);
        //Adaptive Threshold
        Imgproc.adaptiveThreshold(test_image,test_image,255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV,15, 2);

        Mat test = new Mat(1, test_image.rows() * test_image.cols(), CvType.CV_32FC1);
        int count = 0;
        for(int i = 0 ; i < test_image.rows(); i++)
        {
            for(int j = 0 ; j < test_image.cols(); j++) {
                test.put(0, count, test_image.get(i, j)[0]);
                count++;
            }
        }

        Mat results = new Mat(1, 1, CvType.CV_8U);

        //K-NN Prediction
        //return (int)knn.findNearest(test, 10, results);

        //SVM Prediction
        return (int)svm.predict(test);
    }
}

package net.johnhany.moaap_chp3_r3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int SELECT_PHOTO_1 = 1;
    private final int SELECT_PHOTO_2 = 2;
    private ImageView ivImage1;
    private TextView tvKeyPointsObject1, tvKeyPointsObject2, tvKeyPointsMatches, tvTime;
    private int keypointsObject1, keypointsObject2, keypointMatches;
    Mat src1, src2, src1_gray, src2_gray;
    static int ACTION_MODE = 0;
    private boolean src1Selected = false, src2Selected = false;
    static int REQUEST_READ_EXTERNAL_STORAGE = 11;
    static int REQUEST_WRITE_EXTERNAL_STORAGE = 12;
    static boolean read_external_storage_granted = false;
    static boolean write_external_storage_granted = false;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //DO YOUR WORK/STUFF HERE
                    System.loadLibrary("xfeatures2d");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mOpenCVCallBack);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivImage1 = (ImageView)findViewById(R.id.ivImage1);
        tvKeyPointsObject1 = (TextView) findViewById(R.id.tvKeyPointsObject1);
        tvKeyPointsObject2 = (TextView) findViewById(R.id.tvKeyPointsObject2);
        tvKeyPointsMatches = (TextView) findViewById(R.id.tvKeyPointsMatches);
        keypointsObject1 = keypointsObject2 = keypointMatches = -1;
        tvTime = (TextView) findViewById(R.id.tvTime);
        Intent intent = getIntent();

        if(intent.hasExtra("ACTION_MODE")){
            ACTION_MODE = intent.getIntExtra("ACTION_MODE", 0);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "request READ_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }else {
            Log.i("permission", "READ_EXTERNAL_STORAGE already granted");
            read_external_storage_granted = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_load_first_image && read_external_storage_granted) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO_1);
            return true;
        } else if (id == R.id.action_load_second_image && read_external_storage_granted) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO_2);
            return true;
        } else if(!read_external_storage_granted) {
            Log.e("APP", "pick image failed");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO_1:
                if(resultCode == RESULT_OK && read_external_storage_granted){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        src1 = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                        src1_gray = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC1);
//                        ivImage1.setImageBitmap(selectedImage);
                        Utils.bitmapToMat(selectedImage, src1);
                        Imgproc.cvtColor(src1, src1_gray, Imgproc.COLOR_BGRA2GRAY);
                        src1Selected = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO_2:
                if(resultCode == RESULT_OK && read_external_storage_granted){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        src2 = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                        src2_gray = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC1);
                        Utils.bitmapToMat(selectedImage, src2);
                        Imgproc.cvtColor(src2, src2_gray, Imgproc.COLOR_BGRA2GRAY);
                        src2Selected = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        Toast.makeText(MainActivity.this, "First image: " + src1Selected + " Second image: " + src2Selected, Toast.LENGTH_SHORT).show();

        if(src1Selected && src2Selected){
            Log.i("APP", "Before Execute");
            new AsyncTask<Void, Void, Bitmap>() {
                private long startTime, endTime;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    startTime = System.currentTimeMillis();
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    return executeTask();
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    endTime = System.currentTimeMillis();
                    ivImage1.setImageBitmap(bitmap);
                    tvKeyPointsObject1.setText(getString(R.string.result_target_1, keypointsObject1));
                    tvKeyPointsObject2.setText(getString(R.string.result_target_2, keypointsObject2));
                    tvKeyPointsMatches.setText(getString(R.string.result_target_matches, keypointMatches));
                    tvTime.setText(getString(R.string.result_time_cost, endTime-startTime));
                }
            }.execute();
        }
    }

    private Bitmap executeTask(){
        Log.i("APP", "Execute");
        final int MAX_MATCHES = 50;
        FeatureDetector detector;
        MatOfKeyPoint keypoints1, keypoints2;
        DescriptorExtractor descriptorExtractor;
        Mat descriptors1, descriptors2;
        DescriptorMatcher descriptorMatcher;
        MatOfDMatch matches = new MatOfDMatch();
        keypoints1 = new MatOfKeyPoint();
        keypoints2 = new MatOfKeyPoint();
        descriptors1 = new Mat();
        descriptors2 = new Mat();
        Log.i("APP", "before switch");
        switch (ACTION_MODE){
            case HomeActivity.MODE_SIFT:
                detector = FeatureDetector.create(FeatureDetector.SIFT);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);
//                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
                break;
            case HomeActivity.MODE_SURF:
                detector = FeatureDetector.create(FeatureDetector.SURF);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);
                break;
            case HomeActivity.MODE_ORB:
                detector = FeatureDetector.create(FeatureDetector.ORB);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
                break;
            case HomeActivity.MODE_BRISK:
                detector = FeatureDetector.create(FeatureDetector.BRISK);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.BRISK);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
                break;
            case HomeActivity.MODE_FREAK:
                detector = FeatureDetector.create(FeatureDetector.FAST);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.FREAK);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
                break;
            default:
                detector = FeatureDetector.create(FeatureDetector.FAST);
                descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.BRIEF);
                descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
                break;
        }
        Log.i("APP", "After switch");

        detector.detect(src2_gray, keypoints2);
        detector.detect(src1_gray, keypoints1);
        Log.i("APP", CvType.typeToString(src1_gray.type())+" "+CvType.typeToString(src2_gray.type()));
        Log.i("APP", keypoints1.toArray().length+" keypoints");
        Log.i("APP", keypoints2.toArray().length+" keypoints");
        Log.i("APP", "Detect");

        keypointsObject1 = keypoints1.toArray().length;
        keypointsObject2 = keypoints2.toArray().length;

        descriptorExtractor.compute(src1_gray, keypoints1, descriptors1);
        descriptorExtractor.compute(src2_gray, keypoints2, descriptors2);

        descriptorMatcher.match(descriptors1, descriptors2, matches);

        Log.i("APP", matches.toArray().length+" matches");

        keypointMatches = matches.toArray().length;

        Collections.sort(matches.toList(), new Comparator<DMatch>() {
            @Override
            public int compare(DMatch o1, DMatch o2) {
                if(o1.distance<o2.distance)
                    return -1;
                if(o1.distance>o2.distance)
                    return 1;
                return 0;
            }
        });

        List<DMatch> listOfDMatch = matches.toList();
        if(listOfDMatch.size()>MAX_MATCHES){
            matches.fromList(listOfDMatch.subList(0,MAX_MATCHES));
        }

//        Mat src3 = src1.clone();
//        Features2d.drawMatches(src1, keypoints1, src2, keypoints2, matches, src3);
        Mat src3 = drawMatches(src1_gray, keypoints1, src2_gray, keypoints2, matches, false);

        Log.i("APP", CvType.typeToString(src3.type()));

        Bitmap image1 = Bitmap.createBitmap(src3.cols(), src3.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src3, image1);
        Imgproc.cvtColor(src3, src3, Imgproc.COLOR_BGR2RGB);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "request WRITE_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            Log.i("permission", "WRITE_EXTERNAL_STORAGE already granted");
            write_external_storage_granted = true;
        }

        if(write_external_storage_granted) {
            boolean bool = Imgcodecs.imwrite(Environment.getExternalStorageDirectory() + "/Download/" + ACTION_MODE + ".png", src3);
            Log.i("APP", bool + " " + Environment.getExternalStorageDirectory() + "/Download/" + ACTION_MODE + ".png");
        }
        return image1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.i("permission", "READ_EXTERNAL_STORAGE granted");
                read_external_storage_granted = true;
            } else {
                // permission denied
                Log.i("permission", "READ_EXTERNAL_STORAGE denied");
            }
        }else if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.i("permission", "WRITE_EXTERNAL_STORAGE granted");
                write_external_storage_granted = true;
            } else {
                // permission denied
                Log.i("permission", "WRITE_EXTERNAL_STORAGE denied");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static Mat drawMatches(Mat img1, MatOfKeyPoint key1, Mat img2, MatOfKeyPoint key2, MatOfDMatch matches, boolean imageOnly){
        //https://github.com/mustafaakin/image-matcher/tree/master/src/in/mustafaak/imagematcher
        Mat out = new Mat();
        Mat im1 = new Mat();
        Mat im2 = new Mat();
        Imgproc.cvtColor(img1, im1, Imgproc.COLOR_GRAY2RGB);
        Imgproc.cvtColor(img2, im2, Imgproc.COLOR_GRAY2RGB);
        if ( imageOnly){
            MatOfDMatch emptyMatch = new MatOfDMatch();
            MatOfKeyPoint emptyKey1 = new MatOfKeyPoint();
            MatOfKeyPoint emptyKey2 = new MatOfKeyPoint();
            Features2d.drawMatches(im1, emptyKey1, im2, emptyKey2, emptyMatch, out);
        } else {
            Features2d.drawMatches(im1, key1, im2, key2, matches, out);
        }
        //Bitmap bmp = Bitmap.createBitmap(out.cols(), out.rows(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(out, out, Imgproc.COLOR_BGR2RGB);
        Imgproc.putText(out, "Frame", new Point(img1.width() / 2,30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0,255,255),3);
        Imgproc.putText(out, "Match", new Point(img1.width() + img2.width() / 2,30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255,0,0),3);
        return out;
    }

}

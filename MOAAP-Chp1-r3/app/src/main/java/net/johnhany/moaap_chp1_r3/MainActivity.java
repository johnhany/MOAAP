package net.johnhany.moaap_chp1_r3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private final int SELECT_PHOTO = 1;
    private ImageView ivImage, ivImageProcessed;
    Mat src, src_gray;
    static int ACTION_MODE = 0;
    static int REQUEST_READ_EXTERNAL_STORAGE = 0;
    static boolean read_external_storage_granted = false;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //DO YOUR WORK/STUFF HERE
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

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mOpenCVCallBack);

        //Add a left arrow on Action Bar to return to the previous Activity
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivImage = (ImageView)findViewById(R.id.ivImage);
        ivImageProcessed = (ImageView)findViewById(R.id.ivImageProcessed);
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

        if (id == R.id.action_load_image && read_external_storage_granted) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);

            return true;
        }else if(!read_external_storage_granted) {
            Log.i("permission", "READ_EXTERNAL_STORAGE denied");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        //Put it there, just in case:)
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK && read_external_storage_granted){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        src = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                        Utils.bitmapToMat(selectedImage, src);
                        src_gray = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC1);
                        switch (ACTION_MODE) {
                            case HomeActivity.GAUSSIAN_BLUR:
                                Imgproc.GaussianBlur(src, src, new Size(9, 9), 0);
                                break;
                            case HomeActivity.MEAN_BLUR:
                                Imgproc.blur(src, src, new Size(9, 9));
                                break;
                            case HomeActivity.MEDIAN_BLUR:
                                Imgproc.medianBlur(src, src, 9);
                                break;
                            case HomeActivity.SHARPEN:
                                Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
                                //int[] values = {0, -1, 0, -1, 5, -1, 0, -1, 0};
                                Log.d("imageType", CvType.typeToString(src.type()) + "");
                                kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
                                Imgproc.filter2D(src, src, src_gray.depth(), kernel);
                                break;
                            case HomeActivity.DILATE:
                                Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
                                Imgproc.threshold(src_gray, src_gray, 100, 255, Imgproc.THRESH_BINARY);
                                Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
                                Imgproc.dilate(src_gray, src_gray, kernelDilate);
                                Imgproc.cvtColor(src_gray, src, Imgproc.COLOR_GRAY2RGBA, 4);
                                break;
                            case HomeActivity.ERODE:
                                Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
                                Imgproc.threshold(src_gray, src_gray, 100, 255, Imgproc.THRESH_BINARY);
                                Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
                                Imgproc.erode(src_gray, src_gray, kernelErode);
                                Imgproc.cvtColor(src_gray, src, Imgproc.COLOR_GRAY2RGBA, 4);
                                break;
                            case HomeActivity.THRESHOLD:
                                Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
                                Imgproc.threshold(src_gray, src_gray, 100, 255, Imgproc.THRESH_BINARY);
                                Imgproc.cvtColor(src_gray, src, Imgproc.COLOR_GRAY2RGBA, 4);
                                break;
                            case HomeActivity.ADAPTIVE_THRESHOLD:
                                Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
                                Imgproc.adaptiveThreshold(src_gray, src_gray, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 0);
                                Imgproc.cvtColor(src_gray, src, Imgproc.COLOR_GRAY2RGBA, 4);
                                break;
                        }
                        Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                        Log.i("imageType", CvType.typeToString(src.type()) + "");
                        Utils.matToBitmap(src, processedImage);
                        ivImage.setImageBitmap(selectedImage);
                        ivImageProcessed.setImageBitmap(processedImage);
                        Log.i("process", "process done");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.i("permission", "READ_EXTERNAL_STORAGE granted");
                read_external_storage_granted = true;
            } else {
                // permission denied
            }
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

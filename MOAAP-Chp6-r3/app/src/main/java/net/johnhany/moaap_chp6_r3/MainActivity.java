package net.johnhany.moaap_chp6_r3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int CLICK_PHOTO = 1;
    private final int SELECT_PHOTO = 2;
    private Uri fileUri;
    private ImageView ivImage;
    private Mat src;
    private List<Mat> listImage	= new ArrayList<>();
    private static final String FILE_LOCATION = Environment.getExternalStorageDirectory() + "/Download/MOAAP/Chapter6/";
    static int REQUEST_READ_EXTERNAL_STORAGE = 11;
    static boolean read_external_storage_granted = false;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    System.loadLibrary("stitcher");
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
        ivImage = (ImageView)findViewById(R.id.ivImage);

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mOpenCVCallBack);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "request READ_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }else {
            Log.i("permission", "READ_EXTERNAL_STORAGE already granted");
            read_external_storage_granted = true;
        }

        Button bClickImage, bDone;

        bClickImage = (Button)findViewById(R.id.bClickImage);
        bDone = (Button)findViewById(R.id.bDone);

        bClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imagesFolder = new File(FILE_LOCATION);
                //imagesFolder.mkdirs();
                File image = new File(imagesFolder, "panorama_"+ (listImage.size()+1) + ".jpg");
                fileUri = Uri.fromFile(image);
                Log.d("MainActivity", "File URI = " + fileUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CLICK_PHOTO);
            }
        });

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listImage.size()==0){
                    Toast.makeText(getApplicationContext(), "No images clicked", Toast.LENGTH_SHORT).show();
                } else if(listImage.size()==1){
                    Toast.makeText(getApplicationContext(), "Only one image clicked", Toast.LENGTH_SHORT).show();
                    Bitmap image = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(src, image);
                    ivImage.setImageBitmap(image);
                } else {
                    createPanorama();
                }
            }
        });
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
        } else if(!read_external_storage_granted) {
            Log.e("MainActivity", "pick image failed");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Log.d("MainActivity", "request code " + requestCode + ", click photo " + CLICK_PHOTO + ", result code " + resultCode + ", result ok " + RESULT_OK);

        switch(requestCode) {
            case CLICK_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        Log.d("MainActivity", fileUri.toString());
                        final InputStream imageStream = getContentResolver().openInputStream(fileUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        src = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                        Imgproc.resize(src, src, new Size(src.rows()/4, src.cols()/4));
                        Utils.bitmapToMat(selectedImage, src);
                        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2RGB);
                        listImage.add(src);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK && read_external_storage_granted){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        src = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                        Imgproc.resize(src, src, new Size(src.rows()/4, src.cols()/4));
                        Utils.bitmapToMat(selectedImage, src);
                        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR);
                        Log.d("MainActivity", "image height " + src.rows() + ", image width " + src.cols());
                        listImage.add(src);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
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
        }
    }

    private void createPanorama(){
        new AsyncTask<Void, Void, Bitmap>() {
            ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(MainActivity.this, "Building Panorama", "Please Wait");
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                int	elems = listImage.size();
                long[] tempobjadr = new long[elems];
                for	(int i=0; i<elems; i++){
                    tempobjadr[i] = listImage.get(i).getNativeObjAddr();
                }
                Mat result = new Mat();

                int stitchstatus = StitchPanorama(tempobjadr, result.getNativeObjAddr());
                Log.d("MainActivity", "result height " + result.rows() + ", result width " + result.cols());
                if(stitchstatus != 0){
                    Log.e("MainActivity", "Stitching failed: " + stitchstatus);
                    return null;
                }

                Imgproc.cvtColor(result, result, Imgproc.COLOR_BGR2RGBA);
                Bitmap bitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(result, bitmap);

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                dialog.dismiss();
                if(bitmap!=null) {
                    ivImage.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public native int StitchPanorama(long[]	imageAddressArray, long	outputAddress);

}

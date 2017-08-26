package com.example.amyli.nutritiontracker;
/**
 * Author : Haripriya
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;

public class SecondActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, Camera.AutoFocusCallback {
    TextView txtView;
    ImageView myImageView;

    //Variables for QRCode scanning start
    private SurfaceView cameraView;
    static CameraSource cameraSource;
    TextView barcodeInfo;
    TextRecognizer recognizer;
    BarcodeDetector barcodeDetector;
    public static int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    Barcode thisCode;
    String barcodeString;
    StringBuffer ocrString;
    //Variables for QRCode scanning end




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        //new barcode functionality start
        final Box box = new Box(this);
        addContentView(box, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        //capture button
        Button captureButton = (Button) findViewById(R.id.button2);
        captureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SurfaceView surfaceview = (SurfaceView) findViewById(R.id.surface_view);
                if(surfaceview.getVisibility() == View.GONE){
                    surfaceview.setVisibility(View.VISIBLE);
                }
                else{
                    surfaceview.setVisibility(View.GONE);
                    box.setVisibility(View.GONE);
                }

            }
        });

        cameraView = (SurfaceView)findViewById(R.id.surface_view);
        barcodeInfo = (TextView)findViewById(R.id.barcode_value);
        recognizer = new TextRecognizer.Builder(this).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
        cameraSource = new CameraSource.Builder(this, recognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //noinspection MissingPermission
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        recognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                /*final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeString = barcodes.valueAt(0).displayValue;
                    Log.e(TAG,barcodeString);
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            barcodeInfo.setText(    // Update the TextView
                                    barcodeString
                            );
                        }
                    });
                }*/
                SparseArray<TextBlock> items = detections.getDetectedItems();

                ocrString = new StringBuffer();
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    List<Line> lines = (List<Line>) item.getComponents();
                    Log.d("total lines: ", String.valueOf(lines.size()));

                    if (item != null && item.getValue() != null) {
//                        Log.d("OcrDetectorProcessor", "Text detected! " + item.getValue());
//                        ocrString.append(item.getValue());
//                        ocrString.append("\n");
                        String itemLine = item.getValue();
                        if (item.getValue().contains(getString(R.string.nutrient0))) {
                            ocrString.append(item.getValue().substring(9));
                            ocrString.append("\n");
                            Log.d("Calories: ", item.getValue().substring(9));
                        }
                        if (item.getValue().contains("Total Fat")) {
                            ocrString.append(item.getValue().substring(10));
                            ocrString.append("\n");
                            Log.d("Total Fat: ", item.getValue().substring(10));
                        }
                        if (item.getValue().contains("Cholesterol")) {
                            ocrString.append(item.getValue().substring(12));
                            ocrString.append("\n");
                            Log.d("Cholesterol: ", item.getValue().substring(12));
                        }
                        if (item.getValue().contains("Sodium")) {
                            ocrString.append(item.getValue().substring(7));
                            ocrString.append("\n");
                            Log.d("Sodium: ", item.getValue().substring(7));
                        }
                        if (item.getValue().contains("Total Carbohydrate")) {
                            ocrString.append(item.getValue().substring(19));
                            ocrString.append("\n");
                            Log.d("Total Carbohydrate: ", item.getValue().substring(19));
                        }
                        if (item.getValue().contains("Protein")) {
                            ocrString.append(item.getValue().substring(8));
                            ocrString.append("\n");
                            Log.d("Protein", item.getValue().substring(8));
                        }

                    }

                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            barcodeInfo.setText(    // Update the TextView
                                    ocrString
                            );
                        }
                    });

                }
            }
        });


        //new barcode functionality ends
    }

    //new barcode functionality start
    @Override
    public void onAutoFocus(boolean b, Camera camera) { }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) { }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }

    @Override
    public void onClick(View view) { }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }
   }


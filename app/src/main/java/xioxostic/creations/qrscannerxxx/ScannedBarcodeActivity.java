package xioxostic.creations.qrscannerxxx;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;

import android.os.Environment;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;
import android.content.ClipboardManager;

public class ScannedBarcodeActivity extends AppCompatActivity {


    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final int PERMISSIONS_REQUEST_CODE = 1000;
    Button mCopy, next;
    String intentData = "";
    String infoRecorder="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scanned_barcode);
        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        next = (Button) findViewById(R.id.Button02);
        mCopy=findViewById(R.id.copy_button);
        mCopy.setVisibility(View.GONE);
        infoRecorder="";

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied text from QR Scanner", txtBarcodeValue.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, 1);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
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


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            mCopy.setVisibility(View.VISIBLE);
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText("Scanned Data :\n"+intentData);
                                callForAction(intentData);
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
    private void determineTheMessage(String scanned){

        String regexStr = "^[+]?[0-9]{10,13}$";
        if (scanned.contains("://")){
            Toast.makeText(getApplicationContext(), "This is an URL.", Toast.LENGTH_SHORT).show();
            showAleartDialogue("Do you want to follow the link?","URL");

        }
        else if (scanned.contains("@")){
            Toast.makeText(getApplicationContext(), "This is an Email.", Toast.LENGTH_SHORT).show();
            showAleartDialogue("Do you want to send a mail to the address?","EMAIL");
        }
        else if (scanned.matches(regexStr)){
            Toast.makeText(getApplicationContext(), "This is an Mobile Number", Toast.LENGTH_SHORT).show();
            showAleartDialogue("Do you want to make a call?","CALL");
        }
        else if (scanned.contains("www")){
            Toast.makeText(getApplicationContext(), "This is an URL.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case 1:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setContentView(R.layout.activity_scanned_barcode);
                    initViews();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "We are very sorry.\nThe app can not work work without Camera.", Toast.LENGTH_SHORT).show();
                    closeNow();
                }
                break;
            case 2:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try {
                        Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                        my_callIntent.setData(Uri.parse("tel:"+infoRecorder));
                        startActivity(my_callIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Error while calling.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "We are very sorry.\nThe app can not make a call without calling permission.", Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void closeNow() {
        View view;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
    private void showAleartDialogue(final String messagetoalert, final String workInfo){

        AlertDialog.Builder builder1
                = new AlertDialog
                .Builder(ScannedBarcodeActivity.this);
        builder1.setMessage(messagetoalert);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Go!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(workInfo=="CALL"){
                            if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                try {
                                    Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                                    my_callIntent.setData(Uri.parse("tel:"+infoRecorder));
                                    startActivity(my_callIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getApplicationContext(), "Error while calling.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                        String[]{Manifest.permission.CALL_PHONE}, 2);
                            }

                        }
                        else if (workInfo=="EMAIL"){
                            String adressmail=""+intentData;
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{adressmail});
                            intent.setType("text/html");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");
                            intent.putExtra(Intent.EXTRA_TEXT, "");
                            startActivity(Intent.createChooser(intent, "Send Email"));

                        }
                        else if (workInfo=="URL"){
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentData));
                            startActivity(browserIntent);
                        }
                        else {
                            dialog.cancel();
                        }
                    }
                });
        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    private void callForAction(String scanned){
        if(infoRecorder==""){
            infoRecorder=scanned;
            determineTheMessage(scanned);
        }
        else{
            if(infoRecorder.equals(""+scanned)){
            }
            else{
                infoRecorder=scanned;
                determineTheMessage(scanned);
            }
        }
    }
}
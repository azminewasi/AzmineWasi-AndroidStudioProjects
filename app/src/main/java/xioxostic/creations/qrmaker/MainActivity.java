package xioxostic.creations.qrmaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import java.util.Calendar;
import java.util.Date;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.media.MediaScannerConnection.scanFile;


public class MainActivity extends AppCompatActivity {

    ImageView mQR;
    LottieAnimationView mAninaton;
    EditText textToShow;
    Button submitButton,saveImg;
    Date currentTime = Calendar.getInstance().getTime();
    String timeNow=currentTime.toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQR = findViewById(R.id.qrImage);
        textToShow = findViewById(R.id.textForQR);
        mAninaton = findViewById(R.id.lodaindAnimation);
        submitButton = findViewById(R.id.submitbutton);
        saveImg=findViewById(R.id.saveImg);

        mQR.setVisibility(View.GONE);
        saveImg.setVisibility(View.GONE);
        mAninaton.setVisibility(View.VISIBLE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String textToSend=textToShow.getText().toString();
                initQRCode(textToSend);

            }
        });
        saveImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    savingQR();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "File save failed", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void initQRCode(String textToSend) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToSend.toString(), BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            mQR.setImageBitmap(bitmap);
            mAninaton.setVisibility(View.GONE);
            mQR.setVisibility(View.VISIBLE);
            saveImg.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "QR Making failed", Toast.LENGTH_SHORT).show();

        }

    }


    private void savingQR() throws IOException {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                mQR.buildDrawingCache();
                Bitmap bm=mQR.getDrawingCache();
                MediaStore.Images.Media.insertImage(getContentResolver(), bm, "QR"+timeNow+".png" , "Description");
                Toast.makeText(getApplicationContext(), "QR code saved.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                    mQR.buildDrawingCache();
                    Bitmap bm=mQR.getDrawingCache();
                    MediaStore.Images.Media.insertImage(getContentResolver(), bm, "QR"+timeNow+".png" , "Description");
                    Toast.makeText(getApplicationContext(), "QR code saved.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "We can't save the photo without storage permission.", Toast.LENGTH_SHORT).show();
                    initQRCode(textToShow.getText().toString());
                }
                break;

        }
    }
}
package xioxostic.creations.flashlight;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import static android.graphics.Color.parseColor;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView LightOn, LightOff, LightOn2;
    RelativeLayout FullScreen;
    private static final int CAMERA_REQUEST = 50;
    private boolean flashLightStatus = false;


    Button mSettings,mBackButton,mSaveButtton;
    RelativeLayout SettingScreen;
    Switch fullScreenTouchSense,lightOnOnStartUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }

        FullScreen=findViewById(R.id.fullscreenlayout);
        LightOff=findViewById(R.id.offbutton);
        LightOn=findViewById(R.id.onbutton);
        LightOn2=findViewById(R.id.lighton);
        mSettings=findViewById(R.id.settingsbutton);
        SettingScreen=findViewById(R.id.settinglayout);
        mBackButton=findViewById(R.id.backbutton);
        mSaveButtton=findViewById(R.id.savebutton);
        fullScreenTouchSense=findViewById(R.id.FullScreenTouchSense);
        lightOnOnStartUp=findViewById(R.id.LightOnOnStartUp);


        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        Boolean fullscreenOn = mPrefs.getBoolean("fullscreen", true);
        Boolean lighonStartUP = mPrefs.getBoolean("lightonstartup", false);


        final boolean hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.CAMERA},
                CAMERA_REQUEST);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingScreen.setVisibility(View.VISIBLE);
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingScreen.setVisibility(View.GONE);
            }
        });
        mSaveButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putBoolean("fullscreen", fullScreenTouchSense.isChecked()).commit();
                mEditor.putBoolean("lightonstartup", lightOnOnStartUp.isChecked()).commit();
                Toast.makeText(MainActivity.this, "Settings Saved",
                        Toast.LENGTH_SHORT).show();

            }
        });

        FullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPrefs.getBoolean("fullscreen",true)){
                    if (hasCameraFlash) {
                    if (flashLightStatus)
                        flashLightOff();
                    else
                        flashLightOn();
                } else {
                    Toast.makeText(MainActivity.this, "No flash available on your device",
                            Toast.LENGTH_SHORT).show();
                }
                }
            }
        });
        LightOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPrefs.getBoolean("fullscreen",true)){
                    if (hasCameraFlash) {
                        if (flashLightStatus)
                            flashLightOff();
                        else
                            flashLightOn();
                    } else {
                        Toast.makeText(MainActivity.this, "No flash available on your device",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        LightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPrefs.getBoolean("fullscreen",true)){
                    if (hasCameraFlash) {
                        if (flashLightStatus)
                            flashLightOff();
                        else
                            flashLightOn();
                    } else {
                        Toast.makeText(MainActivity.this, "No flash available on your device",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
            LightOn.setVisibility(View.GONE);
            LightOn2.setVisibility(View.VISIBLE);
            LightOff.setVisibility(View.VISIBLE);
            FullScreen.setBackgroundColor(parseColor("#5B6D70"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark, this.getTheme()));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
            }
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
            LightOn2.setVisibility(View.GONE);
            LightOff.setVisibility(View.GONE);
            LightOn.setVisibility(View.VISIBLE);
            FullScreen.setBackgroundColor(parseColor("#9AC8C4"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary, this.getTheme()));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
            }
        } catch (CameraAccessException e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    FullScreen.setEnabled(true);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for the Camera.\nSorry! Without camera, we can't access flashlight.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
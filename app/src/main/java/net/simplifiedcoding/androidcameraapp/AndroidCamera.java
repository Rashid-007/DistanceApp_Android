package net.simplifiedcoding.androidcameraapp;

import java.io.IOException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidCamera extends Activity implements SurfaceHolder.Callback, SensorEventListener {
    SensorManager mSensorManager;
    Sensor magnetSensor;
    Sensor accSensor;
    float[] gravity=new float[3];
    float[] geoMagnetic=new float[3];
    Button captureButton;
    float azimut;
    float pitch;
    float roll;
    double sensorHeight=1.4;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    EditText inputHeight;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        magnetSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //String theText = inputHeight.getText().toString();
       // Intent i = new Intent(getApplicationContext(),CalculateActivity.class);
        //i.putExtra("text_label", theText);
        //startActivity(i);
       // final String text=inputHeight.getText().toString();
        //String text=inputHeight.getText().toString();
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
        captureButton=(Button)findViewById(R.id.capButton);
        inputHeight=(EditText) findViewById(R.id.hh);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorHeight=Double.valueOf(AndroidCamera.this.inputHeight.getText().toString());
                //String text=inputHeight.getText().toString();
                //sensorHeight=Double.parseDouble(text);
                //Intent i = new Intent(getApplicationContext(),CalculateActivity.class);
                //i.putExtra("text_label", theText);
               // startActivity(i);
                // get an image from the camera
                //holding the camera in pitch landscape: use roll value to calculate distance(if portrait then pitch)
                double d = (Math.tan(Math.toRadians(Math.abs(roll))) * sensorHeight);
               // float d = Math.abs((float) (1.4f * Math.tan(pitch * Math.PI / 180)));
                DecimalFormat val = new DecimalFormat("0.00");

                Toast.makeText(
                        getApplicationContext(),
                        "Distance = "
                                + String.valueOf(val.format(d))
                                +" Meters"
                                + "  Angle = "
                                + String.valueOf(Math.toRadians(Math.abs(roll))),
                        Toast.LENGTH_LONG).show();


            }
        });

    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geoMagnetic = event.values.clone();
        if (gravity != null && geoMagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity,geoMagnetic);
            if (success) {
                /* Orientation has azimuth, pitch and roll */
                float orientation[] = new float[3];
                //SensorManager.remapCoordinateSystem(R, 1, 3, orientation);
                SensorManager.getOrientation(R, orientation);
                azimut = 57.29578F * orientation[0];
                pitch = 57.29578F * orientation[1];
                roll = 57.29578F * orientation[2];
            }
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
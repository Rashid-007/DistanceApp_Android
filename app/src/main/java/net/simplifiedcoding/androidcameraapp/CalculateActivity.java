package net.simplifiedcoding.androidcameraapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CalculateActivity extends ActionBarActivity implements SensorEventListener {
    SensorManager mSensorManager;
    Sensor magnetSensor;
    Sensor accSensor;
    float[] gravity=new float[3];
    float[] geoMagnetic=new float[3];
    Button captureButton;
    float azimut;
    float pitch;
    float roll;

    //EditText inputHeight;
    String  uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        magnetSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final Double d;
        //inputHeight=(EditText) findViewById(R.id.hh);
        //String text=inputHeight.getText().toString();
        //sensorHeight=Double.parseDouble(text);


        captureButton=(Button)findViewById(R.id.capButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = getIntent();
                 uriString= i.getStringExtra("text_label");
                Double h=Double.parseDouble(uriString);
                //holding the camera in pitch landscape: use roll value to calculate distance(if portrait then pitch)

                double d = (Math.tan(Math.toRadians(Math.abs(roll))) * h);
                // float d = Math.abs((float) (1.4f * Math.tan(pitch * Math.PI / 180)));
                Toast.makeText(
                        getApplicationContext(),
                        "Distance = "
                                + String.valueOf(d)
                                + "m  Angle = "
                                + String.valueOf(Math.toRadians(Math.abs(roll))),
                        Toast.LENGTH_LONG).show();


            }
        });
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
    public void onAccuracyChanged(Sensor sensor, int i) {

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

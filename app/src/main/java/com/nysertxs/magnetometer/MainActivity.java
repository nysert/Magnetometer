package com.nysertxs.magnetometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sm;
    private Sensor magSensor;
    private Sensor accSensor;

    private CustomGauge gauge;
    private float[] magValues;
    private float[] accValues;
    private TextView magAngleZ;
    private TextView magAngleX;
    private TextView magAngleY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        magAngleZ = (TextView) findViewById(R.id.angleZ);
        magAngleX = (TextView) findViewById(R.id.angleX);
        magAngleY = (TextView) findViewById(R.id.angleY);
        gauge = (CustomGauge) findViewById(R.id.gauge1);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        magSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magValues = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accValues = event.values;
        }

        if (magValues != null && accValues != null){
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R,I,accValues,magValues);
            if (success){
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                double angleZ = Math.toDegrees(orientation[0]);
                angleZ = (angleZ + 360) % 360;//converting from -180,180 to 0,360
                double angleX = Math.toDegrees(orientation[1]);
                double angleY = Math.toDegrees(orientation[2]);
                magAngleZ.setText("z="+String.valueOf(angleZ));
                magAngleX.setText("x=" + String.valueOf(angleX));
                magAngleY.setText("y=" + String.valueOf(angleY));
                gauge.setValue(-(int)angleZ);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

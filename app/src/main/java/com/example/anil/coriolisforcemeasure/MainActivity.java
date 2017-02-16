package com.example.anil.coriolisforcemeasure;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    SensorManager mSensorManager;
    Sensor mAccelerometerSensor;
    Sensor mGyroscopeSensor;


    TextView mForceValueText;
    TextView mXAccValueText;
    TextView mYAccValueText;
    TextView mZAccValueText;
    TextView mXGyrValueText;
    TextView mYGyrValueText;
    TextView mZGyrValueText;

    Context context;

    Button start;
    Button stop;

    File accelerometer_data;
    File gyroscope_data;

    FileOutputStream gyroscope_writer;
    FileOutputStream accelerometer_writer;

    long time;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button)findViewById(R.id.button_start);
        stop = (Button)findViewById(R.id.button_stop) ;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        if (sensors.size() > 0) {
            for (Sensor sensor : sensors) {
                switch (sensor.getType()) {
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        if (mAccelerometerSensor == null) mAccelerometerSensor = sensor;
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        if (mGyroscopeSensor == null) mGyroscopeSensor = sensor;
                        break;
                    default:
                        break;
                }
            }
        }
        context= this;

        mForceValueText = (TextView)findViewById(R.id.value_force);
        mXAccValueText = (TextView)findViewById(R.id.value_x);
        mYAccValueText = (TextView)findViewById(R.id.value_y);
        mZAccValueText = (TextView)findViewById(R.id.value_z);
        mXGyrValueText = (TextView)findViewById(R.id.value_gyr_x);
        mYGyrValueText = (TextView)findViewById(R.id.value_gyr_y);
        mZGyrValueText = (TextView)findViewById(R.id.value_gyr_z);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    time = System.currentTimeMillis() % 1000;
                    mSensorManager.registerListener(MainActivity.this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
                    mSensorManager.registerListener(MainActivity.this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
                }

        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSensorManager.unregisterListener(MainActivity.this);
            }
        });

    }

    @Override
    protected void onPause() {
        //mSensorManager.unregisterListener(this);
        super.onPause();

        if (accelerometer_writer != null) {
            try {
                accelerometer_writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (gyroscope_writer != null) {
            try {
                gyroscope_writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            accelerometer_writer = openFileOutput(System.currentTimeMillis()+"accelerometer_data.txt", Context.MODE_PRIVATE);
            gyroscope_writer = openFileOutput(System.currentTimeMillis()+"gyroscope_data.txt", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //accelerometer_writer = new FileWriter(System.currentTimeMillis()+"accelerometer_data.txt", true);
        //gyroscope_writer = new FileWriter(System.currentTimeMillis()+"gyroscope_data.txt", true);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis() % 1000;
        if (currentTime - time > 0.5) {
            time = currentTime;

            float[] values = event.values;
            switch (event.sensor.getType()) {
                case Sensor.TYPE_LINEAR_ACCELERATION: {
                    mXAccValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_X]));
                    mYAccValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Y]));
                    mZAccValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Z]));

                    try {
                        accelerometer_writer.write((mXAccValueText+","+mYAccValueText+","+mZAccValueText+"\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case Sensor.TYPE_GYROSCOPE: {
                    mXGyrValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_X]));
                    mYGyrValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Y]));
                    mZGyrValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Z]));

                    try {
                        gyroscope_writer.write((mXGyrValueText+","+mYGyrValueText+","+mZGyrValueText+"\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    double CoriolisForce = 0.0f;
                    double mass = 0.145;
                    CoriolisForce += -2 * mass * 1 * values[SensorManager.DATA_Y];
                    mForceValueText.setText(String.format("%1.3f", CoriolisForce));

                    break;
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start.performClick();
                } else {
                    Toast.makeText(context, "Impossible to write data to the file", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


}

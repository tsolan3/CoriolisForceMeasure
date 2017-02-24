package com.example.anil.coriolisforcemeasure;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    TextView mXVelValueText;
    TextView mYVelValueText;
    TextView mZVelValueText;



    Context context;

    Button start;
    Button stop;

    double velocity_x;
    double velocity_y;
    double velocity_z;

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
        mXVelValueText = (TextView)findViewById(R.id.value_vel_x);
        mYVelValueText = (TextView)findViewById(R.id.value_vel_y);
        mZVelValueText = (TextView)findViewById(R.id.value_vel_z);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                time = System.currentTimeMillis();
                mSensorManager.registerListener(MainActivity.this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
                mSensorManager.registerListener(MainActivity.this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);

                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES);
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
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - time > 1000) {


            time = currentTime;
            switch (event.sensor.getType()) {


                case Sensor.TYPE_GYROSCOPE: {
                    mXGyrValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_X]));
                    mYGyrValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Y]));
                    mZGyrValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Z]));

                    double[] ang_velocity = {event.values[SensorManager.DATA_X], event.values[SensorManager.DATA_Y],
                    event.values[SensorManager.DATA_Z]};

                    break;
                }


                case Sensor.TYPE_LINEAR_ACCELERATION: {
                    mXAccValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_X]));
                    mYAccValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Y]));
                    mZAccValueText.setText(String.format("%1.3f",
                            event.values[SensorManager.DATA_Z]));

                    if (event.values[SensorManager.DATA_X] > 0.5 || event.values[SensorManager.DATA_X] < -0.5) {
                        mXVelValueText.setText((String.format("%1.3f",
                                velocity_x += event.values[SensorManager.DATA_X])));
                    }

                    if (event.values[SensorManager.DATA_Y] > 0.5 || event.values[SensorManager.DATA_Y] < -0.5) {
                        mYVelValueText.setText((String.format("%1.3f",
                                velocity_y += event.values[SensorManager.DATA_Y])));
                    }

                    if (event.values[SensorManager.DATA_Z] > 0.5 || event.values[SensorManager.DATA_Z] < -0.5) {
                        mZVelValueText.setText((String.format("%1.3f",
                                velocity_x += event.values[SensorManager.DATA_Y])));
                    }
                    double[] velocity = {velocity_x, velocity_y, velocity_z};

                    break;
                }

            }
        }

            }


    private void writeToFile(String data,int time, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(time+"gyroscope_data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
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

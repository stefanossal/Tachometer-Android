package com.example.tachometer_ergasia1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity implements LocationListener,View.OnClickListener,SensorEventListener {
    LocationManager locationManager;
    static final int REQ_CODE = 654;
    TTS TTS;
    SharedPreferences preferences;
    SQLiteDatabase db;
    private SensorManager sensorManager;
    private Sensor steps;
    private Sensor steps2;
    Gauge gauge;
    private int stepCounter = 0;
    private TextView textview,textview5;
    int maxspeed;
    int curValue;
    //Μετρητές για το όριο ταχύτητας
    int sl1=0;
    int sl2=0;
    //Μετρητές για τα results από το SpeechRecognition
    int t=0;
    int k=0;
    int r=0;
    int m=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        TTS = new TTS(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        steps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        steps2 = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        textview = (TextView) findViewById(R.id.textView);
        textview5 = (TextView) findViewById(R.id.textView5);
        gauge = (Gauge) findViewById(R.id.gauge);
        curValue = 0;
        gauge.setValue(curValue);
        textview.setText("Steps:0");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = openOrCreateDatabase("SpeedRecords",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Records( longitudex DOUBLE, latitudey DOUBLE ,speedlimit INTEGER, timestamp TEXT);");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, steps, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, steps2, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            stepCounter = stepCounter + 1;
            textview.setText("Steps:" + String.valueOf(stepCounter));
        }
        else if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            textview5.setText("TotalSteps:" + String.valueOf(event.values[0]));
        }
    }

    @Override
    //Κουμπί Stop
    public void onClick(View v) {
        TextView text1 = (TextView) findViewById(R.id.textView1);
        if(v.getId() == R.id.button2){
            onPause();
            Toast.makeText(getApplicationContext(),"GPS stopped",Toast.LENGTH_SHORT).show();
            TTS.speak("GPS stopped");
            text1.setText("Speed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Κουμπί Start
    public void gpson(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(
                    this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQ_CODE);
        }else
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, this);

        Toast.makeText(getApplicationContext(),"Opening GPS",Toast.LENGTH_SHORT).show();
        TTS.speak("Opening GPS");
    }


    @Override
    //Υπολογισμός ταχύτητας,χρόνου,γεωγραφικού μήκους,γεωγραφικού πλάτους και εισαγωγή στη βάση δεδομένων
    public void onLocationChanged(Location location) {
        TextView text1 = (TextView) findViewById(R.id.textView1);
        maxspeed = preferences.getInt("speedlimit1",0);
        if (location == null){
            Toast.makeText(getApplicationContext(),"Can't find the location",Toast.LENGTH_SHORT).show();
        }
        else{
            int speed=(int) ((location.getSpeed()*3600)/1000);
            String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(location.getTime());
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            text1.setText(speed + "km/h");
            curValue = speed;
            gauge.moveToValue(curValue);
            if(speed >= 60 && speed <= 80){
                text1.setTextColor(Color.rgb(232,97,14));
            }
            else if(speed >= 80){
                text1.setTextColor(Color.rgb(214,35,26));
            }
            else {
                text1.setTextColor(Color.BLACK);
            }
            //Έλεγχος αν η μεταβλητή maxspeed από το shared preferences είναι μεγαλύτερη του 0
            // και διάφορη του 80 που είναι το default όριο ταχύτητας
            if((maxspeed != 80) && (maxspeed > 0)){
                if(speed >= maxspeed){
                    sl1=sl1+1;
                    if(sl1==1){
                        TTS.speak("You are going too fast.Please slow down");
                        showMessage("Serious Message","You are going too fast.Please slow down");
                        db.execSQL("INSERT INTO Records values" + "('"+ longitude +"','"+ latitude +"','"+ speed +"','" + time +"');");
                        Toast.makeText(this,"Inserted into database",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    sl1=0;
                }
            }
            else {
                if(speed >= 80){
                    sl2=sl2+1;
                    if(sl2==1){
                        TTS.speak("You are going too fast.Please slow down");
                        showMessage("Serious Message","You are going too fast.Please slow down");
                        db.execSQL("INSERT INTO Records values" + "('"+ longitude +"','"+ latitude +"','"+ speed +"','" + time +"');");
                        Toast.makeText(this,"Inserted into database",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    sl2=0;
                }
            }

        }
    }

    //Alert Dialog
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setPositiveButton("OK",null);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(237,9,9)));
    }

    //SpeechRecognition results
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==742 && resultCode==RESULT_OK){
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            StringBuffer buffer = new StringBuffer();
            for (String s :
                    results) {
                buffer.append(s.toLowerCase() + "\n");
                if(s.contains("records") || s.contains("recordings") || s.contains("open records") || s.contains("open recordings")){
                    r=r+1;
                    if(r==1){
                        SelectData();
                        Toast.makeText(this,"Opening records",Toast.LENGTH_SHORT).show();
                        Intent intent6 = new Intent(this,Records.class);
                        startActivity(intent6);
                    }
                }
                else if(s.contains("maps") || s.contains("google maps") || s.contains("map") || s.contains("open maps") || s.contains("open google maps") || s.contains("open map")){
                    m=m+1;
                    if(m==1){
                        Toast.makeText(this,"Opening google maps",Toast.LENGTH_SHORT).show();
                        Intent intent4 = new Intent(this,MapsActivity.class);
                        startActivity(intent4);
                    }
                }
                else if(s.contains("settings") || s.contains("open settings")){
                    k=k+1;
                    if(k==1){
                        Toast.makeText(this,"Opening settings",Toast.LENGTH_SHORT).show();
                        Intent intent5 = new Intent(this,Main2Activity.class);
                        startActivity(intent5);
                    }
                }
                else if(s.contains("how many steps i have done") || s.contains("what's my steps") || s.contains("steps") || s.contains("tell my steps") || s.contains("how many steps i have today")){
                    t=t+1;
                    if(t==1){
                        TTS.speak("Today you have done" + stepCounter + "steps");
                    }

                }
            }
            r=0;
            m=0;
            k=0;
            t=0;
        }
    }

    //Κουμπί SpeechRecognition
    public void go(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please give me an order!");
        startActivityForResult(intent,742);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Προσθήκη των δεδομένων στο Records
    public void SelectData(){
        Cursor cursor = db.rawQuery("SELECT * FROM Records",null);
        if (cursor.getCount()==0)
            Toast.makeText(this,"No records found",Toast.LENGTH_LONG).show();
        else {
            StringBuffer buffer = new StringBuffer();
            while (cursor.moveToNext()){
                buffer.append(cursor.getDouble(0));
                buffer.append(",");
                buffer.append(cursor.getDouble(1));
                buffer.append(",");
                buffer.append(cursor.getInt(2));
                buffer.append(",");
                buffer.append(cursor.getString(3));
                buffer.append("\n");;
            }
            String data = buffer.toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("data1",data);
            editor.commit();
        }
    }

    @Override
    //Μενού
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item1){
            SelectData();
            Intent intent3 = new Intent(this,Records.class);
            startActivity(intent3);
        }
        else if(item.getItemId() == R.id.item2){
            Intent intent2 = new Intent(this,MapsActivity.class);
            startActivity(intent2);
        }
        else if(item.getItemId() == R.id.item3){
            Intent intent1 = new Intent(this,Main2Activity.class);
            startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.tachometer_ergasia1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Settings");
    }

    //Κουμπί Save για τη τιμή του ορίου ταχύτητας με SharedPreferences
    public void SaveBtn(View view){
        EditText editText = findViewById(R.id.editText);
        String edittext = editText.getText().toString();
        int edittextint = Integer.parseInt(edittext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("speedlimit1",edittextint);
        editor.commit();
        Toast.makeText(this,"Το όριο ταχύτητας άλλαξε σε " + editText.getText().toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

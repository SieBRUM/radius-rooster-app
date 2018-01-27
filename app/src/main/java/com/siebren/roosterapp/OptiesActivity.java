package com.siebren.roosterapp;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class OptiesActivity extends AppCompatActivity {
    EditText user;
    EditText pass;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String temp;
    RadioGroup rg;
    int selectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opties);
        spinner = (Spinner)findViewById(R.id.spinnerKlas);
        adapter = ArrayAdapter.createFromResource(this, R.array.klas,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        rg = (RadioGroup)findViewById(R.id.radioGroupWeek);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp = ""+ position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId){
                selectedButton = checkedId;
            }
        });

        user = (EditText)findViewById(R.id.editUsername);
        pass = (EditText)findViewById(R.id.editPassword);
        saveInfo();
        LoadPrefs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_opties, menu);
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

    private void SavePrefsUser(String key, String value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private void saveInfo () {
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String temp1;
                String temp2;

                temp1 = user.getText().toString();
                temp2 = pass.getText().toString();
                if (temp1.equals("") || temp2.equals("") ){
                    Toast.makeText(OptiesActivity.this, "Geen gegevens ingevult!", Toast.LENGTH_SHORT).show();
                }else {
                    SavePrefsUser("USER", user.getText().toString());
                    SavePrefsUser("PASS", pass.getText().toString());
                    SavePrefsUser("KLAS", temp);
                    SavePrefsUser("WEEK", Integer.toString(selectedButton));
                    LoadPrefs();
                    Toast.makeText(OptiesActivity.this, "Gegevens succesvol opgeslagen!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        });
    }

    private void LoadPrefs(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sp.getString("USER", "");
        String password = sp.getString("PASS", "");
        String klas = sp.getString("KLAS","");
        String radiobutton = sp.getString("WEEK","");
        user.setText(username);
        pass.setText(password);
        if(!klas.trim().isEmpty()) {
            if(spinner.getAdapter().getCount() < Integer.parseInt(klas.trim())) {
                klas = "1";
            }
            spinner.setSelection(Integer.parseInt(klas));
        }

        if(!radiobutton.equals("")) {
            rg.check(Integer.parseInt(radiobutton));
        }
    }
}


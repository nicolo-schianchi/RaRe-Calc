package racepredictioncalculator.com;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;
import java.util.List;

import static racepredictioncalculator.com.RaceToRace.INTENT_BOOL_RACE;
import static racepredictioncalculator.com.Vo2Max.INTENT_BOOL;

public class HeatAdjustment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ConstraintLayout layout;

    private Spinner spinner_temp_unit;
    private boolean isFahrenheit;

    private TextInputLayout temp_et, dew_point_et, hh_et_heat, mm_et_heat, ss_et_heat;
    private Button calculate_heat;
    private TextView projected_equivalent_tv_heat;

    private BottomNavigationView bottomNavigationView;


    public static final String INTENT_BOOL_HEAT = "INTENT_BOOL_HEAT";
    public static final String RESULT_HEAT = "RESULT_HEAT";

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.racepredictioncalculator";

    private boolean isIntentPassed;
    private String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_adjustment);


        Log.e("CHECK - HEAT", "onCreate() - Heat");

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.about);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        isIntentPassed = true;
                        result = "T" + temp_et.getEditText().getText().toString() + "U" + spinner_temp_unit.getSelectedItemPosition() + "D" + dew_point_et.getEditText().getText().toString() + "H" + hh_et_heat.getEditText().getText().toString() + "M" + mm_et_heat.getEditText().getText().toString() + "S" + ss_et_heat.getEditText().getText().toString() + result;
                        startActivity(new Intent(getApplicationContext(), Vo2Max.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        return true;
                    case R.id.home:
                        isIntentPassed = true;
                        result = "T" + temp_et.getEditText().getText().toString() + "U" + spinner_temp_unit.getSelectedItemPosition() + "D" + dew_point_et.getEditText().getText().toString() + "H" + hh_et_heat.getEditText().getText().toString() + "M" + mm_et_heat.getEditText().getText().toString() + "S" + ss_et_heat.getEditText().getText().toString() + result;
                        startActivity(new Intent(getApplicationContext(), RaceToRace.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        return true;
                    case R.id.about:
                        return true;
                }
                return false;
            }
        });

        layout = findViewById(R.id.heat_layout);

        spinner_temp_unit = findViewById(R.id.spinner_temp_unit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.temp_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_temp_unit.setAdapter(adapter);
        spinner_temp_unit.setOnItemSelectedListener(this);

        isFahrenheit = true;

        temp_et = findViewById(R.id.temp_et_parent);
        dew_point_et = findViewById(R.id.dew_point_et_parent);
        hh_et_heat = findViewById(R.id.hh_et_heat_parent);
        mm_et_heat = findViewById(R.id.mm_et_heat_parent);
        ss_et_heat = findViewById(R.id.ss_et_heat_parent);

        calculate_heat = findViewById(R.id.calculate_heat);

        projected_equivalent_tv_heat = findViewById(R.id.projected_equivalent_tv_heat);


        result = "";


        calculate_heat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int heat_hours = -1;
                int heat_minutes = -1;
                double heat_seconds = -1;
                double heat_temperature = -1;
                double heat_dew_point = -1;

                boolean error = false;

                if (temp_et.getEditText().getText().toString().equals("")) {
                    temp_et.setError("Field is incomplete");
                    error = true;
                } else {
                    temp_et.setError("");
                    temp_et.setHelperText("*Required");
                }

                if (!ss_et_heat.getEditText().getText().toString().equals("") && Double.parseDouble(ss_et_heat.getEditText().getText().toString()) >= 60) {
                    ss_et_heat.setError("Below 60");
                    error = true;
                } else {
                    ss_et_heat.setError("");
                }

                if (!mm_et_heat.getEditText().getText().toString().equals("") && Integer.parseInt(mm_et_heat.getEditText().getText().toString()) >= 60) {
                    mm_et_heat.setError("Below 60");
                    error = true;
                } else {
                    mm_et_heat.setError("");
                }


                if (!error) {
                    boolean tempIsOk = true;


                    if (hh_et_heat.getEditText().getText().toString().equals("")) {
                        hh_et_heat.getEditText().setText("00");
                    }

                    if (mm_et_heat.getEditText().getText().toString().equals("")) {
                        mm_et_heat.getEditText().setText("00");
                    }

                    if (ss_et_heat.getEditText().getText().toString().equals("")) {
                        ss_et_heat.getEditText().setText("00");
                    }

                    if (isFahrenheit) {
                        heat_temperature = Double.parseDouble(temp_et.getEditText().getText().toString());

                        if (dew_point_et.getEditText().getText().toString().equals("")) {
                            if (heat_temperature < 0) {
                                heat_dew_point = heat_temperature;
                                dew_point_et.getEditText().setText("" + heat_dew_point);
                            } else {
                                heat_dew_point = 0;
                                dew_point_et.getEditText().setText("0");
                            }
                        } else {
                            heat_dew_point = Double.parseDouble(dew_point_et.getEditText().getText().toString());
                        }

                        if (heat_temperature + heat_dew_point > 250) {
                            temp_et.setError("Temperature + dew point cannot be above 250 °F");
                            dew_point_et.setError("Temperature + dew point cannot be above 250 °F");
                            tempIsOk = false;
                        } else if (heat_temperature + heat_dew_point < 50) {
                            temp_et.setError("Temperature + dew point cannot be below 50 °F");
                            dew_point_et.setError("Temperature + dew point cannot be below 50 °F");
                            tempIsOk = false;
                        } else if (heat_temperature < heat_dew_point) {
                            temp_et.setError("");
                            temp_et.setHelperText("*Required");
                            dew_point_et.setError("Dew point cannot be above temperature");
                            tempIsOk = false;
                        } else {
                            temp_et.setError("");
                            dew_point_et.setError("");
                        }

                    } else {
                        heat_temperature = (Double.parseDouble(temp_et.getEditText().getText().toString()));

                        if (dew_point_et.getEditText().getText().toString().equals("")) {
                            if (heat_temperature < 0) {
                                heat_dew_point = heat_temperature;
                                dew_point_et.getEditText().setText("" + heat_dew_point);
                            } else {
                                heat_dew_point = 0;
                                dew_point_et.getEditText().setText("0");
                            }
                        } else {
                            heat_dew_point = Double.parseDouble(dew_point_et.getEditText().getText().toString());
                        }

                        if (heat_temperature + heat_dew_point > 120) {
                            temp_et.setError("Temperature + dew point cannot be above 120 °C");
                            dew_point_et.setError("Temperature + dew point cannot be above 120 °C");
                            tempIsOk = false;
                        } else if (heat_temperature + heat_dew_point < 10) {
                            temp_et.setError("Temperature + dew point cannot be below 10 °C");
                            dew_point_et.setError("Temperature + dew point cannot be below 10 °C");
                            tempIsOk = false;
                        } else if (heat_temperature < heat_dew_point) {
                            temp_et.setError("");
                            temp_et.setHelperText("*Required");
                            dew_point_et.setError("Dew point cannot be above temperature");
                            tempIsOk = false;
                        } else {
                            temp_et.setError("");
                            dew_point_et.setError("");
                            heat_temperature = ((heat_temperature) * 1.8) + 32;
                            heat_dew_point = (heat_dew_point * 1.8) + 32;
                        }
                    }

                    if (tempIsOk) {
                        result = "";
                        heat_hours = Integer.parseInt(hh_et_heat.getEditText().getText().toString());
                        heat_minutes = Integer.parseInt(mm_et_heat.getEditText().getText().toString());
                        heat_seconds = Double.parseDouble(ss_et_heat.getEditText().getText().toString());

                        double total_minutes = 60 * heat_hours + heat_minutes + heat_seconds / 60;

                        String projected_time = "";
                        String low = getLowTimeString(total_minutes, heat_temperature, heat_dew_point);
                        String high = getHighTimeString(total_minutes, heat_temperature, heat_dew_point);

                        if (low.equals(high)) {
                            projected_time = "Projected equivalent with perfect temperature is " + low;
                        } else {
                            projected_time = "Projected equivalent with perfect temperature is between " + low + " and " + high;
                        }

                        projected_equivalent_tv_heat.setText(projected_time);
                        result = projected_time;

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(layout.getApplicationWindowToken(), 0);
                    }
                }
            }
        });


    }

    private String getLowTimeString(double total_minutes, double heat_temperature, double heat_dew_point) {
        double totalTime = total_minutes;

        if (heat_temperature + heat_dew_point >= 100 && heat_temperature + heat_dew_point < 110) {
            totalTime *= 0.9950248756;
        } else if (heat_temperature + heat_dew_point >= 110 && heat_temperature + heat_dew_point < 120) {
            totalTime *= 0.9900990099;
        } else if (heat_temperature + heat_dew_point >= 120 && heat_temperature + heat_dew_point < 130) {
            totalTime *= 0.9803921569;
        } else if (heat_temperature + heat_dew_point >= 130 && heat_temperature + heat_dew_point < 140) {
            totalTime *= 0.9708737864;
        } else if (heat_temperature + heat_dew_point >= 140 && heat_temperature + heat_dew_point < 150) {
            totalTime *= 0.956937799;
        } else if (heat_temperature + heat_dew_point >= 150 && heat_temperature + heat_dew_point < 160) {
            totalTime *= 0.9433962264;
        } else if (heat_temperature + heat_dew_point >= 160 && heat_temperature + heat_dew_point < 170) {
            totalTime *= 0.9259259259;
        } else if (heat_temperature + heat_dew_point >= 170 && heat_temperature + heat_dew_point < 180) {
            totalTime *= 0.9090909091;
        } else if (heat_temperature + heat_dew_point >= 180 && heat_temperature + heat_dew_point < 190) {
            totalTime *= 0.8888888889;
            Toast.makeText(getApplicationContext(), "It isn't recommended to run hard in these conditions", Toast.LENGTH_LONG).show();
        } else if (heat_temperature + heat_dew_point >= 190 && heat_temperature + heat_dew_point < 200) {
            totalTime *= 0.8695652174;
            Toast.makeText(getApplicationContext(), "It isn't recommended to run hard in these conditions", Toast.LENGTH_LONG).show();
        } else if (heat_temperature + heat_dew_point >= 200 && heat_temperature + heat_dew_point < 210) {
            totalTime *= 0.8474576271;
            Toast.makeText(getApplicationContext(), "It isn't recommended to run hard in these conditions", Toast.LENGTH_LONG).show();
        } else if (heat_temperature + heat_dew_point >= 210 && heat_temperature + heat_dew_point < 220) {
            totalTime *= 0.826446281;
            Toast.makeText(getApplicationContext(), "It isn't recommended to run hard in these conditions", Toast.LENGTH_LONG).show();
        } else if (heat_temperature + heat_dew_point >= 220 && heat_temperature + heat_dew_point < 230) {
            totalTime *= 0.8032128514;
            Toast.makeText(getApplicationContext(), "It isn't recommended to run hard in these conditions", Toast.LENGTH_LONG).show();
        } else if (heat_temperature + heat_dew_point >= 230 && heat_temperature + heat_dew_point < 240) {
            totalTime *= 0.78125;
            Toast.makeText(getApplicationContext(), "It isn't recommended to run hard in these conditions", Toast.LENGTH_LONG).show();
        } else if (heat_temperature + heat_dew_point >= 240/* && heat_temperature+heat_dew_point<=250*/) {
            totalTime *= 0.757575757576;
            Toast.makeText(getApplicationContext(), "It isn't recommended to run hard in these conditions", Toast.LENGTH_LONG).show();
        }

        int hours = calculateHrs(totalTime);
        int minutes = calculateMins(totalTime);
        int seconds = calculateSec(totalTime);

        String formattedTime = getFormattedTime(hours, minutes, seconds);

        return formattedTime;
    }

    private String getHighTimeString(double total_minutes, double heat_temperature, double heat_dew_point) {
        double totalTime = total_minutes;

        if (heat_temperature + heat_dew_point >= 110 && heat_temperature + heat_dew_point < 120) {
            totalTime *= 0.9950248756;
        } else if (heat_temperature + heat_dew_point >= 120 && heat_temperature + heat_dew_point < 130) {
            totalTime *= 0.9900990099;
        } else if (heat_temperature + heat_dew_point >= 130 && heat_temperature + heat_dew_point < 140) {
            totalTime *= 0.9803921569;
        } else if (heat_temperature + heat_dew_point >= 140 && heat_temperature + heat_dew_point < 150) {
            totalTime *= 0.9708737864;
        } else if (heat_temperature + heat_dew_point >= 150 && heat_temperature + heat_dew_point < 160) {
            totalTime *= 0.956937799;
        } else if (heat_temperature + heat_dew_point >= 160 && heat_temperature + heat_dew_point < 170) {
            totalTime *= 0.9433962264;
        } else if (heat_temperature + heat_dew_point >= 170 && heat_temperature + heat_dew_point < 180) {
            totalTime *= 0.9259259259;
        } else if (heat_temperature + heat_dew_point >= 180 && heat_temperature + heat_dew_point < 190) {
            totalTime *= 0.9090909091;
        } else if (heat_temperature + heat_dew_point >= 190 && heat_temperature + heat_dew_point < 200) {
            totalTime *= 0.8888888889;
        } else if (heat_temperature + heat_dew_point >= 200 && heat_temperature + heat_dew_point < 210) {
            totalTime *= 0.8695652174;
        } else if (heat_temperature + heat_dew_point >= 210 && heat_temperature + heat_dew_point < 220) {
            totalTime *= 0.8474576271;
        } else if (heat_temperature + heat_dew_point >= 220 && heat_temperature + heat_dew_point < 230) {
            totalTime *= 0.826446281;
        } else if (heat_temperature + heat_dew_point >= 230 && heat_temperature + heat_dew_point < 240) {
            totalTime *= 0.8032128514;
        } else if (heat_temperature + heat_dew_point >= 240 /*&& heat_temperature+heat_dew_point<=250*/) {
            totalTime *= 0.78125;
        }

        int hours = calculateHrs(totalTime);
        int minutes = calculateMins(totalTime);
        int seconds = calculateSec(totalTime);

        String formattedTime = getFormattedTime(hours, minutes, seconds);

        return formattedTime;
    }

    private int calculateHrs(double totalTime) {
        int minsAsInt = (int) totalTime;
        int hours = minsAsInt / 60;
        return hours;
    }

    private int calculateMins(double totalTime) {
        int minsAsInt = (int) totalTime;
        int minutes = minsAsInt % 60;
        return minutes;
    }

    private int calculateSec(double totalTime) {
        int seconds = (int) (((totalTime + 0.00833333333333) % 1) * 60);
        return seconds;
    }

    private String getFormattedTime(int hours, int minutes, int seconds) {
        String hoursStr = "" + hours;
        String minutesStr;
        if (minutes < 10) {
            minutesStr = "0" + minutes;
        } else {
            minutesStr = "" + minutes;
        }
        String secondsStr;
        if (seconds < 10) {
            secondsStr = "0" + seconds;
        } else {
            secondsStr = "" + seconds;
        }

        return hoursStr + ":" + minutesStr + ":" + secondsStr;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        isFahrenheit = position == 0;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.e("CHECK - HEAT", "onStart() - Heat");
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.about);

        Log.e("CHECK - HEAT", "onResume() - Heat");

        if (mPreferences.getBoolean(INTENT_BOOL, false) || mPreferences.getBoolean(INTENT_BOOL_RACE, false)) {
            Log.e("CHECK - RACEACT", "WE ARE IN!");


            String mResult = mPreferences.getString(RESULT_HEAT, "");
            if (mResult.contains("Projected") && mResult.contains("U")) {
                result = mResult.substring(mResult.indexOf("Projected"));

                temp_et.getEditText().setText(mResult.substring(1, mResult.indexOf("U")));
                spinner_temp_unit.setSelection(Integer.parseInt(mResult.substring(mResult.indexOf("U") + 1, mResult.indexOf("D"))));
                dew_point_et.getEditText().setText(mResult.substring(mResult.indexOf("D") + 1, mResult.indexOf("H")));
                hh_et_heat.getEditText().setText(mResult.substring(mResult.indexOf("H") + 1, mResult.indexOf("M")));
                mm_et_heat.getEditText().setText(mResult.substring(mResult.indexOf("M") + 1, mResult.indexOf("S")));
                ss_et_heat.getEditText().setText(mResult.substring(mResult.indexOf("S") + 1, mResult.indexOf("Projected")));
                projected_equivalent_tv_heat.setText(mResult.substring(mResult.indexOf("Projected")));
            } else if (!mResult.equals("")) {
                temp_et.getEditText().setText(mResult.substring(1, mResult.indexOf("U")));
                spinner_temp_unit.setSelection(Integer.parseInt(mResult.substring(mResult.indexOf("U") + 1, mResult.indexOf("D"))));
                dew_point_et.getEditText().setText(mResult.substring(mResult.indexOf("D") + 1, mResult.indexOf("H")));
                hh_et_heat.getEditText().setText(mResult.substring(mResult.indexOf("H") + 1, mResult.indexOf("M")));
                mm_et_heat.getEditText().setText(mResult.substring(mResult.indexOf("M") + 1, mResult.indexOf("S")));
                ss_et_heat.getEditText().setText(mResult.substring(mResult.indexOf("S") + 1));
            }

        }


        SharedPreferences.Editor preferenceEditor = mPreferences.edit();
        preferenceEditor.putBoolean(INTENT_BOOL, false);
        preferenceEditor.putBoolean(INTENT_BOOL_RACE, false);
        preferenceEditor.apply();
    }


    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();

        Log.e("CHECK - RACEACT", "onPause() - Heat");


        preferencesEditor.putBoolean(INTENT_BOOL_HEAT, isIntentPassed);
        preferencesEditor.putString(RESULT_HEAT, result);

        preferencesEditor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("CHECK - HEAT", "onStop() - Heat");

        boolean ret = false;
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            String packageName = getApplication().getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    ret = true;
                }
            }
        }

        if (!ret) {
            SharedPreferences.Editor preferenceEditor = mPreferences.edit();
            preferenceEditor.clear();
            preferenceEditor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("CHECK - HEAT", "onDestroy() - Heat");
    }

}
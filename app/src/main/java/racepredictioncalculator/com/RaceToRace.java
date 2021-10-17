package racepredictioncalculator.com;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import static racepredictioncalculator.com.HeatAdjustment.INTENT_BOOL_HEAT;
import static racepredictioncalculator.com.Vo2Max.INTENT_BOOL;

public class RaceToRace extends AppCompatActivity {


    public static final String INTENT_BOOL_RACE = "INTENT_BOOL_RACE";
    public static final String RESULT_RACE = "RESULT_RACE";


    private ConstraintLayout layout;
    private TextInputLayout distance_et_race, hh_et_race, mm_et_race, ss_et_race, custom_distance_et_race;
    private Button calculate_race;
    private TextView five_km_projected_time_tv_race, ten_km_projected_time_tv_race, half_marathon_projected_time_tv_race, marathon_projected_time_tv_race, custom_distance_projected_time_tv_race;

    private BottomNavigationView bottomNavigationView;

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.racepredictioncalculator";


    private boolean isIntentPassed;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_to_race);

        Log.e("CHECK - RACEACT", "onCreate() - Race");


        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        isIntentPassed = true;
                        result = "D" + distance_et_race.getEditText().getText().toString() + "H" + hh_et_race.getEditText().getText().toString() + "M" + mm_et_race.getEditText().getText().toString() + "S" + ss_et_race.getEditText().getText().toString() + "C" + custom_distance_et_race.getEditText().getText().toString() + result;
                        startActivity(new Intent(getApplicationContext(), Vo2Max.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.about:
                        isIntentPassed = true;
                        result = "D" + distance_et_race.getEditText().getText().toString() + "H" + hh_et_race.getEditText().getText().toString() + "M" + mm_et_race.getEditText().getText().toString() + "S" + ss_et_race.getEditText().getText().toString() + "C" + custom_distance_et_race.getEditText().getText().toString() + result;
                        startActivity(new Intent(getApplicationContext(), HeatAdjustment.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        return true;
                }
                return false;
            }
        });


        layout = findViewById(R.id.race_layout);

        distance_et_race = findViewById(R.id.distance_et_parent);
        hh_et_race = findViewById(R.id.hh_et_parent);
        mm_et_race = findViewById(R.id.mm_et_parent);
        ss_et_race = findViewById(R.id.ss_et_parent);
        custom_distance_et_race = findViewById(R.id.custom_distance_et_parent);

        calculate_race = findViewById(R.id.calculate_race);

        five_km_projected_time_tv_race = findViewById(R.id.five_km_projected_time_tv_race);
        ten_km_projected_time_tv_race = findViewById(R.id.ten_km_projected_time_tv_race);
        half_marathon_projected_time_tv_race = findViewById(R.id.half_marathon_projected_time_tv_race);
        marathon_projected_time_tv_race = findViewById(R.id.marathon_projected_time_tv_race);
        custom_distance_projected_time_tv_race = findViewById(R.id.custom_distance_projected_time_tv_race);

        result = "";

        calculate_race.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    private String get5KmString(double race_distance, int race_hours, int race_minutes, double race_seconds) {
        double total5KmTime = (60 * race_hours + race_minutes + race_seconds / 60) * Math.pow(5000 / race_distance, 1.06);
        int hoursOf5Km = calculateHrs(total5KmTime);
        int minutesOf5Km = calculateMins(total5KmTime);
        int secondsOf5Km = calculateSec(total5KmTime);

        String formattedTime = getFormattedTime(hoursOf5Km, minutesOf5Km, secondsOf5Km);

        return formattedTime;
    }

    private String get10KmString(double race_distance, int race_hours, int race_minutes, double race_seconds) {
        double total10KmTime = (60 * race_hours + race_minutes + race_seconds / 60) * Math.pow(10000 / race_distance, 1.06);
        int hoursOf10Km = calculateHrs(total10KmTime);
        int minutesOf10Km = calculateMins(total10KmTime);
        int secondsOf10Km = calculateSec(total10KmTime);

        String formattedTime = getFormattedTime(hoursOf10Km, minutesOf10Km, secondsOf10Km);

        return formattedTime;
    }

    private String getHalfMarathonString(double race_distance, int race_hours, int race_minutes, double race_seconds) {
        double totalHalfMarathonTime = (60 * race_hours + race_minutes + race_seconds / 60) * Math.pow(21097.5 / race_distance, 1.06);
        int hoursOfHalfMarathon = calculateHrs(totalHalfMarathonTime);
        int minutesOfHalfMarathon = calculateMins(totalHalfMarathonTime);
        int secondsOfHalfMarathon = calculateSec(totalHalfMarathonTime);

        String formattedTime = getFormattedTime(hoursOfHalfMarathon, minutesOfHalfMarathon, secondsOfHalfMarathon);

        return formattedTime;
    }

    private String getMarathonString(double race_distance, int race_hours, int race_minutes, double race_seconds) {
        double totalMarathonTime = (60 * race_hours + race_minutes + race_seconds / 60) * Math.pow(42195 / race_distance, 1.06);
        int hoursOfMarathon = calculateHrs(totalMarathonTime);
        int minutesOfMarathon = calculateMins(totalMarathonTime);
        int secondsOfMarathon = calculateSec(totalMarathonTime);

        String formattedTime = getFormattedTime(hoursOfMarathon, minutesOfMarathon, secondsOfMarathon);

        return formattedTime;
    }

    private String getCustomString(double race_distance, int race_hours, int race_minutes, double race_seconds, double custom_distance) {
        double totalCustomDistanceTime = (60 * race_hours + race_minutes + race_seconds / 60) * Math.pow(custom_distance / race_distance, 1.06);
        int hoursOfCustomDistance = calculateHrs(totalCustomDistanceTime);
        int minutesOfCustomDistance = calculateMins(totalCustomDistanceTime);
        int secondsOfCustomDistance = calculateSec(totalCustomDistanceTime);

        String formattedTime = getFormattedTime(hoursOfCustomDistance, minutesOfCustomDistance, secondsOfCustomDistance);

        if (totalCustomDistanceTime < 3.5 || totalCustomDistanceTime > 240) {
            formattedTime = "≈" + formattedTime;
        }

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
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();

        Log.e("CHECK - RACEACT", "onPause() - Race");


        preferencesEditor.putBoolean(INTENT_BOOL_RACE, isIntentPassed);
        preferencesEditor.putString(RESULT_RACE, result);

        preferencesEditor.apply();
    }


    private void calculate() {
        double race_distance = -1;
        int race_hours = -1;
        int race_minutes = -1;
        double race_seconds = -1;

        boolean error = false;

        if (distance_et_race.getEditText().getText().toString().equals("")) {
            distance_et_race.setError("Field is incomplete");
            error = true;
        } else {
            distance_et_race.setError("");
            distance_et_race.setHelperText("*Required");
        }

        if (!ss_et_race.getEditText().getText().toString().equals("") && Double.parseDouble(ss_et_race.getEditText().getText().toString()) >= 60) {
            ss_et_race.setError("Below 60");
            error = true;
        } else {
            ss_et_race.setError("");
        }

        if (!mm_et_race.getEditText().getText().toString().equals("") && Integer.parseInt(mm_et_race.getEditText().getText().toString()) >= 60) {
            mm_et_race.setError("Below 60");
            error = true;
        } else {
            mm_et_race.setError("");
        }


        if (!error) {


            result = "";

            if (hh_et_race.getEditText().getText().toString().equals("")) {
                hh_et_race.getEditText().setText("00");
            }

            if (mm_et_race.getEditText().getText().toString().equals("")) {
                mm_et_race.getEditText().setText("00");
            }

            if (ss_et_race.getEditText().getText().toString().equals("")) {
                ss_et_race.getEditText().setText("00");
            }


            race_distance = Double.parseDouble(distance_et_race.getEditText().getText().toString());
            race_hours = Integer.parseInt(hh_et_race.getEditText().getText().toString());
            race_minutes = Integer.parseInt(mm_et_race.getEditText().getText().toString());
            race_seconds = Double.parseDouble(ss_et_race.getEditText().getText().toString());


            String projected5KmTime = "5km - " + get5KmString(race_distance, race_hours, race_minutes, race_seconds);
            five_km_projected_time_tv_race.setText(projected5KmTime);

            String projected10KmTime = "10km - " + get10KmString(race_distance, race_hours, race_minutes, race_seconds);
            ten_km_projected_time_tv_race.setText(projected10KmTime);

            String projectedHalfMarathonTime = "Half Marathon - " + getHalfMarathonString(race_distance, race_hours, race_minutes, race_seconds);
            half_marathon_projected_time_tv_race.setText(projectedHalfMarathonTime);

            String projectedMarathonTime = "Marathon - " + getMarathonString(race_distance, race_hours, race_minutes, race_seconds);
            marathon_projected_time_tv_race.setText(projectedMarathonTime);

            if (!custom_distance_et_race.getEditText().getText().toString().equals("")) {
                if (Double.parseDouble(custom_distance_et_race.getEditText().getText().toString()) < 0) {
                    custom_distance_et_race.setError("Custom distance must be larger than 0");
                    result = projected5KmTime + projected10KmTime + projectedHalfMarathonTime + projectedMarathonTime;
                } else {
                    String projectedCustomTime = "Custom Distance - " + getCustomString(race_distance, race_hours, race_minutes, race_seconds, Double.parseDouble(custom_distance_et_race.getEditText().getText().toString()));
                    custom_distance_projected_time_tv_race.setText(projectedCustomTime);
                    result = projected5KmTime + projected10KmTime + projectedHalfMarathonTime + projectedMarathonTime + projectedCustomTime;
                }
            } else {
                result = projected5KmTime + projected10KmTime + projectedHalfMarathonTime + projectedMarathonTime;
                custom_distance_projected_time_tv_race.setText("Custom Distance - ");
            }

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(layout.getApplicationWindowToken(), 0);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.home);

        Log.e("CHECK - RACEACT", "onResume() - Race");

        if (mPreferences.getBoolean(INTENT_BOOL, false) || mPreferences.getBoolean(INTENT_BOOL_HEAT, false)) {

            String mResult = mPreferences.getString(RESULT_RACE, "");
            if (mResult.contains("5km")) {
                result = mResult.substring(mResult.indexOf("5km"));


                if (!mResult.equals("") && !mResult.contains("Custom Distance")) {
                    distance_et_race.getEditText().setText(mResult.substring(1, mResult.indexOf("H")));
                    hh_et_race.getEditText().setText(mResult.substring(mResult.indexOf("H") + 1, mResult.indexOf("M")));
                    mm_et_race.getEditText().setText(mResult.substring(mResult.indexOf("M") + 1, mResult.indexOf("S")));
                    ss_et_race.getEditText().setText(mResult.substring(mResult.indexOf("S") + 1, mResult.indexOf("C")));
                    custom_distance_et_race.getEditText().setText(mResult.substring(mResult.indexOf("C") + 1, mResult.indexOf("5km")));
                    five_km_projected_time_tv_race.setText(mResult.substring(mResult.indexOf("5km"), mResult.indexOf("10km")));
                    ten_km_projected_time_tv_race.setText(mResult.substring(mResult.indexOf("10km"), mResult.indexOf("Half")));
                    half_marathon_projected_time_tv_race.setText(mResult.substring(mResult.indexOf("Half"), mResult.lastIndexOf("Marathon")));
                    marathon_projected_time_tv_race.setText(mResult.substring(mResult.lastIndexOf("Marathon")));
                } else if (mResult.contains("Custom Distance")) {
                    distance_et_race.getEditText().setText(mResult.substring(1, mResult.indexOf("H")));
                    hh_et_race.getEditText().setText(mResult.substring(mResult.indexOf("H") + 1, mResult.indexOf("M")));
                    mm_et_race.getEditText().setText(mResult.substring(mResult.indexOf("M") + 1, mResult.indexOf("S")));
                    ss_et_race.getEditText().setText(mResult.substring(mResult.indexOf("S") + 1, mResult.indexOf("C")));
                    custom_distance_et_race.getEditText().setText(mResult.substring(mResult.indexOf("C") + 1, mResult.indexOf("5km")));
                    five_km_projected_time_tv_race.setText(mResult.substring(mResult.indexOf("5km"), mResult.indexOf("10km")));
                    ten_km_projected_time_tv_race.setText(mResult.substring(mResult.indexOf("10km"), mResult.indexOf("Half")));
                    half_marathon_projected_time_tv_race.setText(mResult.substring(mResult.indexOf("Half"), mResult.lastIndexOf("Marathon")));
                    marathon_projected_time_tv_race.setText(mResult.substring(mResult.lastIndexOf("Marathon"), mResult.indexOf("Custom Distance")));
                    custom_distance_projected_time_tv_race.setText(mResult.substring(mResult.indexOf("Custom Distance")));
                }

            } else if (!mResult.equals("")) {
                distance_et_race.getEditText().setText(mResult.substring(1, mResult.indexOf("H")));
                hh_et_race.getEditText().setText(mResult.substring(mResult.indexOf("H") + 1, mResult.indexOf("M")));
                mm_et_race.getEditText().setText(mResult.substring(mResult.indexOf("M") + 1, mResult.indexOf("S")));
                ss_et_race.getEditText().setText(mResult.substring(mResult.indexOf("S") + 1, mResult.indexOf("C")));
                custom_distance_et_race.getEditText().setText(mResult.substring(mResult.indexOf("C") + 1));
            }
        }


        SharedPreferences.Editor preferenceEditor = mPreferences.edit();
        preferenceEditor.putBoolean(INTENT_BOOL, false);
        preferenceEditor.putBoolean(INTENT_BOOL_HEAT, false);
        preferenceEditor.apply();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("CHECK - RACEACT", "onStart() - Race");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("CHECK - RACEACT", "onStop() - Race");


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
            Log.e("CHECK - RACEACT", "Preferences Cleared");

            SharedPreferences.Editor preferenceEditor = mPreferences.edit();
            preferenceEditor.clear();
            preferenceEditor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("CHECK - RACEACT", "onDestroy() - Race");


    }
}
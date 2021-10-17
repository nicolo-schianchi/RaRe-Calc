package racepredictioncalculator.com;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Fade;
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
import static racepredictioncalculator.com.HeatAdjustment.RESULT_HEAT;
import static racepredictioncalculator.com.RaceToRace.INTENT_BOOL_RACE;
import static racepredictioncalculator.com.RaceToRace.RESULT_RACE;

public class Vo2Max extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.racepredictioncalculator";

    public static final String INTENT_BOOL = "INTENT_BOOL";
    public static final String RESULT_VO2 = "RESULT_VO2";

    private String result;

    private ConstraintLayout layout;

    private TextInputLayout vo2_max_et, custom_distance_et_vo2_max;

    private Button calculate_vo2_max;
    private TextView five_km_project_time_tv_vo2, ten_km_project_time_tv_vo2, half_marathon_projected_time_tv_vo2, marathon_projected_time_tv_vo2, custom_distance_projected_time_tv_vo2;

    private BottomNavigationView bottomNavigationView;

    private boolean isIntentPassed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vo2_max);


        Log.e("CHECK - VO2", "onCreate() - VO2");


        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        return true;
                    case R.id.home:
                        isIntentPassed = true;
                        result = "V" + vo2_max_et.getEditText().getText().toString() + "C" + custom_distance_et_vo2_max.getEditText().getText().toString() + result;
                        startActivity(new Intent(getApplicationContext(), RaceToRace.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        return true;
                    case R.id.about:
                        isIntentPassed = true;
                        result = "V" + vo2_max_et.getEditText().getText().toString() + "C" + custom_distance_et_vo2_max.getEditText().getText().toString() + result;
                        startActivity(new Intent(getApplicationContext(), HeatAdjustment.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        return true;
                }
                return false;
            }
        });


        layout = findViewById(R.id.vo2_layout);

        vo2_max_et = findViewById(R.id.vo2_max_et_parent);
        custom_distance_et_vo2_max = findViewById(R.id.custom_distance_et_vo2_max_parent);

        calculate_vo2_max = findViewById(R.id.calculate_vo2_max);

        five_km_project_time_tv_vo2 = findViewById(R.id.five_km_projected_time_tv_vo2);
        ten_km_project_time_tv_vo2 = findViewById(R.id.ten_km_projected_time_tv_vo2);
        half_marathon_projected_time_tv_vo2 = findViewById(R.id.half_marathon_projected_time_tv_vo2);
        marathon_projected_time_tv_vo2 = findViewById(R.id.marathon_projected_time_tv_vo2);
        custom_distance_projected_time_tv_vo2 = findViewById(R.id.custom_distance_projected_time_tv_vo2);

        result = "";

        isIntentPassed = false;


        calculate_vo2_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });


    }

    private String getCustomString(double vo2, double distance) {
        double totalCustomTime = calculateCustomTime(vo2, distance);
        int hoursOfCustom = calculateHrs(totalCustomTime);
        int minutesOfCustom = calculateMins(totalCustomTime);
        int secondsOfCustom = calculateSec(totalCustomTime);

        String formattedTime = getFormattedTime(hoursOfCustom, minutesOfCustom, secondsOfCustom);

        if (totalCustomTime < 3.5 || totalCustomTime > 240) {
            formattedTime = "≈" + formattedTime;
        }

        return formattedTime;
    }

    private String getMarathonString(double vo2) {
        double totalMarathonTime = calculateTotalMarathonTime(vo2);
        int hoursOfMarathon = calculateHrs(totalMarathonTime);
        int minutesOfMarathon = calculateMins(totalMarathonTime);
        int secondsOfMarathon = calculateSec(totalMarathonTime);

        String formattedTime = getFormattedTime(hoursOfMarathon, minutesOfMarathon, secondsOfMarathon);

        return formattedTime;
    }

    private String getHalfMarathonString(double vo2) {
        double totalHalfMarathonTime = calculateTotalHalfMarathonTime(vo2);
        int hoursOfHalfMarathon = calculateHrs(totalHalfMarathonTime);
        int minutesOfHalfMarathon = calculateMins(totalHalfMarathonTime);
        int secondsOfHalfMarathon = calculateSec(totalHalfMarathonTime);

        String formattedTime = getFormattedTime(hoursOfHalfMarathon, minutesOfHalfMarathon, secondsOfHalfMarathon);

        return formattedTime;
    }

    private String get10KmString(double vo2) {
        double total10KmTime = calculateTotal10kmTime(vo2);
        int hoursOf10Km = calculateHrs(total10KmTime);
        int minutesOf10Km = calculateMins(total10KmTime);
        int secondsOf10Km = calculateSec(total10KmTime);

        String formattedTime = getFormattedTime(hoursOf10Km, minutesOf10Km, secondsOf10Km);

        return formattedTime;
    }

    private String get5KmString(double vo2) {
        double total5KmTime = calculateTotal5kmTime(vo2);
        int hoursOf5Km = calculateHrs(total5KmTime);
        int minutesOf5Km = calculateMins(total5KmTime);
        int secondsOf5Km = calculateSec(total5KmTime);

        String formattedTime = getFormattedTime(hoursOf5Km, minutesOf5Km, secondsOf5Km);

        return formattedTime;
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

    private double calculateTotal5kmTime(double vo2) {
        double time = -3.5383 * Math.pow(10, -12) * Math.pow(vo2, 7) + 2.2108 * Math.pow(10, -9) * Math.pow(vo2, 6) - 5.6127 * Math.pow(10, -7) * Math.pow(vo2, 5) + 0.0000769559 * Math.pow(vo2, 4) - 0.00629019 * Math.pow(vo2, 3) + 0.315509 * Math.pow(vo2, 2) - 9.51509 * vo2 + 158.099;
        return time;
    }

    private double calculateTotal10kmTime(double vo2) {
        double time = -1.946 * Math.pow(10, -11) * Math.pow(vo2, 7) + 9.7512 * Math.pow(10, -9) * Math.pow(vo2, 6) - 0.00000208806 * Math.pow(vo2, 5) + 0.000249432 * Math.pow(vo2, 4) - 0.0181707 * Math.pow(vo2, 3) + 0.826295 * Math.pow(vo2, 2) - 22.9011 * vo2 + 353.676;
        return time;
    }

    private double calculateTotalHalfMarathonTime(double vo2) {
        double time = -2.7258 * Math.pow(10, -11) * Math.pow(vo2, 7) + 1.4842 * Math.pow(10, -8) * Math.pow(vo2, 6) - 0.00000341908 * Math.pow(vo2, 5) + 0.000435946 * Math.pow(vo2, 4) - 0.0336801 * Math.pow(vo2, 3) + 1.61512 * Math.pow(vo2, 2) - 46.959 * vo2 + 756.966;
        return time;
    }

    private double calculateTotalMarathonTime(double vo2) {
        double time = 8.6229 * Math.pow(10, -11) * Math.pow(vo2, 7) - 2.9412 * Math.pow(10, -8) * Math.pow(vo2, 6) + 0.00000354068 * Math.pow(vo2, 5) - 0.000113241 * Math.pow(vo2, 4) - 0.0130358 * Math.pow(vo2, 3) + 1.50444 * Math.pow(vo2, 2) - 65.3169 * vo2 + 1345.25;
        return time;
    }

    private double calculateCustomTime(double vo2, double distance) {
        double closestDistanceTime;
        double time = 0;

        if (distance < 7500) {
            closestDistanceTime = calculateTotal5kmTime(vo2);
            time = closestDistanceTime * Math.pow(distance / 5000, 1.06);
        } else if (distance >= 7500 && distance < 15500) {
            closestDistanceTime = calculateTotal10kmTime(vo2);
            time = closestDistanceTime * Math.pow(distance / 10000, 1.06);
        } else if (distance >= 15500 && distance < 31646) {
            closestDistanceTime = calculateTotalHalfMarathonTime(vo2);
            time = closestDistanceTime * Math.pow(distance / 21097.5, 1.06);
        } else {
            closestDistanceTime = calculateTotalMarathonTime(vo2);
            time = closestDistanceTime * Math.pow(distance / 42195, 1.06);
        }

        return time;
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


    @Override
    protected void onPause() {
        super.onPause();

        Log.e("CHECK - VO2", "onPause() - VO2");

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();

        preferencesEditor.putBoolean(INTENT_BOOL, isIntentPassed);
        preferencesEditor.putString(RESULT_VO2, result);

        preferencesEditor.apply();
    }


    private void calculate() {
        double vo2 = -1;


        if (!vo2_max_et.getEditText().getText().toString().equals("")) {
            vo2 = Double.parseDouble(vo2_max_et.getEditText().getText().toString());
        }

        if (vo2_max_et.getEditText().getText().toString().equals("")) {
            vo2_max_et.setError("Field is incomplete");
        } else if (vo2 < 20 || vo2 > 90) {
            vo2_max_et.setError("VO₂ Max must be between 20 and 90");
        } else {
            vo2_max_et.setError("");
            vo2_max_et.setHelperText("*Required");


            String projected5KmTime = "5km - " + get5KmString(vo2);
            five_km_project_time_tv_vo2.setText(projected5KmTime);

            String projected10KmTime = "10km - " + get10KmString(vo2);
            ten_km_project_time_tv_vo2.setText(projected10KmTime);

            String projectedHalfMarathonTime = "Half Marathon - " + getHalfMarathonString(vo2);
            half_marathon_projected_time_tv_vo2.setText(projectedHalfMarathonTime);

            String projectedMarathonTime = "Marathon - " + getMarathonString(vo2);
            marathon_projected_time_tv_vo2.setText(projectedMarathonTime);

            if (!custom_distance_et_vo2_max.getEditText().getText().toString().equals("")) {

                if (Double.parseDouble(custom_distance_et_vo2_max.getEditText().getText().toString()) < 0) {
                    custom_distance_et_vo2_max.setError("Custom distance must be larger than 0");
                    result = projected5KmTime + projected10KmTime + projectedHalfMarathonTime + projectedMarathonTime;
                } else {
                    String projectedCustomTime = "Custom Distance - " + getCustomString(vo2, Double.parseDouble(custom_distance_et_vo2_max.getEditText().getText().toString()));
                    custom_distance_projected_time_tv_vo2.setText(projectedCustomTime);
                    result = projected5KmTime + projected10KmTime + projectedHalfMarathonTime + projectedMarathonTime + projectedCustomTime;
                }

            } else {
                custom_distance_projected_time_tv_vo2.setText("Custom Distance - ");
                result = projected5KmTime + projected10KmTime + projectedHalfMarathonTime + projectedMarathonTime;
            }


            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(layout.getApplicationWindowToken(), 0);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        Log.e("CHECK - VO2", "onResume() - VO2");


        if (mPreferences.getBoolean(INTENT_BOOL_RACE, false) || mPreferences.getBoolean(INTENT_BOOL_HEAT, false)) {
            SharedPreferences.Editor preferenceEditor = mPreferences.edit();
            preferenceEditor.putBoolean(INTENT_BOOL_RACE, false);
            preferenceEditor.apply();

            String mResult = mPreferences.getString(RESULT_VO2, "");
            if (mResult.contains("5km")) {
                result = mResult.substring(mResult.indexOf("5km"));


                if (!mResult.equals("") && !mResult.contains("Custom Distance")) {
                    vo2_max_et.getEditText().setText(mResult.substring(1, mResult.indexOf("C")));
                    custom_distance_et_vo2_max.getEditText().setText(mResult.substring(mResult.indexOf("C") + 1, mResult.indexOf("5km")));
                    five_km_project_time_tv_vo2.setText(mResult.substring(mResult.indexOf("5km"), mResult.indexOf("10km")));
                    ten_km_project_time_tv_vo2.setText(mResult.substring(mResult.indexOf("10km"), mResult.indexOf("Half")));
                    half_marathon_projected_time_tv_vo2.setText(mResult.substring(mResult.indexOf("Half"), mResult.lastIndexOf("Marathon")));
                    marathon_projected_time_tv_vo2.setText(mResult.substring(mResult.lastIndexOf("Marathon")));
                    custom_distance_projected_time_tv_vo2.setText("Custom Distance - ");
                } else if (mResult.contains("Custom Distance")) {
                    vo2_max_et.getEditText().setText(mResult.substring(1, mResult.indexOf("C")));
                    custom_distance_et_vo2_max.getEditText().setText(mResult.substring(mResult.indexOf("C") + 1, mResult.indexOf("5km")));
                    five_km_project_time_tv_vo2.setText(mResult.substring(mResult.indexOf("5km"), mResult.indexOf("10km")));
                    ten_km_project_time_tv_vo2.setText(mResult.substring(mResult.indexOf("10km"), mResult.indexOf("Half")));
                    half_marathon_projected_time_tv_vo2.setText(mResult.substring(mResult.indexOf("Half"), mResult.lastIndexOf("Marathon")));
                    marathon_projected_time_tv_vo2.setText(mResult.substring(mResult.lastIndexOf("Marathon"), mResult.indexOf("Custom Distance")));
                    custom_distance_projected_time_tv_vo2.setText(mResult.substring(mResult.indexOf("Custom Distance")));
                }
            } else if (!mResult.equals("")) {
                vo2_max_et.getEditText().setText(mResult.substring(1, mResult.indexOf("C")));
                custom_distance_et_vo2_max.getEditText().setText(mResult.substring(mResult.indexOf("C") + 1));
            }
        }


        SharedPreferences.Editor preferenceEditor = mPreferences.edit();
        preferenceEditor.putBoolean(INTENT_BOOL_RACE, false);
        preferenceEditor.putBoolean(INTENT_BOOL_HEAT, false);
        preferenceEditor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("CHECK - VO2", "onStart() - VO2");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("CHECK - VO2", "onStop() - VO2");


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

        Log.e("CHECK - VO2", "onDestroy() - VO2");
    }
}
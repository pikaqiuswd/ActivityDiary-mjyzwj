/*
 * ActivityDiary
 *
 * Copyright (C) 2024 Raphael Mack http://www.raphael-mack.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.rampro.activitydiary.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.zkk.view.rulerview.RulerView;


import java.text.SimpleDateFormat;
import java.util.Date;

import de.rampro.activitydiary.Data;
import de.rampro.activitydiary.R;
import java.util.Calendar;
public class DiaryActivity extends AppCompatActivity {
    private RulerView ruler_height,ruler_weight;
    private TextView textView4,textView5,tv_bmi,tv_steps,tv_calorie,tv_date,textView2;

    private double height = 165;
    private double weight = 60;
    private int calorie = 0;

    private SensorManager mSensorManager;
    private MySensorEventListener mListener;
    private int mStepDetector = 0;  // 自应用运行以来STEP_DETECTOR检测到的步数
    private int mStepCounter = 0;   // 自系统开机以来STEP_COUNTER检测到的步数

    private Button dateButton;
    private Calendar calendar;
    private int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Diary");//设置显示在界面顶部的标题为“Diary”
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//启用返回按钮以便在ActionBar中显示返回图标
        setContentView(R.layout.activity_diary);//设置当前界面的布局为activity_diary.xml文件定义的布局


        calendar = Calendar.getInstance();//获取日期选择器来处理日期和时间
        year = calendar.get(Calendar.YEAR);//得到选择的年月日
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        ruler_height=(RulerView)findViewById(R.id.ruler_height);
        ruler_weight=(RulerView)findViewById(R.id.ruler_weight);
        tv_bmi=findViewById(R.id.tv_bmi);
        textView4=findViewById(R.id.tv_height);
        textView5=findViewById(R.id.tv_weight);
        tv_date=findViewById(R.id.tv_date);
        tv_calorie=findViewById(R.id.tv_calorie);
        tv_steps=findViewById(R.id.tv_steps);

        ruler_height.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            //监听身高数值变化
            @Override
            public void onValueChange(float value) {
                height = value;
                textView4.setText(""+value);
                double s = height/100;
                double ans = (weight/s/s);
                if (ans < 18.5){
                    tv_bmi.setText("您的BMI："+get2num(ans)+"【偏瘦】");
                }else if (ans<=23.9){
                    tv_bmi.setText("您的BMI："+get2num(ans)+"【正常】");
                }else {
                    tv_bmi.setText("您的BMI："+get2num(ans)+"【偏重】");
                }
                tv_calorie.setText((""+(int)((height/100.0*0.4)*weight*0.5)*Data.steps));
                calorie = (int)((height/100.0*0.4)*weight*0.5)*Data.steps;
            }
        });
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        tv_date.setText(dateNowStr);

        ruler_weight.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                weight = value;
                textView5.setText(""+value);
                double s = height/100;
                double ans = (weight/s/s);
                if (ans < 18.5){
                    tv_bmi.setText("您的BMI："+get2num(ans)+"【偏瘦】");
                }else if (ans<=23.9){
                    tv_bmi.setText("您的BMI："+get2num(ans)+"【正常】");
                }else {
                    tv_bmi.setText("您的BMI："+get2num(ans)+"【偏重】");
                }
                tv_calorie.setText((""+(int)((height/100.0*0.4)*weight*0.5)*Data.steps));
                calorie = (int)((height/100.0*0.4)*weight*0.5)*Data.steps;
            }
        });
        ruler_height.setValue(165, 80, 250, 1);
        ruler_weight.setValue(60, 20, 200, 0.1f);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mListener = new MySensorEventListener();

        findViewById(R.id.button).setOnClickListener(view -> savaToday());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add:
                ss();
                break;
        }
        return true;
    }
    class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.out.println("@@@:"+event.sensor.getType()+"--"+ Sensor.TYPE_STEP_DETECTOR+"--"+Sensor.TYPE_STEP_COUNTER);
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (event.values[0] == 1.0f) {
                    mStepDetector++;
                }
            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                mStepCounter = (int) event.values[0];
            }

            String desc = String.format("设备检测到您当前走了%d步，自开机以来总数为%d步", mStepDetector, mStepCounter);
            //Toast.makeText(context, ""+desc, Toast.LENGTH_SHORT).show();
            int a = savaToday()+1;


            tv_steps.setText(""+a);
            tv_calorie.setText((""+(int)((height/100.0*0.4)*weight*0.5)*Data.steps));
            calorie = (int)((height/100.0*0.4)*weight*0.5)*Data.steps;
            Data.steps = a;


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
    private int savaToday() {
        //  保存今日步数和显示给用户
        SharedPreferences sp = getSharedPreferences("steps",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        int steps = sp.getInt(dateNowStr,0);
        if (steps == 0){
            editor.putInt(dateNowStr,mStepCounter);
        }else {
            steps = mStepCounter-steps;
        }
        editor.putInt(dateNowStr+"weight",(int)weight);
        editor.putInt(dateNowStr+"height",(int)height);
        editor.putInt(dateNowStr+"calorie",calorie);
        editor.commit();
        return steps;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // 处理选择的日期
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;

                        // 在这里执行你的逻辑，比如更新UI显示选择的日期
                        dateButton.setText(year + "-" + (month + 1) + "-" + day);

                        SharedPreferences sp = getSharedPreferences("steps",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        Date d = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dateNowStr = sdf.format(d);

                        int steps = sp.getInt(dateNowStr,0);
                        int weight = sp.getInt(dateNowStr+"weight",0);
                        int height = sp.getInt(dateNowStr+"height",0);
                        int calorie = sp.getInt(dateNowStr+"calorie",0);
                        textView2.setText(dateNowStr+"\nheight:"+height+"\nweight:"+weight+"\ncalorie:"+calorie+"\nsteps:"+steps);

                    }
                },
                year, month, day);

        // 显示日期选择器对话框
        datePickerDialog.show();
    }

    private void ss(){
        // ss
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.twindows, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();
        dateButton = view.findViewById(R.id.dateButton);
        textView2 = view.findViewById(R.id.textView2);
        textView2.setText("");
        dateButton.setOnClickListener(view1 -> {
            showDatePickerDialog();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    public static String get2num(double value) {

        return String.format("%.2f", value).toString();
    }
    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mListener);
    }
}
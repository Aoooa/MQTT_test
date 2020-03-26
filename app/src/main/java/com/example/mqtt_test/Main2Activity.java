package com.example.mqtt_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {
    private Button back;
    private EditText setting_host;
    private EditText setting_name;
    private EditText setting_pasw;
    private EditText setting_id;
    private TextView text_data;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setting_host = (EditText)findViewById(R.id.setting_host);
        setting_name = (EditText)findViewById(R.id.setting_name);
        setting_pasw = (EditText)findViewById(R.id.setting_pasw);
        setting_id = (EditText)findViewById(R.id.setting_id);
        text_data = (TextView)findViewById(R.id.text_data);
        setting_host.setText(MainActivity.host);
        setting_name.setText(MainActivity.userName);
        setting_pasw.setText(MainActivity.passWord);
        setting_id.setText(MainActivity.mqtt_id);
        text_data.setText("Host: "+MainActivity.host+"\nName: "+MainActivity.userName+"\nPassward: "+MainActivity.passWord+"\nId: "+MainActivity.mqtt_id);
        Settinginit();
        changeStatusBarTextImgColor(true);
    }
    /**
     * 界面设置状态栏字体颜色
     */
    public void changeStatusBarTextImgColor(boolean isBlack) {
        if (isBlack) {
            //设置状态栏黑色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //恢复状态栏白色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
    public void Settinginit()
    {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String host = setting_host.getText().toString();
                final String name = setting_name.getText().toString();
                final String pasw = setting_pasw.getText().toString();
                final String id = setting_id.getText().toString();
                if(!host.equals("")){
                    MainActivity.host = host;
                    Toast.makeText(Main2Activity.this,"host设置成功",Toast.LENGTH_SHORT).show();
                }
                if(!name.equals("")){
                    MainActivity.userName = name;
                    Toast.makeText(Main2Activity.this,"用户名设置成功",Toast.LENGTH_SHORT).show();
                }
                if(!pasw.equals("")){
                    MainActivity.passWord = pasw;
                    Toast.makeText(Main2Activity.this,"密码设置成功",Toast.LENGTH_SHORT).show();
                }
                if(!id.equals("")){
                    MainActivity.mqtt_id = id;
                    Toast.makeText(Main2Activity.this,"id设置成功",Toast.LENGTH_SHORT).show();
                }
                getSharePreferences();
                text_data.setText("Host: "+MainActivity.host+"\nName: "+MainActivity.userName+"\nPassward: "+MainActivity.passWord+"\nId: "+MainActivity.mqtt_id);
                Intent reconnect=new Intent(Main2Activity.this,MainActivity.class);
                reconnect.putExtra("reconnect","YES");
                setResult(1,reconnect);
                getSharePreferences();
                finish();
            }
        });
    }
    private void getSharePreferences() {
        @SuppressLint("WrongConstant")
        SharedPreferences preferences = getSharedPreferences("data",MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FirstUse",true);
        editor.putString("host",MainActivity.host);
        editor.putString("name",MainActivity.userName);
        editor.putString("pasw",MainActivity.passWord);
        editor.putString("id",MainActivity.mqtt_id);
        editor.commit();
    }
}

package com.example.mqtt_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sub_activity extends AppCompatActivity {
    private EditText edit_4;
    private Button add_1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity);
        final List<Map<String, String>> List_Item = new ArrayList<Map<String, String>>();
            //定义一个临时的hashMap
        for (int i = 0;i < MainActivity.sub_list.length;i ++) {
            Map<String, String> Map = new HashMap<String, String>();
            //存入姓名键值对
            Map.put("ShowTopic", " Topic");
            Map.put("Topic", MainActivity.sub_list[i]);
            //讲hashMap存入List
            List_Item.add(Map);
        }
        SimpleAdapter listAdpter = new SimpleAdapter(
                this,
                List_Item,
                R.layout.list_activity,
                new String[] {"ShowTopic","Topic"},
                new int[] {R.id.user_name,R.id.user_id}
        );
        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdpter);
        edit_4 = findViewById(R.id.edit_4);
        add_1 = findViewById(R.id.add_1);
        add_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTopic = (edit_4.getText()).toString();
                Toast.makeText(sub_activity.this,"len:"+MainActivity.sub_list.length,Toast.LENGTH_SHORT).show();
                if(!getTopic.equals("")) {
                    String[] sub_list_temp = new String[MainActivity.sub_list.length+1];
                    for (int i = 0;i < MainActivity.sub_list.length;i ++) {
                        sub_list_temp[i] = MainActivity.sub_list[i];
                    }
                    sub_list_temp[MainActivity.sub_list.length] = getTopic;
                    MainActivity.sub_list = sub_list_temp;
                    Map<String, String> M = new HashMap<String, String>();
                    //存入姓名键值对
                    M.put("ShowTopic"," Topic");
                    M.put("Topic",MainActivity.sub_list[MainActivity.sub_list.length-1]);
                    //讲hashMap存入List
                    List_Item.add(M);
                    SimpleAdapter sAdapter = (SimpleAdapter)listView.getAdapter();
                    sAdapter.notifyDataSetChanged();
                }
                Toast.makeText(sub_activity.this,"Topic:"+getTopic+"\n添加至订阅列表",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(sub_activity.this,MainActivity.class);
                intent.putExtra("subscribe","YES");
                setResult(2,intent);
                finish();
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.close_in_animation_btt,R.anim.close_out_animation_btt);
    }
}

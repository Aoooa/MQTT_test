package com.example.mqtt_test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sub_activity extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity);

        List<Map<String, String>> List_Item = new ArrayList<Map<String, String>>();
        for(int i=0;i<40;i++) {
            //定义一个临时的hashMap
            Map<String, String> Map = new HashMap<String, String>();
            //存入姓名键值对
            Map.put("user_name", "user_" + i);
            //存入ID
            Map.put("user_id", "100" + i);
            //讲hashMap存入List
            List_Item.add(Map);
        }
        SimpleAdapter listAdpter = new SimpleAdapter(
                this,
                List_Item,
                R.layout.list_activity,
                new String[] {"user_name","user_id"},
                new int[] {R.id.user_name,R.id.user_id}
        );
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdpter);
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.close_in_animation_btt,R.anim.close_out_animation_btt);
    }
}

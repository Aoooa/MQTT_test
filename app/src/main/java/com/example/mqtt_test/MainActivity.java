package com.example.mqtt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity {
    private Button bu_1;
    private Button bu_2;
    private Button next;
    private ImageView img_1;
    private TextView text_1;
    private EditText edit_1;
    private EditText edit_2;
    private EditText edit_3;
    private Spinner spin_1;
    public static String host = "tcp://0.0.0.0:1883";
    public static String userName = "app";
    public static String passWord = "123456";
    public static String mqtt_id = "app/test";
    private String mqtt_sub_topic = "mqtt/test";
    private String mqtt_pub_topic = "mqtt/test";
    private ScheduledExecutorService scheduler;
    private MqttClient client;
    private MqttConnectOptions options;
    private Handler handler;
    private int mqtt_connect_symbol = 0;
    private TextSwitcher mTextSwicher = null;
    private String mqtt_sub_topic_correct ;
    private String[] mContext = {
            "未连接",
            "已连接",
            "#be002f"
    };
    private int mIndex = 0;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        Boolean First = sp.contains("FirstUse");
        if(First){
            userName = sp.getString("name","123456");
            Toast.makeText(MainActivity.this,userName,Toast.LENGTH_SHORT).show();
            passWord = sp.getString("pasw","123456");
            mqtt_id = sp.getString("id","app/test");
            host = sp.getString("host","tcp://0.0.0.0:1883");
        }
        else {
            getSharePreferences();
        }

        edit_1 = findViewById(R.id.edit_1);
        edit_2 = findViewById(R.id.edit_2);
        edit_3 = findViewById(R.id.edit_3);
        text_1 = findViewById(R.id.text_1);
        bu_1 = findViewById(R.id.bu_1);
        bu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mqtt_connect_symbol == 1){
                    final String edit_text = edit_1.getText().toString();
                    final String edit_topic = edit_2.getText().toString();
                    String edit_sub_correct = edit_3.getText().toString();
                    if(!edit_topic.equals("")) {
                        publishmessageplus(edit_topic,edit_text);
                        Toast.makeText(MainActivity.this,"主题"+edit_topic+"发送内容成功",Toast.LENGTH_SHORT).show();
                    }else{
                        publishmessageplus("app/test",edit_text);
                        Toast.makeText(MainActivity.this,"默认主题发送内容成功",Toast.LENGTH_SHORT).show();
                    }
                }else Toast.makeText(MainActivity.this,"未连接上服务器",Toast.LENGTH_SHORT).show();
            }
        });

        bu_2 = findViewById(R.id.bu_2);
        bu_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mqtt_connect_symbol == 1){
                    if(!(edit_3.getText().toString()).equals("")) {mqtt_sub_topic = (edit_3.getText().toString());}
                    try {
                        client.subscribe(mqtt_sub_topic, 1);//java库 订阅
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this,"Topic:"+mqtt_sub_topic+"\n订阅成功",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(MainActivity.this,"未连接上服务器",Toast.LENGTH_SHORT).show();

            }
        });

        mTextSwicher = (TextSwitcher) findViewById(R.id.textSWitcher_1);
        mTextSwicher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(MainActivity.this);
                tv.setTextSize(12);
                   tv.setTextColor(Color.GREEN);
                return tv;
            }
        });

        mTextSwicher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,mContext[mqtt_connect_symbol],Toast.LENGTH_SHORT).show();
            }
        });

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Main2Activity.class);
                startActivityForResult(intent,1);
            }
        });
/*
        Resources res = getResources();
        String[] spin_list = res.getStringArray(R.array.res_spin);//把res_spin的内容添加到数组中
        spin_1 = (Spinner) findViewById(R.id.spin_1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, spin_list);
        spin_1.setAdapter(adapter);
        spin_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"你选择了" + id,Toast.LENGTH_SHORT).show();
                spin_id = id;
                Mqtt_init();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
        changeStatusBarTextImgColor(true);
//*****************************************************************************************************************************************************
        Mqtt_init();
        startReconnect();

        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                final String edit_sub_correct = edit_3.getText().toString();
                switch (msg.what) {
                    case 1: //开机校验更新回传
                        break;
                    case 2:  // 反馈回传

                        break;
                    case 3:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                            text_1.setText("Topic："+mqtt_sub_topic_correct+"\n--"+msg.obj.toString());
                        break;
                    case 30:  //连接失败
                        mqtt_connect_symbol = 0;
                        mTextSwicher.setText(mContext[0]);
                        break;
                    case 31:   //连接成功
                        mqtt_connect_symbol = 1;
                        mTextSwicher.setText(mContext[1]);
                        try {
                            client.subscribe("app/test", 1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void Mqtt_init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    //startReconnect();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 3;   //收到消息标志位
                    mqtt_sub_topic_correct = topicName;
                    msg.obj = message.toString();
                    handler.sendMessage(msg);    // hander 回传
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!(client.isConnected()))  //如果还未连接
                    {
                        client.connect(options);
                        Message msg = new Message();
                        msg.what = 31;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void publishmessageplus(String topic, String message2) {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //*****************************************************************************************************************
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            Mqtt_init();
        }
    }
    private void getSharePreferences() {
        @SuppressLint("WrongConstant")
        SharedPreferences preferences = getSharedPreferences("data",MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FirstUse",true);
        editor.putString("host",host);
        editor.putString("name",userName);
        editor.putString("pasw",passWord);
        editor.putString("id",MainActivity.mqtt_id);
        editor.commit();
    }
}


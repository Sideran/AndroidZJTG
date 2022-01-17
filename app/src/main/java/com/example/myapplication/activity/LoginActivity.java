package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.Adapter.ListViewAdapter;
import com.example.myapplication.Datatest.Datapp;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.LoginBody;
import com.example.myapplication.util.Util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private final String TAG="getArticle";
    private final String TAG1="Login";
    private Button signin,login;


    private Context context;
    private EditText username,pwd;

    private static final int LoginInfo = 1;
    private Datapp app;
    private ListViewAdapter adapter;
    private List<Article> unRead,Read ;

    @Override
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView((R.layout.activity_login));

        login=(Button)findViewById(R.id.login3);
        signin=(Button)findViewById(R.id.signin3);
        username=(EditText)findViewById(R.id.userName2);
        pwd=(EditText)findViewById(R.id.password2);
        context=LoginActivity.this;
        setlistener();


        app=(Datapp) getApplication();




    }
    //设置监听
    public void setlistener(){
        LoginActivity.ButtonClickListener listener=new ButtonClickListener();
        login.setOnClickListener(listener);
        signin.setOnClickListener(listener);

    }
    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v){


            switch (v.getId()){

                case R.id.signin3:
                    break;
                case R.id.login3:
//                    Log.d(TAG1,"???");
                    login(username.getText().toString(),pwd.getText().toString());
                    break;


            }
        }

    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){

            if(msg.what == LoginInfo ){
                String data = (String) msg.obj;
                Toast.makeText(LoginActivity.this,data,Toast.LENGTH_SHORT).show();
            }
        };

    };
    private void login(String Username,String password){
        final Intent[] intent = {null};
        LoginBody user =new LoginBody();
        user.setUsername(Username);
        user.setPassword(password);
        user.setCode("");
        user.setUuid("");
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        String userjs=JSON.toJSONString(user);
        //userjs = "{\"username\":\"zhangsan\",\"userId\":\"20\"}";
        RequestBody requestBody = RequestBody.create(mediaType, userjs);
        Request request = new Request.Builder()
                .url(app.getURL()+"/login")
                //.url("https://api.github.com/markdown/raw")
                //.addHeader("Authorization","Bearer " + "token123")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                Log.d(TAG1, "onFailure: " + e.getMessage());
                Log.e(TAG1, "onFailure: " + e.getMessage());
                message.obj = "网络错误";
                message.what = LoginInfo;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG1, response.protocol() + " " +response.code() + " " + response.message());
                String text=response.body().string();
                Log.d(TAG1, "onResponse: " + text);
//                Headers headers = response.headers();
//                for (int i = 0; i < headers.size(); i++) {
//                    Log.d(TAG1, headers.name(i) + ":" + headers.value(i));
//                }
                Message message = Message.obtain();
                try{
                    JSONObject js= (JSONObject) JSON.parse(text);

                    if(js.getInteger("code")==500){
                        message.obj = "用户名或密码错误";
                        message.what = LoginInfo;
                        handler.sendMessage(message);
                    }else if(js.getInteger("code")==200) {
                        app.setToken(js.getString("token"));
                        Log.d(TAG1,"token:"+js.getString("token"));
                        getString();

//                        intent[0] =new Intent(MainActivity.this, TabActivity.class);
//                        startActivity(intent[0]);

                    }
                    else{
                        message.obj = "未知错误";
                        message.what = LoginInfo;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    message.obj = "未知错误";
                    message.what = LoginInfo;
                    handler.sendMessage(message);
                }

            }
        });
    }
    private void getString() {
        //String url = "https://wwww.baidu.com";
        String Token=app.getToken();
        String url = app.getURL()+"/learn/article/userId?userId=1";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + Token)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                Log.e(TAG, "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Intent intent = null;
                String text=response.body().string();
                Log.e(TAG, "onResponse: " + text);
                unRead=new LinkedList<>();
                Read=new LinkedList<>();
                try {//Util.decrypt(users)
                    JSONObject js= (JSONObject) JSON.parse(text);
                    JSONArray ja = js.getJSONArray("data");//JSON.parseArray(users,User.class);
                    List<Article> retList=ja.toJavaList(Article.class);
                    for(int i=0;i<retList.size();i++){
                        if(retList.get(i).getStatus().equals("1")){
                            Read.add(retList.get(i));
                        }else if(retList.get(i).getStatus().equals("0")){
                            unRead.add(retList.get(i));
                        }

                    }
                    app.setUnReadList(unRead);
                    app.setReadList(Read);
//                    Log.e(TAG, "onResponse: " +":" + articleTitle);


                } catch (Exception e) {
                    e.printStackTrace();

                }
                intent=new Intent(LoginActivity.this, TabActivity.class);
                startActivity(intent);
            }
        });
    }
}

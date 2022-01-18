package com.example.myapplication;



import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.Datatest.Datapp;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.activity.TabActivity;
import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.LoginBody;
import com.example.myapplication.modle.UserInfo;
import com.example.myapplication.util.Util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity implements OnInitListener{

    private final String TAG="getArticle";
    private final String TAG1="Login";
    private Button login;
    private ImageButton user1, user2, user3,settingURL;

    private Context context;

    private static final int LoginInfo = 1;
    private Datapp app;
    private ProgressDialog progressDialog;
    private List<Article> unRead,Read ;



    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){

            if(msg.what == LoginInfo ){
                String data = (String) msg.obj;
                Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
            }
        };

    };
    @Override
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView((R.layout.activity_main));
        user1=(ImageButton)findViewById(R.id.user1);
        user2=(ImageButton)findViewById(R.id.user2);
        user3=(ImageButton)findViewById(R.id.user3);
        settingURL=(ImageButton)findViewById(R.id.settingURL);
        login=(Button)findViewById(R.id.login);
//        signin=(Button)findViewById(R.id.signin);
//        username=(EditText)findViewById(R.id.UserName);
//        pwd=(EditText)findViewById(R.id.Password);
        context=MainActivity.this;
        setlistener();
        //检测通知是否开启
        checkNotification(context);
        
//        DBManager.getDbManager().init(this);
        app=(Datapp) getApplication();
        String str=Util.getJson(context,"message.json");
        app.setUnReadList(JSON.parseArray(str, Article.class));
        settingURL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeURL(v);
            }
        });


    }
    //设置监听
    public void setlistener(){
        ButtonClickListener listener=new ButtonClickListener();
        login.setOnClickListener(listener);
//        signin.setOnClickListener(listener);
        user1.setOnClickListener(listener);
        user2.setOnClickListener(listener);
        user3.setOnClickListener(listener);

    }
    class ButtonClickListener implements OnClickListener{
        @Override
        public void onClick(View v){


            switch (v.getId()){


                case R.id.user1:

                    login("admin","admin123");
                    break;
//                case R.id.signin:
//                    break;
                case R.id.login:
//                    Log.d(TAG1,"???");
                    Intent intent = null;
                    intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.user2:

                    login("ry","admin123");
                    break;
                case R.id.user3:

                    login("sideran","admin123");

            }
        }

    }

    private void login(String Username,String password){
        progressDialog= Util.MaskUtil.showProgressDialog("登陆中，请稍后",MainActivity.this);
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
                //关闭遮罩层
                progressDialog.dismiss();
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
                        //关闭遮罩层
                        progressDialog.dismiss();
                        message.obj = "用户名或密码错误";
                        message.what = LoginInfo;
                        handler.sendMessage(message);
                    }else if(js.getInteger("code")==200) {
                        app.setToken(js.getString("token"));
                        Log.d(TAG1,"token:"+js.getString("token"));
                        getUserInfo();

                    }
                    else{
                        //关闭遮罩层
                        progressDialog.dismiss();
                        message.obj = "未知错误";
                        message.what = LoginInfo;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    //关闭遮罩层
                    progressDialog.dismiss();
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
                //关闭遮罩层
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: "+e.getMessage());
                Log.e(TAG, "onFailure: "+e.getMessage());
                Message message = Message.obtain();
                message.obj = "获取文章失败";
                message.what = LoginInfo;
                handler.sendMessage(message);
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
                //关闭遮罩层
                progressDialog.dismiss();
                intent=new Intent(MainActivity.this, TabActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getUserInfo() {
        String Token=app.getToken();
        String url = app.getURL()+"/learn/article/userInfo";
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
                //关闭遮罩层
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: "+e.getMessage());
                Log.e(TAG, "onFailure: "+e.getMessage());
                Message message = Message.obtain();
                message.obj = "获取用户信息失败";
                message.what = LoginInfo;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String text=response.body().string();
                Log.e(TAG1, "onResponse: " + text);
                try {//Util.decrypt(users)
                    JSONObject js= (JSONObject) JSON.parseObject(text);
                    JSONObject ja = js.getJSONObject("data");//JSON.parseArray(users,User.class);
                    UserInfo user =JSON.toJavaObject(ja,UserInfo.class);
                    app.setUser(user);
                    getString();

                } catch (Exception e) {
                    //关闭遮罩层
                    progressDialog.dismiss();
                    e.printStackTrace();

                }

            }
        });
    }

    private String sex;//选择的性别
    private String[] sexArrays = new String[]{"172.17.33.30", "192.168.31.184"};//性别选择
    public void changeURL(View view){
        new AlertDialog.Builder(this)
                .setTitle("请选择服务器地址")
                .setIcon(R.mipmap.ic_launcher)
                //第一个参数是单选的字符串数组，第二个是数组的下标设置默认的选择项，-1为默认不选
                .setSingleChoiceItems(sexArrays, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //选中某个值时赋值给定义的变量
                        sex = sexArrays[which];
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击确定按钮将选择的值通过消息对话框显示出来
                        String res = "你选择的是" + sex;
                        app.setURL(sex);
                        /**
                         * 消息对话框是Android系统提供的轻量级信息提醒机制，用于向用户提示即时消息，
                         * Toast对话框显示在应用程序的最上层，显示一段时间后自动消失，不会打断当前操作，也不获得焦点。
                         * 由于Toast只起到提示作用，因此多用在触发事件的监听中。
                         */
                        /**
                         * 通过makeText方法实例化对象，三个参数，第一个为上下文对象（Context），第二个参数为文本显示内容，
                         * 第三个参数为Toast显示时长，通过常量控制：Toast.LENGTH_SHORT时间较短；Toast.LENGTH_LONG显示的时间较长
                         * 最后一定要调用show方法，不然提示消息不会显示
                         */
                        Toast.makeText(MainActivity.this,res,Toast.LENGTH_SHORT).show();
                        sex = "";
                    }
                })
                .show();

    }



    /**
     * 检测是否开启通知
     *
     * @param context
     */
    private void checkNotification(final Context context) {
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("温馨提示")
                    .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }
    /**
     * 如果没有开启通知，跳转至设置界面
     *
     * @param context
     */
    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * 获取通知权限,监测是否开启了系统通知
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onInit(int status) {

    }
}

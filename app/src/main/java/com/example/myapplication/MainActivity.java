package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.app.Activity;
import android.provider.Settings;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.Adapter.ListViewAdapter;
import com.example.myapplication.Datatest.Datapp;
import com.example.myapplication.activity.TabActivity;
import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.ChatMessage;
import com.example.myapplication.util.Util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements OnInitListener{

    private Button enter;
    private Context context;

    private Datapp app;
    private ListViewAdapter adapter;
    private List<Article> unRead;
    private List<Article> Read;

    @Override
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView((R.layout.activity_main));
        enter=(Button)findViewById(R.id.enter);
        context=MainActivity.this;
        setlistener();
//        //启动服务
//        startJWebSClientService();
//        //绑定服务
//        bindService();
//        //注册广播
//        doRegisterReceiver();
        //检测通知是否开启
        checkNotification(context);

        
//        DBManager.getDbManager().init(this);

//        String str=Util.getJson(context,"message.json");

        app=(Datapp) getApplication();
//        app.setUnReadList(JSON.parseArray(str, Article.class));
        getString();

    }
    //设置监听
    public void setlistener(){
        ButtonClickListener listener=new ButtonClickListener();
        enter.setOnClickListener(listener);
    }
    class ButtonClickListener implements OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = null;

            switch (v.getId()){
                case R.id.enter:
                    intent=new Intent(MainActivity.this, TabActivity.class);
                    startActivity(intent);
                    break;
                case R.id.signin:
                    break;
                case R.id.login:
                    break;

            }
        }

    }
    private void getString() {
        //String url = "https://wwww.baidu.com";
        String url = "http://127.0.0.1:8080/learn/article/list";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
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
                String text=response.body().string();
                Log.d(TAG, "onResponse: " + text);
                try {//Util.decrypt(users)
                    List<Article> retList=JSON.parseArray(text,Article.class);//.parseArray(users,User.class);
                    JSONObject js= (JSONObject) JSON.parse(text);
//                    String articleTitle=js.get("articleTitle").toString();
                    for(int i=0;i<retList.size();i++){
                        if(retList.get(i).getStatus()=="0"){
                            unRead.add(retList.get(i));
                        }else if(retList.get(i).getStatus()=="1"){
                            Read.add(retList.get(i));
                        }

                    }


//                    Log.e(TAG, "onResponse: " +":" + articleTitle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

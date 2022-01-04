package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.myapplication.Datatest.Datapp;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.im.OriginalTTSManager;
import com.example.myapplication.im.WhyTTS;
import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.ChatMessage;
import com.example.myapplication.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MessageActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView endTime,time,title,article;
    private ImageButton audioPlay,audioNext;
    private Button nextText,confirm;
    private LinearLayout linearLayout;
    TextToSpeech tts;
    boolean switchS=true;
    private Datapp app;
    int i;
    private List<Article> chatMessageList;
    private static final String TAG = "TTSManagerWhy";
    private static final long TIMEOUT_MILLS = 1 * 1 * 1000L;
    private int status=0;
    int count=0;
    private long startTime=0;
    private int[] span;
    private String[] lines;
    private float pxPerWord;
    private  int startWord;
    Handler handler;
    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        app=(Datapp) getApplication();
        Bundle bundle=getIntent().getExtras();
        i=bundle.getInt("position");
        initWight();
//        chatMessageList= JSON.parseArray(app.getUnReadList(),ChatMessage.class);
        chatMessageList=app.getUnReadList();
        title.setText(chatMessageList.get(i).getArticleTitle());
        article.setText(Util.stripHtml(chatMessageList.get(i).getArticleContent()));
        time.setText("发布时间："+formatTime(chatMessageList.get(i).getStartTime()));
        endTime.setText("截止时间："+formatTime(chatMessageList.get(i).getDeadline()));
        article.setMovementMethod(ScrollingMovementMethod.getInstance());
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                // 如果装载TTS引擎成功
                if (status == TextToSpeech.SUCCESS)
                {
                    // 设置使用美式英语朗读
                    int result = tts.setLanguage(Locale.CHINA);
                    // 如果不支持所设置的语言
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE)
                    {
                        Toast.makeText(MessageActivity.this, "TTS暂时不支持这种语言的朗读。", Toast.LENGTH_LONG)
                                .show();
                        //Util.showToast(MessageActivity.this, "TTS暂时不支持这种语言的朗读。");
                    }
                    tts.setSpeechRate(1.0f);
                    tts.setOnUtteranceProgressListener(new ttsUtteranceListener());

                }
            }
        });
        count=0;
        lines = article.getText().toString().split("。|\\.|？|\\?|！|!|@|···|;|；|:|：|\n|\t|,|，");

        if (lines != null && lines.length > 0) {

            span = new int[lines.length];
            for (int i = 0; i < lines.length; i++) {
                Log.e(TAG, "run: " + i + " " + lines[i]);
                if (i == 0) {
                    span[i] = 0;
                } else {
                    span[i] = span[i - 1] + lines[i - 1].length() + 1;
                }

            }

        }
        pxPerWord=0;
        startWord=0;


    }
    private void initWight(){
        int height=getScreenHeight(this);

        article=(TextView) findViewById(R.id.receiveMessage);
        endTime=(TextView) findViewById(R.id.endTime);
        time=(TextView) findViewById(R.id.tv_sendtime);
        audioPlay=(ImageButton)findViewById(R.id.audioPlay);
        audioNext=(ImageButton) findViewById(R.id.audioNext);
        nextText=(Button) findViewById(R.id.nexttext);
        confirm=(Button) findViewById(R.id.confirm_button);
        title=(TextView)findViewById(R.id.article_title);
        article.setHeight((int)(height*0.4));
    }

    @Override
    public void onClick(View v){
        TextView textView = findViewById(R.id.receiveMessage);
        switch (v.getId()){
            case R.id.back:
                Intent intent = new Intent(MessageActivity.this,TabActivity.class);
                startActivity(intent);
            case R.id.audioPlay:
                if(switchS){
                    if (count==0) {

                        float total = 0;
                        int offset = textView.getLineCount() * textView.getLineHeight();

                        if (offset > textView.getHeight()) {
                            total = (offset - textView.getHeight() * 2 / 3);
                        }
                        startWord = textView.getText().toString().length() * textView.getHeight() * 3 / 5 / offset;
                        pxPerWord = total / (textView.getText().toString().length() - startWord);
                        Log.e(TAG, "startWord=" + startWord);
                        Log.e(TAG, "pxPerWord=" + pxPerWord);
                        textView.scrollTo(0,0);
                    }
                    startSpeaking();
                    switchS=false;
                }else {
                    tts.stop();
                    switchS=true;
                }break;
            case R.id.audioNext:
            case R.id.nexttext:
                try {
                    tts.stop();
                    switchS=true;
                    count=0;
                    status=1;
                    title.setText(chatMessageList.get(i++).getArticleTitle());
                    textView.setText(chatMessageList.get(i++).getArticleContent());
                    startSpeaking();
                    switchS=false;

                } catch (Exception e) {
                    e.printStackTrace();
                }break;
            case R.id.confirm_button:
                chatMessageList.get(i).setStatus("1");
                app.setChatMessageList(chatMessageList);
                intent=new Intent(MessageActivity.this, SignActivity.class);
                startActivity(intent);
        }
    }

    private class ttsUtteranceListener extends UtteranceProgressListener {
        @Override
        public void onStart(String utteranceId) {
            Log.e(TAG, "status=onStart");
        }
        @Override
        public void onDone(String utteranceId) {
            count++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startSpeaking();
                }
            });

//            Toast.makeText(a, "toast", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "status=onDone");
        }
        @Override
        public void onError(String utteranceId) {
            Log.e(TAG, "status=onError");
        }
        @Override
        public void onStop(String utteranceId, boolean interrupted)//当话语在进行中停止或从综合队列中清除时调用。
        {
            Log.e(TAG, "status=onStop");
        }
    }
    private void startSpeaking() {
        TextView at=findViewById(R.id.receiveMessage);
        String content = at.getText().toString();
        if (count>=lines.length){
            Log.e(TAG, "end: 表示这个内容学完了" );
            count=0;
            return;
        }
        int ends;
        if (count==lines.length-1)ends=content.length();
        else ends=span[count+1];
        SpannableStringBuilder builder = new SpannableStringBuilder(content);

        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(Color.parseColor("#00FFFF"));
        ForegroundColorSpan buleSpan = new ForegroundColorSpan(Color.parseColor("#4d8ade"));
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.parseColor("#000000"));
        if (count!=0)
            builder.setSpan(buleSpan,0, span[count]-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(greenSpan, span[count], ends-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (count!=lines.length-1)
            builder.setSpan(blackSpan,ends, content.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        at.setText(builder);
        tts.speak(lines[count],
                TextToSpeech.QUEUE_FLUSH, null,"UniqueID");
        //handler.postDelayed(runnable, TIMEOUT_MILLS);
        status=1;
        if ((count<lines.length-1)&&(span[count]>=startWord)) {
            at.scrollTo(0,Math.round((span[count]-startWord)*pxPerWord));
        }
    }
    /**
     * 获取屏幕高度；
     */
    public static int getScreenHeight(Context context) {
        return context.getApplicationContext().getResources()
                .getDisplayMetrics().heightPixels;
    }

    /**
     * 将标准日期转为日期格式
     *
     * @param date
     * @return
     */
    private String formatTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

}

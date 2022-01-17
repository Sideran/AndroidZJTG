package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Datatest.Datapp;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import com.example.myapplication.R;
import com.example.myapplication.util.Util;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class  SignActivity extends AppCompatActivity  implements View.OnClickListener {
    private ImageView iv;
    private Context mContext;
    private Bitmap bitmap;
    private Canvas canvas;
    private String format;
    private Datapp app;
    private static final String TAG = "tag";
    // 为了不让onItemtSelecter报空指针,所以在这里创建画笔对象
    private Paint paint = new Paint();
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mContext=SignActivity.this;
        //textView = (TextView) findViewById(R.id.textView);
        iv = (ImageView) findViewById(R.id.imageView_hand);
        //paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        //paint.setStrokeWidth(15);
        app=(Datapp)getApplication();
        // 给IV添加触摸监听器
        iv.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // switch触摸的action
                switch (event.getAction()) {

                    // 按下
                    case MotionEvent.ACTION_DOWN:
                        // 获取按下去的坐标点
                        startX = event.getX();
                        startY = event.getY();
                        if (bitmap == null) {
                            // 创建画纸对象 参数1,2:画纸宽高,参数3:画纸质量
                            bitmap = Bitmap.createBitmap(iv.getWidth(),
                                    iv.getHeight(), Bitmap.Config.ARGB_8888);
                            // 创建画板对象
                            canvas = new Canvas(bitmap);
                            canvas.drawColor(Color.WHITE);
                            // 设置画笔粗细
                            paint.setStrokeWidth(25);
                            paint.setStrokeCap(Paint.Cap.ROUND);
                        }
                        break;
                    // 移动
                    case MotionEvent.ACTION_MOVE:
                        // 获取当前移动到的坐标点
                        float currentX = event.getX();
                        float currentY = event.getY();
                        // 将起始点和当前点连线
                        canvas.drawLine(startX, startY, currentX, currentY, paint);
                        // 因为MOVE会多次调用,为了实现连贯效果,绘制完当前直线后将起始点进行重新赋值,把上次的结束点作
                        // 为起始点准备下一次的绘制
                        startX = currentX;
                        startY = currentY;
                        // 将绘制好的bitmap设置给IV显示
                        iv.setImageBitmap(bitmap);

                        break;
                    // 抬起
                    case MotionEvent.ACTION_UP:
                        Log.d("tag", "up");

                        break;

                }

                return true;
            }
        });

    }
    public void save(){//} throws IOException {
        if(bitmap == null) {
            Toast.makeText(this, "您还没画,不需要保存~!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        format = "signature";//sdf.format(new Date());
        Util.showToast(mContext, Environment
                .getExternalStorageDirectory().toString()
                + "/1/"
                + format
                + ".jpg");
        File file = new File(Environment
                .getExternalStorageDirectory().toString()
                + "/1/"
                + format
                + ".jpg");
        // 添加权限<uses-permission
        // android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        Log.d(TAG, Environment
                .getExternalStorageDirectory().toString()
                + "/1/"
                + format
                + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
//            boolean compress = adjustPhotoRotation(bitmap,270).compress(Bitmap.CompressFormat.JPEG, 100, fos);
            boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            //Toast.makeText(this, compress ? "成功" : "失败", Toast.LENGTH_SHORT).show();
            Util.showToast(mContext, compress ? "保存成功" : "保存失败");
            if(compress){
                // 给相册app发送广播,接收到后会更新图库
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
//            intent.setData(Uri.fromFile(file));
//            sendBroadcast(intent);
                try {
                    upLodeFile();
                    Intent intent=new Intent(this,TabActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void clear() {
        // 清空画纸
        bitmap = null;
        iv.setImageBitmap(bitmap);

    }
    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {


        Matrix m = new Matrix();
        m.setRotate(orientationDegree);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            int w=bm1.getWidth();
            int h=bm1.getHeight();
            Bitmap bm0;
            if (w>h){
                Log.d(TAG,"w="+w+",h="+h);
                bm0=Bitmap.createBitmap(w, w+(w-h), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm0);
                canvas.drawBitmap(bm1, 0, (w-h), null);
            }
            else bm0=bm;

            return bm0;

        } catch (OutOfMemoryError ex) {
        }
        return null;

    }
    private String detectImage(Bitmap bitmap){
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        MLTextAnalyzer analyzer = new MLTextAnalyzer.Factory(mContext).setLocalOCRMode(MLLocalTextSetting.OCR_DETECT_MODE).setLanguage("zh").create();
        SparseArray<MLText.Block> blocks = analyzer.analyseFrame(frame);

        String ss="";
        for (int i = 0; i < blocks.size(); i++) {
            List<MLText.TextLine> lines = blocks.get(i).getContents();
            for (int j = 0; j < lines.size(); j++) {
                // Display by line, without displaying empty lines.
                if (lines.get(j).getStringValue() != null && lines.get(j).getStringValue().trim().length() != 0) {
                    //BaseGraphic textGraphic = new LocalTextGraphic(graphicOverlay,
                    //        lines.get(j));
                    //graphicOverlay.addGraphic(textGraphic);
                    ss=ss+lines.get(j).getStringValue();
                }
            }
        }
        Log.e(TAG,ss);
        return ss;
    }
    //recognition
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recognition:
                String ss=detectImage(adjustPhotoRotation(bitmap,270));
                String s1=detectImage(bitmap);
                Util.showToast(mContext,"横屏："+ss+"   竖屏："+s1);
                //LocalTextTransactor ltt=new LocalTextTransactor(mContext);

                //ltt.detectInImage(adjustPhotoRotation(bitmap,270));
                //ltt.detectInImage(bitmap);
                break;
            case R.id.save:
                save();
                break;
            case R.id.clear:
                clear();
                break;

            default:
                break;
        }
    }

    private File file;
    private void upLodeFile() throws IOException {
        file = new File(Environment.getExternalStorageDirectory() + "/1/"+format+".jpg");//获取目录（不加后面的字符串是你的根目录+后面是继续找的意思）
        //getFileLog();
        //ok上传
        String str=app.getRecentSet().toString();
        /**
         * ok上传文件实例
         * 由于服务器暂停维护，接口无法访问
         * 代码就这些
         */
        //创建OK
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //请求体
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("articleIds", str)                //post请求Key,value
                .addFormDataPart("signfile", file.getName(),requestBody)     //post请求Key,value
                .build();
        //构建请求
        Request request = new Request.Builder()
//                .url("http://yun918.cn/study/public/index.php/file_upload.php")
                .url(app.getURL()+"/learn/learnuser/sign")
                .addHeader("Authorization","Bearer " +app.getToken())
                .post(body)
                .build();
        //call对象
        Call call = okHttpClient.newCall(request);
        //call执行请求
        call.enqueue(new Callback() {   //异步
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("tag", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("tag","onResponse:"+json);
                app.clearRecentSet();
//                final UpLoadBean upLoadBean = new Gson().fromJson(json, UpLoadBean.class);
//                if (!TextUtils.isEmpty(json)){
//                    int code = upLoadBean.getCode();
//                    String str = String.valueOf(code);
//                    if (str.equals("200")){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                uploadTv.setText(upLoadBean.getRes());
//                                Glide.with(MainActivity.this).load(upLoadBean.getData().getUrl()).into(uploadImage);
//                            }
//                        });
//                    }else {
//                        Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });
    }
}


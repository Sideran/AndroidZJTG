package com.example.myapplication.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private List<Article> chatMessageList;
    private Context context;
    private LayoutInflater layoutInflater;

    public ListViewAdapter(Context context,List<Article> list){
        this.chatMessageList=list;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView msTitle, msTime, msContext, msPoint;
        public LinearLayout mPointLayout;

        public ViewHolder(View convertView) {

            imageView = (ImageView) convertView.findViewById(R.id.IV_list_Id);
            msTitle = (TextView) convertView.findViewById(R.id.TV_listTitle_Id);
            msTime = (TextView) convertView.findViewById(R.id.TV_listTime_Id);
            msContext = (TextView) convertView.findViewById(R.id.TV_listContext_Id);

            mPointLayout = (LinearLayout) convertView.findViewById(R.id.ll_point);
        }

        public static ViewHolder getHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);

            }
            return holder;

        }
    }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate( R.layout.layout_list_item, parent,false);
            }
            ViewHolder holder = ViewHolder.getHolder(convertView);
            Article chatMessage = chatMessageList.get(position);
            String content = chatMessage.getArticleContent();
            String time = chatMessage.getStartTime().toString();
            String title = chatMessage.getArticleTitle();
            int isRead = Integer.parseInt(chatMessage.getStatus());////是否已读（0未读 1已读）
            //item固定小红点layout
            LinearLayout pointLayout = holder.mPointLayout;
            //item固定小红点
            final TextView point = holder.msPoint;
            boolean visiable;
            if (isRead == 0) {
                visiable = true;
                pointLayout.setVisibility(visiable ? View.VISIBLE : View.GONE);
//                point.setText(String.valueOf(position));
                pointLayout.setTag(position);

            } else {
                visiable = false;
                pointLayout.setVisibility(visiable ? View.VISIBLE : View.GONE);
            }
            holder.msTitle.setText(title);
            holder.msTime.setText(time);
            holder.msContext.setText(content);


//        Glide.with(context).load("http://pic18.nipic.com/20120203/2457331_104836021342_2.jpg").into(holder.imageView);
            return convertView;
        }

        /**
         * 将毫秒数转为日期格式
         *
         * @param timeMillis
         * @return
         */
        private String formatTime(String timeMillis) {
            long timeMillisl = Long.parseLong(timeMillis);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(timeMillisl);
            return simpleDateFormat.format(date);
        }
    }


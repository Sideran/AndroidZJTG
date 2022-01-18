package com.example.myapplication.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Adapter.MyFragmentAdapter;
import com.example.myapplication.Datatest.Datapp;
import com.example.myapplication.R;
import com.example.myapplication.modle.UserInfo;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private final String TAG="message";
    private Datapp app;
    private UserInfo userInfo;

    //private ArrayList<Fragment> fragments;
    private TabLayoutMediator mediator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

//        getString();
        app=(Datapp) getApplication();


        this.setTitle(app.getUser().getDeptName()+" "+app.getUser().getNickName());
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        final String[] tabs = new String[]{"未学习课程", "已学习课程"};

        //禁用预加载
        viewPager2.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        //Adapter
        viewPager2.setAdapter(new MyFragmentAdapter(this));
        //viewPager 页面切换监听监听
        viewPager2.registerOnPageChangeCallback(changeCallback);

        mediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("未学习课程");
                        break;
                    case 1:
                        tab.setText("已学习课程");
                        break;

                }
                //这里可以自定义TabView
//                TextView tabView = new TextView(TabActivity.this);
//
//                int[][] states = new int[2][];
//                states[0] = new int[]{android.R.attr.state_selected};
//                states[1] = new int[]{};
//
//                int[] colors = new int[]{activeColor, normalColor};
//                ColorStateList colorStateList = new ColorStateList(states, colors);
//                tabView.setText(tabs[position]);
//                tabView.setTextSize(normalSize);
//                tabView.setTextColor(colorStateList);
//
//                tab.setCustomView(tabView);

            }
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
    }
    private ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            //可以来设置选中时tab的大小
            int tabCount = tabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                TextView tabView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
//                    tabView.setTextSize(activeSize);
//                    tabView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
//                    tabView.setTextSize(normalSize);
//                    tabView.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        mediator.detach();
        viewPager2.unregisterOnPageChangeCallback(changeCallback);
        super.onDestroy();
    }
}
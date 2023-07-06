package com.example.forecast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.forecast.city_manager.CityManagerActivity;


import com.example.forecast.entity.City;
import com.example.forecast.utils.JSONUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView addCityIv,moreIv;
    LinearLayout pointLayout;
    RelativeLayout outLayout;
    ViewPager mainVp;
//    ViewPager的数据源
    List<Fragment> fragmentList;
//    表示需要显示的城市的集合
    List<String> cityList;
//    表示ViewPager的页数指数器显示集合
    List<ImageView>imgList;
    private CityFragmentPagerAdapter adapter;
    private SharedPreferences pref;
    private int bgNum;
    private String st;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        st = getIntent().getStringExtra("city_name");
        addCityIv = findViewById(R.id.main_iv_add);
        moreIv = findViewById(R.id.main_iv_refresh);
        pointLayout = findViewById(R.id.main_layout_point);
        outLayout = findViewById(R.id.main_out_layout);
        mainVp = findViewById(R.id.main_vp);
//        添加点击事件
        addCityIv.setOnClickListener(this);
        moreIv.setOnClickListener(this);
        SharedPreferences pref1 = getSharedPreferences("CityData",MODE_PRIVATE);
        cityList = new ArrayList<>(Arrays.asList(pref1.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());
//        cityList = DBManager.queryAllCityName();  //获取数据库包含的城市信息列表
        fragmentList = new ArrayList<>();
        imgList = new ArrayList<>();
//        添加固定的城市天气
        if(cityList.size()==0){
            cityList.add("北京:101010100");
        }
        /* 因为可能搜索界面点击跳转此界面，会传值，所以此处获取一下*/
        try {
            Intent intent = getIntent();
            String city = intent.getStringExtra("city");
            if (!cityList.contains(city)&&!TextUtils.isEmpty(city)) {
                cityList.add(city);
            }
        }catch (Exception e){
            Log.i("Lee","程序出现问题了！！");
        }

//        初始化ViewPager页面的方法
        initPager();
        adapter = new CityFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mainVp.setAdapter(adapter);
//        创建小圆点指示器
        initPoint();
//        添加城市显示最后一个城市信息
        mainVp.setCurrentItem(fragmentList.size()-1);
//        设置ViewPager页面监听，小圆点跟着页面变化
        setPagerListener();

        SharedPreferences pref = getSharedPreferences("JsonData",MODE_PRIVATE);
        String name = pref.getString("北京","");
        if (name.equals("")) {
            JSONUtil jsonUtil = new JSONUtil(this);
            List<City> dataList = jsonUtil.getJson(); // 获取数据的方法
            for (City data : dataList) {
                // 使用 data 进行操作
                SharedPreferences.Editor editor
                        = getSharedPreferences("JsonData", MODE_PRIVATE).edit();
                editor.putString(data.getCity_name(), data.getCity_code());
                editor.apply();
            }

        }

    }


    private void setPagerListener() {
        /* 设置滑动监听事件*/
        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < imgList.size(); i++) {
                    imgList.get(i).setImageResource(R.mipmap.a1);
                }
                imgList.get(position).setImageResource(R.mipmap.a2);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initPoint() {
//        创建滑动小圆点图标 ViewPager页面指示器的函数
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView pIv = new ImageView(this);
            pIv.setImageResource(R.mipmap.a1);
            pIv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pIv.getLayoutParams();
            lp.setMargins(0,0,20,0);
            imgList.add(pIv);
            pointLayout.addView(pIv);
        }
        imgList.get(imgList.size()-1).setImageResource(R.mipmap.a2);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initPager() {

//        创建Fragment对象，添加到ViewPager数据源当中
        for (int i =0; i < cityList.size();i++){
            CityWeatherFragment cwFrag = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city",cityList.get(i));
            cwFrag.setArguments(bundle);
            fragmentList.add(cwFrag);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.main_iv_add:
                intent.setClass(this, CityManagerActivity.class);
                startActivity(intent);
                break;
                /*在这里加刷新功能嗷记得*/
            case R.id.main_iv_refresh:
                Refresh();
                break;
        }

    }

    private void Refresh() {
        SharedPreferences pref1 = getSharedPreferences("CityData",MODE_PRIVATE);
        @SuppressLint({"NewApi", "LocalSuppress"}) List<String> list = new ArrayList<>(Arrays.asList(pref1.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());

        if (list.size()==0) {
            list.add("北京:101010100");
        }
        cityList.clear();    //重写加载之前，清空原本数据源
        cityList.addAll(list);
//        剩余城市也要创建对应的fragment页面
        fragmentList.clear();
        initPager();
        adapter.notifyDataSetChanged();
//        页面数量发生改变，指示器的数量也会发生变化，重写设置添加指示器
        imgList.clear();
        pointLayout.removeAllViews();   //将布局当中所有元素全部移除
        initPoint();
        mainVp.setCurrentItem(fragmentList.size()-1);

    }

    /* 当页面重新加载时会调用的函数，这个函数在页面获取焦点之前进行调用，此处完成ViewPager页数的更新*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onRestart() {
        super.onRestart();
//        获取数据库当中还剩下的城市集合
        SharedPreferences pref1 = getSharedPreferences("CityData",MODE_PRIVATE);
        List<String> list = new ArrayList<>(Arrays.asList(pref1.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());

        if (list.size()==0) {
            list.add("北京:101010100");
        }
        cityList.clear();    //重写加载之前，清空原本数据源
        cityList.addAll(list);
//        剩余城市也要创建对应的fragment页面
        fragmentList.clear();
        initPager();
        adapter.notifyDataSetChanged();
//        页面数量发生改变，指示器的数量也会发生变化，重写设置添加指示器
        imgList.clear();
        pointLayout.removeAllViews();   //将布局当中所有元素全部移除
        initPoint();
        mainVp.setCurrentItem(fragmentList.size()-1);
    }
}
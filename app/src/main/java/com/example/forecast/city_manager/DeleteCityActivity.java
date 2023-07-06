package com.example.forecast.city_manager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.forecast.R;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteCityActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView errorIv,rightIv;
    ImageView delete_laji;
    ListView deleteLv;
    List<String> mDatas;   //listview的数据源
    List<String> tempDatas;
    List<String>deleteCitys;  //表示存储了删除的城市信息
    Map<Integer,String> mySelect;
    List<String> colCity; //存储了收藏城市信息
    List<String> nolcolCity; //存储了未收藏城市信息

    private DeleteCityAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_city);
        errorIv = findViewById(R.id.delete_iv_error);
        rightIv = findViewById(R.id.delete_iv_right);
        delete_laji = findViewById(R.id.delete_laji);
        deleteLv = findViewById(R.id.delete_lv);
        deleteCitys = new ArrayList<>();
        colCity = new ArrayList<>();
        nolcolCity = new ArrayList<>();
//        设置点击监听事件
        errorIv.setOnClickListener(this);
        rightIv.setOnClickListener(this);
        delete_laji.setOnClickListener(this);
//        适配器的设置
        List<String> tempDatas = new ArrayList<>();
        Map<Integer,String> mySelect = new HashMap<Integer,String>();



        SharedPreferences pref = getSharedPreferences("CityData",MODE_PRIVATE);

            if(tempDatas != null){
                tempDatas = adapter.getTempDatas();
            }
            else{
                tempDatas =null;
            }
            if(colCity !=null){
                colCity = adapter.getColCity();
            }
            else{
                colCity = null;
            }
            if(mySelect != null){
                mySelect = adapter.getMySelect();
            }
            else {
                mySelect = null;
            }
            init_twice(tempDatas,colCity,mySelect);
            mDatas = new ArrayList<>(Arrays.asList(pref.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                    .collect(Collectors.toList());
            adapter = new DeleteCityAdapter(this, mDatas, deleteCitys,colCity,mySelect,tempDatas);
            deleteLv.setAdapter(adapter);



    }

    @SuppressLint("NewApi")
    private void init_twice(List<String> tempDatas, List<String> colCity, Map<Integer,String> mySelect) {
        SharedPreferences sourcePrefs = getApplicationContext().getSharedPreferences("CityData", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sourcePrefs.getAll();
        SharedPreferences targetPrefs = getApplicationContext().getSharedPreferences("linshiData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = targetPrefs.edit();
        editor.clear();
        editor.apply();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Set) {
                editor.putStringSet(key, (Set<String>) value);
            }
        }
        editor.apply();
    }

    public Map<Integer,String> deleteSelect(){
        Map<Integer,String> mySelect = adapter.getMySelect();
        Map<Integer,String> myDelete = adapter.getMySelect();
        for(Map.Entry<Integer, String> entry : myDelete.entrySet()){
            String value = entry.getValue();
            mDatas.remove(value);
        }
        return mySelect;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Intent intent;
        intent = new Intent(this,CityManagerActivity.class);
        switch (v.getId()){
            case R.id.delete_iv_error:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示信息").setMessage("您确定要舍弃更改么？")
                        .setPositiveButton("舍弃更改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();   //关闭当前的activity
                            }
                        });
                builder.setNegativeButton("取消",null);
                builder.create().show();
                break;
            case R.id.delete_laji:
                SharedPreferences pref = getSharedPreferences("CityData",MODE_PRIVATE);
                modify_sql(pref,null,null,mySelect);
                mySelect = deleteSelect();
                adapter = new DeleteCityAdapter(this, mDatas, deleteCitys,colCity,mySelect,tempDatas);
                deleteLv.setAdapter(adapter);
                bundle.putSerializable("mapKey", (Serializable) adapter.getMySelect());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.delete_iv_right:
                tempDatas = adapter.getTempDatas();
                colCity = adapter.getColCity();
                modify_sql(getSharedPreferences("CityData",MODE_PRIVATE), tempDatas,colCity,null);
                mDatas = modify_colcity(adapter.getTempDatas());
                mDatas = modify_colcity_1(adapter.getColCity());
                adapter = new DeleteCityAdapter(this, mDatas, deleteCitys,colCity,mySelect,tempDatas);
                deleteLv.setAdapter(adapter);
                intent = new Intent(this,CityManagerActivity.class);
                bundle.putSerializable("listKey", (Serializable) adapter.getTempDatas());
                bundle.putSerializable("colKey", (Serializable) adapter.getColCity());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());


        }
    }


    public List<String> modify_colcity_1(List<String> colCity){
        List<String> linshi = adapter.getTempDatas();
        Iterator<String> iterator = mDatas.iterator();
        int i =0;
        for(String item : linshi){
            while(iterator.hasNext()){
                String item1 = iterator.next();
                if(item.equals(item1)){
                    StringBuilder t = new StringBuilder(item1);
                    t.setCharAt(13,'已');
                    mDatas.set(i, t.toString());
                }
                i++;
            }
        }

        return mDatas;
    }



    public List<String> modify_colcity(List<String> tempDatas){
        List<String> linshi = adapter.getTempDatas();
        Iterator<String> iterator = mDatas.iterator();
        int i =0;
        for(String item : linshi){
            while(iterator.hasNext()){
                String item1 = iterator.next();
                if(item.equals(item1)){
                    StringBuilder t = new StringBuilder(item1);
                    t.setCharAt(13,'未');
                    mDatas.set(i, t.toString());
                }
                i++;
            }
        }

        return mDatas;
    }


    @SuppressLint("NewApi")
    public void modify_sql(SharedPreferences pref,List<String> tempDatas, List<String> colCity, Map<Integer,String> mySelect){

        List<String> list = new ArrayList<>(Arrays.asList(pref.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());
        list = new ArrayList<>(Arrays.asList(pref.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());
        SharedPreferences.Editor editor = pref.edit();
        if(mySelect != null){
            for(Map.Entry<Integer, String> entry : mySelect.entrySet()){
                String value = entry.getValue();
                list.remove(value);
            }
        }


        if(tempDatas != null){
            for(String item: tempDatas){
                String linshi = item.substring(0, 2);
                StringBuilder t = new StringBuilder(item);
                t.setCharAt(13, '未');
                Iterator<String> iterator = list.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    String item0 = iterator.next();
                    if (item0.contains(linshi)) {
                        list.set(i,t.toString());
                    }
                    i++;
                }
            }
        }


        if(colCity != null){
            for(String item: colCity){
                String linshi = item.substring(0, 2);
                StringBuilder t = new StringBuilder(item);
                t.setCharAt(13, '已');
                Iterator<String> iterator = list.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    String item0 = iterator.next();
                    if (item0.contains(linshi)) {
                        list.set(i,t.toString());
                    }
                    i++;
                }
            }
        }

        Comparator<String> customComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                char c1 = (s1.length() >= 14) ? s1.charAt(13) : '\0';
                char c2 = (s2.length() >= 14) ? s2.charAt(13) : '\0';

                if (c1 == '已' && c2 != '已') {
                    return -1; // 已开头的字符串排在前面
                } else if (c1 != '已' && c2 == '已') {
                    return 1; // 未开头的字符串排在后面
                } else {
                    return s1.compareTo(s2); // 其他情况按照字符串默认顺序进行排序
                }
            }
        };

        Collections.sort(list, customComparator);

        modify_SharedPreferences(list);
    }



    @SuppressLint("NewApi")
    public void modify_SharedPreferences(List<String> dataList){
        SharedPreferences pref = getSharedPreferences("CityData",MODE_PRIVATE);
        /*修改数据库*/
        StringBuilder stringBuilder = new StringBuilder();
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        for (int i = 0; i < dataList.size(); i++) {
            stringBuilder.append(dataList.get(i));
            if (i < dataList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        String dataString = stringBuilder.toString();

        editor.putString("city", dataString);
        editor.apply();


    }
}
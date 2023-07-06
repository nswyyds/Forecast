package com.example.forecast.city_manager;



import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.forecast.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteCityAdapter extends BaseAdapter {
    Context context;
    List<String> mDatas;
    List<String> deleteCitys;
    List<String> tempDatas;
    List<String> colCity;
    List<String> nolcolCity;
    Map<Integer, String> mySelect;

    CheckBox cBOX;

    public DeleteCityAdapter(Context context,
                             List<String> mDatas, List<String> deleteCitys, List<String> colCity, Map<Integer, String> mySelect, List<String> tempDatas) {
        this.context = context;
        this.mDatas = mDatas;
        this.tempDatas = tempDatas;
        this.deleteCitys = deleteCitys;
        this.colCity = colCity;
        this.mySelect = mySelect;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setNolcolCity(List<String> nolcolCity) {
        this.nolcolCity = nolcolCity;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_deletecity, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SharedPreferences pref = context.getSharedPreferences("CityData", MODE_PRIVATE);
        SharedPreferences pref1 = context.getSharedPreferences("linshiData", MODE_PRIVATE);


        this.mDatas = new ArrayList<>(Arrays.asList(pref1.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());
        final String[] city = {mDatas.get(position)};
        ViewHolder finalHolder = holder;

        holder.tv.setText(city[0].substring(0, 2));
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMap(position, city[0]);
                notifyDataSetChanged();  //删除了提示适配器更新
            }
        });

        if (city[0].substring(13, 16).equals("已收藏")) {
            holder.col.setChecked(true);
            notifyDataSetChanged();  //删除了提示适配器更新
        }
        if (city[0].substring(13, 16).equals("未收藏")) {
            holder.col.setChecked(false);
            notifyDataSetChanged();  //删除了提示适配器更新
        }
        holder.col.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                if (city[0].substring(13, 16).equals("已收藏")) {
                    tempDatas.add(city[0]);
                    modify_sql(pref1,tempDatas,null,null);
                    //finalHolder.col.setChecked(false);
                    notifyDataSetChanged();
                } else if (city[0].substring(13, 16).equals("未收藏")) {
                    colCity.add(city[0]);
                    modify_sql(pref1,null,colCity,mySelect);
                    //finalHolder.col.setChecked(true);
                    notifyDataSetChanged();
                }

            }
        });

        return convertView;
    }

    public List<String> getTempDatas() {
        return tempDatas;
    }

    public List<String> getColCity() {
        return colCity;
    }

    public List<String> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<String> mDatas) {
        this.mDatas = mDatas;
    }

    public void addMap(int position, String wayBillNo) {
        Integer pos = Integer.valueOf(position);
        mySelect.put(pos, wayBillNo);
    }

    class ViewHolder {
        TextView tv;
        CheckBox iv;
        CheckBox col;

        public ViewHolder(View itemView) {
            tv = itemView.findViewById(R.id.item_delete_tv);
            iv = itemView.findViewById(R.id.item_delete_iv);
            col = itemView.findViewById(R.id.item_col_iv);
        }
    }

    public void modifyData(String cityinfo, boolean flag) {
        List<String> linshi = this.mDatas;
        if (flag == false) {
            String cityname = cityinfo.substring(0, 2);
            StringBuilder t = new StringBuilder(cityinfo);
            t.setCharAt(13, '未');
            Iterator<String> iterator = this.mDatas.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String item0 = iterator.next();
                if (item0.contains(cityname)) {
                    this.mDatas.set(i, t.toString());
                }
                i++;
            }
        } else {
            String cityname = cityinfo.substring(0, 2);
            StringBuilder t = new StringBuilder(cityinfo);
            t.setCharAt(13, '已');
            Iterator<String> iterator = this.mDatas.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String item0 = iterator.next();
                if (item0.contains(cityname)) {
                    this.mDatas.set(i, t.toString());
                }
                i++;
            }
        }
    }

    public Map<Integer, String> getMySelect() {
        return mySelect;
    }

    public void setMySelect(Map<Integer, String> mySelect) {
        this.mySelect = mySelect;
    }


    @SuppressLint("NewApi")
    public void modify_sql(SharedPreferences pref, List<String> tempDatas, List<String> colCity, Map<Integer, String> mySelect) {

        List<String> list = new ArrayList<>(Arrays.asList(pref.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());
        list = new ArrayList<>(Arrays.asList(pref.getString("city", "").split(","))).stream().distinct().filter(element -> element != null && !element.isEmpty())
                .collect(Collectors.toList());
        SharedPreferences.Editor editor = pref.edit();
        if (mySelect != null) {
            for (Map.Entry<Integer, String> entry : mySelect.entrySet()) {
                String value = entry.getValue();
                list.remove(value);
            }
        }


        if (tempDatas != null) {
            for (String item : tempDatas) {
                String linshi = item.substring(0, 2);
                StringBuilder t = new StringBuilder(item);
                t.setCharAt(13, '未');
                Iterator<String> iterator = list.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    String item0 = iterator.next();
                    if (item0.contains(linshi)) {
                        list.set(i, t.toString());
                    }
                    i++;
                }
            }
        }


        if (colCity != null) {
            for (String item : colCity) {
                String linshi = item.substring(0, 2);
                StringBuilder t = new StringBuilder(item);
                t.setCharAt(13, '已');
                Iterator<String> iterator = list.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    String item0 = iterator.next();
                    if (item0.contains(linshi)) {
                        list.set(i, t.toString());
                    }
                    i++;
                }
            }
        }

        modify_SharedPreferences(list);
    }

    @SuppressLint("NewApi")
    public void modify_SharedPreferences(List<String> dataList){
        SharedPreferences pref = context.getSharedPreferences("linshiData",MODE_PRIVATE);
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

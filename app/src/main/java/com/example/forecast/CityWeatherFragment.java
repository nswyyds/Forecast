package com.example.forecast;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.forecast.base.BaseFragment;
import com.example.forecast.entity.Data;
import com.example.forecast.entity.Forecast;
import com.example.forecast.entity.Root;
import com.example.forecast.utils.HttpUtils;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityWeatherFragment extends BaseFragment implements View.OnClickListener {
    TextView cityTv,conditionTv,windTv,tempRangeTv,dateTv;
    ImageView dayIv;
    LinearLayout futureLayout;
    ScrollView outLayout;
    String city;
    private SharedPreferences pref;
    private int bgNum;

    private List<Forecast> forecast;

    private Data  data;
    private String st;

    public CityWeatherFragment() {

    }
    public CityWeatherFragment(String st) {
        super();
        this.st = st;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        initView(view);

        Bundle bundle = getArguments();
        city = bundle.getString("city");

        parseShowData(city.substring(3,12));

        return view;
    }


    private void parseShowData(String result) {
//        使用gson解析数据

        HttpUtils utils = new HttpUtils();
        utils.setUrl_Code(result);
        Root root = utils.getURlContext();
        forecast = root.getData().getForecast();
        data = root.getData();

//        设置TextView
        dateTv.setText(root.getTime()); // 获取今日的日期
        cityTv.setText(root.getCityInfo().getCity()); // 获取今日的城市
        windTv.setText(forecast.get(0).getFx()+forecast.get(0).getFl()); //风向
        tempRangeTv.setText(forecast.get(0).getHigh()+"/"+forecast.get(0).getLow()); //最低温/最高温
        conditionTv.setText(forecast.get(0).getType()); //天气情况
//        获取未来七天的天气情况，加载到layout当中

        for (int i = 1; i < 8; i++) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_center, null);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            futureLayout.addView(itemView);
            TextView idateTv = itemView.findViewById(R.id.item_center_tv_date);
            TextView iconTv = itemView.findViewById(R.id.item_center_tv_con);
            TextView windTv = itemView.findViewById(R.id.item_center_tv_wind);
            TextView itemprangeTv = itemView.findViewById(R.id.item_center_tv_temp);
//          获取对应的位置的天气情况
            Forecast dailyBean = forecast.get(i);
            idateTv.setText(dailyBean.getDate());
            iconTv.setText(dailyBean.getType());
            itemprangeTv.setText(dailyBean.getLow()+"/"+dailyBean.getHigh());
            windTv.setText(dailyBean.getFx());
        }
    }



    private void initView(View view) {
//        用于初始化控件操作
        cityTv = view.findViewById(R.id.frag_tv_city);
        conditionTv = view.findViewById(R.id.frag_tv_condition);
        windTv = view.findViewById(R.id.frag_tv_wind);
        tempRangeTv = view.findViewById(R.id.frag_tv_temprange);
        dateTv = view.findViewById(R.id.frag_tv_date);
        futureLayout = view.findViewById(R.id.frag_center_layout);
        outLayout = view.findViewById(R.id.out_layout);
    }

    @Override
    public void onClick(View v) {

    }

}
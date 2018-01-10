package com.example.xiayong.framework_selflearn.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 工具类: 解析json文件
 * Created by cugyong on 2018/1/9.
 */

public class ParseJson {

    public static Map<String, List<String> > parseCityDatas(Context context){
        Map<String, List<String> > cityDatas = new TreeMap<>();
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open("city_datas.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null){
                builder.append(line);
            }
            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("provinces");
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject1.getJSONArray("citys");
                for (int j=0; j<jsonArray1.length(); j++){
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                    String cityName = jsonObject2.getString("citysName");
                    String firstChar = PinYin4J.getPinYinFirstLetter(cityName);
                    firstChar = firstChar.toUpperCase();
                    if (cityDatas.containsKey(firstChar)){
                        List<String> datas = cityDatas.get(firstChar);
                        datas.add(cityName);
                    } else {
                        List<String> datas = new ArrayList<>();
                        datas.add(cityName);
                        cityDatas.put(firstChar, datas);
                    }
                }
            }
            br.close();
            isr.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return cityDatas;
    }
}

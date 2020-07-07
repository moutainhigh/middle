package com.njwd.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class SignUtil {

    /**
     * 按ASCII码给json对象排序（规定：升序）
     * @param json
     * @return
     */
    public  static String sortJson(String json){
        String res = "";
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            ArrayList<String> nameList = new ArrayList<>();
            Set<String> keySet = jsonObject.keySet();
            Iterator keys = keySet.iterator();
            while(keys.hasNext()) {
                String key = keys.next().toString();
                nameList.add(key);
            }
            //key排序，升序
            Collections.sort(nameList);
            StringBuffer sb = new StringBuffer();
            sb.append("{");
            for (int i = 0; i < nameList.size(); i++) {
                String name = nameList.get(i);
                //null值不做排序
                if(jsonObject.get(name)!=null){
                    String value = jsonObject.getString(name);
                    if(i != 0){sb.append(",");}
                    //添加键值对，区分字符串与json对象
                    if(value.startsWith("{")||value.startsWith("[")){
                        sb.append(String.format("\"%s\":%s",name,value));
                    }else{
                        sb.append(String.format("\"%s\":\"%s\"",name,value));
                    }
                }
            }
            sb.append("}");
            res = sb.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return res;
    }


    /**
     * 按ASCII码给json对象排序（规定：升序）
     * @param json
     * @return
     */
    public  static LinkedHashMap<String,Object> sortJsonForMap(String json){
        LinkedHashMap  resultMap = new LinkedHashMap<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            ArrayList<String> nameList = new ArrayList<>();
            Set<String> keySet = jsonObject.keySet();
            Iterator keys = keySet.iterator();
            while(keys.hasNext()) {
                String key = keys.next().toString();
                nameList.add(key);
            }
            //key排序，升序
            Collections.sort(nameList);
            for (int i = 0; i < nameList.size(); i++) {
                String name = nameList.get(i);
                //null值不做排序
                if(jsonObject.get(name)!=null){
                    resultMap.put(name,jsonObject.get(name));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    /**
     * URLEncode编码
     * @param json
     * @return
     */
    public static String enCodeJson(String json){
        String res = "";
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            ArrayList<String> nameList = new ArrayList<>();
            Set<String> keySet = jsonObject.keySet();
            Iterator keys = keySet.iterator();
            while(keys.hasNext()) {
                String key = keys.next().toString();
                nameList.add(key);
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nameList.size(); i++) {
                String name = nameList.get(i);
                //null值不做编码
                if(jsonObject.get(name)!=null){
                    String value = jsonObject.getString(name);
                    sb.append(name).append("=").append(value);
                    //添加键值对，区分字符串与json对象
//                    if(value.startsWith("{")||value.startsWith("[")){
//                        sb.append(String.format("\"%s\":%s",name,value));
//                    }else{
//                        sb.append(String.format("\"%s\":\"%s\"",name,value));
//                    }
                }
            }
            res = sb.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return res;
    }

    public static void main(String[] args) throws Exception{

    }



}

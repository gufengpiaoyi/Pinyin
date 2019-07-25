package com.troila.lib;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Change {
    public final static String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private static City city = new City();
    private static List<City.LocationBean.CountryRegionBean.StateBean.CityBean> data = new ArrayList<>();
    private static List<LetterModel> letterData = new ArrayList<>();
    public static void main(String[] str){
        getProvince();
        initData();
        getJson(new JSONArray(letterData).toString());
    }
    private static void getProvince(){
        data.clear();
        JSONObject js;
        try {
//            String path = Change.class.getClassLoader().getResource("src/main/resources/city.json").getPath();
            js = new JSONObject(readJsonFile("D:/ww/workspace4git/samples/Pinyin/lib/src/main/resources/city.json"));
        } catch (Exception e) {
            e.printStackTrace();
            js = new JSONObject();
        }
        city = new Gson().fromJson(js.toString(),new TypeToken<City>(){}.getType());
        for (City.LocationBean.CountryRegionBean model:city.getLocation().getCountryRegion()) {
            if(model.getState() != null) {
                for (City.LocationBean.CountryRegionBean.StateBean bean : model.getState()) {
                    if(bean.getCity() != null) {
                        for (City.LocationBean.CountryRegionBean.StateBean.CityBean cityBean : bean.getCity()) {
                            cityBean.setLetter(PinyinUtil.getPinYinFirstLetter4City(cityBean.getName()).toUpperCase());
                            cityBean.setCountry(model.getName());
                            cityBean.setState(bean.getName());
                            cityBean.setPinyin(PinyinUtil.getPinYinFull(cityBean.getName()));
                            cityBean.setJane(PinyinUtil.getPinYinJane(cityBean.getName()));
                            data.add(cityBean);
                        }
                    }
                }
            }
        }
    }
    /**
     * 读取json文件，返回json串
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static void initData() {
        //#符号
        ArrayList<City.LocationBean.CountryRegionBean.StateBean.CityBean> tempdata = new ArrayList<>();
        tempdata.addAll(data);
        //排序
        Collections.sort(tempdata,new Comparator<City.LocationBean.CountryRegionBean.StateBean.CityBean>() {
            @Override
            public int compare(City.LocationBean.CountryRegionBean.StateBean.CityBean s1, City.LocationBean.CountryRegionBean.StateBean.CityBean s2) {
                String o1 = s1.getName();
                String o2 = s2.getName();
                for (int i = 0; i < o1.length() && i < o2.length(); i++) {

                    int codePoint1 = o1.charAt(i);
                    int codePoint2 = o2.charAt(i);

                    if (Character.isSupplementaryCodePoint(codePoint1)
                            || Character.isSupplementaryCodePoint(codePoint2)) {
                        i++;
                    }

                    if (codePoint1 != codePoint2) {
                        if (Character.isSupplementaryCodePoint(codePoint1)
                                || Character.isSupplementaryCodePoint(codePoint2)) {
                            return codePoint1 - codePoint2;
                        }

                        String pinyin1 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint1) == null
                                ? null : PinyinHelper.toHanyuPinyinStringArray((char) codePoint1)[0];
                        String pinyin2 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint2) == null
                                ? null : PinyinHelper.toHanyuPinyinStringArray((char) codePoint2)[0];

                        if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
                            if (!pinyin1.equals(pinyin2)) {
                                return pinyin1.compareTo(pinyin2);
                            }
                        } else {
                            return codePoint1 - codePoint2;
                        }
                    }
                }
                return o1.length() - o2.length();
            }
        });
        Collections.reverse(tempdata);
        //数据遍历
        for (int i = 0; i < letter.length; i++) {
            ArrayList<City.LocationBean.CountryRegionBean.StateBean.CityBean> item = new ArrayList<>();
            LetterModel letterModel = new LetterModel();
            for (int j = tempdata.size() - 1; j > -1; j--) {
                City.LocationBean.CountryRegionBean.StateBean.CityBean cityBean = tempdata.get(j);
                if (letter[i].equals(cityBean.getLetter())) {
                    item.add(tempdata.get(j));
                    tempdata.remove(j);
                }
            }
            if ((item != null && item.size() > 0)) {
                letterModel.setLetter(letter[i]);
                letterModel.setList(item);
                letterData.add(letterModel);
            }
        }

        if (tempdata.size() > 0) {
            if(letterData.size()>0){
                LetterModel letterModel = letterData.get(letterData.size() - 1);
                if ("#".equals(letterModel.getLetter())) {
                    letterModel.getList().addAll(tempdata);
                } else {
                    LetterModel lm = new LetterModel();
                    lm.setLetter("#");
                    lm.setList(tempdata);
                    letterData.add(lm);
                }
            }else{
                LetterModel lm = new LetterModel();
                lm.setLetter("#");
                lm.setList(tempdata);
                letterData.add(lm);
            }
        }
    }
    private static void getJson(String json){
        try {
            File f = new File("D:/city.txt");
//            if(!f.exists()){
//                f.mkdir();
//            }
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter dos = new OutputStreamWriter(fos);
            dos.write(json);
            dos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

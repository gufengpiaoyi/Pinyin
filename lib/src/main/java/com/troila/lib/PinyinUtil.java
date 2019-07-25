package com.troila.lib;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * $desc
 *
 * @author Administrator
 * @time 2016/11/23
 */

public class PinyinUtil {
    /**
     * 姓氏的特殊汉字
     */
    private char[] hanzi = {'种', '单', '解', '查', '曾', '盖', '缪', '朴', '繁', '仇', '么'};
    /**
     * 特殊姓氏的读音
     */
    private String[] pinyin = {"chong", "shan", "xie", "zha", "zeng", "ge", "miao", "piao", "po",
            "qiu", "yao"};

    /**
     * 获取拼音的集合（包含多音字）
     *
     * @param content
     * @return
     */
    private String[][] getPinyinss(String content) {
        char[] srcChar = content.toCharArray();
        HanyuPinyinOutputFormat hpof = new HanyuPinyinOutputFormat();
        //HanyuPinyinCaseType.UPPERCASE表示大写，HanyuPinyinCaseType.LOWERCASE表示小写
        hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        /**
         * HanyuPinyinToneType.WITHOUT_TONE表示不标声调，HanyuPinyinToneType
         * .WITH_TONE_NUMBER表示在拼音末尾标注数字1-4表示四个声调，HanyuPinyinToneType.WITH_TONE_MARK表示在拼音上方标注四个声调的符号
         */
        hpof.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        /**
         * HanyuPinyinVCharType.WITH_V表示ü显示为字符v，HanyuPinyinVCharType.WITH_U_AND_COLON表示ü显示为字符u:，
         * HanyuPinyinVCharType.WITH_U_UNICODE表示ü显示为字符ü
         * 当声调形式设置为符号声调时，setVCharType只能选择HanyuPinyinVCharType.WITH_U_UNICODE，
         * 否则运行时会报错“net.sourceforge.pinyin4j.format.exception
         * .BadHanyuPinyinOutputFormatCombination: tone marks cannot be added to v or u:”
         */
        hpof.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String[][] temp = new String[content.length()][];
        for (int i = 0; i < srcChar.length; i++) {
            try {
                char c = srcChar[i];
                // 是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
                    if (i == 0) {
                        String[] str = new String[1];
                        String first = getSpeWord(c);
                        if (first != null) {
                            str[0] = first;
                            temp[i] = str;
                        } else {
                            temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hpof);
                        }
                    } else {
                        temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hpof);
                    }

                } else if (((int) c >= 65 && (int) c <= 90)
                        || ((int) c >= 97 && (int) c <= 122)) {
                    temp[i] = new String[]{String.valueOf(srcChar[i])};
                } else {
                    temp[i] = new String[]{""};
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        return temp;
    }

    /**
     * 获取拼音的集合（多音字默认取第一个）
     *
     * @param content
     * @return
     */
    public String[] getPinyins(String content) {
        char[] srcChar = content.toCharArray();
        HanyuPinyinOutputFormat hpof = new HanyuPinyinOutputFormat();
        //HanyuPinyinCaseType.UPPERCASE表示大写，HanyuPinyinCaseType.LOWERCASE表示小写
        hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        /**
         * HanyuPinyinToneType.WITHOUT_TONE表示不标声调，
         * HanyuPinyinToneType.WITH_TONE_NUMBER表示在拼音末尾标注数字1-4表示四个声调，
         * HanyuPinyinToneType.WITH_TONE_MARK表示在拼音上方标注四个声调的符号
         */
        hpof.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        /**
         * HanyuPinyinVCharType.WITH_V表示ü显示为字符v，HanyuPinyinVCharType.WITH_U_AND_COLON表示ü显示为字符u:，
         * HanyuPinyinVCharType.WITH_U_UNICODE表示ü显示为字符ü
         * 当声调形式设置为符号声调时，setVCharType只能选择HanyuPinyinVCharType.WITH_U_UNICODE，
         * 否则运行时会报错“net.sourceforge.pinyin4j.format.exception
         * .BadHanyuPinyinOutputFormatCombination: tone marks cannot be added to v or u:”
         */
        hpof.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String[] temp = new String[content.length()];
        for (int i = 0; i < srcChar.length; i++) {
            try {
                char c = srcChar[i];
                // 是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
                    if (i == 0) {
                        String first = getSpeWord(c);
                        if (first != null) {
                            temp[i] = first;
                        } else {
                            temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hpof)[0];
                        }
                    } else {
                        temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hpof)[0];
                    }

                } else if (((int) c >= 65 && (int) c <= 90)
                        || ((int) c >= 97 && (int) c <= 122)) {
                    temp[i] = String.valueOf(srcChar[i]);
                } else {
                    temp[i] = "";
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        return temp;
    }

    /**
     * 获取全拼
     *
     * @param content
     * @return
     */
    public String getPinyin(String content) {
        StringBuffer sb = new StringBuffer();
        String[] pinyins = getPinyins(content);
        for (int i = 0; i < pinyins.length; i++) {
            sb.append(pinyins[i]);
        }
        return sb.toString();
    }

    /**
     * 获取简拼
     *
     * @param content
     * @return
     */
    public String getPinyinJane(String content) {
        StringBuffer sb = new StringBuffer();
        String[] pinyins = getPinyins(content);
        for (int i = 0; i < pinyins.length; i++) {
            if (!"".equals(pinyins[i])) {
                sb.append(pinyins[i]);
            }

        }
        return sb.toString();
    }

    /**
     * 特殊姓氏汉字返回的结果
     */
    private String getSpeWord(char word) {
        for (int i = 0; i < hanzi.length; i++) {
            if (hanzi[i] == word) {
                return pinyin[i];
            }
        }
        return null;

    }

    /**
     * 判断字符串中是否有中文
     *
     * @param str
     * @return
     */
    public static boolean isChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    /**
     * 判断字符串是否全是中文
     *
     * @param str
     * @return
     */
    public static boolean isAllChineseChar(String str) {
        String reg = "[\\u4e00-\\u9fa5]+";

        return str.matches(reg);
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toLowerCase().toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    } else {
                        pybf.append(arr[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    pybf.append(arr[i]);
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }

    /**
     * 获取汉字串拼音，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    public static String getFullSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toLowerCase().toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0]);
                    } else {
                        pybf.append(arr[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    pybf.append(arr[i]);
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }

    /**
     * 获取汉字串拼音，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    public static String[] getFullSpellArray(String chinese) {
        char[] arr = chinese.toLowerCase().toCharArray();
        String[] arrays = new String[arr.length];
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        arrays[i] = temp[0];
                    } else {
                        arrays[i] = String.valueOf(arr[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    arrays[i] = String.valueOf(arr[i]);
                    e.printStackTrace();
                }
            } else {
                arrays[i] = String.valueOf(arr[i]);
            }
        }
        return arrays;
    }

    /**
     * 获取汉字字符串的第一个字母
     */
    public static String getPinYinFirstLetter(String str) {

        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        char c = str.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyinArray != null) {
            sb.append(pinyinArray[0].charAt(0));
        } else {
            sb.append(c);
        }
        return sb.toString();
    }

    private static Map<String, List<String>> pinyinMap = new HashMap<String, List<String>>();
    /**
     * 初始化 所有的多音字词组
     *
     * @param fileName
     */
    public static void initPinyin(String fileName) {
        // 读取多音字的全部拼音表;
        InputStream file = null;
        try {
//            String path = Change.class.getClassLoader().getResource(fileName).getPath();
            file = new FileInputStream(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(file));

        String s = null;
        try {
            while ((s = br.readLine()) != null) {

                if (s != null) {
                    String[] arr = s.split("#");
                    String pinyin = arr[0];
                    String chinese = arr[1];

                    if(chinese!=null){
                        String[] strs = chinese.split(" ");
                        List<String> list = Arrays.asList(strs);
                        pinyinMap.put(pinyin, list);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获取第一个字母
     */
    public static String getPinYinFirstLetter4City(String chinese) {

        if(pinyinMap.size() == 0) {
            initPinyin("D:/ww/workspace4git/samples/Pinyin/lib/src/main/resources/duoyinzi_dic.txt");
        }
        StringBuffer pinyin = new StringBuffer();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        char[] arr = chinese.toCharArray();
            char ch = arr[0];

            if (ch > 128) { // 非ASCII码
                // 取得当前汉字的所有全拼
                try {

                    String[] results = PinyinHelper.toHanyuPinyinStringArray(
                            ch, defaultFormat);

                    if (results == null) {  //非中文

                        return "#";
                    } else {

                        int len = results.length;

                        if (len == 1) { // 不是多音字
                            pinyin.append(results[0].charAt(0));

                        }else if(results[0].equals(results[1])){    //非多音字 有多个音，取第一个
                            pinyin.append(results[0].charAt(0));

                        }else { // 多音字

                            int length = chinese.length();


                            String s = null;

                            List<String> keyList =null;

                            for (int x = 0; x < len; x++) {

                                String py = results[x];

                                if(py.contains("u:")){  //过滤 u:
                                    py = py.replace("u:", "v");
                                }

                                keyList = pinyinMap.get(py);

                                if (length>=3) {   //后向匹配2个汉字  大西洋
                                    s = chinese.substring(0, 3);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        break;
                                    }
                                }

                                if (length >=2) {   //后向匹配 1个汉字  大西
                                    s = chinese.substring(0,2);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        break;
                                    }
                                }
                            }

                            if (pinyin.length() == 0) {    //都没有找到，匹配默认的 读音  大
                                pinyin.append(results[0].charAt(0));
                            }
                        }
                    }

                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    if(pinyin.length()==0){
                        pinyin.append("#");
                    }
                }
            } else {
                pinyin.append("#");
            }
        return pinyin.toString();
    }
    /**
     * 获取全拼
     */
    public static String getPinYinFull(String chinese) {

        if(pinyinMap.size() == 0) {
            initPinyin("D:/ww/workspace4git/samples/Pinyin/lib/src/main/resources/duoyinzi_dic.txt");
        }
        StringBuffer pinyin = new StringBuffer();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        char[] arr = chinese.toCharArray();
        for(char ch:arr){
            if (ch > 128) { // 非ASCII码
                // 取得当前汉字的所有全拼
                try {

                    String[] results = PinyinHelper.toHanyuPinyinStringArray(
                            ch, defaultFormat);

                    if (results == null) {  //非中文


                    } else {
                        StringBuffer dy = new StringBuffer();
                        int len = results.length;

                        if (len == 1) { // 不是多音字
                            pinyin.append(results[0]);
                            dy.append(results[0]);
                        }else if(results[0].equals(results[1])){    //非多音字 有多个音，取第一个
                            pinyin.append(results[0]);
                            dy.append(results[0]);

                        }else { // 多音字

                            int length = chinese.length();


                            String s = null;

                            List<String> keyList =null;

                            for (int x = 0; x < len; x++) {

                                String py = results[x];

                                if(py.contains("u:")){  //过滤 u:
                                    py = py.replace("u:", "v");
                                }

                                keyList = pinyinMap.get(py);

                                if (length>=3) {   //后向匹配2个汉字  大西洋
                                    s = chinese.substring(0, 3);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py);
                                        dy.append(py);
                                        break;
                                    }
                                }

                                if (length >=2) {   //后向匹配 1个汉字  大西
                                    s = chinese.substring(0,2);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py);
                                        dy.append(py);
                                        break;
                                    }
                                }
                            }

                            if (dy.length() == 0) {    //都没有找到，匹配默认的 读音  大
                                pinyin.append(results[0]);
                            }
                        }
                    }

                } catch (BadHanyuPinyinOutputFormatCombination e) {

                }
            } else {

            }
        }

        return pinyin.toString();
    }
    /**
     * 获取简拼
     */
    public static String getPinYinJane(String chinese) {

        if(pinyinMap.size() == 0) {
            initPinyin("D:/ww/workspace4git/samples/Pinyin/lib/src/main/resources/duoyinzi_dic.txt");
        }
        StringBuffer pinyin = new StringBuffer();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        char[] arr = chinese.toCharArray();
        for(char ch:arr){
            if (ch > 128) { // 非ASCII码
                // 取得当前汉字的所有全拼
                try {

                    String[] results = PinyinHelper.toHanyuPinyinStringArray(
                            ch, defaultFormat);

                    if (results == null) {  //非中文

                    } else {
                        StringBuffer dy = new StringBuffer();
                        int len = results.length;

                        if (len == 1) { // 不是多音字
                            pinyin.append(results[0].charAt(0));
                            dy.append(results[0].charAt(0));

                        }else if(results[0].equals(results[1])){    //非多音字 有多个音，取第一个
                            pinyin.append(results[0].charAt(0));
                            dy.append(results[0].charAt(0));

                        }else { // 多音字

                            int length = chinese.length();


                            String s = null;

                            List<String> keyList =null;

                            for (int x = 0; x < len; x++) {

                                String py = results[x];

                                if(py.contains("u:")){  //过滤 u:
                                    py = py.replace("u:", "v");
                                }

                                keyList = pinyinMap.get(py);

                                if (length>=3) {   //后向匹配2个汉字  大西洋
                                    s = chinese.substring(0, 3);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        dy.append(py.charAt(0));
                                        break;
                                    }
                                }

                                if (length >=2) {   //后向匹配 1个汉字  大西
                                    s = chinese.substring(0,2);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        dy.append(py.charAt(0));
                                        break;
                                    }
                                }
                            }

                            if (dy.length() == 0) {    //都没有找到，匹配默认的 读音  大
                                pinyin.append(results[0].charAt(0));
                            }
                        }
                    }

                } catch (BadHanyuPinyinOutputFormatCombination e) {

                }
            } else {

            }
        }

        return pinyin.toString();
    }
}
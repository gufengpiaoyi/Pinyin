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
     * ���ϵ����⺺��
     */
    private char[] hanzi = {'��', '��', '��', '��', '��', '��', '��', '��', '��', '��', 'ô'};
    /**
     * �������ϵĶ���
     */
    private String[] pinyin = {"chong", "shan", "xie", "zha", "zeng", "ge", "miao", "piao", "po",
            "qiu", "yao"};

    /**
     * ��ȡƴ���ļ��ϣ����������֣�
     *
     * @param content
     * @return
     */
    private String[][] getPinyinss(String content) {
        char[] srcChar = content.toCharArray();
        HanyuPinyinOutputFormat hpof = new HanyuPinyinOutputFormat();
        //HanyuPinyinCaseType.UPPERCASE��ʾ��д��HanyuPinyinCaseType.LOWERCASE��ʾСд
        hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        /**
         * HanyuPinyinToneType.WITHOUT_TONE��ʾ����������HanyuPinyinToneType
         * .WITH_TONE_NUMBER��ʾ��ƴ��ĩβ��ע����1-4��ʾ�ĸ�������HanyuPinyinToneType.WITH_TONE_MARK��ʾ��ƴ���Ϸ���ע�ĸ������ķ���
         */
        hpof.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        /**
         * HanyuPinyinVCharType.WITH_V��ʾ����ʾΪ�ַ�v��HanyuPinyinVCharType.WITH_U_AND_COLON��ʾ����ʾΪ�ַ�u:��
         * HanyuPinyinVCharType.WITH_U_UNICODE��ʾ����ʾΪ�ַ���
         * ��������ʽ����Ϊ��������ʱ��setVCharTypeֻ��ѡ��HanyuPinyinVCharType.WITH_U_UNICODE��
         * ��������ʱ�ᱨ��net.sourceforge.pinyin4j.format.exception
         * .BadHanyuPinyinOutputFormatCombination: tone marks cannot be added to v or u:��
         */
        hpof.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String[][] temp = new String[content.length()][];
        for (int i = 0; i < srcChar.length; i++) {
            try {
                char c = srcChar[i];
                // �����Ļ���a-z����A-Zת��ƴ��(�ҵ������Ǳ������Ļ���a-z����A-Z)
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
     * ��ȡƴ���ļ��ϣ�������Ĭ��ȡ��һ����
     *
     * @param content
     * @return
     */
    public String[] getPinyins(String content) {
        char[] srcChar = content.toCharArray();
        HanyuPinyinOutputFormat hpof = new HanyuPinyinOutputFormat();
        //HanyuPinyinCaseType.UPPERCASE��ʾ��д��HanyuPinyinCaseType.LOWERCASE��ʾСд
        hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        /**
         * HanyuPinyinToneType.WITHOUT_TONE��ʾ����������
         * HanyuPinyinToneType.WITH_TONE_NUMBER��ʾ��ƴ��ĩβ��ע����1-4��ʾ�ĸ�������
         * HanyuPinyinToneType.WITH_TONE_MARK��ʾ��ƴ���Ϸ���ע�ĸ������ķ���
         */
        hpof.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        /**
         * HanyuPinyinVCharType.WITH_V��ʾ����ʾΪ�ַ�v��HanyuPinyinVCharType.WITH_U_AND_COLON��ʾ����ʾΪ�ַ�u:��
         * HanyuPinyinVCharType.WITH_U_UNICODE��ʾ����ʾΪ�ַ���
         * ��������ʽ����Ϊ��������ʱ��setVCharTypeֻ��ѡ��HanyuPinyinVCharType.WITH_U_UNICODE��
         * ��������ʱ�ᱨ��net.sourceforge.pinyin4j.format.exception
         * .BadHanyuPinyinOutputFormatCombination: tone marks cannot be added to v or u:��
         */
        hpof.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String[] temp = new String[content.length()];
        for (int i = 0; i < srcChar.length; i++) {
            try {
                char c = srcChar[i];
                // �����Ļ���a-z����A-Zת��ƴ��(�ҵ������Ǳ������Ļ���a-z����A-Z)
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
     * ��ȡȫƴ
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
     * ��ȡ��ƴ
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
     * �������Ϻ��ַ��صĽ��
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
     * �ж��ַ������Ƿ�������
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
     * �ж��ַ����Ƿ�ȫ������
     *
     * @param str
     * @return
     */
    public static boolean isAllChineseChar(String str) {
        String reg = "[\\u4e00-\\u9fa5]+";

        return str.matches(reg);
    }

    /**
     * ��ȡ���ִ�ƴ������ĸ��Ӣ���ַ�����
     *
     * @param chinese ���ִ�
     * @return ����ƴ������ĸ
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
     * ��ȡ���ִ�ƴ����Ӣ���ַ�����
     *
     * @param chinese ���ִ�
     * @return ����ƴ��
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
     * ��ȡ���ִ�ƴ����Ӣ���ַ�����
     *
     * @param chinese ���ִ�
     * @return ����ƴ��
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
     * ��ȡ�����ַ����ĵ�һ����ĸ
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
     * ��ʼ�� ���еĶ����ִ���
     *
     * @param fileName
     */
    public static void initPinyin(String fileName) {
        // ��ȡ�����ֵ�ȫ��ƴ����;
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
     * ��ȡ��һ����ĸ
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

            if (ch > 128) { // ��ASCII��
                // ȡ�õ�ǰ���ֵ�����ȫƴ
                try {

                    String[] results = PinyinHelper.toHanyuPinyinStringArray(
                            ch, defaultFormat);

                    if (results == null) {  //������

                        return "#";
                    } else {

                        int len = results.length;

                        if (len == 1) { // ���Ƕ�����
                            pinyin.append(results[0].charAt(0));

                        }else if(results[0].equals(results[1])){    //�Ƕ����� �ж������ȡ��һ��
                            pinyin.append(results[0].charAt(0));

                        }else { // ������

                            int length = chinese.length();


                            String s = null;

                            List<String> keyList =null;

                            for (int x = 0; x < len; x++) {

                                String py = results[x];

                                if(py.contains("u:")){  //���� u:
                                    py = py.replace("u:", "v");
                                }

                                keyList = pinyinMap.get(py);

                                if (length>=3) {   //����ƥ��2������  ������
                                    s = chinese.substring(0, 3);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        break;
                                    }
                                }

                                if (length >=2) {   //����ƥ�� 1������  ����
                                    s = chinese.substring(0,2);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        break;
                                    }
                                }
                            }

                            if (pinyin.length() == 0) {    //��û���ҵ���ƥ��Ĭ�ϵ� ����  ��
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
     * ��ȡȫƴ
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
            if (ch > 128) { // ��ASCII��
                // ȡ�õ�ǰ���ֵ�����ȫƴ
                try {

                    String[] results = PinyinHelper.toHanyuPinyinStringArray(
                            ch, defaultFormat);

                    if (results == null) {  //������


                    } else {
                        StringBuffer dy = new StringBuffer();
                        int len = results.length;

                        if (len == 1) { // ���Ƕ�����
                            pinyin.append(results[0]);
                            dy.append(results[0]);
                        }else if(results[0].equals(results[1])){    //�Ƕ����� �ж������ȡ��һ��
                            pinyin.append(results[0]);
                            dy.append(results[0]);

                        }else { // ������

                            int length = chinese.length();


                            String s = null;

                            List<String> keyList =null;

                            for (int x = 0; x < len; x++) {

                                String py = results[x];

                                if(py.contains("u:")){  //���� u:
                                    py = py.replace("u:", "v");
                                }

                                keyList = pinyinMap.get(py);

                                if (length>=3) {   //����ƥ��2������  ������
                                    s = chinese.substring(0, 3);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py);
                                        dy.append(py);
                                        break;
                                    }
                                }

                                if (length >=2) {   //����ƥ�� 1������  ����
                                    s = chinese.substring(0,2);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py);
                                        dy.append(py);
                                        break;
                                    }
                                }
                            }

                            if (dy.length() == 0) {    //��û���ҵ���ƥ��Ĭ�ϵ� ����  ��
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
     * ��ȡ��ƴ
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
            if (ch > 128) { // ��ASCII��
                // ȡ�õ�ǰ���ֵ�����ȫƴ
                try {

                    String[] results = PinyinHelper.toHanyuPinyinStringArray(
                            ch, defaultFormat);

                    if (results == null) {  //������

                    } else {
                        StringBuffer dy = new StringBuffer();
                        int len = results.length;

                        if (len == 1) { // ���Ƕ�����
                            pinyin.append(results[0].charAt(0));
                            dy.append(results[0].charAt(0));

                        }else if(results[0].equals(results[1])){    //�Ƕ����� �ж������ȡ��һ��
                            pinyin.append(results[0].charAt(0));
                            dy.append(results[0].charAt(0));

                        }else { // ������

                            int length = chinese.length();


                            String s = null;

                            List<String> keyList =null;

                            for (int x = 0; x < len; x++) {

                                String py = results[x];

                                if(py.contains("u:")){  //���� u:
                                    py = py.replace("u:", "v");
                                }

                                keyList = pinyinMap.get(py);

                                if (length>=3) {   //����ƥ��2������  ������
                                    s = chinese.substring(0, 3);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        dy.append(py.charAt(0));
                                        break;
                                    }
                                }

                                if (length >=2) {   //����ƥ�� 1������  ����
                                    s = chinese.substring(0,2);
                                    if (keyList != null && (keyList.contains(s))) {
                                        pinyin.append(py.charAt(0));
                                        dy.append(py.charAt(0));
                                        break;
                                    }
                                }
                            }

                            if (dy.length() == 0) {    //��û���ҵ���ƥ��Ĭ�ϵ� ����  ��
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
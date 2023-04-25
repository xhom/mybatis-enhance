package com.vz.mybatis.enhance.common.mapper.hp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/25 10:30
 */
public class NameHelper {
    private static final Pattern UpperCasePattern = Pattern.compile("[A-Z]");//匹配大写字母的正则
    /**
     * 驼峰转下划线
     * @param str 驼峰
     * @return 下划线
     */
    public static String camel2underline(String str){
        Matcher matcher = UpperCasePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        if('_'==sb.charAt(0)){
            return sb.deleteCharAt(0).toString();
        }
        return sb.toString();
    }
}

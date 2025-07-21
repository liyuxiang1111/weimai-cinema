package com.liyuxiang.film;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmallTest {
    public static void main(String[] args) {
        String str = "http://liyuxiang.com.cn:8899/group1/M00/00/00/rBoZWmQJvkSAXZ1LAAFTbEhHxdo726.jpg";
        String pattern = "(?<=8899/).*";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        if (m.find()) {
            String group = m.group(); // 获取"2023/03/21/ba292f2693614cfea39f881364be705b.mp4"
            System.out.println(group);
        }
    }
}
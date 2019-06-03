package com.baishiyuan.utils;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 2YVTFQ2 on 2019/4/19.
 */
public class AdressParse {

    public static List<String> provinces = Arrays.asList("河北、山西、吉林、辽宁、黑龙江、陕西、甘肃、青海、山东、福建、浙江、台湾、河南、湖北、湖南、江西、江苏、安徽、广东、海南、四川、贵州、云南、北京、天津、上海、重庆、内蒙古、新疆维吾尔、宁夏回族、广西壮族、西藏、香港、澳门".split("、"));

    public static List<String> regionNames = Arrays.asList("区、市、旗、岛、市辖区、行政单位、自治州、地区、行政区、村、小区、花园、楼".split("、"));

    public static final Pattern MOBILENOCHECK = Pattern.compile("1[2|3|4|5|6|7|8|9][\\d]{9}");

    public static AddressInfo parseAddress(String info) {
        String province = "";
        int provinceIndex = -1;
        for(String provinceSingle : provinces) {
            if(info.contains(provinceSingle)) {
                int index = info.indexOf(provinceSingle, 0);
                String subString = info.substring(index);
                if(subString.startsWith(provinceSingle + "路") || subString.startsWith(provinceSingle + "大道") || subString.startsWith(provinceSingle + "街")) {
                    continue;
                }else {
                    province = provinceSingle;
                    provinceIndex = index;
                }
            }
        }

        if (province == "" || provinceIndex == -1) {
            return null;
        }

        AddressInfo addressInfo = new AddressInfo();

        String subString = info.substring(provinceIndex);
        int provinceEndIndex = subString.length() - 1;
        for(int i = 0; i < subString.length() - 1; i++) {
            if(" ".equals(subString.substring(i, i+1))) {
                String temp = subString.substring(i+1);
                boolean isAddress = false;
                for(String regionName : regionNames) {
                    if(temp.contains(regionName)) {
                        isAddress = true;
                        break;
                    }
                }
                if(!isAddress) {
                    addressInfo.setAddress(info.substring(provinceIndex, provinceIndex + i + 1));
                    provinceEndIndex = provinceIndex + i;
                    break;
                }
            }
        }
        if(StringUtils.isEmpty(addressInfo.getAddress())) {
            addressInfo.setAddress(subString);
        }

        String firstsubString = info.substring(0, provinceIndex);
        String secondsubString = info.substring(provinceEndIndex + 1, info.length());
        Matcher matcher = MOBILENOCHECK.matcher(firstsubString);
        boolean hasMBN = matcher.find();
        if(hasMBN) {
            String mbn = matcher.group();
            addressInfo.setPhone(mbn);

            String userName = firstsubString.replace(mbn, "").trim();
            if(userName == null | "".equals(userName)) {
                if(secondsubString.trim() == null | "".equals(secondsubString.trim())) {
                    return null;
                }else {
                    addressInfo.setName(secondsubString.trim());
                    return addressInfo;
                }
            }else {
                addressInfo.setName(userName);
                return addressInfo;
            }
        }else {
            Matcher secondMatcher = MOBILENOCHECK.matcher(secondsubString);
            hasMBN = secondMatcher.find();
            if(!hasMBN) {
                return null;
            }
            String mbn = secondMatcher.group();
            addressInfo.setPhone(mbn);
            String userName = secondsubString.replace(mbn, "").trim();
            if(userName == null | "".equals(userName)) {
                if(firstsubString.trim() == null | "".equals(firstsubString.trim())) {
                    return null;
                }else {
                    addressInfo.setName(firstsubString.trim());
                    return addressInfo;
                }
            }else {
                addressInfo.setName(userName);
                return addressInfo;
            }
        }

    }

}

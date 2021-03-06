package com.fetal.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputChecker {
	
	/**
	 * 手机号码验证
	 * @param mobile
	 * @return
	 */
	public static int checkMobile(String mobile) {
		if ("".equals(mobile) || "请填写手机号码".equals(mobile)) {
			return 1;
		} else if (mobile.length() > 11){
			return 2;
		} else {
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,1-9]))\\d{8}$");  
            Matcher m = p.matcher(mobile);  
            if (m.matches()) {
            	return 0;
            } else {
            	return 2;
            }
		}
	}
	
	/**
	 * 昵称验证
	 * @param nickname
	 * @return
	 */
	public static int checkNickname(String nickname) {
		if ("".equals(nickname) || "请填写昵称".equals(nickname)) {
			return 1;
		} else if (nickname.length() > 16){
			return 2;
		} else {
			return 0;
		}
	}
	
	/**
	 * 预产期验证
	 * @param birthday
	 * @return
	 */
	public static int checkBirthday(String birthday) {
		
		if ("".equals(birthday)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 昵称收货地址
	 * @param addr
	 * @return
	 */
	public static int checkAddr(String addr) {
		if ("".equals(addr) || "请填写收货地址".equals(addr)) {
			return 1;
		} else if (addr.length() > 100){
			return 2;
		} else {
			return 0;
		}
	}
}

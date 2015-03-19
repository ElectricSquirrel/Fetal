package com.fetal.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputChecker {
	
	/**
	 * ÊÖ»úºÅÂëÑéÖ¤
	 * @param mobile
	 * @return
	 */
	public static int checkMobile(String mobile) {
		if ("".equals(mobile) || "ÇëÌîĞ´ÊÖ»úºÅÂë".equals(mobile)) {
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
	 * êÇ³ÆÑéÖ¤
	 * @param nickname
	 * @return
	 */
	public static int checkNickname(String nickname) {
		if ("".equals(nickname) || "ÇëÌîĞ´êÇ³Æ".equals(nickname)) {
			return 1;
		} else if (nickname.length() > 16){
			return 2;
		} else {
			return 0;
		}
	}
	
	/**
	 * Ô¤²úÆÚÑéÖ¤
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
	 * êÇ³ÆÊÕ»õµØÖ·
	 * @param addr
	 * @return
	 */
	public static int checkAddr(String addr) {
		if ("".equals(addr) || "ÇëÌîĞ´ÊÕ»õµØÖ·".equals(addr)) {
			return 1;
		} else if (addr.length() > 100){
			return 2;
		} else {
			return 0;
		}
	}
}

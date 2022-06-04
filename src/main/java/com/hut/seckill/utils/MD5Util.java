package com.hut.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 * @author DUANQI
 * DateTime: 2022-05-23 16:10
 */
public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String SALT = "duanqi123";

    /**
     * 第一次加密
     * @param inputPass 输入的密码
     * @return 加密后的密码
     */
    public static String inputPassToFromPass(String inputPass){
        String str = "" + SALT.charAt(0) + SALT.charAt(3) + inputPass + SALT.charAt(2) + SALT.charAt(7);
        return md5(str);
    }

    /**
     * 第二次加密
     * @param fromPass 经过第一次加密后的密码
     * @param salt
     * @return 存入数据库的密码
     */
    public static String fromPassToDBPass(String fromPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(4) + fromPass + salt.charAt(1) + salt.charAt(3);
        return md5(str);
    }

    /**
     * 调用两次加密
     * @param inputPass
     * @param salt
     * @return
     */
    public static String inputPassToDBPass(String inputPass, String salt){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("123456")); // 5305e3fa3b586c04a5737acce0c0c2f9
        System.out.println(fromPassToDBPass("5305e3fa3b586c04a5737acce0c0c2f9", "duanqi123"));
        System.out.println(inputPassToDBPass("123456", "duanqi123"));
    }
}

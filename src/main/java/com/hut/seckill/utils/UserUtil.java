package com.hut.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hut.seckill.pojo.User;
import com.hut.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 生成用户工具类
 * @author DUANQI
 * DateTime: 2022-05-26 14:47
 */
public class UserUtil {
    //中国移动
    private static final String[] CHINA_MOBILE = {
            "134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159",
            "182", "183", "184", "187", "188", "178", "147", "172", "198"
    };
    //中国联通
    private static final String[] CHINA_UNICOM = {
            "130", "131", "132", "145", "155", "156", "166", "171", "175", "176", "185", "186", "166"
    };
    //中国电信
    private static final String[] CHINA_TELECOME = {
            "133", "149", "153", "173", "177", "180", "181", "189", "199"
    };

    /**
     * 生成手机号
     *
     * @param op 0 移动 1 联通 2 电信
     */
    private static long createMobile(int op) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        String mobile01;//手机号前三位
        int temp;
        switch (op) {
            case 0:
                mobile01 = CHINA_MOBILE[random.nextInt(CHINA_MOBILE.length)];
                break;
            case 1:
                mobile01 = CHINA_UNICOM[random.nextInt(CHINA_UNICOM.length)];
                break;
            case 2:
                mobile01 = CHINA_TELECOME[random.nextInt(CHINA_TELECOME.length)];
                break;
            default:
                mobile01 = "op标志位有误！";
                break;
        }
        if (mobile01.length() > 3) {
            return Long.parseLong(mobile01);
        }
        sb.append(mobile01);
        //生成手机号后8位
        for (int i = 0; i < 8; i++) {
            temp = random.nextInt(10);
            sb.append(temp);
        }
        return Long.parseLong(sb.toString());
    }

    public static void main(String[] args) throws Exception {
        //createUser(5000);
        createToken();
    }

    @SuppressWarnings("all")
    private static void createToken() throws Exception {
        Connection conn = getConn();
        String sql = "select * from t_user where login_count=1";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<User> users = new ArrayList<>(5000);
        while (resultSet.next()){
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setNickname(resultSet.getString("nickname"));
            user.setPassword(resultSet.getString("password"));
            user.setSalt(resultSet.getString("salt"));
            user.setRegisterDate(resultSet.getTimestamp("register_date"));
            user.setLoginCount(resultSet.getInt("login_count"));
            users.add(user);
        }
        resultSet.close();
        statement.close();
        //登录，生成token
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\DUANQI\\Desktop\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            User user = new User();
            int op = random.nextInt(3); //随机运营商标志位
            user.setId(createMobile(op));
            user.setNickname("user" + i);
            user.setSalt("duanqi123");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");
        //插入数据库
        Connection conn = getConn();
        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id) values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("insert to db");
        //登录，生成token
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\DUANQI\\Desktop\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://172.18.11.234:3386/seckill";
        String username = "root";
        String password = "root";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}

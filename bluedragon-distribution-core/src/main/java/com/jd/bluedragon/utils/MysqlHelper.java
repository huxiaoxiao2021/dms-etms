package com.jd.bluedragon.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MysqlHelper {

    private static final Pattern SQL_ALLOW_PATTERN = Pattern.compile("\b*(select)\\s+",Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_PARSE_PATTERN = Pattern.compile("^(\\s*(select)[\\s\\w\\s\\W]+(from)\\s+(\\w+)\\s+)[\\s\\w\\s\\W]+" +
                    "(limit)\\s+(\\d+);--(\\w+)\\s*$",
            Pattern.CASE_INSENSITIVE);

    public static Connection getConnection(String jdbcUrl,String username,String pword){
        if(jdbcUrl == null || username == null || pword == null){
            throw new RuntimeException("不能创建Connection。host、username、pword 都不能为null");
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("加载mysql驱动失败");
        }
        Connection conn;
        try {
            conn = DriverManager.getConnection(jdbcUrl, username, pword);
        } catch (SQLException e) {
            throw new RuntimeException("获取连接失败");
        }
        return conn;
    }

    public static String getPrimaryKey(String sql){
        Matcher matcher = SQL_PARSE_PATTERN.matcher(sql);
        if(matcher.find()){
            return matcher.group(7);
        }
        return null;
    }

    public static String getTable(String sql){
        Matcher matcher = SQL_PARSE_PATTERN.matcher(sql);
        if(matcher.find()){
            return matcher.group(4);
        }
        return null;
    }

    public static String getSqlBeforeWhere(String sql){
        Matcher matcher = SQL_PARSE_PATTERN.matcher(sql);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    public static String getLimit(String sql){
        Matcher matcher = SQL_PARSE_PATTERN.matcher(sql);
        if(matcher.find()){
            return matcher.group(6);
        }
        return null;
    }

    /**
     * 查询
     *
     * @param sql 查询sql
     * @return List(字段名, 值)
     */
    public static List<LinkedHashMap<String, String>> executeQuery(String jdbcUrl,String username,String pword,String sql) {
        Connection conn = getConnection(jdbcUrl,username,pword);
        return executeQuery(conn, sql);
    }


    public static boolean isSelectAllowSql(String sql) {

        Matcher matcher = SQL_ALLOW_PATTERN.matcher(sql);
        while (matcher.find()){
            return true;
        }
        return false;
    }

    /**
     * 无参数查询,返回类型据情况稍加修改,以mysql为例，其他类似
     *
     * @return 所有记录的名字的list
     */
    public static List<LinkedHashMap<String, String>> executeQuery(Connection conn, String sql) {
        List<LinkedHashMap<String, String>> info = new ArrayList<LinkedHashMap<String, String>>();
        if(!isSelectAllowSql(sql)){
            throw new RuntimeException("只能执行查询sql");
        }
        PreparedStatement ps = null;
        int rows = 2000;
        int row = 1;

        ResultSet rs;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            List<String> nameList = new ArrayList<String>();
            for (int i = 0; i < meta.getColumnCount(); i++) {
                nameList.add(meta.getColumnName(i + 1));
            }
            while (rs.next()) {
                if (row > rows) {
                    break;
                }
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                for (String col : nameList) {
                    map.put(col, rs.getString(col));
                }
                info.add(map);
                row++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(conn);
        }
        return info;
    }

    private static void close(AutoCloseable closeable){
        try {
            if(closeable != null){
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        List<LinkedHashMap<String, String>> list = executeQuery("jdbc:mysql://192.168.183.84:3358/bd_dms_con?characterEncoding=UTF-8&autoReconnect=true",
//                "root","1qaz@WSX","select * from system_log where  create_time > '' limit 1000;##id");
//        System.out.println(list);
        Matcher matcher = SQL_PARSE_PATTERN.matcher("select * from ddd where  create_time > '' limit 1000;--id");

        System.out.println(getPrimaryKey("select * from ddd where  create_time > '' limit 1000;--id"));
        System.out.println(getTable("select * from ddd where  create_time > '' limit 1000;--id"));
        System.out.println(getSqlBeforeWhere("select * from ddd where  create_time > '' limit 1000;--dd"));
        System.out.println(getPrimaryKey("select * from ddd   limit 100;--id"));
        System.out.println(getTable("select * from ddd  limit 1000;--id"));
        System.out.println(getSqlBeforeWhere("select * from ddd  limit 1000;--dd"));
    }
}

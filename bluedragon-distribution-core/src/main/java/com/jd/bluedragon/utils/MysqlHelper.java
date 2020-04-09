package com.jd.bluedragon.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MysqlHelper {

    private static final Logger logger = Logger.getLogger(MysqlHelper.class);
    private Map<String, String> database = new HashMap<String, String>();
    private static final String keyseed = "12AE8C8D5A9ECE84FBD142081CC7767E";
    private static final Pattern SQL_PATTERN = Pattern.compile("\b*(select)\\s+",Pattern.CASE_INSENSITIVE);

    public MysqlHelper(String host, String username, String password) {
        database.put("host", host);
        database.put("username", username);
        database.put("password", password);
    }

    public static Connection getConnection(String jdbcUrl,String username,String password){
        if(jdbcUrl == null || username == null || password == null){
            throw new RuntimeException("不能创建Connection。host、username、password 都不能为null");
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("加载mysql驱动失败");
        }
        Connection conn;
        try {
            conn = DriverManager.getConnection(database.get("host"), database.get("username"), database.get("password"));
        } catch (SQLException e) {
            throw new RuntimeException("获取连接失败");
        }
        return conn;
    }
    /**
     * 查询
     *
     * @param sql 查询sql
     * @return List(字段名, 值)
     */
    public List<LinkedHashMap<String, String>> executeQuery(String sql) {
        return executeQuery(this.database, sql);
    }

    /**
     * 查询
     *
     * @param sql 查询sql
     * @return List(字段名, 值)
     */
    public List<LinkedHashMap<String, Object>> executeQuery(String sql,Object[] args) {
        return executeQuery(this.database, sql,args);
    }



    public static boolean isSelectAllowSql(String sql) {

        Matcher matcher = SQL_PATTERN.matcher(sql);
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
    public List<LinkedHashMap<String, String>> executeQuery(Connection conn, String sql) {
        PreparedStatement ps = null;
        List<LinkedHashMap<String, String>> info = new ArrayList<LinkedHashMap<String, String>>();
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
            logger.error("执行sql失败：", e);
            throw new RuntimeException("执行sql失败");
        } finally {
            close(ps);
        }
        return info;
    }

    /**
     * 无参数查询,返回类型据情况稍加修改,以mysql为例，其他类似
     *
     * @return 所有记录的名字的list
     */
    private List<LinkedHashMap<String, String>> executeQuery(Map<String, String> database, String sql) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("加载mysql驱动失败");
        }
        Connection conn;
        try {
            logger.debug("host:" + database.get("host") + ",username:" + database.get("username") + ",password:" + database.get("password"));
            conn = DriverManager.getConnection(database.get("host"), database.get("username"), database.get("password"));
        } catch (SQLException e) {
            throw new RuntimeException("获取连接失败");
        }
        PreparedStatement ps = null;
        List<LinkedHashMap<String, String>> info = new ArrayList<LinkedHashMap<String, String>>();
        int rows = 8000;
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
            logger.error("执行sql失败：", e);
            throw new RuntimeException("执行sql失败");
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
    /**
     *有参数查询
     *
     * @return 所有记录的名字的list
     */
    private List<LinkedHashMap<String, Object>> executeQuery(Map<String, String> database, String sql,Object[] args) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("加载mysql驱动失败");
        }
        Connection conn;
        try {
            logger.debug("host:" + database.get("host") + ",username:" + database.get("username") + ",password:" + database.get("password"));
            conn = DriverManager.getConnection(database.get("host"), database.get("username"), database.get("password"));
        } catch (SQLException e) {
            throw new RuntimeException("获取连接失败");
        }
        PreparedStatement ps = null;
        List<LinkedHashMap<String, Object>> info = new ArrayList<LinkedHashMap<String, Object>>();
        int rows = 2000;
        int row = 1;

        ResultSet rs;
        try {
            ps = conn.prepareStatement(sql);
            if(null!=args&&args.length>0){
                int idx=1;
                for(Object obj:args){
                    ps.setObject(idx,obj);
                    idx++;
                }
            }
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
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                for (String col : nameList) {
                    map.put(col, rs.getObject(col));
                }
                info.add(map);
                row++;
            }
        } catch (Exception e) {
            logger.error("执行sql失败：", e);
            throw new RuntimeException("执行sql失败");
        } finally {
            if (ps != null) try {
                ps.close();
                conn.close();
            } catch (SQLException e) {
                logger.error("执行sql失败：", e);
            }
        }
        return info;
    }


    public String getKey() {
        //key 1小时内有效
        int y, m, d, h;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        h = cal.get(Calendar.HOUR_OF_DAY);
        StringBuilder vkSb = new StringBuilder();
        vkSb.append(h).
                append("-").
                append(d).
                append("-").
                append(m).
                append("-").
                append(y).
                append("-").
                append(this.keyseed);
        return MD5(vkSb.toString());
    }


    private String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据mysql的jdbc连接串解析一个简单的数据库名称
     *
     * @param jdbcUrl like:jdbc:mysql://192.168.159.59:3358/pl?useUnicode=true&characterEncoding=utf-8
     *                or jdbc:mysql://192.168.159.59:3358/pl
     * @return ip加端口的命令
     */
    public static String getDbName(String jdbcUrl) {
        if (StringUtils.isBlank(jdbcUrl)) return "";
        Pattern p = Pattern.compile("mysql\\://(.*)/(.*)(\\?.*)|\\:\\d{2,6}/(.*)");
        Matcher ff = p.matcher(jdbcUrl);
        String str0 = "", str1 = "", str2 = "";
        while (ff.find()) {
            str0 = ff.group(1);
            str1 = ff.group(2);
            str2 = ff.group(4);
        }

        if (!StringUtils.isBlank(str1)) return str0 + "/" + str1;
        if (!StringUtils.isBlank(str2)) return str0 + "/" + str2;
        return "";
    }

    public List<String> getTableNames() {
        Connection conn = null;
        ResultSet tableRet = null;
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("加载mysql驱动失败");
            }

            try {
                logger.info("host:" + database.get("host") + ",username:" + database.get("username") + ",password:" + database.get("password"));
                conn = DriverManager.getConnection(database.get("host"), database.get("username"), database.get("password"));
            } catch (SQLException e) {
                throw new RuntimeException("获取连接失败");
            }
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String schem = "%";
            tableRet = dbMetaData.getTables(null, schem, "%", new String[]{"TABLE"});
            List<String> tables = new ArrayList<String>();
            while (tableRet.next()) {
                tables.add(tableRet.getString("TABLE_NAME"));
            }
            return tables;
        } catch (Exception e) {
            logger.error("获取表信息异常", e);
        } finally {
            if (tableRet != null) {
                try {
                    tableRet.close();
                } catch (SQLException e) {
                    logger.error("获取表信息异常关闭tableRet异常", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("获取表信息异常关闭连接异常", e);
                }
            }
        }

        return null;
    }


    public static void main(String[] args) {
        //生成key
//        MysqlHelper mysqlHelper = new MysqlHelper();
//        System.out.println(mysqlHelper.getKey());
    }


    //    public static void main(String[] args) {
//
//        String sql = "select * from gis_node limit 10";
//        String sql2 = "update gis_node set node_name='123' where node_id=1";
//
//        FastUtils fastUtils = new FastUtils();
//        Map<String,String> map=new HashMap<String, String>();
//        map.put("host","jdbc:mysql://192.168.159.59:3306/gaia?characterEncoding=UTF-8");
//        map.put("username","gaia");
//        map.put("password","gaia");
//
//        System.out.println(fastUtils.executeUpdate(map, sql2));
//
//    }
}

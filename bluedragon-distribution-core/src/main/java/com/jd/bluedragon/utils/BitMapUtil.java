package com.jd.bluedragon.utils;

import java.math.BigInteger;

public class BitMapUtil {

    /**
     * 创建bitmap数组
     */
    public static byte[] create(int n) {
        return new byte[getIndex(n) + 1];
    }

    /**
     * 标记指定数字（num）在bitmap中的值，标记其已经出现过<br/>
     * 将1左移position后，那个位置自然就是1，然后和以前的数据做|，这样，那个位置就替换成1了
     * @param bits
     * @param num
     */
    public static void add(byte[] bits, int num){
        bits[getIndex(num)] |= 1 << getPosition(num);
    }

    /**
     * 判断指定数字num是否存在<br/>
     * 将1左移position后，那个位置自然就是1，然后和以前的数据做&，判断是否为0即可
     * @param bits
     * @param num
     * @return
     */
    public static boolean contains(byte[] bits, int num){
        return (bits[getIndex(num)] & 1 << getPosition(num)) != 0;
    }

    /**
     * num/8得到byte[]的index
     * @param num
     * @return
     */
    public static int getIndex(int num){
        return num >> 3;
    }

    /**
     * num%8得到在byte[index]的位置
     * @param num
     * @return
     */
    public static int getPosition(int num){
        return num & 0x07;
    }

    /**
     * 重置某一数字对应在bitmap中的值<br/>
     * 对1进行左移，然后取反，最后与byte[index]作与操作。
     * @param bits
     * @param num
     */
    public static void clear(byte[] bits, int num){
        bits[getIndex(num)] &= ~(1 << getPosition(num));
    }

    /**
     * 获取byte[]的二进制字符串
     * @param bits
     * @return
     */
    public static String getBinaryString(byte[] bits){
        StringBuffer sb = new StringBuffer();
        for(byte b : bits){
            byte[] array = new byte[8];
            for(int i = 7; i >= 0; i--){
                array[i] = (byte)(b & 1);
                b = (byte)(b >> 1);
            }
            StringBuffer sb1 = new StringBuffer();
            for (byte b1 : array) {
                sb1.append(b1) ;
            }
            sb.insert(0, sb1.toString());
        }
        return sb.toString();
    }

    /**
     * 获取字节数字对应的int
     * @param bits
     * @return
     */
    public static int getInteger(byte[] bits){
        return Integer.parseInt(getBinaryString(bits), 2);
    }


    public static String turn2to16(byte[] bits) {
        String str = BitMapUtil.getBinaryString(bits);
        return BitMapUtil.turn2to16(str);
    }
    /**
     * 二进制转十六进制字符串
     * @param str
     * @return
     */
    public static String turn2to16(String str) {
        String str16="";
        int t=str.length()%4;
        if(t!=0){
            for(int i=str.length();i-4>=0;i=i-4){
                String s=str.substring(i-4,i);
                int tem=Integer.parseInt(String.valueOf(s), 2);
                str16=Integer.toHexString(tem).toUpperCase()+str16;
            }
            String st=str.substring(0,t);

            int tem=Integer.parseInt(String.valueOf(st), 2);
            str16=Integer.toHexString(tem).toUpperCase()+str16;

        }
        else{
            for(int i=str.length();i-4>=-1;i=i-4){
                String s=str.substring(i-4,i);
                int tem=Integer.parseInt(String.valueOf(s), 2);
                str16=Integer.toHexString(tem).toUpperCase()+str16;
            }
        }
        return str16;
    }


    public static String turn16to2(String hexString) {
        //16进制转10进制
        BigInteger sint = new BigInteger(hexString, 16);
        //10进制转2进制
        String result = sint.toString(2);
        //字符串反转
        return new StringBuilder(result).reverse().toString();
    }


}

package com.jd.bluedragon.utils;

import java.util.HashSet;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    /**
     * 将一个list,每隔n个子元素分成一个list,主要通过偏移量来实现的
     * @param source
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> source, int n){
        if(CollectionUtils.isEmpty(source)||n<=0){
            return null;
        }
        List<List<T>> result=new ArrayList<List<T>>();
        int remaider=source.size()%n;  //(先计算出余数)
        int number=source.size()/n;  //然后是商
        for(int i=0;i<number+1;i++){
            List<T> value=null;
            if(i<number){
                value=source.subList(i*n,(i+1)*n);
            }else {
                value=source.subList(i*n,i*n+remaider);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * List集合去除重复数据 ,通过List中的contians方法
     去除List集合中重复数据 前提是需要重重写对象的 hashcode和equal方法
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T> List<T> processDuplicateByContains(List<T> list) {
        List<T> listAll = new ArrayList<T>();
        for (T t : list) {
            if (!listAll.contains(t)) {
                listAll.add(t);
            }
        }
        return listAll;
    }

    /**
     * List集合去除重复数据 , 通过List中的contians方法去除
     List集合中重复数据 前提是需要重重写对象的 hashcode和equal方法
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T> List<T> processDuplicateByHashSet(List<T> list) {
        List<T> listAll = new ArrayList<T>();
        HashSet<T> hashSet = new HashSet<T>(list);
        listAll.addAll(hashSet);
        return listAll;
    }


}

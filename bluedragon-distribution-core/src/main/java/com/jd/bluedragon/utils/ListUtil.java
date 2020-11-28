package com.jd.bluedragon.utils;

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

}

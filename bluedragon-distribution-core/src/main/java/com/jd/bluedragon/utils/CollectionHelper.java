package com.jd.bluedragon.utils;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.send.domain.SendM;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CollectionHelper<E> {

    private static Logger log = LoggerFactory.getLogger(CollectionHelper.class);

    public Set<E> toSet(List<E> list) {
        if (list == null) {
            return Collections.emptySet();
        }

        Set<E> objects = new HashSet<E>();
        for (E e : list) {
            objects.add(e);
        }

        return objects;
    }

    public Set<E> toSet(E[] array) {
        if (array == null) {
            return Collections.emptySet();
        }

        Set<E> objects = new HashSet<E>();
        for (E e : array) {
            objects.add(e);
        }

        return objects;
    }

    public List<E> toList(Set<E> set) {
        if (set == null) {
            return Collections.emptyList();
        }

        List<E> objects = new ArrayList<E>();
        for (E e : set) {
            objects.add(e);
        }

        return objects;
    }

    public static <E> List<E> joinToList(Collection<?> objects, String methodName) {
        List<E> result = new ArrayList<>();
        for (Iterator<?> iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            if(o == null){
                continue;
            }
            try {
                Method method = o.getClass().getMethod(methodName);
                E value = (E)method.invoke(o);
                if(value == null){
                    continue;
                }
                result.add(value);
            } catch (NoSuchMethodException e) {
                log.error("joinToList错误",e);
            } catch (IllegalAccessException e) {
                log.error("joinToList错误",e);
            } catch (InvocationTargetException e) {
                log.error("joinToList错误",e);
            }
        }
        return result;
    }
    /**
     * 返回2个列表的交集
     * @param <E>
     * @param a
     * @param b
     * @return
     */
    public static <E> List<E> retainAll(List<E> a, List<E> b)
    {
    	List<E> result = new ArrayList<>();
    	if(a != null && b != null ) {
    		result.addAll(CollectionUtils.retainAll(a, b));
    	}
    	return result;
    }

    /**
     * 拆分集合
     *
     * @param source
     * @param splitNum
     * @param <E>
     * @return
     */
    public static <E> List<List<E>> splitList(List<E> source, int splitNum) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        if (splitNum <= 0) {
            return Collections.emptyList();
        }

        return Lists.partition(source, splitNum);
    }
    /**
     * 拆分集合,先按每组数量perMinElementNum拆分，最大不超过maxGroupNum的值
     * @param <E>
     * @param source
     * @param maxGroupNum 拆分组数量
     * @param perMinElementNum 每组拆分最少元素数
     * @return
     */
    public static <E> List<List<E>> splitList(List<E> source, int maxGroupNum,int perMinElementNum) {
    	List<List<E>> result = Lists.newArrayList();
    	if(source == null
    			|| source.size() == 0) {
    		return result;
    	}
    	long size = source.size();
    	//不需要拆分,add原列表
    	if(size <= perMinElementNum) {
    		result.add(source);
    		return result;
    	}
    	int perNum = perMinElementNum;
		int groupNum = maxGroupNum;
		if(size > ((long)perNum * (long)groupNum)) {
			perNum = (int)(size/groupNum);
			if(size%groupNum > 0) {
				perNum +=1;
			}
		}
        return Lists.partition(source, perNum);
    }
    public static void main(String[] args) {
        List<SendM> list = new ArrayList<>();
        SendM sendM = new SendM();
        sendM.setBoxCode("dsdfsd");
        SendM sendM2 = new SendM();
        SendM sendM3 = new SendM();
        sendM3.setBoxCode("1111");
        list.add(sendM);
        list.add(sendM2);
        list.add(sendM3);
        System.out.println(joinToList(list,"getBoxCode"));
        
        List<String> s = new ArrayList<String>();
        for(int i=1;i<=201;i++) {
        	s.add(""+i);
        }
        System.out.println(splitList(s,2,100));
        System.out.println(splitList(s,10000000,100));
        System.out.println(splitList(s,Integer.MAX_VALUE,100));
        System.out.println(splitList(s,2,Integer.MAX_VALUE));
    }

}

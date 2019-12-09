package com.jd.bluedragon.utils;

import com.jd.bluedragon.distribution.send.domain.SendM;
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
    }

}

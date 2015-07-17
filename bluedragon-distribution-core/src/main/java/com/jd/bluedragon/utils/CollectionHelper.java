package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionHelper<E> {

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

}

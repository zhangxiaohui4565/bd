package com.gp.bd.sample.storm.utils;

import java.util.*;

/**
 * Map 排序工具类
 */
public class MapSortUtil {

    public static Map<String, Long> sortByValue(Map<String, Long> map) {
        if (map == null) {
            return null;
        }

        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                Comparable sort1 = (Comparable) ((Map.Entry) o1).getValue();
                Comparable sort2 = (Comparable) ((Map.Entry) o2).getValue();
                return sort2.compareTo(sort1);
            }

        });
        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}

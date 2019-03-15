package com.jd.bluedragon.utils.jddl;

import com.jdwl.jddl.utils.JProxyPartition;

/**
 * Created by hanjiaxing1 on 2019/1/14.
 */
public class DmsJddlUtils {

    public static final int partitionCount = 512;
    public static final int instanceCount = 16;

    public static int getDbInstanceIndex(int hashVal) {
        int index = getDbPartitionIndex(hashVal);
        return index % instanceCount;
    }

    public static int getDbInstanceIndex(long hashVal) {
        int index = getDbPartitionIndex(hashVal);
        return index % instanceCount;
    }

    public static int getDbInstanceIndex(String hashVal) {
        int index = getDbPartitionIndex(hashVal);
        return index % instanceCount;
    }

    public static int getDbPartitionIndex(int hashVal) {
        int hashCode = JProxyPartition.simpleHashPartition(hashVal, partitionCount);
        int count = partitionCount / instanceCount;
        return Math.abs((hashCode % count) * instanceCount + hashCode / count);
    }

    public static int getDbPartitionIndex(long hashVal) {
        int hashCode = JProxyPartition.simpleHashPartition(hashVal, partitionCount);
        int count = partitionCount / instanceCount;
        return Math.abs((hashCode % count) * instanceCount + hashCode / count);
    }
    public static int getDbPartitionIndex(String hashVal) {
        int hashCode = JProxyPartition.simpleHashPartition(hashVal, partitionCount);
        int count = partitionCount / instanceCount;
        return Math.abs((hashCode % count) * instanceCount + hashCode/ count);
    }

    public static void main(String[] args) {
        System.out.println(DmsJddlUtils.getDbPartitionIndex(1022));

    }
}

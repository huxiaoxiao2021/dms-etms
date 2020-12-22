package com.jd.bluedragon.test;

import com.jd.bluedragon.common.router.CacheTablePartition;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/16 20:25
 */
public class TestDbName {
    public static void main(String[] args) {
        String waybillCode = "JDX000156816231";
        String db = CacheTablePartition.getDmsCacheDb(waybillCode);
         String tableName = CacheTablePartition.getDmsPackageWeightingCacheTableName(waybillCode);
        System.out.println("db:"+db+",tableName:"+tableName);
    }
}
    

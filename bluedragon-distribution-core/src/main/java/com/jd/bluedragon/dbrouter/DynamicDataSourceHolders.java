package com.jd.bluedragon.dbrouter;

public class DynamicDataSourceHolders {

    private static final ThreadLocal<DynamicDataSourceType> holder = new ThreadLocal<DynamicDataSourceType>();

    public static void putDataSource(DynamicDataSourceType dataSource){
        holder.set(dataSource);
    }

    public static DynamicDataSourceType getDataSource(){
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }

    public static DynamicDataSourceType getDataSources(String dataSource){
        DynamicDataSourceType d = null;
        if (DynamicDataSourceType.DEFAULT.name().equals(dataSource)){
            d =DynamicDataSourceType.DEFAULT;
        }
        if (DynamicDataSourceType.DMS_UNDIV_MAIN.name().equals(dataSource)){
            d =DynamicDataSourceType.DMS_UNDIV_MAIN;
        }
        if (DynamicDataSourceType.DMS_UNDIV_SLAVE.name().equals(dataSource)){
            d =DynamicDataSourceType.DMS_UNDIV_SLAVE;
        }
        return d;
    }
}

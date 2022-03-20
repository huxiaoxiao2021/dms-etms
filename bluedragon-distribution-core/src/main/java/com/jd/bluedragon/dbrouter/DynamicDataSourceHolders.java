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
        if (DynamicDataSourceType.FAILSHIFT.name().equals(dataSource)){
            d =DynamicDataSourceType.FAILSHIFT;
        }
        else if (DynamicDataSourceType.FACTORMISS.name().equals(dataSource)){
            d=DynamicDataSourceType.FACTORMISS;
        }
        else if(DynamicDataSourceType.CONFIGCENTER.name().equals(dataSource)) {
            d =DynamicDataSourceType.CONFIGCENTER;
        }
        else if(DynamicDataSourceType.CLL0814.name().equals(dataSource)) {
            d =DynamicDataSourceType.CLL0814;
        }
        else if(DynamicDataSourceType.HELING0603.name().equals(dataSource)) {
            d =DynamicDataSourceType.HELING0603;
        }
        else if(DynamicDataSourceType.XIANGLIAOBANK.name().equals(dataSource)) {
            d =DynamicDataSourceType.XIANGLIAOBANK;
        }
        else if(DynamicDataSourceType.XIANLIAO1229.name().equals(dataSource)) {
            d =DynamicDataSourceType.XIANLIAO1229;
        } else if(DynamicDataSourceType.ZHIWEILAI.name().equals(dataSource)) {
            d =DynamicDataSourceType.ZHIWEILAI;
        }
        else if(DynamicDataSourceType.CJBANK.name().equals(dataSource)) {
            d =DynamicDataSourceType.CJBANK;
        }
        return d;
    }
}

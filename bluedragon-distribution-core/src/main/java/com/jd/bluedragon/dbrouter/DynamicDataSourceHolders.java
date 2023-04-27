package com.jd.bluedragon.dbrouter;

public class DynamicDataSourceHolders {

  private static final ThreadLocal<DynamicDataSourceType> holder = new ThreadLocal<DynamicDataSourceType>();

  public static void putDataSource(DynamicDataSourceType dataSource){
    holder.set(dataSource);
  }

  public static DynamicDataSourceType getDataSource(){ return holder.get(); }

  public static void clearDataSource() {
    holder.remove();
  }


  public static DynamicDataSourceType getDataSources(String dataSource){
    DynamicDataSourceType d = null;
    if (DynamicDataSourceType.DEFAULT.getName().equals(dataSource)){
      d =DynamicDataSourceType.DEFAULT;
    }
    if (DynamicDataSourceType.JY_CORE.getName().equals(dataSource)){
      d =DynamicDataSourceType.JY_CORE;
    }
    else if (DynamicDataSourceType.AGGS_MAIN.getName().equals(dataSource)){
      d =DynamicDataSourceType.AGGS_MAIN;
    }
    else if (DynamicDataSourceType.AGGS_SLAVE.getName().equals(dataSource)){
      d =DynamicDataSourceType.AGGS_SLAVE;
    }
    return d;
  }
}

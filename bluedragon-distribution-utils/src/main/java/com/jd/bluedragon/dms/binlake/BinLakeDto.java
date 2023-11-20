package com.jd.bluedragon.dms.binlake;


import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-04 11:40
 */
public class BinLakeDto {

    private String businessId;

    private String database;

    private String eventType;

    private String executeTime;

    private String tableName;

    private List<ColumnRecord> afterChangeOfColumns;

    private List<ColumnRecord> beforeChangeOfColumns;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnRecord> getAfterChangeOfColumns() {
        return afterChangeOfColumns;
    }

    public void setAfterChangeOfColumns(List<ColumnRecord> afterChangeOfColumns) {
        this.afterChangeOfColumns = afterChangeOfColumns;
    }

    public List<ColumnRecord> getBeforeChangeOfColumns() {
        return beforeChangeOfColumns;
    }

    public void setBeforeChangeOfColumns(List<ColumnRecord> beforeChangeOfColumns) {
        this.beforeChangeOfColumns = beforeChangeOfColumns;
    }
}

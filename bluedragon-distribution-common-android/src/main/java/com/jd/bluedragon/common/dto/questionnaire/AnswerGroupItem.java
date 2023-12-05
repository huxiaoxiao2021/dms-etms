package com.jd.bluedragon.common.dto.questionnaire;

import java.io.Serializable;

public class AnswerGroupItem implements Serializable {

    /**
     * 行id
     */
    private String rowId;

    /**
     * 列id
     */
    private String columnId;

    /**
     * 列选项
     */
    private String columnName;

    /**
     * 行选项
     */
    private String rowName;

    /**
     * 附加填空内容
     */
    private String extraText;

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }
}

package com.jd.bluedragon.dms.binlake;

import lombok.Data;

/**
 * @author liwenji
 * @date 2022-12-04 11:51
 */
@Data
public class ColumnRecord {

    private Integer index;

    private boolean key;

    private Integer length;

    private String mysqlType;

    private String name;

    private Integer sqlType;

    private boolean update;

    private String value;
}

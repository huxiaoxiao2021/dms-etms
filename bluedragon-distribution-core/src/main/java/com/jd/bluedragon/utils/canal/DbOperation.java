package com.jd.bluedragon.utils.canal;

/**
 * Created by zhanglei51 on 2016/11/28.
 */
public enum DbOperation {
    INSERT,
    UPDATE,
    DELETE,
    DDL,
    NOT_SPECIFIED;

    private DbOperation() {
    }

    public static DbOperation parse(String operationName) {
        String upperName = operationName.toUpperCase();
        return valueOf(upperName);
    }

    public boolean in(DbOperation... operations) {
        DbOperation[] arr$ = operations;
        int len$ = operations.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            DbOperation operation = arr$[i$];
            if(this == operation) {
                return true;
            }
        }

        return false;
    }
}

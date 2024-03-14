package com.jd.bluedragon.distribution.jy.exception.query;

import com.jd.dms.java.utils.sdk.base.BaseQuery;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class JyExceptionInterceptDetailKvQuery extends BaseQuery {
    private static final long serialVersionUID = 5454155825314635342L;

    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
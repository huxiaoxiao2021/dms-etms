package com.jd.bluedragon.distribution.jy.exception.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class JyExceptionInterceptDetailKv implements Serializable{
    private static final long serialVersionUID = 5454155825314635342L;

    //columns START
    /**
     * 主键ID  db_column: id
     */
    private Long id;
    /**
     * 关键字  db_column: keyword
     */
    private String keyword;
    /**
     * 值  db_column: value
     */
    private String value;
    /**
     * 时间戳  db_column: ts
     */
    private Date ts;
    //columns END

    public JyExceptionInterceptDetailKv(){
    }
    public JyExceptionInterceptDetailKv(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "JyExceptionInterceptDetailKv{" +
                "id=" + id +
                ", keyword='" + keyword + '\'' +
                ", value='" + value + '\'' +
                ", ts=" + ts +
                '}';
    }
}

package com.jd.bluedragon.distribution.jy.unload;


import java.io.Serializable;
import java.util.Date;


/**
 * 封车批次关系表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:18:58
 */
public class JyBizUnloadVehicleBatchEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 逻辑主键
     */
    private Long id;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;

    public Long setId(Long id) {
        return this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String setSealCarCode(String sealCarCode) {
        return this.sealCarCode = sealCarCode;
    }

    public String getSealCarCode() {
        return this.sealCarCode;
    }

    public String setBatchCode(String batchCode) {
        return this.batchCode = batchCode;
    }

    public String getBatchCode() {
        return this.batchCode;
    }

    public Integer setYn(Integer yn) {
        return this.yn = yn;
    }

    public Integer getYn() {
        return this.yn;
    }

    public Date setTs(Date ts) {
        return this.ts = ts;
    }

    public Date getTs() {
        return this.ts;
    }

}

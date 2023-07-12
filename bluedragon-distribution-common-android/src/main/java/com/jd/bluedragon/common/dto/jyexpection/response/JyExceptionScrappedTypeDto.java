package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 19:34
 * @Description:
 */
public class JyExceptionScrappedTypeDto implements Serializable {


    /**
     * 报废类型code 值
     */
    private Integer scrappedTypCode;

    /**
     * 报废类型名称
     */
    private String scrappedTypeName;

    public Integer getScrappedTypCode() {
        return scrappedTypCode;
    }

    public void setScrappedTypCode(Integer scrappedTypCode) {
        this.scrappedTypCode = scrappedTypCode;
    }

    public String getScrappedTypeName() {
        return scrappedTypeName;
    }

    public void setScrappedTypeName(String scrappedTypeName) {
        this.scrappedTypeName = scrappedTypeName;
    }
}

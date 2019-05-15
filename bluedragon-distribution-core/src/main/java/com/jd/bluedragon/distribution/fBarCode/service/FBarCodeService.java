package com.jd.bluedragon.distribution.fBarCode.service;

import com.jd.bluedragon.distribution.fBarCode.domain.FBarCode;

import java.util.List;

/**
 * Created by yanghongqiang on 14-8-1.
 */
public interface  FBarCodeService {

    Integer add(FBarCode fBarCode);

    /** 批量生成箱子信息 */
    List<FBarCode> batchAdd(FBarCode fBarCode);

    /** 支持箱号打印，每打印一次， 打印次数加1. */
    Integer print(FBarCode fBarCode);
    /**重打*/
    Integer reprint(FBarCode fBarCode);

    List<FBarCode> findFBarCodees(FBarCode fBarCode);

    FBarCode findFBarCodeByCode(String fBarCodeCode);

    FBarCode findFBarCodeByFBarCodeCode(FBarCode fBarCode);

    List<FBarCode> findFBarCodeesBySite(FBarCode fBarCode);

    /**
     * 查询缓存箱号
     */
    FBarCode findFBarCodeCacheByCode(String fBarCodeCode);

    /**
     * 删除缓存箱号
     */
    Long delfBarCodeCodeCache(String fBarCodeCode);
}

package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.barcode.domain.DmsBarCode;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: omc 查询商品接口
 * @date 2018年07月16日 16时:45分
 */
public interface OmcGoodManager {
    /**
     * 通过编码获取商品基本信息和扩展属性信息
     *
     * @param barcode 商品编码
     * @return
     */
    public List<DmsBarCode> getBaseAndSpecInfo(String barcode);
}

package com.jd.bluedragon.distribution.goodsPhoto.service;

import com.jd.bluedragon.common.dto.photo.GoodsPhotoInfoDto;
import com.jd.bluedragon.distribution.goodsPhoto.domain.GoodsPhotoInfo;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/27 20:56
 * @Description:
 */
public interface GoodsPhoteService {

    Boolean insert(GoodsPhotoInfoDto record);

    GoodsPhotoInfoDto getOneByBarCode(String barCode);
}

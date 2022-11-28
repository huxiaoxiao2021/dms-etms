package com.jd.bluedragon.distribution.goodsPhoto.service.impl;

import com.jd.bluedragon.common.dto.photo.GoodsPhotoInfoDto;
import com.jd.bluedragon.distribution.goodsPhoto.dao.GoodsPhotoDao;
import com.jd.bluedragon.distribution.goodsPhoto.domain.GoodsPhotoInfo;
import com.jd.bluedragon.distribution.goodsPhoto.service.GoodsPhoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/27 20:55
 * @Description:
 */
@Service("goodsPhoteService")
public class GoodsPhotoServiceImpl implements GoodsPhoteService {

    @Autowired
    private GoodsPhotoDao goodsPhotoDao;

    @Override
    public Boolean insert(GoodsPhotoInfoDto dto) {

        GoodsPhotoInfo po = new GoodsPhotoInfo();
        po.setUserCode(dto.getUser().getUserCode());
        po.setUserName(dto.getUser().getUserName());
        po.setSiteCode(dto.getCurrentOperate().getSiteCode());
        po.setSiteName(dto.getCurrentOperate().getSiteName());
        po.setBarCode(dto.getBarCode());
        po.setOperateNode(1);

        po.setCreateUser(dto.getUser().getUserName());
        po.setCreateTime(new Date());
        goodsPhotoDao.insert(po);
        return goodsPhotoDao.insert(po) > 0;
    }

    @Override
    public GoodsPhotoInfoDto getOneByBarCode(String barCode) {
        return null;
    }
}

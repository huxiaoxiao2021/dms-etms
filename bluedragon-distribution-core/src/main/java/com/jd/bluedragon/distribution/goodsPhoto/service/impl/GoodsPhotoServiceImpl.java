package com.jd.bluedragon.distribution.goodsPhoto.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.operation.workbench.enums.PhotoTakeNodeEnum;
import com.jd.bluedragon.common.dto.photo.GoodsPhotoInfoDto;
import com.jd.bluedragon.distribution.goodsPhoto.dao.GoodsPhotoDao;
import com.jd.bluedragon.distribution.goodsPhoto.domain.GoodsPhotoInfo;
import com.jd.bluedragon.distribution.goodsPhoto.service.GoodsPhoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/27 20:55
 * @Description:
 */
@Slf4j
@Service("goodsPhoteService")
public class GoodsPhotoServiceImpl implements GoodsPhoteService {

    @Autowired
    private GoodsPhotoDao goodsPhotoDao;

    @Override
    public Boolean insert(GoodsPhotoInfoDto dto) {
        log.info("insert 货物照片保存入参-{}", JSON.toJSONString(dto));
        GoodsPhotoInfo po = new GoodsPhotoInfo();
        po.setUserCode(dto.getUser().getUserCode());
        po.setUserName(dto.getUser().getUserName());
        po.setSiteCode(dto.getCurrentOperate().getSiteCode());
        po.setSiteName(dto.getCurrentOperate().getSiteName());
        po.setBarCode(dto.getBarCode());
        po.setOperateNode(dto.getOperateNode());
        po.setOperateDesc(PhotoTakeNodeEnum.getDescByCode(dto.getOperateNode()));
        if(CollectionUtils.isNotEmpty(dto.getPhotoUrls())){
            for (int i = 0; i < dto.getPhotoUrls().size(); i++) {
                if(i == 0){
                    po.setUrl1(dto.getPhotoUrls().get(i));
                }else if(i == 1){
                    po.setUrl1(dto.getPhotoUrls().get(i));
                }else if(i == 2){
                    po.setUrl2(dto.getPhotoUrls().get(i));
                }else if(i == 3){
                    po.setUrl3(dto.getPhotoUrls().get(i));
                }else if(i == 4){
                    po.setUrl4(dto.getPhotoUrls().get(i));
                }else if(i == 5){
                    po.setUrl5(dto.getPhotoUrls().get(i));
                }else if(i == 6){
                    po.setUrl6(dto.getPhotoUrls().get(i));
                }
            }
        }
        po.setCreateUser(dto.getUser().getUserName());
        po.setCreateTime(new Date());
        log.info("货物照片保存入参-{}", JSON.toJSONString(po));
        return goodsPhotoDao.insert(po) > 0;
    }

    @Override
    public GoodsPhotoInfo getOneByBarCode(String barCode) {
        return goodsPhotoDao.selectByBarCode(barCode);
    }
}

package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.photo.GoodsPhotoInfoDto;
import com.jd.bluedragon.distribution.goodsPhoto.service.GoodsPhoteService;
import com.jd.bluedragon.external.gateway.service.GoodsPhotoInfoGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/12/2 15:59
 * @Description:
 */
public class GoodsPhotoInfoGatewayServiceImpl implements GoodsPhotoInfoGatewayService {

    private final Logger logger = LoggerFactory.getLogger(GoodsPhotoInfoGatewayServiceImpl.class);


    @Autowired
    private GoodsPhoteService goodsPhoteService;

    @Override
    public JdCResponse<Boolean> saveGoodsPhotoInfo(GoodsPhotoInfoDto dto) {
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.toSucceed("成功");
        try{
            String checkResult = checkParam(dto);
            if(StringUtils.isNotBlank(checkResult)){
                response.toFail(checkResult);
                return  response;
            }
            response.setData(goodsPhoteService.insert(dto));
        }catch (Exception e){
            logger.error("添加货物照片异常!-{}",e.getMessage(),e);
            response.toError("添加货物照片异常!");
        }
        return response;
    }

    private String checkParam(GoodsPhotoInfoDto dto){
        if(dto == null){
            return "入参不能为空!";
        }
        if(dto.getUser() == null || (dto.getUser().getUserCode())<= 0){
            return "操作用户信息不能为空!";
        }
        if(dto.getCurrentOperate() == null || dto.getCurrentOperate().getSiteCode() <= 0){
            return "操作站点信息不能为空!";
        }
        if(StringUtils.isBlank(dto.getBarCode())){
            return "单号不能为空!";
        }
        return "";

    }
}

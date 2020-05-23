package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.three.no.ExpInfoSummaryDto;
import com.jd.bluedragon.external.gateway.service.ExpInfoSummaryGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ps.data.epf.dto.CommonDto;
import com.jd.ps.data.epf.service.ExpInfoSummaryJsfService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jinjingcheng
 * @date 2020/5/18
 */
public class ExpInfoSummaryGatewayServiceImpl implements ExpInfoSummaryGatewayService {
    private final Logger logger = LoggerFactory.getLogger(ExpInfoSummaryGatewayServiceImpl.class);
    @Autowired
    ExpInfoSummaryJsfService expInfoSummaryJsfService;
    @Override
    public JdCResponse<Boolean> addEpfExpInfo(ExpInfoSummaryDto expInfoSummaryDto) {
        JdCResponse<Boolean> response = checkParam(expInfoSummaryDto);
        if(!response.getData()){
            return response;
        }
        com.jd.ps.data.epf.dto.ExpInfoSummaryDto dto = new com.jd.ps.data.epf.dto.ExpInfoSummaryDto();
        CommonDto commonDto = null;
        try {
            BeanUtils.copyProperties(expInfoSummaryDto,dto);
            commonDto = expInfoSummaryJsfService.addEpfExpInfo(dto);
        }catch (Exception e){
            logger.error("根据dto:{}调慧眼上传是三无图片信息时异常", JsonHelper.toJson(dto), e);
            response.toFail("上传失败！");
            response.setData(Boolean.FALSE);
            return response;
        }
        if(commonDto == null){
            logger.warn("根据dto:{}调慧眼上传是三无图片信息，无返回值", JsonHelper.toJson(dto));
            response.toFail("上传失败请稍后重试！");
            response.setData(Boolean.FALSE);
            return response;
        }
        if(CommonDto.CODE_SUCCESS != commonDto.getCode()){
            logger.warn("根据dto:{}调慧眼上传是三无图片信息，返回值:{}", JsonHelper.toJson(dto), JsonHelper.toJson(commonDto));
            response.toFail("上传失败！");
            response.setData(Boolean.FALSE);
            return response;
        }
        response.setMessage("上传成功！");
        return response;
    }
    private JdCResponse<Boolean> checkParam(ExpInfoSummaryDto expInfoSummaryDto){
        JdCResponse<Boolean> response = new JdCResponse<Boolean>();
        response.setData(Boolean.FALSE);
        if(StringUtils.isBlank(expInfoSummaryDto.getExpCode())){
            response.toFail("三无码不能为空，请扫描正确的三无码");
            return response;
        }
        if(StringUtils.isBlank(expInfoSummaryDto.getHappenTime())){
            response.toFail("三无发生时间不能为空，请重试！");
            return response;
        }
        if(StringUtils.isBlank(expInfoSummaryDto.getPin())){
            response.toFail("操作人不能为空，请重试！");
            return response;
        }
        if(CollectionUtils.isEmpty(expInfoSummaryDto.getImageUrls())){
            response.toFail("请选择要上传的图片！");
            return response;
        }
        response.setData(Boolean.TRUE);
        response.toSucceed();
        return response;
    }


}

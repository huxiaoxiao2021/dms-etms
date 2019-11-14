package com.jd.bluedragon.distribution.eclpPackage.manager.impl;

import com.jd.bluedragon.distribution.eclpPackage.manager.EclpPackageApiManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ldop.oms.api.ResponseDTO;
import com.jd.ldop.oms.api.order.PackageApi;
import com.jd.ldop.oms.api.order.dto.PackageDTO;
import com.jd.ldop.oms.api.order.dto.PackageQueryDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Service("eclpPackageApiManager")
public class EclpPackageApiManagerImpl implements EclpPackageApiManager {


    private Logger logger = LoggerFactory.getLogger(EclpPackageApiManagerImpl.class);

    @Autowired
    private PackageApi eclpPackageApi;

    /**
     * 根据商家ID和商家单号获取一个包裹
     * @param busiId 商家ID
     * @param busiOrderCode 商家单号
     * @return
     */
    public String queryPackage(Integer busiId ,String busiOrderCode){
        if(busiId == null || StringUtils.isBlank(busiOrderCode)){
            //参数错误
            return StringUtils.EMPTY;
        }

        PackageQueryDTO packageQueryDTO = new PackageQueryDTO();
        packageQueryDTO.setCustomerId(busiId);
        packageQueryDTO.setBoxCodeList(Arrays.asList(busiOrderCode));
        try{
            ResponseDTO<List<PackageDTO>> responseDTO = eclpPackageApi.queryPackageList(packageQueryDTO);

            if(ResponseDTO.SUCCESS_CODE.equals(responseDTO.getStatusCode())
                    && responseDTO.getData()!=null && !responseDTO.getData().isEmpty()){
                return responseDTO.getData().get(0).getPackageCode();
            }else{
                logger.error(MessageFormat.format("根据商家ID和商家单号获取一个包裹失败{0}|{1}|{2}",busiId,busiOrderCode, JsonHelper.toJson(responseDTO)));
            }
        }catch (Exception e){
            logger.error(MessageFormat.format("根据商家ID和商家单号获取一个包裹异常{0}|{1}",busiId,busiOrderCode),e);
        }

        return StringUtils.EMPTY;
    }
}

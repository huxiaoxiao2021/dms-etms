package com.jd.bluedragon.distribution.siteRetake.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.SiteRetakeManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeCondition;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeOperation;
import com.jd.bluedragon.distribution.siteRetake.service.SiteRetakeService;
import com.jd.common.orm.page.Page;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.erp.dto.vendor.ReTakeRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 驻厂批量再取
 * @date 2018年08月02日 14时:47分
 */
@Service("siteRetakeService")
public class SiteRetakeServiceImpl implements SiteRetakeService {
    @Autowired
    private SiteRetakeManager siteRetakeManager;

    private int operatorSource = 3;

    /**
     * 查商家
     *
     * @param key
     * @return
     */
    @Override
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key) {
        return siteRetakeManager.queryBasicTraderInfoByKey(key);
    }

    public Page<VendorOrder> queryVendorOrderList(SiteRetakeCondition siteRetakeCondition, Page page) {
        VendorOrder vendorOrder = new VendorOrder();
        if (siteRetakeCondition.getSiteCode()!=null){
            vendorOrder.setSiteCode(siteRetakeCondition.getSiteCode());
        }
        if (siteRetakeCondition.getVendorId()!=null){
            vendorOrder.setSellerId(siteRetakeCondition.getVendorId());
        }
        if (siteRetakeCondition.getAssignTime()!=null){
            vendorOrder.setAssignTime(siteRetakeCondition.getAssignTime());
        }
        if (siteRetakeCondition.getWaybillCreateTime()!=null){
            vendorOrder.setWaybillCreateTime(siteRetakeCondition.getWaybillCreateTime());
        }
        if (siteRetakeCondition.getWaybillCode()!=null){
            vendorOrder.setWaybillCode(siteRetakeCondition.getWaybillCode());
        }
        Page<VendorOrder> pageResult = siteRetakeManager.selectVendorOrderList(vendorOrder, page);
        pageResult.setCurrentPage(page.getCurrentPage());
        pageResult.setPageSize(page.getPageSize());
        return pageResult;


    }

    public JdResponse<String> updateCommonOrderStatus(SiteRetakeOperation siteRetakeOperation) {
        JdResponse<String> result = new JdResponse<String>();
        String[] waybillcodes = siteRetakeOperation.getWaybillCode().split(Constants.SEPARATOR_COMMA);
        if (waybillcodes != null && waybillcodes.length > 0) {
            List< ReTakeRequestDTO > reTakeRequestDTOS = new ArrayList<>();
            for (String waybillCode : waybillcodes) {
                ReTakeRequestDTO reTakeRequestDTO = new ReTakeRequestDTO();
                reTakeRequestDTO.setOperatorId(siteRetakeOperation.getOperatorId());
                reTakeRequestDTO.setStatus(siteRetakeOperation.getStatus());
                reTakeRequestDTO.setOperatorName(siteRetakeOperation.getOperatorName());
                reTakeRequestDTO.setSiteCode(siteRetakeOperation.getSiteCode());
                reTakeRequestDTO.setSiteName(siteRetakeOperation.getSiteName());
                reTakeRequestDTO.setEndReason(siteRetakeOperation.getEndReason());
                reTakeRequestDTO.setRequiredStartTime(siteRetakeOperation.getRequiredStartTime());
                reTakeRequestDTO.setRequiredEndTime(siteRetakeOperation.getRequiredEndTime());
                reTakeRequestDTO.setOperatorSource(operatorSource);//系统标识
                reTakeRequestDTO.setRemark(siteRetakeOperation.getRemark());
                reTakeRequestDTO.setUpdateTime(siteRetakeOperation.getOperatorTime());
                reTakeRequestDTO.setWaybillCode(waybillCode);
                reTakeRequestDTOS.add(reTakeRequestDTO);
            }
            try {
                List<String> failWaybillCodes = siteRetakeManager.batchUpdateReTakeTime(reTakeRequestDTOS);
                if(failWaybillCodes!=null && !failWaybillCodes.isEmpty()){
                    result.setData(failWaybillCodes.toString());
                }
            }catch (Exception e){
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
                return result;
            }


        } else {
            result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            result.setMessage("无可操作运单");
        }
        return result;
    }


}

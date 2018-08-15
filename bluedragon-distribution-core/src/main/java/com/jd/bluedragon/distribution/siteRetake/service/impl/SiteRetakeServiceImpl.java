package com.jd.bluedragon.distribution.siteRetake.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.SiteRetakeManager;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeCondition;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeOperation;
import com.jd.bluedragon.distribution.siteRetake.service.SiteRetakeService;
import com.jd.common.orm.page.Page;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
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

    public List<VendorOrder> queryVendorOrderList(SiteRetakeCondition siteRetakeCondition) {
        List<VendorOrder> resultDate = new ArrayList<VendorOrder>();
        VendorOrder vendorOrder = new VendorOrder();
        vendorOrder.setSiteCode(siteRetakeCondition.getSiteCode());
        if (siteRetakeCondition.getVendorId()!=null){
            vendorOrder.setSellerId(siteRetakeCondition.getVendorId());
        }
        if (SiteRetakeCondition.TIME_TYPE_ASSIGNTIME.equals(siteRetakeCondition.getTimeType())){
            vendorOrder.setAssignTime(siteRetakeCondition.getSelectTime());
        }else{
            vendorOrder.setWaybillCreateTime(siteRetakeCondition.getSelectTime());
        }
        int totalPage = 1;//总页数
        int currPage = 0;//翻页控制
        while (currPage < totalPage) {
            currPage++;//自动翻页查询
            if (currPage > 100) {
                break;//防止对面系统有BUG，导致我们循环
            }
            Page page = new Page();
            page.setCurrentPage(currPage);
            page.setPageSize(100);
            Page<VendorOrder> pageResult = siteRetakeManager.selectVendorOrderList(vendorOrder, page);
            if (pageResult == null) {
                break;
            } else {
                //获取总页数
                totalPage = pageResult.getTotalPage();
            }
            resultDate.addAll(pageResult.getResult());
        }
        return resultDate;
    }

    public InvokeResult<String> updateCommonOrderStatus(SiteRetakeOperation siteRetakeOperation) {
        VendorOrder vendorOrder = new VendorOrder();
        vendorOrder.setStatus(siteRetakeOperation.getStatus());
        vendorOrder.setOperatorId(siteRetakeOperation.getOperatorId());
        vendorOrder.setSiteCode(siteRetakeOperation.getSiteCode());
        vendorOrder.setEndReason(siteRetakeOperation.getEndReason());
        vendorOrder.setRequiredStartTime(siteRetakeOperation.getRequiredStartTime());
        vendorOrder.setRequiredEndTime(siteRetakeOperation.getRequiredEndTime());
        vendorOrder.setOperatorSource(3);//系统标识
        vendorOrder.setRemark(siteRetakeOperation.getRemark());
        InvokeResult<String> result=new InvokeResult<String>();
        result.setCode(200);
        result.setMessage("");
        String[] waybillcodes=siteRetakeOperation.getWaybillCode().split(Constants.SEPARATOR_COMMA);
        if (waybillcodes!=null&&waybillcodes.length>0){

            for (String waybillCode:waybillcodes){
                vendorOrder.setWaybillCode(waybillCode);
                InvokeResult<String> resultOne= siteRetakeManager.updateCommonOrderStatus(vendorOrder);
                if (resultOne.getCode()!=200){
                    result.setCode(101);
                    result.setMessage(result.getMessage()+"运单["+waybillCode+"]保存失败："+resultOne.getMessage()+"\n");
                }
            }
        }else{
            result.setCode(100);
            result.setMessage("无可操作运单");
        }
        return result;
    }
}

package com.jd.bluedragon.distribution.kuaiyun.weight.service;

import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.common.web.LoginContext;

/**
 * 运单称重
 * B2B的称重量方简化功能，支持按运单/包裹号维度录入总重量（KG）和总体积（立方米）
 * @author luyue  2017-12
 */
public interface WeighByWaybillService
{
    public void insertWaybillWeightEntry(WaybillWeightVO vo) throws WeighByWaybillExcpetion;

    public String convertToWaybillCode(String codeStr) throws WeighByWaybillExcpetion;

    public boolean validateWaybillCodeReality(String waybillCode) throws WeighByWaybillExcpetion;

    public void validWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion;

    public void invalidWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion;

    public void errorLogForOperator(WaybillWeightVO dto,LoginContext loginContext,boolean isImport);

    boolean isOpenIntercept();
}

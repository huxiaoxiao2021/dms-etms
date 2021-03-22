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

    boolean waybillTransferB2C(WaybillWeightVO vo);
   /* *//**校验是否是包裹号*//*
    String checkPackageCode(String codeStr) throws WeighByWaybillExcpetion;
    *//**校验包裹号是否存在 需要同时校验运单号是否存在*//*
    boolean validatePackageCodeReality(String packageCode) throws WeighByWaybillExcpetion;

    void insertPackageWeightEntry(WaybillWeightVO vo) throws WeighByWaybillExcpetion;*/
}

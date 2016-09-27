package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by dudong on 2016/9/26.
 */
public class SendDetailIndexDao extends SendDatailDao{

    private static final Log logger = LogFactory.getLog(SendDetailIndexDao.class);

    @Autowired
    private KvIndexDao kvIndexDao;

    @Override
    public Integer add(String namespace, SendDetail entity) { //// FIXME: 2016/9/26
        try {
            KvIndex kvIndex = new KvIndex();
            kvIndex.setKeyword(entity.getWaybillCode());
            kvIndex.setValue(String.valueOf(entity.getCreateSiteCode()));
            kvIndexDao.add(KvIndexDao.namespace,kvIndex);
            kvIndex.setKeyword(entity.getBoxCode());
            kvIndexDao.add(kvIndexDao.namespace,kvIndex);
        } catch (Throwable e) {
            logger.error("保存到中间索引表失败 " + JsonHelper.toJson(entity), e);
        }
        return super.add(namespace, entity);
    }

    @Override
    public Integer update(String namespace, SendDetail entity) {
        return super.update(namespace, entity);
    }

    @Override
    public List<SendDetail> querySendDatailsByBoxCode(SendDetail tSendDatail) { //// FIXME: 2016/9/26 
        return super.querySendDatailsByBoxCode(tSendDatail);
    }

    @Override
    public List<SendDetail> querySendDatailsByPackageCode(SendDetail tSendDatail) { //// FIXME: 2016/9/26
        return super.querySendDatailsByPackageCode(tSendDatail);
    }

    @Override
    public SendDetail querySendDatailBySendStatus(SendDetail tSendDatail) {
        return super.querySendDatailBySendStatus(tSendDatail);
    }

    @Override
    public SendDetail queryOneSendDatailByBoxCode(String boxCode) { //// FIXME: 2016/9/26
        return super.queryOneSendDatailByBoxCode(boxCode);
    }

    @Override
    public List<SendDetail> findSendDetails(SendDetail param) {
        return super.findSendDetails(param);
    }

    @Override
    public List<SendDetail> queryBySiteCodeAndSendCode(SendDetail query) {
        return super.queryBySiteCodeAndSendCode(query);
    }

    @Override
    public List<SendDetail> querySendDatailsBySelective(SendDetail querySendDatail) {
        return super.querySendDatailsBySelective(querySendDatail);
    }

    @Override
    public boolean updateSendDatail(SendDetail SendDatail) {
        return super.updateSendDatail(SendDatail);
    }

    @Override
    public boolean cancelSendDatail(SendDetail sendDetial) {
        return super.cancelSendDatail(sendDetial);
    }

    @Override
    public List<SendDetail> findUpdatewaybillCodeMessage() {
        return super.findUpdatewaybillCodeMessage();
    }

    @Override
    public boolean updatewaybillCodeStatus(SendDetail sendDetial) {
        return super.updatewaybillCodeStatus(sendDetial);
    }

    @Override
    public boolean updateSendStatusByPackage(SendDetail sendDetial) {
        return super.updateSendStatusByPackage(sendDetial);
    }

    @Override
    public boolean updateWeight(SendDetail sendDetail) {
        return super.updateWeight(sendDetail);
    }

    @Override
    public Boolean canCancel(SendDetail sendDetail) {
        return super.canCancel(sendDetail);
    }

    @Override
    public Boolean canCancel2(SendDetail sendDetail) {
        return super.canCancel2(sendDetail);
    }

    @Override
    public Boolean canCancelFuzzy(SendDetail sendDetail) {
        return super.canCancelFuzzy(sendDetail);
    }

    @Override
    public Integer updateCancel(SendDetail sendDetail) {
        return super.updateCancel(sendDetail);
    }

    @Override
    public Integer updateCancelBatch(SendDetail sendDetail) {
        return super.updateCancelBatch(sendDetail);
    }

    @Override
    public List<SendDetail> querySendCodesByWaybills(String waybillCodeIn) { //// FIXME: 2016/9/26 
        return super.querySendCodesByWaybills(waybillCodeIn);
    }

    @Override
    public List<SendDetail> querySendCodesByWaybill(String waybillCodeIn) {
        return super.querySendCodesByWaybill(waybillCodeIn);
    }

    @Override
    public List<SendDetail> getSendSiteID(SendDetail sendDetail) {
        return super.getSendSiteID(sendDetail);
    }

    @Override
    public boolean checkSendByPackage(SendDetail sendDetail) {
        return super.checkSendByPackage(sendDetail);
    }

    @Override
    public boolean checkSendSendCode(SendDetail sendDetail) {
        return super.checkSendSendCode(sendDetail);
    }

    @Override
    public List<SendDetail> findOrder(SendDetail query) {
        return super.findOrder(query);
    }

    @Override
    public List<SendDetail> findDeliveryPackageBySite(SendDetail query) { //// FIXME: 2016/9/26 
        return super.findDeliveryPackageBySite(query);
    }

    @Override
    public List<SendDetail> findDeliveryPackageByCode(SendDetail query) { //// FIXME: 2016/9/26
        return super.findDeliveryPackageByCode(query);
    }

    @Override
    public List<SendDetail> querySortingDiff(SendDetail query) {
        return super.querySortingDiff(query);
    }

    @Override
    public List<SendDetail> queryWithoutPackageNum(SendDetail query) {
        return super.queryWithoutPackageNum(query);
    }

    @Override
    public Integer updatePackageNum(SendDetail sendDetail) {
        return super.updatePackageNum(sendDetail);
    }

    @Override
    public Integer cancelDelivery(SendDetail sendDetail) {
        return super.cancelDelivery(sendDetail);
    }

    @Override
    public List<SendDetail> queryBySendCodeAndSendType(SendDetail query) {
        return super.queryBySendCodeAndSendType(query);
    }

    @Override
    public List<SendDetail> batchQuerySendDList(SendDetail querySendDatail) {
        return super.batchQuerySendDList(querySendDatail);
    }

    @Override
    public List<SendDetail> queryWaybillsByPackCode(String packCode) {
        return super.queryWaybillsByPackCode(packCode);
    }

    @Override
    public List<SendDetail> queryWaybillsByBoxCode(String boxCode) {
        return super.queryWaybillsByBoxCode(boxCode);
    }

    @Override
    public List<SendDetail> queryWaybillsBySendCode(String sendCode) {
        return super.queryWaybillsBySendCode(sendCode);
    }

    @Override
    public List<SendDetail> queryWaybillsByDepartID(Long departureID) {
        return super.queryWaybillsByDepartID(departureID);
    }

    @Override
    public List<String> querySendCodesByDepartID(Long departureID) {
        return super.querySendCodesByDepartID(departureID);
    }
}

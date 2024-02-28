package com.jd.bluedragon.distribution.consumer.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxPackageDto;
import com.jd.bluedragon.distribution.box.domain.StoreBoxDetail;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxReq;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.distribution.box.domain.Box.BOX_STATUS_SEALED;

@Service("boxDetailFromStoreConsumer")
@Slf4j
public class BoxDetailFromStoreConsumer extends MessageBaseConsumer {

    private int batchSize =512;

    @Autowired
    private UccPropertyConfiguration ucc;
    @Autowired
    BoxService boxService;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Autowired
    SortingService sortingService;
    @Autowired
    SendDetailService sendDetailService;
    @Override
    public void consume(Message message) throws Exception {
        if (ObjectHelper.isEmpty(message) || StringUtils.isEmpty(message.getText())) {
            log.warn("boxDetailFromStoreConsumer data  is empty！");
            return;
        }

        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("boxDetailFromStoreConsumer consume -->receive data not json type，content is：【{}】", message.getText());
            return;
        }

        StoreBoxDetail boxDetail = JsonHelper.fromJson(message.getText(), StoreBoxDetail.class);
        if (ObjectHelper.isEmpty(boxDetail) || CollectionUtils.isEmpty(boxDetail.getPackageList())) {
            log.warn("receive store box detail exception：{}", message.getText());
            return;
        }
        if (boxDetail.getPackageList().size() > ucc.getStorageBoxDetailMaxSizeLimit()){
            //关键字告警
            log.error("receive store box detail size too large：boxCode：{},data：{}", boxDetail.getBoxCode(),message.getText());
            return;
        }
        log.info("boxDetailFromStoreConsumer data:{}",message.getText());

        //校验箱号的合法性
        if (!checkBoxDetailLegality(boxDetail)){
            return;
        }
        //幂等执行 打包明细存储
        execStorageBoxDetailIdempotently(boxDetail);
        //全部执行成功，变更状态
        updateBoxStatus(boxDetail);
    }

    private void updateBoxStatus(StoreBoxDetail boxDetail) {
    }

    private boolean checkBoxDetailLegality(StoreBoxDetail boxDetail) {
        if (ObjectHelper.isEmpty(boxDetail.getBoxCode()) || ObjectHelper.isEmpty(boxDetail.getStoreInfo())
        || ObjectHelper.isEmpty(boxDetail.getReceiveSiteCode()) || CollectionUtils.isEmpty(boxDetail.getPackageList())){
            log.error("receive store box illegal argument:{}",JsonHelper.toJson(boxDetail));
            return false;
        }
        //查询箱号
        Box box =boxService.findBoxByCode(boxDetail.getBoxCode());
        if (ObjectHelper.isEmpty(box)){
            log.warn("receive store box detail exception：box:{} non-existent",box.getCode());
            return false;
        }
        checkBoxIfNeedUpdate(box,boxDetail);
        checkIfNeedConvertUserInfo(boxDetail);
        final Result<Boolean> upsertBoxMaterialRelation4WmsBoxUsageResult = boxService.upsertBoxMaterialRelation4WmsBoxUsage(boxDetail);
        if (!upsertBoxMaterialRelation4WmsBoxUsageResult.isSuccess()) {
            log.error("checkBoxDetailLegality BoxService.upsertBoxMaterialRelation4WmsBoxUsage fail {} {}", JsonHelper.toJson(upsertBoxMaterialRelation4WmsBoxUsageResult), JsonHelper.toJson(boxDetail));
            // throw new RuntimeException("处理箱号绑定物资失败！");
        }
        //判断箱号状态-总体消息执行的状态-幂等防重
        if (BOX_STATUS_SEALED.equals(box.getStatus())){//TODO  用这个状态会不是对其他业务有影响，换成预留字段 或者 redis
            //消息重放场景-直接跳过
            return false;
        }
        //判断是否已经发货
        boolean success =boxService.checkBoxIsSent(box.getCode(),box.getCreateSiteCode());
        if (success){
            log.warn("receive store box detail exception：box:{} has been sent",box.getCode());
            return false;
        }
        return true;
    }

    private void checkIfNeedConvertUserInfo(StoreBoxDetail boxDetail) {
        for (BoxPackageDto packageDto :boxDetail.getPackageList()){
            if (ObjectHelper.isEmpty(packageDto.getUserCode()) && ObjectHelper.isNotNull(packageDto.getUserErp())) {
                BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseMajorManager.getBaseStaffByErpCache(packageDto.getUserErp());
                if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getStaffNo())){
                    packageDto.setUserCode(baseStaffSiteOrgDto.getStaffNo());
                    if(StringUtils.isBlank(boxDetail.getOperateUserErp())){
                        boxDetail.setOperateUserErp(packageDto.getUserErp());
                    }
                }else {
                    packageDto.setUserCode(-1);
                }
            } else {
                if(StringUtils.isBlank(boxDetail.getOperateUserErp())){
                    boxDetail.setOperateUserErp(packageDto.getUserErp());
                }
            }
        }

        if(StringUtils.isBlank(boxDetail.getOperateUserErp())){
            boxDetail.setOperateUserErp("system");
        }
    }

    private void checkBoxIfNeedUpdate(Box box, StoreBoxDetail boxDetail) {
        if (ObjectHelper.isEmpty(box.getCreateSiteCode()) || ObjectHelper.isEmpty(box.getReceiveSiteCode())){
            if (ObjectHelper.isNotNull(boxDetail.getStoreInfo()) && ObjectHelper.isNotNull(boxDetail.getStoreInfo().getCky2()) && ObjectHelper.isNotNull(boxDetail.getStoreInfo().getStoreId())){
                PsStoreInfo result =baseMajorManager.getStoreByCky2("wms",boxDetail.getStoreInfo().getCky2(),boxDetail.getStoreInfo().getStoreId());
                if (ObjectHelper.isEmpty(result)
                        || ObjectHelper.isEmpty(result.getDmsSiteId()) || ObjectHelper.isEmpty(result.getDmsStoreName())){
                    blockingWait(3000);
                    throw new RuntimeException("未获取到仓对应的基础资料场地信息！");
                }
                box.setCreateSiteCode(result.getDmsSiteId());
                box.setCreateSiteName(result.getDmsStoreName());
                boxDetail.setCreateSiteCode(result.getDmsSiteId());
                boxDetail.setCreateSiteName(result.getDmsStoreName());
            }


            BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseMajorManager.getBaseSiteBySiteId(boxDetail.getReceiveSiteCode());
            if (ObjectHelper.isEmpty(baseStaffSiteOrgDto) || ObjectHelper.isEmpty(baseStaffSiteOrgDto.getDmsName())){
                blockingWait(3000);
                throw new RuntimeException("未获取到目的场地信息！");
            }
            box.setReceiveSiteName(baseStaffSiteOrgDto.getSiteName());
            boxDetail.setReceiveSiteName(baseStaffSiteOrgDto.getSiteName());

            UpdateBoxReq updateBoxReq =assembleUpdateBoxReq(box,boxDetail);
            boxService.updateBox(updateBoxReq);
            return;
        }

        boxDetail.setCreateSiteCode(box.getCreateSiteCode());
        boxDetail.setCreateSiteName(box.getCreateSiteName());
        boxDetail.setReceiveSiteName(boxDetail.getReceiveSiteName());
    }

    private  UpdateBoxReq assembleUpdateBoxReq(Box box,StoreBoxDetail boxDetail) {
        UpdateBoxReq updateBoxReq =new UpdateBoxReq();
        updateBoxReq.setBoxCode(box.getCode());
        updateBoxReq.setCreateSiteCode(box.getCreateSiteCode());
        updateBoxReq.setCreateSiteName(box.getCreateSiteName());
        updateBoxReq.setReceiveSiteCode(box.getReceiveSiteCode());
        updateBoxReq.setOpeateTime(new Date());
        updateBoxReq.setMixBoxType(0);
        updateBoxReq.setTransportType(1);

        if (ObjectHelper.isNotNull(boxDetail.getPackageList().get(0).getUserCode())){
            updateBoxReq.setUserCode(boxDetail.getPackageList().get(0).getUserCode());
        }
        else if (ObjectHelper.isNotNull(boxDetail.getPackageList().get(0).getUserErp())) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseMajorManager.getBaseStaffByErpCache(boxDetail.getPackageList().get(0).getUserErp());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getStaffNo())){
                updateBoxReq.setUserCode(baseStaffSiteOrgDto.getStaffNo());
            }
        }
        updateBoxReq.setUserName(boxDetail.getPackageList().get(0).getUserName());
        return updateBoxReq;
    }

    private static void blockingWait(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("消费等待异常",e);
        }
    }

    private void execStorageBoxDetailIdempotently(StoreBoxDetail boxDetail) {
        final List<BoxPackageDto> rawPackageList =boxDetail.getPackageList();

        if (rawPackageList.size() > batchSize){
            for (int i = 0; i < rawPackageList.size(); i += batchSize) {
                List<BoxPackageDto> subPackageList = rawPackageList.subList(i, Math.min(i + batchSize, boxDetail.getPackageList().size()));
                boxDetail.setPackageList(subPackageList);
                storageBoxDetailIdempotently(boxDetail);
            }
        }else {
            storageBoxDetailIdempotently(boxDetail);
        }
    }

    private void storageBoxDetailIdempotently(StoreBoxDetail boxDetail) {
        storageSorting(boxDetail);
        storageSendD(boxDetail);
    }

    private void storageSendD(StoreBoxDetail boxDetail) {
        List<SendDetail> sendDetailList =assembleSendDetailList(boxDetail);
        sendDetailService.deleteOldAndInsertNewSendD(sendDetailList);
    }

    private List<SendDetail> assembleSendDetailList(StoreBoxDetail boxDetail) {
        List<SendDetail> sendDetailList =new ArrayList<>();
        List<BoxPackageDto> packageDtoList =boxDetail.getPackageList();
        Date now =new Date();
        for (BoxPackageDto packageDto : packageDtoList){
            SendDetail sendDetail = assembleSendDetail(boxDetail, packageDto, now);
            sendDetailList.add(sendDetail);
        }
        return sendDetailList;
    }

    private SendDetail assembleSendDetail(StoreBoxDetail boxDetail, BoxPackageDto packageDto, Date time) {
        SendDetail sendDetail =new SendDetail();

        sendDetail.setCreateSiteCode(boxDetail.getCreateSiteCode());
        sendDetail.setReceiveSiteCode(boxDetail.getReceiveSiteCode());

        sendDetail.setBoxCode(boxDetail.getBoxCode());
        sendDetail.setPackageBarcode(packageDto.getPackageCode());
        sendDetail.setWaybillCode(packageDto.getWaybillCode());

        sendDetail.setCreateUserCode(packageDto.getUserCode());
        sendDetail.setCreateUser(packageDto.getUserName());

        sendDetail.setCreateTime(time);
        sendDetail.setUpdateTime(time);

        sendDetail.setOperateTime(packageDto.getOpeateTime());
        sendDetail.setIsCancel(Constants.YN_NO);
        sendDetail.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        sendDetail.setBizSource(SendBizSourceEnum.ANDROID_PDA_SEND.getCode());
        return sendDetail;
    }

    private void storageSorting(StoreBoxDetail boxDetail) {
        List<Sorting> sortingList =assembleSortingList(boxDetail);
        sortingService.deleteOldAndInsertNewSorting(sortingList);
    }

    private List<Sorting> assembleSortingList(StoreBoxDetail boxDetail) {
        List<Sorting> sortingList =new ArrayList<>();
        List<BoxPackageDto> packageDtoList =boxDetail.getPackageList();
        Date now =new Date();
        for (BoxPackageDto packageDto : packageDtoList){
            Sorting sorting = assembleSorting(boxDetail, packageDto, now);
            sortingList.add(sorting);
        }
        return sortingList;
    }

    private static Sorting assembleSorting(StoreBoxDetail boxDetail, BoxPackageDto packageDto, Date time) {
        Sorting sorting =new Sorting();

        sorting.setCreateSiteCode(boxDetail.getCreateSiteCode());
        sorting.setCreateSiteName(boxDetail.getCreateSiteName());
        sorting.setReceiveSiteCode(boxDetail.getReceiveSiteCode());
        sorting.setReceiveSiteName(boxDetail.getReceiveSiteName());

        sorting.setBoxCode(boxDetail.getBoxCode());
        sorting.setPackageCode(packageDto.getPackageCode());
        sorting.setWaybillCode(packageDto.getWaybillCode());

        sorting.setCreateUserCode(packageDto.getUserCode());
        sorting.setCreateUser(packageDto.getUserName());
        sorting.setUpdateUserCode(packageDto.getUserCode());
        sorting.setUpdateUser(packageDto.getUserName());
        sorting.setCreateTime(time);
        sorting.setUpdateTime(time);

        sorting.setOperateTime(packageDto.getOpeateTime());
        sorting.setIsCancel(Constants.YN_NO);
        sorting.setType(Constants.BUSSINESS_TYPE_POSITIVE);
        sorting.setBizSource(SortingBizSourceEnum.ANDROID_SORTING.getCode());
        return sorting;
    }

}

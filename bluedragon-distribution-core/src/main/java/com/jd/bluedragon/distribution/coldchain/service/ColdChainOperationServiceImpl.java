package com.jd.bluedragon.distribution.coldchain.service;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.minlog.Log;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.ColdChainOptimizeManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.coldchain.dto.*;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ccmp.ctm.dto.QueryUnloadDto;
import com.jd.common.util.StringUtils;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.tms.basic.dto.BasicDictDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 冷链货物操作服务
 *
 * @author lixin39
 * @date date
 */
@Service
public class ColdChainOperationServiceImpl implements ColdChainOperationService {

    @Autowired
    private ColdChainOptimizeManager coldChainOptimizeManager;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

    @Autowired
    @Qualifier("ccInAndOutBoundProducer")
    private DefaultJMQProducer ccInAndOutBoundProducer;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private SortingService sortingService;

    /**
     * 上传卸货数据
     *
     * @param unloadDto
     * @return
     */
    @Override
    public boolean addUploadTask(ColdChainUnloadDto unloadDto) {
        if (unloadDto != null) {
            return coldChainOptimizeManager.receiveUnloadData(unloadDto);
        }
        return false;
    }

    /**
     * 查询卸货任务
     *
     * @param request
     * @return
     */
    @Override
    public List<QueryUnloadDto> queryUnloadTask(ColdChainQueryUnloadTaskRequest request) {
        if (request != null) {
            return coldChainOptimizeManager.queryUnloadTask(request);
        }
        return Collections.emptyList();
    }

    /**
     * 卸货完成
     *
     * @param taskNo
     * @param operateErp
     * @return
     */
    @Override
    public boolean unloadTaskComplete(String taskNo, String operateErp) {
        if (StringUtils.isNotBlank(taskNo) && StringUtils.isNotBlank(operateErp)) {
            return coldChainOptimizeManager.unloadTaskComplete(taskNo, operateErp);
        }
        return false;
    }

    @Override
    public boolean inAndOutBound(ColdChainInAndOutBoundRequest request) throws JMQException {
        if (request == null) {
            return false;
        }

        String barCode = request.getBarCode();

        List<Message> messages = null;
        if (WaybillUtil.isPackageCode(barCode)) {
            messages = this.buildMessageByPackageCode(request);
        } else if (WaybillUtil.isWaybillCode(barCode)) {
            messages = this.buildMessageByWaybillCode(request);
        } else if (BusinessUtil.isBoxcode(barCode)) {
            messages = this.buildMessageByBoxCode(request);
        } else {
            Log.warn("无法识别的条码");
            return false;
        }
        ccInAndOutBoundProducer.batchSend(messages);
        return true;
    }

    private List<Message> buildMessageByPackageCode(ColdChainInAndOutBoundRequest request) {
        CCInAndOutBoundMessage body = new CCInAndOutBoundMessage();
        body.setPackageNo(request.getBarCode());
        body.setWaybillNo(WaybillUtil.getWaybillCode(request.getBarCode()));
        body.setOrgId(String.valueOf(request.getOrgId()));
        body.setOrgName(request.getOrgName());
        body.setSiteId(String.valueOf(request.getSiteId()));
        body.setSiteName(request.getSiteName());
        body.setOperateERP(request.getOperateERP());
        body.setOperateTime(request.getOperateTime());
        if (request.getOperateType() == 1) {
            body.setOperateType(ColdChainOperateTypeEnum.IN_BOUND.getType());
        } else {
            body.setOperateType(ColdChainOperateTypeEnum.OUT_BOUND.getType());
        }
        Message message = new Message();
        message.setBusinessId(request.getBarCode());
        message.setText(JSON.toJSONString(body));
        message.setTopic(ccInAndOutBoundProducer.getTopic());
        List<Message> messageList = new ArrayList<>(1);
        messageList.add(message);
        return messageList;
    }

    private List<Message> buildMessageByWaybillCode(ColdChainInAndOutBoundRequest request) {
        String waybillCode = request.getBarCode();
        List<String> packageCodeList = this.getPackageCodeListByWaybillCode(waybillCode);
        CCInAndOutBoundMessage body = new CCInAndOutBoundMessage();
        body.setWaybillNo(waybillCode);
        body.setOrgId(String.valueOf(request.getOrgId()));
        body.setOrgName(request.getOrgName());
        body.setSiteId(String.valueOf(request.getSiteId()));
        body.setSiteName(request.getSiteName());
        body.setOperateERP(request.getOperateERP());
        body.setOperateTime(request.getOperateTime());
        if (request.getOperateType() == 1) {
            body.setOperateType(ColdChainOperateTypeEnum.IN_BOUND.getType());
        } else {
            body.setOperateType(ColdChainOperateTypeEnum.OUT_BOUND.getType());
        }
        List<Message> messageList = new ArrayList<>();
        for (String packageCode : packageCodeList) {
            body.setPackageNo(packageCode);
            Message message = new Message();
            message.setBusinessId(request.getBarCode());
            message.setText(JSON.toJSONString(body));
            message.setTopic(ccInAndOutBoundProducer.getTopic());
            messageList.add(message);
        }
        return messageList;
    }

    /**
     * 获取运单下的运单-包裹关系Map
     *
     * @param waybillCode
     * @return
     */
    private List<String> getPackageCodeListByWaybillCode(String waybillCode) {
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCode(waybillCode);
        if (baseEntity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
            List<DeliveryPackageD> packageDList = baseEntity.getData();
            if (packageDList.size() > 0) {
                return this.getPackageCodeStringList(packageDList);
            }
        }
        return null;
    }

    private List<String> getPackageCodeStringList(List<DeliveryPackageD> packageDList) {
        List<String> resultList = new ArrayList<>(packageDList.size());
        for (DeliveryPackageD packageD : packageDList) {
            resultList.add(packageD.getPackageBarcode());
        }
        return resultList;
    }

    private List<Message> buildMessageByBoxCode(ColdChainInAndOutBoundRequest request) {
        String boxCode = request.getBarCode();
        List<String> packageCodeList = sortingService.getPackageCodeListByBoxCode(boxCode);
        CCInAndOutBoundMessage body = new CCInAndOutBoundMessage();
        body.setOrgId(String.valueOf(request.getOrgId()));
        body.setOrgName(request.getOrgName());
        body.setSiteId(String.valueOf(request.getSiteId()));
        body.setSiteName(request.getSiteName());
        body.setOperateERP(request.getOperateERP());
        body.setOperateTime(request.getOperateTime());
        if (request.getOperateType() == 1) {
            body.setOperateType(ColdChainOperateTypeEnum.IN_BOUND.getType());
        } else {
            body.setOperateType(ColdChainOperateTypeEnum.OUT_BOUND.getType());
        }
        List<Message> messageList = new ArrayList<>();
        for (String packageCode : packageCodeList) {
            body.setPackageNo(packageCode);
            body.setWaybillNo(WaybillUtil.getWaybillCode(packageCode));
            Message message = new Message();
            message.setBusinessId(request.getBarCode());
            message.setText(JSON.toJSONString(body));
            message.setTopic(ccInAndOutBoundProducer.getTopic());
            messageList.add(message);
        }
        return messageList;
    }


    @Override
    public List<VehicleTypeDict> getVehicleTypeByType() {
        // type 所有：0 ，冷链：1 ，非冷链：2
        List<BasicDictDto> basicDictDtoList = basicQueryWSManager.getVehicleTypeByType(null, 1);
        return this.convert(basicDictDtoList);
    }

    private List<VehicleTypeDict> convert(List<BasicDictDto> basicDictDtoList) {
        if (basicDictDtoList == null) {
            return null;
        }

        if (basicDictDtoList.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<VehicleTypeDict> vehicleTypeDictList = new ArrayList<>();
        for (BasicDictDto dto : basicDictDtoList) {
            if (dto.getYn() == 0) {
                continue;
            }
            VehicleTypeDict dict = new VehicleTypeDict();
            dict.setDictCode(dto.getDictCode());
            dict.setDictName(dto.getDictName());
            vehicleTypeDictList.add(dict);
        }
        return vehicleTypeDictList;
    }

}

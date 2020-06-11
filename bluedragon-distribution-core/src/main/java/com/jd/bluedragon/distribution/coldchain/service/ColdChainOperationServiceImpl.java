package com.jd.bluedragon.distribution.coldchain.service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.ColdChainOptimizeManager;
import com.jd.bluedragon.core.base.WayBillThermometerApiManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ColdChainOperationResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.coldchain.dto.*;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ccmp.ctm.dto.QueryUnloadDto;
import com.jd.ccmp.ctm.dto.WaybillRequest;
import com.jd.common.util.StringUtils;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.tms.basic.dto.BasicDictDto;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private WayBillThermometerApiManager wayBillThermometerApiManager;

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
    public List<ColdChainUnloadQueryResultDto> queryUnloadTask(ColdChainQueryUnloadTaskRequest request) {
        if (request != null) {
            request.setQueryDays(this.getQueryDaysString());
            List<QueryUnloadDto> result = coldChainOptimizeManager.queryUnloadTask(request);
            return this.assembleResultDto(result);
        }
        return Collections.emptyList();
    }

    /**
     * 获取查询天数字符串，优先去配置，其次取页面传入值，最后取系统默认7天
     *
     * @return
     */
    private String getQueryDaysString() {
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(Constants.SYS_CONFIG_COLD_CHAIN_UNLOAD_QUERY_DAYS);
        if (!sysConfigs.isEmpty()) {
            String content = sysConfigs.get(0).getConfigContent();
            if (NumberUtils.isNumber(content)) {
                if (Integer.valueOf(content) > 0) {
                    return content;
                }
            }
        }
        return null;
    }

    private List<ColdChainUnloadQueryResultDto> assembleResultDto(List<QueryUnloadDto> unloadDtoList) {
        if (unloadDtoList == null || unloadDtoList.size() == 0) {
            return Collections.emptyList();
        }
        List<ColdChainUnloadQueryResultDto> resultList = new ArrayList<>(unloadDtoList.size());
        for (QueryUnloadDto dto : unloadDtoList) {
            ColdChainUnloadQueryResultDto resultDto = new ColdChainUnloadQueryResultDto();
            resultDto.setTaskNo(dto.getTaskNo());
            resultDto.setUnloadTime(dto.getUnloadTime());
            resultDto.setVehicleNo(dto.getVehicleNo());
            resultDto.setVehicleModelName(dto.getVehicleModelName());
            resultDto.setRemainingTime(this.remainingTimeToView(dto.getRemainingTime()));
            resultList.add(resultDto);
        }
        return resultList;
    }

    /**
     * 剩余时间显示转换
     *
     * @param remainingTime
     * @return
     */
    private String remainingTimeToView(String remainingTime) {
        if (StringUtils.isBlank(remainingTime)) {
            return remainingTime;
        }

        // 未超时
        if (!remainingTime.startsWith(Constants.NEGATIVE_SIGN)) {
            return remainingTime;
        }

        String time = remainingTime.substring(1);
        String[] timeArray = time.split(":");
        if (timeArray.length == 1) {
            return "超时" + timeArray[0] + "小时";
        } else if (timeArray.length > 1) {
            if (!NumberUtils.isNumber(timeArray[0]) || !NumberUtils.isNumber(timeArray[1])) {
                return remainingTime;
            }
            float hour = Float.parseFloat(timeArray[0]);
            float minute = Float.parseFloat(timeArray[1]);
            if (minute == 0) {
                return "超时" + timeArray[0] + "小时";
            }

            float minuteFloat = BigDecimal.valueOf(minute / 60).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            if (minuteFloat == 0) {
                return "超时" + timeArray[0] + "小时";
            }

            float viewNum = hour + minuteFloat;
            //四舍五入
            return "超时" + viewNum + "小时";
        } else {
            return remainingTime;
        }
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
    public ColdChainOperationResponse inAndOutBound(ColdChainInAndOutBoundRequest request) throws JMQException {
        ColdChainOperationResponse response = new ColdChainOperationResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        String barCode = request.getBarCode();
        List<Message> messages = null;

        BarCodeType codeType = BusinessUtil.getBarCodeType(barCode);

        switch (codeType) {
            case PACKAGE_CODE: {
                messages = this.buildMessageByPackageCode(request);
                break;
            }
            case WAYBILL_CODE: {
                List<String> packageCodeList = this.getPackageCodeListByWaybillCode(barCode);
                if (packageCodeList != null && packageCodeList.size() > 0) {
                    messages = this.buildMessageByPackageCodeList(request, packageCodeList);
                } else {
                    response.setCode(JdResponse.CODE_PARAM_ERROR);
                    response.setMessage("无效运单号或该运单下无包裹");
                }
                break;
            }
            case BOX_CODE: {
                List<String> packageCodeList = sortingService.getPackageCodeListByBoxCode(barCode);
                if (packageCodeList != null && packageCodeList.size() > 0) {
                    messages = this.buildMessageByPackageCodeList(request, packageCodeList);
                } else {
                    response.setCode(JdResponse.CODE_PARAM_ERROR);
                    response.setMessage("无效箱号或为空箱");
                }
                break;
            }
            default: {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("无法识别的条码");
                break;
            }
        }
        ccInAndOutBoundProducer.batchSend(messages);
        return response;
    }

    private List<Message> buildMessageByPackageCode(ColdChainInAndOutBoundRequest request) {
        List<String> packageCodeList = new ArrayList<>(1);
        packageCodeList.add(request.getBarCode());
        return this.buildMessageByPackageCodeList(request, packageCodeList);
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

    private List<Message> buildMessageByPackageCodeList(ColdChainInAndOutBoundRequest request, List<String> packageCodeList) {
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
    public List<VehicleTypeDict> getVehicleModelList() {
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

    @Override
    public ColdChainOperationResponse boundThermometer(ThermometerRequest request) {
        ColdChainOperationResponse response = new ColdChainOperationResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        WaybillRequest waybillRequest = new WaybillRequest();
        waybillRequest.setTemperDeviceID(request.getThermometerCode());
        waybillRequest.setPackageCode(request.getPackageCode());
        waybillRequest.setWaybillCode(WaybillUtil.getWaybillCode(request.getPackageCode()));
        waybillRequest.setBoxNo(request.getCabinetCode());

        response = wayBillThermometerApiManager.bindWaybillPackage(waybillRequest);

        return response;
    }

}

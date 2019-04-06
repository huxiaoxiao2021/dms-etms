package com.jd.bluedragon.distribution.receive.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.receive.dao.ReceiveWeightCheckDao;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/2/28 18:01
 */
@Service
public class ReceiveWeightCheckServiceImpl implements ReceiveWeightCheckService {

    private Logger log = Logger.getLogger(ReceiveWeightCheckServiceImpl.class);

    @Autowired
    private ReceiveWeightCheckDao receiveWeightCheckDao;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 数据不存在就插入，存在就更新
     * @param receiveWeightCheckResult
     * @return
     */
    @Override
    public int insert(ReceiveWeightCheckResult receiveWeightCheckResult) {
        ReceiveWeightCheckResult receiveWeightCheckResult1 = receiveWeightCheckDao.queryByPackageCode(receiveWeightCheckResult.getPackageCode());
        if(receiveWeightCheckResult1 != null){
            //更新
            return receiveWeightCheckDao.updateByPackageCode(receiveWeightCheckResult);
        }else{
            //插入
            return receiveWeightCheckDao.insert(receiveWeightCheckResult);
        }
    }

    @Override
    public PagerResult<ReceiveWeightCheckResult> queryByCondition(ReceiveWeightCheckCondition condition) {
        PagerResult<ReceiveWeightCheckResult>  result = new PagerResult<ReceiveWeightCheckResult>();
        List<ReceiveWeightCheckResult> list = receiveWeightCheckDao.queryByCondition(condition);
        Integer num = queryNumByCondition(condition);
        result.setRows(list);
        result.setTotal(num);
        return result;
    }

    @Override
    public Integer queryNumByCondition(ReceiveWeightCheckCondition condition) {
        return receiveWeightCheckDao.queryNumByCondition(condition);
    }

    /**
     * 整理导出数据
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(ReceiveWeightCheckCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("复核日期");
        heads.add("包裹号");
        heads.add("商家名称");
        heads.add("复核区域");
        heads.add("复核分拣");
        heads.add("复核人erp");
        heads.add("分拣复重kg");
        heads.add("复核长宽高cm");
        heads.add("复核体积cm³");
        heads.add("揽收区域");
        heads.add("揽收营业部");
        heads.add("揽收人erp");
        heads.add("揽收重量kg");
        heads.add("揽收长宽高cm");
        heads.add("揽收体积cm³");
        heads.add("重量差异");
        heads.add("体积重量差异");
        heads.add("误差标准值");
        heads.add("是否超标(1:超标)");
        resList.add(heads);
        condition.setLimit(-1);
        PagerResult<ReceiveWeightCheckResult> result = queryByCondition(condition);
        if(result != null && result.getRows() != null && result.getRows().size() > 0){
            List<ReceiveWeightCheckResult> list = result.getRows();
            //表格信息
            for(ReceiveWeightCheckResult receiveWeightCheckResult : list){
                List<Object> body = Lists.newArrayList();
                body.add(receiveWeightCheckResult.getReviewDate() == null ? null : DateHelper.formatDate(receiveWeightCheckResult.getReviewDate(), Constants.DATE_TIME_FORMAT));
                body.add(receiveWeightCheckResult.getPackageCode());
                body.add(receiveWeightCheckResult.getBusiName());
                body.add(receiveWeightCheckResult.getReviewOrg());
                body.add(receiveWeightCheckResult.getReviewCreateSiteName());
                body.add(receiveWeightCheckResult.getReviewErp());
                body.add(receiveWeightCheckResult.getReviewWeight());
                body.add(receiveWeightCheckResult.getReviewLwh());
                body.add(receiveWeightCheckResult.getReviewVolume());
                body.add(receiveWeightCheckResult.getReceiveOrg());
                body.add(receiveWeightCheckResult.getReceiveDepartment());
                body.add(receiveWeightCheckResult.getReceiveErp());
                body.add(receiveWeightCheckResult.getReceiveWeight());
                body.add(receiveWeightCheckResult.getReceiveLwh());
                body.add(receiveWeightCheckResult.getReceiveVolume());
                body.add(receiveWeightCheckResult.getWeightDiff());
                body.add(receiveWeightCheckResult.getVolumeWeightDiff());
                body.add(receiveWeightCheckResult.getDiffStandard());
                body.add(receiveWeightCheckResult.getIsExcess());
                resList.add(body);
            }
        }
        return  resList;
    }

    @Override
    public InvokeResult<Integer> waybillExchangeCheckWeightAndVolume(String oldWaybillCode, String newWaybillCode) {
        InvokeResult<Integer> result = new InvokeResult<>();
        result.success();
        //初始化不需要称重
        result.setData(0);

        if (StringHelper.isEmpty(oldWaybillCode) || StringHelper.isEmpty(newWaybillCode)) {
            log.error("参数错误，oldWaybillCode：" + oldWaybillCode + "，newWaybillCode：" + newWaybillCode);
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("参数错误，oldWaybillCode：" + oldWaybillCode + "，newWaybillCode：" + newWaybillCode);
            result.setData(-1);
            return result;
        }
        try{
            boolean isNeedCheck = false;
            //如果老单是取件单，则需要进行重量体积校验，如果不是需要判断老单是否是拒收状态的运单
            if (WaybillUtil.isPickupCode(oldWaybillCode)) {
                isNeedCheck = true;
            } else {
                String waybillCode = oldWaybillCode;
                if (WaybillUtil.isPackageCode(oldWaybillCode)){
                    waybillCode = WaybillUtil.getWaybillCode(oldWaybillCode);
                }
                //判断老单是否是自营订单并且有拒收状态，拒收状态160
                if (WaybillUtil.isJDWaybillCode(waybillCode) && waybillTraceManager.isWaybillRejected(waybillCode)) {
                    isNeedCheck = true;
                }
            }
            //重量体积校验逻辑
            if (isNeedCheck) {
                //根据新单获取大运单对象
                WChoice wChoice = new WChoice();
                wChoice.setQueryWaybillC(true);
                wChoice.setQueryWaybillE(true);
                wChoice.setQueryWaybillM(true);
                BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(newWaybillCode, wChoice);

                boolean isNeedWeight = false;
                boolean isNeedVolume = false;
                //如果大运单对象存在，并且运单信息存在
                if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                    //运单对象中的复重重量
                    Double againWeight = baseEntity.getData().getWaybill().getAgainWeight();

                    //运单中的商品重量
                    Double goodWeight = baseEntity.getData().getWaybill().getGoodWeight();

                    //运单对象中的复核体积
                    String againVolumeStr = baseEntity.getData().getWaybill().getSpareColumn2();

                    //运单中的商品体积
                    Double goodVolume = baseEntity.getData().getWaybill().getGoodVolume();

                    //如果无重量，或者重量小于等于0，需要进行称重
                    if ((againWeight == null || againWeight.compareTo(0d) <= 0) && (goodWeight == null || goodWeight.compareTo(0d) <= 0)) {
                        isNeedWeight = true;
                    }
                    //体积转换为数值类型
                    Double againVolume = null;
                    if (againVolumeStr != null) {
                        try {
                            againVolume = Double.parseDouble(againVolumeStr);
                        } catch (Exception e) {
                            log.error("体积数据转换异常，体积值为：" + againVolumeStr);
                        }
                    }
                    //如果体积不存在，或者体积小于等于0，需要进行量方
                    if ((againVolume == null || againVolume.compareTo(0d) <= 0) && (goodVolume == null || goodVolume.compareTo(0d) <= 0)) {
                        isNeedVolume = true;
                    }

                    //二进制
                    //00表示不需要称重，不需要量方，十进制为0
                    //01表示不需要称重，需要量方，十进制为1
                    //10表示需要称重，不需要量方，十进制为2
                    //11表示需要称重，需要量方，十进制为3
                    if (! isNeedWeight && ! isNeedVolume) {
                        result.setData(0);
                    }
                    else if (! isNeedWeight) {
                        result.setData(1);
                    }
                    else if (! isNeedVolume) {
                        result.setData(2);
                    }
                    else {
                        result.setData(3);
                    }

                    if (isNeedWeight || isNeedVolume) {
                        log.warn("老单号:" + newWaybillCode + ",新单号：" + newWaybillCode + "，需要称重或量方！result：" + result.getData());
                    }
                } else {
                    log.error("调用运单接口获取换新单号的运单信息为空，waybillCode:" + newWaybillCode);
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage("新单号:"+ newWaybillCode + "的运单信息为空，请联系IT人员处理");
                    result.setData(-1);
                    return result;
                }
            }

        }catch (Exception e){
            log.error("判断新单是否必须称重量方服务异常，老单号:" + newWaybillCode + ",新单号：" + newWaybillCode, e);
            result.setData(-1);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}

package com.jd.bluedragon.distribution.labelPrint.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.label.domain.farmar.FarmarEntity;
import com.jd.bluedragon.distribution.label.enums.FarmarCheckTypeEnum;
import com.jd.bluedragon.distribution.labelPrint.dao.farmar.FarmarPrintRecordDao;
import com.jd.bluedragon.distribution.labelPrint.domain.farmar.FarmarPrintEntity;
import com.jd.bluedragon.distribution.labelPrint.domain.farmar.FarmarPrintRequest;
import com.jd.bluedragon.distribution.labelPrint.service.LabelPrintService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tp.common.utils.Objects;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签打印服务实现
 *
 * @author hujiping
 * @date 2022/8/22 4:52 PM
 */
@Service("labelPrintService")
public class LabelPrintServiceImpl implements LabelPrintService {

    // 砝码编码构成中间部分和最后部分（W表示重量标准，V表示尺寸标准）
    private static final String FM_CONSTITUTE_MIDDLE = "-1-1-";
    private static final String FM_CONSTITUTE_LAST_M = "W";
    private static final String FM_CONSTITUTE_LAST_V = "V";

    @Autowired
    private FarmarPrintRecordDao farmarPrintRecordDao;

    @Autowired
    private IGenerateObjectId genObjectId;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public InvokeResult<FarmarPrintEntity> farmarPrintDeal(FarmarPrintRequest request) {
        InvokeResult<FarmarPrintEntity> result = new InvokeResult<>();
        if(Objects.equals(request.getPrintType(), 1)){
            // 补打
            reprint(result, request);
            return result;
        }
        // 打印
        List<FarmarEntity> list = Lists.newArrayList();
        for (int i = 0; i < request.getPrintCount(); i++) {
            list.add(buildFarmarEntity(request));
        }
        int count = farmarPrintRecordDao.batchInsert(list);
        if(count != request.getPrintCount()){
            result.parameterError("砝码打印异常，请重新操作!");
            return result;
        }
        result.setData(convertToPrintResult(list));
        return result;
    }

    private FarmarEntity buildFarmarEntity(FarmarPrintRequest request) {
        FarmarEntity farmarEntity = new FarmarEntity();
        BeanUtils.copyProperties(request, farmarEntity);
        // 自动生成砝码编码
        farmarEntity.setFarmarCode(generateFarmarCode(request));
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getCreateSiteCode());
        farmarEntity.setOrgId(baseSite == null ? null : baseSite.getOrgId());
        farmarEntity.setOrgName(baseSite == null ? null : baseSite.getOrgName());
        return farmarEntity;
    }

    private String generateFarmarCode(FarmarPrintRequest request) {
        String fmPrefix = DmsConstants.CODE_PREFIX_FM.concat(StringHelper.padZero(this.genObjectId.getObjectId(FarmarEntity.class.getName()), 8));
        String fmSuffix = FM_CONSTITUTE_MIDDLE + (Objects.equals(request.getFarmarCheckType(), FarmarCheckTypeEnum.FARMAR_CHECK_TYPE_WEIGHT.getCode())
                ? FM_CONSTITUTE_LAST_M : FM_CONSTITUTE_LAST_V);
        return fmPrefix.concat(fmSuffix);
    }

    private void reprint(InvokeResult<FarmarPrintEntity> result, FarmarPrintRequest request) {
        String farmarCode = request.getFarmarCode();
        FarmarEntity farmarEntity = farmarPrintRecordDao.queryByFarmarCode(farmarCode);
        if(farmarEntity == null){
            result.parameterError("砝码编码" + farmarCode + "不存在!");
            return;
        }
        result.setData(convertToPrintResult(Lists.newArrayList(farmarEntity)));
    }

    private FarmarPrintEntity convertToPrintResult(List<FarmarEntity> list) {
        FarmarEntity farmarEntity = list.get(0);
        FarmarPrintEntity printResult = new FarmarPrintEntity();
        BeanUtils.copyProperties(farmarEntity, printResult);
        List<String> fmCodeList = Lists.newArrayList();
        for (FarmarEntity entity : list) {
            fmCodeList.add(entity.getFarmarCode());
        }
        printResult.setFarmarCodeList(fmCodeList);
        return printResult;
    }

    @Override
    public InvokeResult<FarmarEntity> getFarmarInfo(String farmarCode) {
        InvokeResult<FarmarEntity> result = new InvokeResult<>();
        if(StringUtils.isEmpty(farmarCode)){
            result.parameterError();
            return result;
        }
        FarmarEntity record = farmarPrintRecordDao.queryByFarmarCode(farmarCode);
        if(record == null){
            result.parameterError("根据砝码条码" + farmarCode + "未查询到数据!");
            return result;
        }
        result.setData(record);
        return result;
    }
}

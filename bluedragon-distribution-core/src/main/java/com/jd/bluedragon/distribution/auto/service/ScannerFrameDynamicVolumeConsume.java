package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BigDecimalHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Date;
import static com.jd.bluedragon.Constants.DMS_OUT_MEASURE_BARCODE_TYPE_BOXCODE;
import static com.jd.bluedragon.Constants.DMS_OUT_MEASURE_BARCODE_TYPE_PACKAGECODE;
import static com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume.OPERATE_TYPE_DYNAMIC;

/**
 * @Description 应付动态量方
 * @author jinjingcheng
 * @date 2018/9/14.
 */
@Service("scannerFrameDynamicVolumeConsume")
public class ScannerFrameDynamicVolumeConsume implements ScannerFrameConsume{
    @Autowired
    DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;

    private static final Log logger= LogFactory.getLog(ScannerFrameDynamicVolumeConsume.class);
    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {
        DmsOutWeightAndVolume dmsOutWeightAndVolume = toDmsOutWeightAndVolume(uploadData, config);
        //条码不是包裹号和箱号不处理
        if(dmsOutWeightAndVolume == null){
            return Boolean.TRUE;
        }
        dmsOutWeightAndVolumeService.saveOrUpdate(dmsOutWeightAndVolume);
        return Boolean.TRUE;
    }

    private DmsOutWeightAndVolume toDmsOutWeightAndVolume(UploadData uploadData, GantryDeviceConfig config){
        DmsOutWeightAndVolume dmsOutWeightAndVolume = new  DmsOutWeightAndVolume();
        //扫描的条码
        dmsOutWeightAndVolume.setBarCode(uploadData.getBarCode());
        //条码类型
        int barCodeType = 0;
        //包裹号
        if(WaybillUtil.isPackageCode(uploadData.getBarCode())){
            barCodeType = DMS_OUT_MEASURE_BARCODE_TYPE_PACKAGECODE;
        }else if(BusinessUtil.isBoxcode(uploadData.getBarCode())){
            //箱号
            barCodeType = DMS_OUT_MEASURE_BARCODE_TYPE_BOXCODE;
        }else { //不是包裹号和箱号不处理
            logger.warn("应付动态量方扫描的非包裹号和箱号");
            return null;
        }
        dmsOutWeightAndVolume.setBarCodeType(barCodeType);
        //操作站点编码
        dmsOutWeightAndVolume.setCreateSiteCode(config.getCreateSiteCode());
        //创建时间
        dmsOutWeightAndVolume.setCreateTime(new Date());
        //高 （厘米）
        dmsOutWeightAndVolume.setHeight(uploadData.getHeight() == null ? 0d : new BigDecimal(String.valueOf(uploadData.getHeight())).doubleValue());

        //长（厘米） 这样转 防止float 转double 出现误差
        dmsOutWeightAndVolume.setLength(uploadData.getLength() == null ? 0d : new BigDecimal(String.valueOf(uploadData.getLength())).doubleValue());
        //重量（KG）这样转 防止float 转double 出现误差
        dmsOutWeightAndVolume.setWeight(uploadData.getWeight() == null ? 0d : new BigDecimal(String.valueOf(uploadData.getWeight())).doubleValue());
        //宽（厘米）这样转 防止float 转double 出现误差
        dmsOutWeightAndVolume.setWidth(uploadData.getWidth() == null ? 0d : new BigDecimal(String.valueOf(uploadData.getWidth())).doubleValue());
        //计算体积
        double volume = BigDecimalHelper.mul(dmsOutWeightAndVolume.getHeight(), dmsOutWeightAndVolume.getWidth());
        volume = BigDecimalHelper.mul(volume, dmsOutWeightAndVolume.getLength());
//        体积 立方厘米 转m³
//        volume = BigDecimalHelper.div(volume, 1000000, 6);
        dmsOutWeightAndVolume.setVolume(volume);
        //修改时间
        dmsOutWeightAndVolume.setUpdateTime(dmsOutWeightAndVolume.getCreateTime());
        //量方时间
        dmsOutWeightAndVolume.setMeasureTime(uploadData.getScannerTime());
        //量方操作人青龙id
        dmsOutWeightAndVolume.setMeasureUserCode(config.getOperateUserId());
        // 操作类型 1动态量方
        dmsOutWeightAndVolume.setOperateType(OPERATE_TYPE_DYNAMIC);
        //称重时间
        dmsOutWeightAndVolume.setWeightTime(uploadData.getScannerTime());
        //量方操作人名称
        dmsOutWeightAndVolume.setMeasureUserName(config.getOperateUserName());
        //称重操作人id
        dmsOutWeightAndVolume.setWeightUserCode(config.getOperateUserId());
        //称重操作人名称
        dmsOutWeightAndVolume.setWeightUserName(config.getOperateUserName());
        //有效
        dmsOutWeightAndVolume.setIsDelete(0);
        return dmsOutWeightAndVolume;
    }

}

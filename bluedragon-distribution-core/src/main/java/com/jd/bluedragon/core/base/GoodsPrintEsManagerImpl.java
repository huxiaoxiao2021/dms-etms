package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月19日 18时:42分
 */
@Service("goodsPrintEsManager")
public class GoodsPrintEsManagerImpl implements GoodsPrintEsManager {
    private static final Logger logger = Logger.getLogger(GoodsPrintEsManagerImpl.class);
    @Autowired
    ReportExternalService reportExternalService;

    @Override
    public boolean insertOrUpdate(GoodsPrintDto goodsPrintDto) {
        try {
            BaseEntity<Boolean> booleanBaseEntity= reportExternalService.insertOrUpdate(goodsPrintDto);
            if (booleanBaseEntity.isSuccess()){
                return booleanBaseEntity.getData();
            }else{
                logger.warn("GoodsPrintEsManagerImpl.insertOrUpdate错误，"+booleanBaseEntity.getMessage());
                return false;
            }
        }catch (Exception e){
            logger.error("GoodsPrintEsManagerImpl.insertOrUpdate错误，",e);
        }
        return false;
    }

    @Override
    public GoodsPrintDto findGoodsPrintBySendCodeAndWaybillCode(String sendCode, String waybillCode) {
        try {
            BaseEntity<GoodsPrintDto>  baseEntity=reportExternalService.findGoodsPrintBySendCodeAndWaybillCode(sendCode,waybillCode);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                logger.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndWaybillCode："+baseEntity.getMessage());
                return null;
            }
        }catch (Exception e){
            logger.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndWaybillCode错误，",e);
        }
        return null;
    }

    @Override
    public List<GoodsPrintDto> findGoodsPrintBySendCodeAndStatus(String sendCode) {
        try {
            BaseEntity<List<GoodsPrintDto>> baseEntity= reportExternalService.findGoodsPrintBySendCodeAndStatus(sendCode,Constants.GOODS_PRINT_WAYBILL_STATUS_1);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                logger.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCode："+baseEntity.getMessage());
                return new ArrayList<GoodsPrintDto>();
            }
        }catch (Exception e){
            logger.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCode错误，",e);
        }
        return null;
    }

    @Override
    public List<GoodsPrintDto> findGoodsPrintBySendCodeAndStatusOfPage(String sendCode, int pageNo, int pageSize) {
        try {
            BaseEntity<List<GoodsPrintDto>> baseEntity=  reportExternalService.findGoodsPrintBySendCodeAndStatusOfPage(sendCode,Constants.GOODS_PRINT_WAYBILL_STATUS_1,pageNo,pageSize);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                logger.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeOfPage："+baseEntity.getMessage());
                return new ArrayList<GoodsPrintDto>();
            }
        }catch (Exception e){
            logger.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeOfPage，",e);
        }
        return null;
    }


    @Override
    public Long findGoodsPrintBySendCodeAndStatusCount(String sendCode) {
        try {
            BaseEntity<Long> baseEntity= reportExternalService.findGoodsPrintBySendCodeAndStatusCount(sendCode,Constants.GOODS_PRINT_WAYBILL_STATUS_1);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                logger.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeCount："+baseEntity.getMessage()+",sendCode:"+sendCode);
                return 0L;
            }
        }catch (Exception e){
            logger.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeCount，"+",sendCode:"+sendCode,e);
        }
        return 0L;
    }
}

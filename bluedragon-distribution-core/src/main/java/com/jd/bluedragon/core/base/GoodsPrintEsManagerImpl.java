package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(GoodsPrintEsManagerImpl.class);
    @Autowired
    ReportExternalService reportExternalService;

    @Override
    public boolean insertOrUpdate(GoodsPrintDto goodsPrintDto) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.GoodsPrintEsManagerImpl.insertOrUpdate", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            if(log.isDebugEnabled()){
                log.debug("GoodsPrintEsManagerImpl.insertOrUpdate插入或修改：{}",JsonHelper.toJson(goodsPrintDto));
            }
            BaseEntity<Boolean> booleanBaseEntity= reportExternalService.insertOrUpdate(goodsPrintDto);
            if (booleanBaseEntity.isSuccess()){
                return booleanBaseEntity.getData();
            }else{
                log.warn("GoodsPrintEsManagerImpl.insertOrUpdate失败，{}-{}",JsonHelper.toJson(goodsPrintDto),booleanBaseEntity.getMessage());
                return false;
            }
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("GoodsPrintEsManagerImpl.insertOrUpdate错误：{}",JsonHelper.toJson(goodsPrintDto),e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return false;
    }

    @Override
    public GoodsPrintDto findGoodsPrintBySendCodeAndWaybillCode(String sendCode, String waybillCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndWaybillCode", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseEntity<GoodsPrintDto>  baseEntity=reportExternalService.findGoodsPrintBySendCodeAndWaybillCode(sendCode,waybillCode);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                log.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndWaybillCode：{}-{}-{}",sendCode,waybillCode,baseEntity.getMessage());
                return null;
            }
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndWaybillCode错误：{}-{}",sendCode,waybillCode,e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @Override
    public List<GoodsPrintDto> findGoodsPrintBySendCodeAndStatus(String sendCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndStatus", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseEntity<List<GoodsPrintDto>> baseEntity= reportExternalService.findGoodsPrintBySendCodeAndStatus(sendCode,Constants.GOODS_PRINT_WAYBILL_STATUS_1);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                log.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCode：{}-{}",sendCode,baseEntity.getMessage());
                return new ArrayList<GoodsPrintDto>();
            }
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCode错误:{}",sendCode,e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @Override
    public List<GoodsPrintDto> findGoodsPrintBySendCodeAndStatusOfPage(String sendCode, int pageNo, int pageSize) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndStatusOfPage", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseEntity<List<GoodsPrintDto>> baseEntity=  reportExternalService.findGoodsPrintBySendCodeAndStatusOfPage(sendCode,Constants.GOODS_PRINT_WAYBILL_STATUS_1,pageNo,pageSize);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                log.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeOfPage：{}-{}",sendCode,baseEntity.getMessage());
                return new ArrayList<GoodsPrintDto>();
            }
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeOfPage:{}",sendCode,e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }


    @Override
    public Long findGoodsPrintBySendCodeAndStatusCount(String sendCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeAndStatusCount", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseEntity<Long> baseEntity= reportExternalService.findGoodsPrintBySendCodeAndStatusCount(sendCode,Constants.GOODS_PRINT_WAYBILL_STATUS_1);
            if (baseEntity.isSuccess()){
                return baseEntity.getData();
            }else {
                log.warn("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeCount：{}-{}",sendCode,baseEntity.getMessage());
                return 0L;
            }
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("GoodsPrintEsManagerImpl.findGoodsPrintBySendCodeCount:{}",sendCode,e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return 0L;
    }
}

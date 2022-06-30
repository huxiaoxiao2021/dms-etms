package com.jd.bluedragon.distribution.seal.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.seal.manager.SealCarManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCodesDto;
import com.jd.etms.vos.ws.SealCarWS;
import com.jd.tms.dtp.api.DtpTransAbnormalApi;
import com.jd.tms.dtp.dto.AccountDto;
import com.jd.tms.dtp.dto.TransAbnormalBillCreateDto;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.seal.manager.impl
 * @ClassName: SealCarManagerImpl
 * @Description: VOS封签线上化相关接口
 * @Author： wuzuxiang
 * @CreateDate 2022/2/23 16:48
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service("sealCarManager")
@Slf4j
public class SealCarManagerImpl implements SealCarManager {

    @Autowired
    private SealCarWS sealCarWS;

    @Autowired
    private DtpTransAbnormalApi dtpTransAbnormalApi;

    @Override
    @JProfiler(jKey = "dms.web.SealCarManager.querySealCodes" , jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public CommonDto<SealCodesDto> querySealCodes(SealCodesDto param) {
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-查询待解封的封签方法, 参数：{}", JsonHelper.toJson(param));
        }
        CommonDto<SealCodesDto> commonDto = sealCarWS.querySealCodes(param);
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-查询待解封的封签方法, 参数：{}, 返回值：{}", JsonHelper.toJson(param), JsonHelper.toJson(commonDto));
        }
        if (null == commonDto || commonDto.getCode() != CommonDto.CODE_SUCCESS || null == commonDto.getData()) {
            log.error("VOS封签线上化相关接口-查询待解封的封签方法-调用失败, 参数：{}, 返回值：{}", JsonHelper.toJson(param), JsonHelper.toJson(commonDto));
        }
        return commonDto;
    }

    @Override
    @JProfiler(jKey = "dms.web.SealCarManager.doDeSealCodes" , jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public CommonDto<String> doDeSealCodes(SealCodesDto param) {
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-无货解封签方法, 参数：{}", JsonHelper.toJson(param));
        }
        CommonDto<String> commonDto = sealCarWS.doDesealCodes(param);
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-无货解封签方法, 参数：{}, 返回值：{}", JsonHelper.toJson(param), JsonHelper.toJson(commonDto));
        }
        if (null == commonDto || commonDto.getCode() != CommonDto.CODE_SUCCESS) {
            log.error("VOS封签线上化相关接口-无货解封签方法-调用失败, 参数：{}, 返回值：{}", JsonHelper.toJson(param), JsonHelper.toJson(commonDto));
        }
        return commonDto;
    }

    @Override
    @JProfiler(jKey = "dms.web.SealCarManager.TransAbnormalBillCreateDto" , jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public com.jd.tms.dtp.dto.CommonDto<String> createTransAbnormalStandard(AccountDto accountDto, TransAbnormalBillCreateDto transAbnormalBillCreateDto) {
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-异常登记提交, 参数1：{}, 参数2：{}",
                    JsonHelper.toJson(accountDto),JsonHelper.toJson(transAbnormalBillCreateDto));
        }
        com.jd.tms.dtp.dto.CommonDto<String> commonDto = dtpTransAbnormalApi.createTransAbnormalStandard(accountDto, transAbnormalBillCreateDto);
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-异常登记提交, 参数1：{}, 参数2：{}, 返回值：{}",
                    JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalBillCreateDto), JsonHelper.toJson(commonDto));
        }
        if (null == commonDto || commonDto.getCode() != CommonDto.CODE_SUCCESS) {
            log.error("VOS封签线上化相关接口-异常登记提交-调用失败, 参数1：{}, 参数2：{}, 返回值：{}",
                    JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalBillCreateDto), JsonHelper.toJson(commonDto));
        }

        return commonDto;
    }
    
    @Override
    @JProfiler(jKey = "dms.web.SealCarManager.doSealCodes" , jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public CommonDto<String> doSealCodes(SealCodesDto param) {
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-无货上封签方法, 参数：{}", JsonHelper.toJson(param));
        }
        CommonDto<String> commonDto = sealCarWS.doSealCodes(param);
        if (log.isInfoEnabled()) {
            log.info("VOS封签线上化相关接口-无货上封签方法, 参数：{}, 返回值：{}", JsonHelper.toJson(param), JsonHelper.toJson(commonDto));
        }
        if (null == commonDto || commonDto.getCode() != CommonDto.CODE_SUCCESS) {
            log.error("VOS封签线上化相关接口-无货上封签方法-调用失败, 参数：{}, 返回值：{}", JsonHelper.toJson(param), JsonHelper.toJson(commonDto));
        }
        return commonDto;
    }    
}

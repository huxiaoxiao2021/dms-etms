package com.jd.bluedragon.distribution.seal.manager;

import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCodesDto;
import com.jd.tms.dtp.dto.AccountDto;
import com.jd.tms.dtp.dto.TransAbnormalBillCreateDto;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.seal.manager
 * @ClassName: SealCarManager
 * @Description: VOS封签线上化相关接口
 * @See <a href="https://cf.jd.com/pages/viewpage.action?pageId=682222837">VOS封签线上化相关接口</a>
 * @See <a href="https://cf.jd.com/pages/viewpage.action?pageId=388786339">异常-异常登记提交(APP、PC)</a>
 * @Author： wuzuxiang
 * @CreateDate 2022/2/23 16:46
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface SealCarManager {

    /**
     * 查询待解封的封签方法
     * @param param vehicleNumber
     * @param param sealSiteId
     * @return vehicleNumber billCode sealCodes
     */
    CommonDto<SealCodesDto> querySealCodes (SealCodesDto param);

    /**
     * 无货解封签方法
     * @param param vehicleNumber
     * @param param desealCodes
     * @param param desealSiteId
     * @param param desealSiteName
     * @param param desealUserCode
     * @param param desealUserName
     * @return
     */
    CommonDto<String> doDeSealCodes (SealCodesDto param);

    /**
     * 运输标准异常上报接口
     * 目前分拣只有一个场景进行调用：解封签异常上报
     * @param accountDto
     * @param transAbnormalBillCreateDto
     * @return
     */
    com.jd.tms.dtp.dto.CommonDto<String> createTransAbnormalStandard (AccountDto accountDto, TransAbnormalBillCreateDto transAbnormalBillCreateDto);
    /**
     * 无货上封签方法
     * @param param vehicleNumber
     * @param param sealCodes
     * @param param sealSiteId
     * @param param sealSiteName
     * @param param sealUserCode
     * @param param sealUserName
     * @return
     */
    CommonDto<String> doSealCodes(SealCodesDto param);
}

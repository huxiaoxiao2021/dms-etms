package com.jd.bluedragon.distribution.transfer.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface TransferService {

    /**
     * 保存操作版本， 不成功给返回提示
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<Boolean> saveOperatePdaVersion(String sealCarCode, String pdaVersion);

    /**
     *
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<Boolean> delOperatePdaVersion(String sealCarCode, String pdaVersion);


    /**
     *
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<String> getOperatePdaVersion(String sealCarCode);

}

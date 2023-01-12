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
     * 删除转运新老版本app操作互斥数据（互斥功能）
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<Boolean> delOperatePdaVersion(String sealCarCode, String pdaVersion);


    /**
     * 查询转运新老版本app操作互斥数据（互斥功能）
     * @param sealCarCode
     * @param sealCarCode AppVersionEnums
     * @return
     */
    InvokeResult<String> getOperatePdaVersion(String sealCarCode);

}

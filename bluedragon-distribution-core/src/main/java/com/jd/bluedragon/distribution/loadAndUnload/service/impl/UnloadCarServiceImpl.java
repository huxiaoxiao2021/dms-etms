package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanRequest;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanResult;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 卸车任务实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:06
 */
@Service("unloadCarService")
public class UnloadCarServiceImpl implements UnloadCarService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarDao unloadCarDao;

    @Override
    public InvokeResult<UnloadCarScanResult> getUnloadCarBySealCarCode(String sealCarCode) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
        if(unloadCar == null){
            result.error("封车编码【" + sealCarCode + "】的卸车任务不存在，请检查");
            return result;
        }
        UnloadCarScanResult unloadCarScanResult = new UnloadCarScanResult();
        unloadCarScanResult.setSealCarCode(unloadCar.getSealCarCode());
        unloadCarScanResult.setPackageTotalCount(unloadCar.getPackageNum());
//        unloadCarScanResult.setPackageScanCount(0);
//        unloadCarScanResult.setPackageUnScanCount(unloadCar.getPackageNum());
//        unloadCarScanResult.setSurplusPackageScanCount(0);
        result.setData(unloadCarScanResult);
        return result;
    }

    @Override
    public InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest request) {

        return null;
    }
}

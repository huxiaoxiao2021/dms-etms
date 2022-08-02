package com.jd.bluedragon.distribution.kuaiyun.weight.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.PackageWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.common.web.LoginContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface WeighByPackageService {
    InvokeResult<Boolean> verifyPackageReality(String codeStr, Map<String,Integer> maps, Integer siteCode);

    boolean validateParam(PackageWeightVO waybillWeightVO);

    InvokeResult checkIsExcess(String codeStr, String toString, String toString1);

    List<List<Object>> getExportDataPackage(List<Map> list);

    void errorLogForOperator(PackageWeightVO dto, LoginContext loginContext, boolean isImport);

    boolean insertPackageWeightEntry(PackageWeightVO vo) throws WeighByWaybillExcpetion;

}

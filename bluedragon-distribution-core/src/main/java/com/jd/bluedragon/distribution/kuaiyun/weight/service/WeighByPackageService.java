package com.jd.bluedragon.distribution.kuaiyun.weight.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.common.web.LoginContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface WeighByPackageService {
    InvokeResult<Boolean> verifyPackageReality(String codeStr);

    boolean validateParam(WaybillWeightVO waybillWeightVO);

    InvokeResult checkIsExcess(String codeStr, String toString, String toString1);

    List<List<Object>> getExportDataPackage(List<Map> list);

    void errorLogForOperator(WaybillWeightVO dto, LoginContext loginContext, boolean isImport);

    boolean insertPackageWeightEntry(WaybillWeightVO vo) throws WeighByWaybillExcpetion;

    boolean uploadExcelToJss(MultipartFile file, String userCode);
}

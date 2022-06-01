package com.jd.bluedragon.distribution.recycle.material.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketEntity;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketPrintInfo;
import com.jd.bluedragon.distribution.recycle.material.enums.MaterialStatusEnum;
import com.jd.bluedragon.distribution.recycle.material.enums.MaterialTypeEnum;
import com.jd.bluedragon.distribution.recycle.material.enums.PrintTypeEnum;
import com.jd.bluedragon.distribution.recycle.material.enums.TransStatusEnum;
import com.jd.bluedragon.distribution.recycle.material.service.RecycleMaterialService;
import com.jd.bluedragon.sdk.common.dto.ApiResult;
import com.jd.bluedragon.sdk.modules.recyclematerial.RecycleMaterialJsfService;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.RecycleMaterial;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.distribution.recycle.material.enums.MaterialTypeEnum.BASKET;

@Service("recycleMaterialService")
public class RecycleMaterialServiceImpl implements RecycleMaterialService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    BoxService boxService;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Autowired
    RecycleMaterialJsfService recycleMaterialJsfService;


    @Override
    @JProfiler(jKey = "dms.web.RecycleMaterialServiceImpl.getPrintInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<RecycleBasketPrintInfo> getPrintInfo(RecycleBasketEntity recycleBasketEntity) {
        // 首打印
        if(PrintTypeEnum.PRINT.getCode() == recycleBasketEntity.getPrintType()){
            return generateRecycleBasketPrintInfo(recycleBasketEntity);
        }
        // 补打
        return getReprintInfo(recycleBasketEntity);
    }

    @Override
    public JdResponse<RecycleBasketPrintInfo> disableAkBox(RecycleBasketEntity recycleBasketEntity) {
        JdResponse<RecycleBasketPrintInfo> response = new JdResponse<>();

        // 校验条码
        String recycleBasketCode = recycleBasketEntity.getRecycleBasketCode();
        ApiResult<RecycleMaterial> materialApiResult = recycleMaterialJsfService.findByMaterialCode(recycleBasketCode);
        if (materialApiResult.getData() == null || Objects.equals(MaterialTypeEnum.BASKET.getCode(), materialApiResult.getData().getMaterialType())) {
            response.toFail("当前周转筐条码不存在：" + recycleBasketCode);
            return response;
        }

        // 作废
        ApiResult<RecycleMaterial> materialCode = recycleMaterialJsfService.disableMaterialByCode(
                recycleBasketCode,
                recycleBasketEntity.getUserErp(),
                recycleBasketEntity.getCreateSiteCode());

        if (!materialCode.isSucceed()) {
            response.toFail(materialCode.getMessage());
        }else {
            response.toSucceed("操作成功!");
        }

        return response;
    }

    private JdResponse<RecycleBasketPrintInfo> generateRecycleBasketPrintInfo(RecycleBasketEntity recycleBasketEntity){
        JdResponse<RecycleBasketPrintInfo> response = new JdResponse<>();
        List<String> codes = boxService.generateRecycleBasketCode(recycleBasketEntity.getQuantity());
        if(CollectionUtils.isEmpty(codes)){
            logger.error("周转筐打印生成编码失败");
            response.toError("周转筐打印生成编码失败，请稍后重试，或联系分拣小秘!");
            return response;
        }
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(recycleBasketEntity.getCreateSiteCode());
        if(baseStaffSiteOrgDto == null){
            logger.error("周转筐打印,根据站点id:{}获取站点信息为空", recycleBasketEntity.getCreateSiteCode());
            response.toError("获取站点信息失败，请稍后重试，或联系分拣小秘!");
            return response;
        }
        //保存到循环物资表
        ApiResult<Integer> addResult = addRecycleMaterials(codes, recycleBasketEntity, baseStaffSiteOrgDto);
        if(!addResult.isSucceed()){
            logger.error("周转筐打印,保存周转筐信息失败：{}", addResult.getMessage());
            response.toFail(addResult.getMessage());
            return response;
        }
        //返回打印信息
        RecycleBasketPrintInfo printInfo = new RecycleBasketPrintInfo();
        printInfo.setRecycleBasketCodes(codes);
        printInfo.setOrgName(baseStaffSiteOrgDto.getOrgName());
        printInfo.setCreateSiteName(baseStaffSiteOrgDto.getSiteName());
        printInfo.setOrgAndSiteName(baseStaffSiteOrgDto.getOrgName() + "-" + baseStaffSiteOrgDto.getSiteName());
        response.setData(printInfo);
        return response;
    }

    private ApiResult<Integer> addRecycleMaterials(List<String> codes, RecycleBasketEntity recycleBasketEntity,
                                                   BaseStaffSiteOrgDto baseStaffSiteOrgDto){
        List<RecycleMaterial> list = new ArrayList<>(codes.size());
        for(String code: codes){
            RecycleMaterial recycleMaterial = new RecycleMaterial();
            recycleMaterial.setMaterialStatus(MaterialStatusEnum.NORMAL.getCode());
            recycleMaterial.setMaterialCode(code);
            recycleMaterial.setMaterialType(BASKET.getCode());
            recycleMaterial.setImpSiteCode(baseStaffSiteOrgDto.getSiteCode());
            recycleMaterial.setImpSiteName(baseStaffSiteOrgDto.getSiteName());
            recycleMaterial.setImpOperatorErp(recycleBasketEntity.getUserErp());
            recycleMaterial.setTransStatus(TransStatusEnum.AT_THE_SITE.getCode());
            recycleMaterial.setOrgId(baseStaffSiteOrgDto.getOrgId());
            recycleMaterial.setOrgName(baseStaffSiteOrgDto.getOrgName());
            recycleMaterial.setCurrentSiteCode(baseStaffSiteOrgDto.getSiteCode());
            recycleMaterial.setCurrentSiteName(baseStaffSiteOrgDto.getSiteName());
            recycleMaterial.setOperationTime(new Date());
            recycleMaterial.setOperatorErp(recycleBasketEntity.getUserErp());
            recycleMaterial.setCreateUser(recycleBasketEntity.getUserErp());
            list.add(recycleMaterial);
        }
        return recycleMaterialJsfService.batchInsertRecycleMaterial(list);
    }

    private  JdResponse<RecycleBasketPrintInfo> getReprintInfo(RecycleBasketEntity recycleBasketEntity){
        JdResponse<RecycleBasketPrintInfo> response = new JdResponse<>();

        ApiResult<RecycleMaterial> recycleMaterialApiResult = recycleMaterialJsfService
                .findByMaterialCode(recycleBasketEntity.getRecycleBasketCode());
        if(!recycleMaterialApiResult.isSucceed()){
            logger.error("周转筐补打根据编码:{}查周转筐信息失败：{}", recycleBasketEntity.getRecycleBasketCode(),
                    recycleMaterialApiResult.getMessage());
            response.toFail("根据编码查周转筐信息失败，请稍后重试！");
            return response;
        }
        RecycleMaterial recycleMaterial = recycleMaterialApiResult.getData();
        if(recycleMaterial == null){
            logger.error("周转筐补打根据编码:{}未查到周转筐信息", recycleBasketEntity.getRecycleBasketCode());
            response.toFail("根据编码未查到周转筐信息，请检查编码是否正确！");
            return response;
        }

        if(!recycleBasketEntity.getCreateSiteCode().equals(recycleMaterial.getCurrentSiteCode())){
            logger.error("周转筐补打根据编码:{}查到周转筐信息的", recycleBasketEntity.getRecycleBasketCode());
            response.toFail("该周转筐目前所属[{}],和你绑定的分拣中心不一致，您不能操作补打！");
            return response;
        }
        RecycleBasketPrintInfo printInfo = new RecycleBasketPrintInfo();
        //查询最新的名称
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(recycleMaterial.getCurrentSiteCode());
        String siteName = recycleMaterial.getCurrentSiteName();
        if(baseStaffSiteOrgDto != null){
            siteName = baseStaffSiteOrgDto.getSiteName();
        }
        printInfo.setCreateSiteName(siteName);
        printInfo.setOrgName(recycleMaterial.getOrgName());
        printInfo.setOrgAndSiteName(recycleMaterial.getOrgName() + "-" + siteName);
        List<String> codes = new ArrayList<>();
        codes.add(recycleMaterial.getMaterialCode());
        printInfo.setRecycleBasketCodes(codes);
        response.setData(printInfo);
        return  response;
    }
}

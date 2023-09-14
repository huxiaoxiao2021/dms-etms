package com.jd.bluedragon.distribution.cycleBox.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.recyclematerial.request.BoxMaterialRelationJSFRequest;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.cycleBox.domain.BoxMaterialRelationDto;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.cyclebox.service.BoxMaterialRelationService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.wb.report.util.BusinessUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("cycleBoxJsfService")
public class CycleBoxJsfServiceImpl implements CycleBoxJsfService{
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoxMaterialRelationService boxMaterialRelationService;
    @Autowired
    private CycleBoxService cycleBoxService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.CycleBoxJsfServiceImpl.getBoxMaterialRelationByMaterialCode", mState = JProEnum.TP)
    public BoxMaterialRelationDto getBoxMaterialRelationByMaterialCode(String materialCode) {
        BoxMaterialRelation boxMaterialRelation = boxMaterialRelationService.getDataByMaterialCode(materialCode);
        if (boxMaterialRelation == null){
            return null;
        }
        BoxMaterialRelationDto result = new BoxMaterialRelationDto();
        BeanUtils.copyProperties(boxMaterialRelation,result);
        result.setOperatorERP(boxMaterialRelation.getOperatorErp());
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.CycleBoxJsfServiceImpl.unBindBoxMaterialRelation", mState = JProEnum.TP)
    public InvokeResult<Boolean> unBindBoxMaterialRelation(String boxCode, String materialCode, Integer createSiteCode) {
        logger.info("集包袋解绑箱号：{}集包袋号：{}，场地id:{}", boxCode, materialCode, createSiteCode);
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(StringUtils.isBlank(boxCode)){
            result.parameterError("箱号不能为空");
            return result;
        }
        if(StringUtils.isBlank(materialCode)){
            result.parameterError("集包袋号不能为空");
            return result;
        }
        if(!BusinessUtil.isBoxcode(boxCode)){
            result.parameterError("箱号不合法");
            return result;
        }
        if(!BusinessUtil.isCollectionBag(materialCode)){
            result.parameterError("集包袋号不合法");
            return result;
        }

        BoxMaterialRelation relation = boxMaterialRelationService.getBoxMaterialRelationByMaterialCodeAndBoxcode(boxCode, materialCode);
        if(relation == null){
            result.customMessage(400, "该箱号和集包袋号绑定关系不存在，或已经解绑");
            logger.error("该箱号和集包袋号绑定关系不存在，或已经解绑箱号：{}集包袋号：{}，场地id:{}", boxCode, materialCode, createSiteCode);
            return result;
        }
        BoxMaterialRelation bo = new BoxMaterialRelation();
        bo.setMaterialCode(materialCode);
        bo.setBoxCode(boxCode);
        int count = boxMaterialRelationService.updateUnBindByMaterialCodeAndBoxCode(bo);
        logger.info("集包袋解绑箱号：{}集包袋号：{}，结果:{}", boxCode, materialCode, count);
        return result;
    }

    /**
     * 根据箱号查询集包袋绑定信息
     * @param boxCode
     * @return
     */
    @Override
    public InvokeResult<BoxMaterialRelationDto> getBoxMaterialRelation(String boxCode) {
        logger.info("CycleBoxJsfServiceImpl->getBoxMaterialRelation查询集包袋信息,箱号：{}", boxCode);
        InvokeResult<BoxMaterialRelationDto> result = new InvokeResult<BoxMaterialRelationDto>();
        if(!BusinessUtil.isBoxcode(boxCode)){
            result.parameterError("箱号不合法");
            return result;
        }
        BoxMaterialRelation boxMaterialRelation = boxMaterialRelationService.getDataByBoxCode(boxCode);
        if (boxMaterialRelation == null){
            result.parameterError("未查询到绑定信息");
            return result;
        }
        BoxMaterialRelationDto dto = new BoxMaterialRelationDto();
        BeanUtils.copyProperties(boxMaterialRelation,dto);
        result.setData(dto);
        return result;
    }

    /**
     * 箱号绑定集包袋
     * @param request
     * @return
     */
    @Override
    public InvokeResult<String> addBoxMaterialRelation(BoxMaterialRelationRequest request) {
        logger.info("CycleBoxJsfServiceImpl->boxMaterialRelationAlter绑定集包袋,入参：{}", JsonHelper.toJson(request));
        InvokeResult<String> result = new InvokeResult<String>();
        if (null == request) {
            result.parameterError("入参不能为空");
            return result;
        }

        com.jd.bluedragon.distribution.base.domain.InvokeResult invokeResultResult = cycleBoxService.boxMaterialRelationAlter(request);
        if (invokeResultResult == null){
            result.parameterError("绑定失败，请重试");
        }
        if(!invokeResultResult.codeSuccess()){
            result.parameterError(invokeResultResult.getMessage());
        }
        return result;
    }
}

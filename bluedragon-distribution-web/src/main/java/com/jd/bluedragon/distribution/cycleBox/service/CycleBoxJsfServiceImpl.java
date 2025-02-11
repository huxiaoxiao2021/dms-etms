package com.jd.bluedragon.distribution.cycleBox.service;

import com.jd.bluedragon.Constants;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.CycleBoxJsfServiceImpl.getBoxMaterialRelation", mState = JProEnum.TP)
    public InvokeResult<BoxMaterialRelationDto> getBoxMaterialRelation(String boxCode) {
        logger.info("查询集包袋信息,箱号：{}", boxCode);
        InvokeResult<BoxMaterialRelationDto> result = new InvokeResult<BoxMaterialRelationDto>();
        if(!BusinessUtil.isBoxcode(boxCode)){
            result.parameterError("箱号不合法");
            return result;
        }
        BoxMaterialRelation boxMaterialRelation = boxMaterialRelationService.getDataByBoxCode(boxCode);
        if (boxMaterialRelation == null){
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.CycleBoxJsfServiceImpl.addBoxMaterialRelation", mState = JProEnum.TP)
    public InvokeResult<Boolean> addBoxMaterialRelation(BoxMaterialRelationRequest request) {
        logger.info("CycleBoxJsfServiceImpl->addBoxMaterialRelation绑定集包袋,入参：{}", JsonHelper.toJson(request));
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if (null == request) {
            result.parameterError("入参不能为空");
            return result;
        }
        if(!BusinessUtil.isBoxcode(request.getBoxCode())){
            result.parameterError("箱号不合法");
            return result;
        }
        if(!BusinessUtil.isCollectionBag(request.getMaterialCode())){
            result.parameterError("集包袋号不合法");
            return result;
        }
        com.jd.bluedragon.distribution.base.domain.InvokeResult invokeResultResult = cycleBoxService.boxMaterialRelationAlter(request);
        if (invokeResultResult == null){
            result.parameterError("绑定失败，请重试");
        }
        if(!invokeResultResult.codeSuccess()){
            result.parameterError(invokeResultResult.getMessage());
        }
        result.setData(Boolean.TRUE);
        return result;
    }

    @Override
    public InvokeResult<List<BoxMaterialRelationDto>> findByMaterialCodeAndBoxCode(BoxMaterialRelationRequest request) {
        InvokeResult<List<BoxMaterialRelationDto>> result = new InvokeResult<>();
        List<BoxMaterialRelation> boxes = boxMaterialRelationService.findByMaterialCodeAndBoxCode(makeRequestMap(request));
        List<BoxMaterialRelationDto> dtoList = new ArrayList<>();
        makeBoxMaterialDto(boxes, dtoList);
        result.setData(dtoList);
        return result;
    }

    @Override
    public InvokeResult<Integer> countByMaterialCodeAndBoxCode(BoxMaterialRelationRequest request) {
        InvokeResult<Integer> result = new InvokeResult<>();
        int count = boxMaterialRelationService.countByMaterialCodeAndBoxCode(makeRequestMap(request));
        result.setData(count);
        return result;
    }

    private void makeBoxMaterialDto(List<BoxMaterialRelation> boxes, List<BoxMaterialRelationDto> dtoList) {

        boxes.forEach(box -> {
            BoxMaterialRelationDto dto = new BoxMaterialRelationDto();
            BeanUtils.copyProperties(box, dto);
            dto.setOperatorERP(box.getOperatorErp());
            dtoList.add(dto);
        });
    }

    private Map<String, Object> makeRequestMap(BoxMaterialRelationRequest request) {
        Map<String, Object> map = JsonHelper.fromJson(JsonHelper.toJson(request), Map.class);
        map.put("offset", (request.getPageNumber()-1)*request.getPageSize());
        return map;
    }
}

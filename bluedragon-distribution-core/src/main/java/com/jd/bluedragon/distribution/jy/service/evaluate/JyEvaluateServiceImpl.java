package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateDimensionDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateTargetInfoDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("jyEvaluateService")
public class JyEvaluateServiceImpl implements JyEvaluateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JyEvaluateServiceImpl.class);

    @Autowired
    private JyEvaluateDimensionDao jyEvaluateDimensionDao;
    @Autowired
    private JyEvaluateTargetInfoDao jyEvaluateTargetInfoDao;
    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.dimensionOptions", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> dimensionOptions() {
        JdCResponse<List<SelectOption>> result = new JdCResponse<>();
        result.toSucceed();
        try {
            List<JyEvaluateDimensionEntity> list = jyEvaluateDimensionDao.findAllDimension();
            if (CollectionUtils.isEmpty(list)) {
                result.setData(new ArrayList<>());
                return result;
            }
            List<SelectOption> options = new ArrayList<>();
            for (JyEvaluateDimensionEntity dimension : list) {
                options.add(new SelectOption(dimension.getCode(), dimension.getName(), dimension.getStatus()));
            }
            result.setData(options);
        } catch (Exception e) {
            LOGGER.error("dimensionOptions|获取评价维度枚举列表接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.checkIsEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> checkIsEvaluate(EvaluateTargetReq request) {
        JdCResponse<Boolean> result = new JdCResponse<>();
        result.toSucceed();
        try {
            JyEvaluateTargetInfoEntity evaluateTargetInfo = jyEvaluateTargetInfoDao.findBySourceBizId(request.getSourceBizId());
            if (evaluateTargetInfo == null) {
                result.setData(Boolean.FALSE);
                return result;
            }
            result.setData(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.error("checkIsEvaluate|查询目标评价与否接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.findTargetEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<EvaluateDimensionDto>> findTargetEvaluateInfo(EvaluateTargetReq request) {
        JdCResponse<List<EvaluateDimensionDto>> result = new JdCResponse<>();
        result.toSucceed();
        try {
            List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(request.getSourceBizId());
            if (CollectionUtils.isEmpty(recordList)) {
                result.setData(new ArrayList<>());
                return result;
            }
            Map<Integer, JyEvaluateDimensionEntity> dimensionEnumMap = jyEvaluateDimensionDao.findAllDimensionMap();
            if (dimensionEnumMap.isEmpty()) {
                result.toError("查询评价维度枚举列表为空");
                return result;
            }
            Map<Integer, EvaluateDimensionDto> map = new HashMap<>();
            for (JyEvaluateRecordEntity record : recordList) {
                transformDataToMap(map, dimensionEnumMap, record);
            }
            result.setData(new ArrayList<>(map.values()));
        } catch (Exception e) {
            LOGGER.error("findTargetEvaluateInfo|查询目标评价详情接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.saveTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> saveTargetEvaluate(EvaluateTargetReq request) {
        JdCResponse<Void> result = new JdCResponse<>();
        result.toSucceed();
        try {
            List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
            List<EvaluateDimensionReq> dimensionList = request.getDimensionList();
            for (EvaluateDimensionReq dimensionReq : dimensionList) {
                JyEvaluateRecordEntity record = new JyEvaluateRecordEntity();
                record.setEvaluateType(1);
                record.setTargetBizId();
                record.setSourceBizId();
                record.setStatus(request.getStatus());
                record.setDimensionCode(dimensionReq.getDimensionCode());
                if (CollectionUtils.isNotEmpty(dimensionReq.getImgUrlList())) {
                    record.setImgCount();
                }
            }

            result.setData(new ArrayList<>(map.values()));
        } catch (Exception e) {
            LOGGER.error("findTargetEvaluateInfo|查询目标评价详情接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.updateTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> updateTargetEvaluate(EvaluateTargetReq request) {
        return null;
    }

    private void transformDataToMap(Map<Integer, EvaluateDimensionDto> map, Map<Integer,
            JyEvaluateDimensionEntity> dimensionEnumMap, JyEvaluateRecordEntity record) {
        EvaluateDimensionDto evaluateDimensionDto = map.get(record.getDimensionCode());
        if (evaluateDimensionDto == null) {
            evaluateDimensionDto = new EvaluateDimensionDto();
            evaluateDimensionDto.setDimensionCode(record.getDimensionCode());
            JyEvaluateDimensionEntity dimensionEnum = dimensionEnumMap.get(record.getDimensionCode());
            evaluateDimensionDto.setDimensionName(dimensionEnum.getName());
            if (StringUtils.isNotBlank(record.getImgUrl())) {
                evaluateDimensionDto.setImgUrlList(Arrays.asList(record.getImgUrl().split(Constants.SEPARATOR_COMMA)));
            }
            if (Constants.NUMBER_ONE.equals(dimensionEnum.getStatus())) {
                evaluateDimensionDto.setStatus(dimensionEnum.getStatus());
            }
            evaluateDimensionDto.setRemark(record.getRemark());
        } else {
            if (StringUtils.isNotBlank(record.getImgUrl())) {
                List<String> currentImgUrlList = Arrays.asList(record.getImgUrl().split(Constants.SEPARATOR_COMMA));
                if (CollectionUtils.isEmpty(evaluateDimensionDto.getImgUrlList())) {
                    evaluateDimensionDto.setImgUrlList(currentImgUrlList);
                } else {
                    evaluateDimensionDto.getImgUrlList().addAll(currentImgUrlList);
                }
            }
            if (StringUtils.isNotBlank(record.getRemark())) {
                if (StringUtils.isNotBlank(evaluateDimensionDto.getRemark())) {
                    evaluateDimensionDto.setRemark(evaluateDimensionDto.getRemark() + "\n" + record.getRemark());
                } else {
                    evaluateDimensionDto.setRemark(record.getRemark());
                }
            }
        }
    }

}

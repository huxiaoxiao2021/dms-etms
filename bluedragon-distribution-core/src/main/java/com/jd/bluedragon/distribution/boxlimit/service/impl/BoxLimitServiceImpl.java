package com.jd.bluedragon.distribution.boxlimit.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.boxlimit.dao.BoxLimitConfigDao;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BoxLimitServiceImpl implements BoxLimitService {
    private static final Logger log = LoggerFactory.getLogger(BoxLimitServiceImpl.class);
    private final int maxImportSize = 5000;
    private static final Integer SITE_BOX_TYPE = 2;
    private static List<String> boxNumberTypes;
    static{
        boxNumberTypes = Arrays.asList("BC","TC","GC","FC","BS","TS","FS","ZC","BX","TW","WJ");
        log.info("初始化箱号类型-{}",JSON.toJSONString(boxNumberTypes));
    }

    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private BoxLimitConfigDao boxLimitConfigDao;

    @Override
    public PagerResult<BoxLimitVO> listData(BoxLimitQueryDTO dto) {
        PagerResult<BoxLimitVO> result = new PagerResult<>();
        dto.setSiteName(dto.getSiteName().trim());
        Integer count = boxLimitConfigDao.countByCondition(dto);
        result.setTotal(count);
        if (count == 0) {
            result.setRows(new ArrayList<BoxLimitVO>());
            return result;
        }
        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryByCondition(dto);
        List<BoxLimitVO> list = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (BoxLimitConfig b : boxLimitConfigs) {
            BoxLimitVO vo = new BoxLimitVO();
            vo.setId(b.getId());
            vo.setSiteName(b.getSiteName());
            vo.setSiteId(b.getSiteId());
            vo.setLimitNum(b.getLimitNum());
            vo.setOperatorErp(b.getOperatorErp());
            vo.setOperatorSiteName(b.getOperatorSiteName());
            vo.setOperatingTime(format.format(b.getOperatingTime()));
            vo.setConfigType(b.getConfigType());
            vo.setBoxNumberType(b.getBoxNumberType());
            list.add(vo);
        }
        result.setRows(list);
        return result;
    }

    @Override
    public JdResponse importData(List<BoxLimitTemplateVO> data, LoginUser operator) {
        log.info("导入数据- {}",JSON.toJSONString(data));
        JdResponse response = new JdResponse();
        checkTemplateData(data, response);
        log.info("建箱包裹数限制：导入数据校验结果为:{}",new Gson().toJson(response));
        if (!response.isSucceed()) {
            return response;
        }
        Date now = new Date();
        String operatorErp = operator.getUserErp();
        Integer operatorSiteId = operator.getSiteCode();
        String operatorName = operator.getSiteName();
        List<BoxLimitConfig> addList = new ArrayList<>();
        List<BoxLimitConfig> updateList = new ArrayList<>();
        //对重复的配置进行去重
        TreeSet<BoxLimitTemplateVO> setVos = new TreeSet<>(new Comparator<BoxLimitTemplateVO>() {
            @Override
            public int compare(BoxLimitTemplateVO o1, BoxLimitTemplateVO o2) {
                int result1 = o1.getSiteName().compareTo(o2.getSiteName());
                if(result1 == 0){
                    int result2 = o1.getBoxNumberType().compareTo(o2.getBoxNumberType());
                    if(result2 == 0){
                        return o1.getSiteId().compareTo(o2.getSiteId());
                    }
                    return result2;
                }
                return result1;
            }
        });
        setVos.addAll(data);
        Iterator<BoxLimitTemplateVO> iterator = setVos.iterator();
        int failCount =0;
        while (iterator.hasNext()){
            BoxLimitTemplateVO vo =iterator.next();
            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(vo.getSiteId());
            log.info("siteOrgDto -{}",JSON.toJSONString(siteOrgDto));
            if (siteOrgDto == null) {
                failCount++;
                continue;
            }
            if (!vo.getSiteName().equals(siteOrgDto.getSiteName())) {
                failCount++;
                continue;
            }
            BoxLimitQueryDTO queryDTO = new BoxLimitQueryDTO();
            queryDTO.setSiteId(vo.getSiteId());
            queryDTO.setSiteName(vo.getSiteName());
            queryDTO.setConfigType(SITE_BOX_TYPE);
            queryDTO.setBoxNumberType(vo.getBoxNumberType());
            //根据条件查询库中数据有无该记录 有则更新 无则新增
            List<BoxLimitConfig> result = boxLimitConfigDao.queryByCondition(queryDTO);
            if(CollectionUtils.isEmpty(result)){
                BoxLimitConfig b = new BoxLimitConfig();
                b.setSiteName(vo.getSiteName());
                b.setSiteId(vo.getSiteId());
                b.setLimitNum(vo.getLimitNum());
                b.setOperatorErp(operatorErp);
                b.setOperatorSiteId(operatorSiteId);
                b.setOperatorSiteName(operatorName);
                b.setOperatingTime(now);
                b.setCreateTime(now);
                b.setUpdateTime(now);
                b.setYn(true);
                b.setBoxNumberType(vo.getBoxNumberType());
                //场地建箱配置类型
                b.setConfigType(SITE_BOX_TYPE);
                addList.add(b);
            }else{
                BoxLimitConfig updateConfig = result.get(0);
                updateConfig.setLimitNum(vo.getLimitNum());
                updateConfig.setOperatorErp(operatorErp);
                updateConfig.setOperatingTime(now);
                updateConfig.setUpdateTime(now);
                updateList.add(updateConfig);
            }
        }
        if(!CollectionUtils.isEmpty(updateList)){
            log.info("修改配置数据-{}",JSON.toJSONString(updateList));
            boxLimitConfigDao.batchUpdate(updateList);
        }
        if(!CollectionUtils.isEmpty(addList)){
            log.info("新增配置数据-{}",JSON.toJSONString(addList));
            boxLimitConfigDao.batchInsert(addList);
        }
        response.setData(failCount);
        return response;
    }

    /**
     * 校验导入数据是否规范
     * @param data
     * @param response
     */
    private void checkTemplateData(List<BoxLimitTemplateVO> data, JdResponse response) {
        if (CollectionUtils.isEmpty(data)) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("上传数据为空!");
            return;
        }
        if (data.size() > maxImportSize) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("最大导入数量不允许超过" + maxImportSize);
            return;
        }
        int row = 1;
        StringBuilder duplicateIds = new StringBuilder();
        Set<BoxLimitTemplateVO> siteIdSet = new HashSet<>();
        for (BoxLimitTemplateVO vo : data) {
            if (vo.getSiteId() == null) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("excel中第%s行 机构ID为空!", row));
                return;
            }
            if (StringUtils.isEmpty(vo.getSiteName())) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("excel中第%s行 机构名称为空!", row));
                return;
            }
            if (vo.getLimitNum() == null || vo.getLimitNum() <= 0) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("excel中第%s行 建箱包裹上限不正确!", row));
                return;
            }
            if(StringUtils.isEmpty(vo.getBoxNumberType()) || !boxNumberTypes.contains(vo.getBoxNumberType())){
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("excel中第%s行 建箱箱号类型为空或箱号类型不在处理范围内!", row));
                return;
            }
        }
    }
    /**
     * 校验输入数据是否规范
     * @param dto
     * @param response
     */
    private void checkDtoData(BoxLimitDTO dto, JdResponse response) {
        //场地建箱配置需要判断机构id
        if (dto.getConfigType().equals(SITE_BOX_TYPE) && dto.getSiteId() == null) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("机构ID为空!");
            return;
        }
        if (dto.getConfigType().equals(SITE_BOX_TYPE) && StringUtils.isEmpty(dto.getSiteName())) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("机构名称为空!");
            return;
        }
        if (dto.getLimitNum() == null || dto.getLimitNum() <= 0) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("建箱包裹上限不正确!");
            return;
        }
        if(StringUtils.isEmpty(dto.getBoxNumberType())){
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("箱号类型为空!");
            return;
        }
        BoxLimitQueryDTO queryDTO = new BoxLimitQueryDTO();
        queryDTO.setSiteId(dto.getSiteId());
        queryDTO.setSiteName(dto.getSiteName());
        queryDTO.setConfigType(dto.getConfigType());
        queryDTO.setBoxNumberType(dto.getBoxNumberType());
        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryByCondition(queryDTO);
        if (!CollectionUtils.isEmpty(boxLimitConfigs)) {
            if (dto.getId() == null || !dto.getId().equals(boxLimitConfigs.get(0).getId())) {
                if(dto.getConfigType().equals(SITE_BOX_TYPE)){
                    response.setCode(JdResponse.CODE_FAIL);
                    response.setMessage(String.format("ID为:%s 的机构配置箱号类型:%s 已经存在,不允许重复配置", dto.getSiteId(),dto.getBoxNumberType()));
                    return;
                }
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("配置箱号类型:%s 已经存在,不允许重复配置", dto.getBoxNumberType()));
                return;
            }
        }
        if(dto.getConfigType().equals(SITE_BOX_TYPE)){
            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(dto.getSiteId());
            if (siteOrgDto == null) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("ID为%s的机构不存在!", dto.getSiteId()));
                return;
            }
            if (!dto.getSiteName().equals(siteOrgDto.getSiteName())) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("ID为%s的机构ID与机构名称不匹配,实际机构名称应为:%s", dto.getSiteId(), siteOrgDto.getSiteName()));
                return;
            }
        }
    }

    @Override
    public JdResponse create(BoxLimitDTO dto, LoginUser operator) {
        JdResponse response = new JdResponse();
        checkDtoData(dto, response);
        log.info("建箱包裹数限制：create数据校验结果为:{}",new Gson().toJson(response));
        if (!response.isSucceed()) {
            return response;
        }

        Date now = new Date();
        BoxLimitConfig boxLimitConfig = new BoxLimitConfig();
        boxLimitConfig.setSiteName(dto.getSiteName());
        boxLimitConfig.setSiteId(dto.getSiteId());
        boxLimitConfig.setLimitNum(dto.getLimitNum());
        boxLimitConfig.setOperatorErp(operator.getUserErp());
        boxLimitConfig.setOperatorSiteId(operator.getSiteCode());
        boxLimitConfig.setOperatorSiteName(operator.getSiteName());
        boxLimitConfig.setOperatingTime(now);

        boxLimitConfig.setCreateTime(now);
        boxLimitConfig.setUpdateTime(now);
        boxLimitConfig.setYn(true);
        boxLimitConfig.setConfigType(dto.getConfigType());
        boxLimitConfig.setBoxNumberType(dto.getBoxNumberType());

        boxLimitConfigDao.insert(boxLimitConfig);

        return response;
    }

    @Override
    public JdResponse update(BoxLimitDTO dto, LoginUser operator) {
        JdResponse response = new JdResponse();
        log.info("建箱包裹数限制：update数据校验结果为:{}",new Gson().toJson(response));
        if (dto.getId() == null) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("ID不能为空!");
            return response;
        }
        BoxLimitConfig boxLimitConfig = boxLimitConfigDao.selectByPrimaryKey(dto.getId());
        if (boxLimitConfig == null) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("数据不存在!");
            return response;
        }
        if (dto.getLimitNum() == null || dto.getLimitNum() <= 0) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("建箱包裹上限不正确!");
            return response;
        }
        if (!response.isSucceed()) {
            return response;
        }

        Date now = new Date();
        boxLimitConfig.setUpdateTime(now);
        boxLimitConfig.setOperatingTime(now);
        boxLimitConfig.setOperatorErp(operator.getUserErp());
        boxLimitConfig.setOperatorSiteId(operator.getSiteCode());
        boxLimitConfig.setOperatorSiteName(operator.getSiteName());
        boxLimitConfig.setSiteId(dto.getSiteId());
        boxLimitConfig.setSiteName(dto.getSiteName());
        boxLimitConfig.setLimitNum(dto.getLimitNum());
        boxLimitConfig.setConfigType(dto.getConfigType());
        boxLimitConfig.setBoxNumberType(dto.getBoxNumberType());

        boxLimitConfigDao.updateByPrimaryKeySelective(boxLimitConfig);
        return response;
    }

    @Override
    public JdResponse delete(List<Long> ids,String operatorErp) {
        log.info("建箱包裹数限制 delete操作, 参数 ids={},操作人:{}", ids, operatorErp);
        boxLimitConfigDao.batchDelete(ids);
        return new JdResponse();
    }

    @Override
    public JdResponse querySiteNameById(Integer siteId) {
        JdResponse response = new JdResponse();
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(siteId);
        if (siteOrgDto == null || siteOrgDto.getSiteName() == null) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("站点不存在!");
        } else {
           response.setData(siteOrgDto.getSiteName());
        }
        return response;
    }

    @Override
    @Cache(key = "BoxLimitServiceImpl.queryLimitNumBySiteId@args0@args1", memoryEnable = true, memoryExpiredTime = 2 * 60 * 1000
            ,redisEnable = true, redisExpiredTime = 2 * 60 * 1000)
    public Integer queryLimitNumBySiteIdAndBoxNumberType(Integer siteId,String boxNumberType) {
        BoxLimitQueryDTO dto = new BoxLimitQueryDTO();
        dto.setSiteId(siteId);
        dto.setBoxNumberType(boxNumberType);
        return boxLimitConfigDao.queryLimitNumBySiteId(dto);
    }

    @Override
    public Integer queryCommonLimitNum(String boxNumberType) {
        return boxLimitConfigDao.queryCommonLimitNum(boxNumberType);
    }

    @Override
    public JdResponse<Integer> countByCondition(BoxLimitQueryDTO dto) {
        dto.setSiteName(dto.getSiteName().trim());
        JdResponse<Integer> response = new JdResponse<>();
        response.toSucceed();
        response.setData(boxLimitConfigDao.countByCondition(dto));
        return response;
    }

}

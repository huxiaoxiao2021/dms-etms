package com.jd.bluedragon.distribution.boxlimit.service.impl;

import com.google.gson.Gson;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.boxlimit.dao.BoxLimitConfigDao;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BoxLimitServiceImpl implements BoxLimitService {
    private static final Logger log = LoggerFactory.getLogger(BoxLimitServiceImpl.class);
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private BoxLimitConfigDao boxLimitConfigDao;

    @Override
    public PagerResult<BoxLimitVO> listData(BoxLimitQueryDTO dto) {
        if (dto.getSiteName() != null) {
            dto.setSiteName(dto.getSiteName().replaceAll("_","\\_").replaceAll("\\?","\\?"));
        }
        PagerResult<BoxLimitVO> result = new PagerResult<>();
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
            list.add(vo);
        }
        result.setRows(list);
        return result;
    }

    @Override
    public JdResponse importData(List<BoxLimitTemplateVO> data, LoginUser operator) {
        JdResponse response = new JdResponse();
        checkTemplateData(data, response);
        log.info("建箱包裹数限制：导入数据校验结果为:{}",new Gson().toJson(response));
        if (!response.isSucceed()) {
            return response;
        }

        List<BoxLimitConfig> list = new ArrayList<>();
        Date now = new Date();
        String operatorErp = operator.getUserErp();
        Integer operatorSiteId = operator.getSiteCode();
        String operatorName = operator.getSiteName();

        for (BoxLimitTemplateVO vo : data) {
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
            list.add(b);
        }
        boxLimitConfigDao.batchInsert(list);
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
        int row = 1;
        Set<Integer> siteIdSet = new HashSet<>();
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
            siteIdSet.add(vo.getSiteId());
            row++;
        }
        if (siteIdSet.size() != data.size()) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("导入的excel表格中存在相同的机构编码!");
            return;
        }
        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryBySiteIds(new ArrayList<>(siteIdSet));
        if (!CollectionUtils.isEmpty(boxLimitConfigs)) {
            StringBuilder names = new StringBuilder();
            for (BoxLimitConfig b : boxLimitConfigs) {
                if (names.length() > 0) {
                    names.append(",");
                }
                names.append(b.getSiteId());
            }
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage(String.format("ID为:%s 的机构配置已经存在,不允许重复配置", names));
            return;
        }
        row = 1;
        for (BoxLimitTemplateVO vo : data) {
            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(vo.getSiteId());
            if (siteOrgDto == null) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("excel中第%s行 ID为%s的机构不存在!", row, vo.getSiteId()));
                return;
            }
            if (!vo.getSiteName().equals(siteOrgDto.getSiteName())) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("excel中第%s行 ID为%s的机构名称不匹配,实际机构名称应为:%s", row, vo.getSiteId(), siteOrgDto.getSiteName()));
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

        if (dto.getSiteId() == null) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("机构ID为空!");
            return;
        }
        if (StringUtils.isEmpty(dto.getSiteName())) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("机构名称为空!");
            return;
        }
        if (dto.getLimitNum() == null || dto.getLimitNum() <= 0) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("建箱包裹上限不正确!");
            return;
        }

        List<BoxLimitConfig> boxLimitConfigs = boxLimitConfigDao.queryBySiteIds(Collections.singletonList(dto.getSiteId()));
        if (!CollectionUtils.isEmpty(boxLimitConfigs)) {
            if (dto.getId() == null || !dto.getId().equals(boxLimitConfigs.get(0).getId())) {
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(String.format("ID为:%s 的机构配置已经存在,不允许重复配置", dto.getSiteId()));
                return;
            }
        }
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
        checkDtoData(dto, response);
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

        boxLimitConfigDao.updateByPrimaryKeySelective(boxLimitConfig);
        return response;
    }

    @Override
    public JdResponse delete(List<Integer> ids,String operatorErp) {
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
    public Integer queryLimitNumBySiteId(Integer siteId) {
        return boxLimitConfigDao.queryLimitNumBySiteId(siteId);
    }

}

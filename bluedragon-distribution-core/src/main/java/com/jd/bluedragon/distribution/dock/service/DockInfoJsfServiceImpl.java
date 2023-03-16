package com.jd.bluedragon.distribution.dock.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.dock.convert.DockInfoConverter;
import com.jd.bluedragon.distribution.dock.dao.DockBaseInfoDao;
import com.jd.bluedragon.distribution.dock.domain.DockBaseInfoPo;
import com.jd.bluedragon.distribution.dock.entity.AllowedVehicleEntity;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.dock.entity.DockPageQueryCondition;
import com.jd.bluedragon.distribution.failqueue.service.IFailQueueService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.service
 * @ClassName: DockInfoJsfServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/12/1 17:17
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service("dockInfoJsfService")
@Slf4j
public class DockInfoJsfServiceImpl implements DockService{

    @Autowired
    private DockBaseInfoDao dockBaseInfoDao;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

    private static final Pattern dockCodePattern = Pattern.compile("^[0-9][0-9][0-9]$");


    @Override
    @JProfiler(jKey = "DMS.BASE.DockInfoJsfServiceImpl.getAllowedVehicleTypeEnums", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<AllowedVehicleEntity>> getAllowedVehicleTypeEnums() {
        List<BasicDictDto> basicDictDtoList = basicQueryWSManager.getVehicleTypeByType(null, 0);
        Response<List<AllowedVehicleEntity>> allowedVehicleEntityResponse = new Response<>();
        allowedVehicleEntityResponse.toSucceed();
        allowedVehicleEntityResponse.setData(new ArrayList<AllowedVehicleEntity>());
        if (!CollectionUtils.isEmpty(basicDictDtoList)) {
            for (BasicDictDto basicDictDto : basicDictDtoList) {
                if (basicDictDto.getYn() == 0) {
                    continue;
                }
                AllowedVehicleEntity entity = new AllowedVehicleEntity();
                entity.setCode(basicDictDto.getDictCode());
                entity.setName(basicDictDto.getDictName());
                allowedVehicleEntityResponse.getData().add(entity);
            }
        }

        return allowedVehicleEntityResponse;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.DockInfoJsfServiceImpl.saveDockInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> saveDockInfo(DockInfoEntity dockInfoEntity) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();

        /* 基础信息校验 */
        if (Objects.isNull(dockInfoEntity)) {
            response.toError("无有效参数");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getSiteCode())) {
            response.toError("未输入有效的站点ID");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getOrgId())) {
            response.toError("未输入有效的站点ID");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getDockCode()) || !checkDockCode(dockInfoEntity.getDockCode())) {
            response.toError("未输入有效的月台编号");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getDockType())) {
            response.toError("未选择有效的月台类型");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getDockAttribute())) {
            response.toError("未选择有效的月台属性");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getAllowedVehicleTypes())) {
            response.toError("未选择有效的可操作车型");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getIsHasDockLeveller())) {
            response.toError("未选择是否包含升降平台");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getIsImmediately())) {
            response.toError("未选择是否即装即卸");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getHeight())) {
            response.toError("未输入有效的月台高度");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getIsHasScales())) {
            response.toError("未选择是否带秤");
            return response;
        }

        /* 对月台高度进行格式化：保留两位小数 */
        dockInfoEntity.setHeight(NumberHelper.doubleFormat(dockInfoEntity.getHeight()));

        /* 相同场地内不允许出现多个dockCode同时生效 */
        DockBaseInfoPo dockBaseInfoPo = dockBaseInfoDao.findByDockCode(DockInfoConverter.convertToPo(dockInfoEntity));
        if (!Objects.isNull(dockBaseInfoPo)
                && Objects.equals(dockBaseInfoPo.getSiteCode(), dockInfoEntity.getSiteCode())
                && Objects.equals(dockBaseInfoPo.getOrgId(), dockInfoEntity.getOrgId())
                && Objects.equals(dockBaseInfoPo.getDockCode(), dockInfoEntity.getDockCode())
        ) {
            response.toError("已经存在相同的月台编号，请重新维护");
            return response;
        }

        response.setData(dockBaseInfoDao.insert(DockInfoConverter.convertToPo(dockInfoEntity)));

        return response;
    }

    private static boolean checkDockCode(String dockCode) {
        return dockCodePattern.matcher(dockCode).matches();
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.DockInfoJsfServiceImpl.queryDockInfoByPage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public PageDto<DockInfoEntity> queryDockInfoByPage(DockPageQueryCondition condition) {
        if (!Objects.isNull(condition) && !Objects.isNull(condition.getSiteCode())) {
            //如果siteCode存在的话，则将值复制给 siteCodeList
            condition.getSiteCodeList().add(condition.getSiteCode());
        }
        PagerResult<DockBaseInfoPo> baseInfoPoPagerResult = dockBaseInfoDao.queryByPagerCondition(condition);
        PageDto<DockInfoEntity> result = new PageDto<>();
        result.setCurrentPage(condition.getPageNumber());
        result.setPageSize(condition.getLimit());
        result.setTotalRow(baseInfoPoPagerResult.getTotal());
        result.setResult(new ArrayList<DockInfoEntity>());
        if (!CollectionUtils.isEmpty(baseInfoPoPagerResult.getRows())) {
            for (DockBaseInfoPo po : baseInfoPoPagerResult.getRows()) {
                result.getResult().add(DockInfoConverter.convertToEntity(po));
            }
         }

        return result;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.DockInfoJsfServiceImpl.deleteDockInfoById", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> deleteDockInfoById(DockInfoEntity dockInfoEntity) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if (Objects.isNull(dockInfoEntity) || Objects.isNull(dockInfoEntity.getId())) {
            response.toError("缺少需要删除的月台信息主键");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getUpdateUserName())) {
            response.toError("缺少操作人信息");
            return response;
        }

        response.setData(dockBaseInfoDao.LogitechDeleteById(DockInfoConverter.convertToPo(dockInfoEntity)));

        return response;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.DockInfoJsfServiceImpl.updateDockInfoById", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> updateDockInfoById(DockInfoEntity dockInfoEntity) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if (Objects.isNull(dockInfoEntity) || Objects.isNull(dockInfoEntity.getId())) {
            response.toError("缺少需要删除的月台信息主键");
            return response;
        }
        if (Objects.isNull(dockInfoEntity.getUpdateUserName())) {
            response.toError("缺少操作人信息");
            return response;
        }
        response.setData(dockBaseInfoDao.update(DockInfoConverter.convertToPo(dockInfoEntity)));

        return response;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.DockInfoJsfServiceImpl.queryDockInfoByDockCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<DockInfoEntity> queryDockInfoByDockCode(DockInfoEntity dockInfoEntity) {
        Response<DockInfoEntity> response = new Response<>();
        response.toSucceed();

        if (Objects.isNull(dockInfoEntity) || Objects.isNull(dockInfoEntity.getSiteCode()) || Objects.isNull(dockInfoEntity.getDockCode())) {
            response.toError("缺少必要查询参数");
            return response;
        }

        DockBaseInfoPo dockBaseInfoPo = dockBaseInfoDao.findByDockCode(DockInfoConverter.convertToPo(dockInfoEntity));
        response.setData(DockInfoConverter.convertToEntity(dockBaseInfoPo));

        return response;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.DockInfoJsfServiceImpl.queryDockListBySiteId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<String>> queryDockListBySiteId(DockInfoEntity dockInfoEntity) {
        Response<List<String>> response = new Response<>();
        response.toSucceed();

        if (Objects.isNull(dockInfoEntity) || Objects.isNull(dockInfoEntity.getSiteCode())) {
            response.toError("缺少必要查询参数");
            return response;
        }

        List<String> dockCodeList = dockBaseInfoDao.findAllDockCodeBySiteCode(dockInfoEntity.getSiteCode());
        response.setData(dockCodeList);

        return response;
    }
}

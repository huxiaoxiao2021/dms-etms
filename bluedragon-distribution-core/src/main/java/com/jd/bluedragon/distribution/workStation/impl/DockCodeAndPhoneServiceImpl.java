package com.jd.bluedragon.distribution.workStation.impl;

import com.jd.bluedragon.core.jsf.workStation.DockCodeAndPhoneMapper;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhone;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.workStation.DockCodeAndPhoneService;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQueryDTO;
import com.jd.bluedragon.distribution.workStation.domain.UserNameAndPhone;

import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.common.utils.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取运输月台号和联系人接口实现
 *
 * @author hujiping
 * @date 2022/4/6 6:05 PM
 */
@Slf4j
@Service("dockCodeAndPhoneService")
public class DockCodeAndPhoneServiceImpl implements DockCodeAndPhoneService {
    @Autowired
    private UserSignRecordService userSignRecordService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private DockCodeAndPhoneMapper dockCodeAndPhoneQuery;

    @Value("${jobCodeList}")
    private List<Integer> jobCodeList;

    @Value("${pageSize}")
    private Integer pageSize;

    /**
     * 获取运输月台号
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return
     */
    @Override
    public Result<List<String>> queryDockCodeByFlowDirection(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO) {
        return dockCodeAndPhoneQuery.queryDockCodeByFlowDirection(dockCodeAndPhoneQueryDTO);
    }

    /**
     * 获取联系人
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<DockCodeAndPhone>
     */
    @Override
    public Result<DockCodeAndPhone> queryPhoneByDockCodeForTms(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO) {
        Result<DockCodeAndPhone> result = new Result<>();
        Result<List<WorkStationGrid>> listResult = dockCodeAndPhoneQuery.queryPhoneByDockCodeForTms(dockCodeAndPhoneQueryDTO);
        if (!listResult.isSuccess()) {
            result.setCode(listResult.getCode());
            result.setMessage(listResult.getMessage());
            return result;
        }
        try {
            if (CollectionUtils.isNotEmpty(listResult.getData())) {
                List<String> erps = new ArrayList<>();
                List<String> workStationGridList = listResult.getData().stream().map(WorkStationGrid::getBusinessKey).collect(Collectors.toList());
                //5、根据business_key去user_sign_record 查询 erp
                if (CollectionUtils.isNotEmpty(workStationGridList)) {
                    UserSignRecordQuery userSignRecordQuery = new UserSignRecordQuery();
                    userSignRecordQuery.setBusinessKeyList(workStationGridList);
                    userSignRecordQuery.setJobCodeList(jobCodeList);
                    userSignRecordQuery.setPageSize(pageSize);
                    erps = userSignRecordService.queryByBusinessKeyForTms(userSignRecordQuery);
                }
                //6、根据erp查询电话号码
                DockCodeAndPhone phone = getPhone(listResult.getData(), erps);
                result.setData(phone);
                result.toSuccess();

            }
            return result;
        } catch (Exception e) {
            log.error("获取联系人号数据异常 {}", e.getMessage(), e);
            result.toFail("获取联系人数据异常!");
        }
        return result;
    }

    /**
     * 根据erp查询电话号码
     *
     * @param erps
     */
    private DockCodeAndPhone getPhone(List<WorkStationGrid> listResult, List<String> erps) {
        DockCodeAndPhone dockCodeAndPhone = new DockCodeAndPhone();
        List<UserNameAndPhone> phoneList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(erps)) {
            for (String erp : erps) {
                if (pageSize.equals(phoneList.size())) {
                    break;
                }
                if (getPhoneByErp(phoneList, erp)) {
                    return null;
                }
            }
        }
        if (ObjectHelper.isEmpty(phoneList.size())) {
            // 兜底逻辑：如果在岗人员为0人，则取网格对应的负责人姓名+手机号
            List<String> ownerUserErps = listResult.stream().map(WorkStationGrid::getOwnerUserErp).distinct().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ownerUserErps)) {
                for (String erp : ownerUserErps) {
                    if (pageSize.equals(phoneList.size())) {
                        break;
                    }
                    if (getPhoneByErp(phoneList, erp)) {
                        return null;
                    }
                }
            }
        }
        return dockCodeAndPhone;
    }

    /**
     * 通过员工erp获取员工电话
     * @param phoneList
     * @param erp
     * @return
     */
    private boolean getPhoneByErp(List<UserNameAndPhone> phoneList, String erp) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffIgnoreIsResignByErp(erp);
        if (ObjectHelper.isEmpty(baseStaffSiteOrgDto)) {
            return true;
        }
        UserNameAndPhone userNameAndPhone = new UserNameAndPhone();
        userNameAndPhone.setUserName(erp);
        userNameAndPhone.setPhone(baseStaffSiteOrgDto.getMobilePhone1());
        phoneList.add(userNameAndPhone);
        return false;
    }

    /**
     * 校验入参
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<DockCodeAndPhone>
     */

    private Result<DockCodeAndPhone> checkInParam(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO) {
        Result<DockCodeAndPhone> result = new Result();
        if (StringUtils.isEmpty(dockCodeAndPhoneQueryDTO.getStartSiteID())) {
            result.toFail("获取月台号数据异常!");
            return result;
        }
        if (StringUtils.isEmpty(dockCodeAndPhoneQueryDTO.getEndSiteID())) {
            result.toFail("获取月台号数据异常!");
            return result;
        }
        if (null != dockCodeAndPhoneQueryDTO.getFlowDirectionType()) {
            result.toFail("获取月台号数据异常!");
            return result;
        }
        result.toSuccess();
        return result;
    }

}

package com.jd.bluedragon.distribution.workStation.impl;

import com.jd.bluedragon.core.jsf.workStation.DockCodeAndPhoneMapper;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhone;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.workStation.DockCodeAndPhoneService;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQueryDTO;
import com.jd.bluedragon.distribution.workStation.domain.UserNameAndPhone;

import com.jd.bluedragon.utils.BaseContants;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.security.aces.mybatis.util.AcesFactory;
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

    @Value("#{'${jobCodeList}'.split(',')}")
    private List<Integer> jobCodeList;

    @Value("${dockCodeAndPhoneSize}")
    private Integer dockCodeAndPhoneSize;

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
                    userSignRecordQuery.setLimit(dockCodeAndPhoneSize);
                    List<UserSignRecord> userSignRecords = userSignRecordService.queryByBusinessKeyAndJobCode(userSignRecordQuery);
                    userSignRecords.forEach(userSignRecord->{
                        if (!StringUtils.isEmpty(userSignRecord.getUserCode())) {
                            erps.add(userSignRecord.getUserCode());
                        }
                    });
                }
                //6、根据erp查询电话号码
                DockCodeAndPhone phone = getPhone(listResult.getData(), erps);
                result.toSuccess();
                result.setData(phone);
            }
            return result;
        } catch (Exception e) {
            log.error("获取联系人号数据异常:", e);
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
                if (phoneList.size() >= dockCodeAndPhoneSize) {
                    break;
                }
                UserNameAndPhone phoneByErp = getPhoneByErp(erp);
                if (ObjectHelper.isNotEmpty(phoneByErp)) {
                    phoneList.add(phoneByErp);
                }
            }
        }
        if (CollectionUtils.isEmpty(phoneList)) {
            // 兜底逻辑：如果在岗人员为0人，则取网格对应的负责人姓名+手机号
            List<String> ownerUserErps = listResult.stream().map(WorkStationGrid::getOwnerUserErp).distinct().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ownerUserErps)) {
                for (String erp : ownerUserErps) {
                    if (phoneList.size() >= dockCodeAndPhoneSize) {
                        break;
                    }
                    UserNameAndPhone phoneByErp = getPhoneByErp(erp);
                    if (ObjectHelper.isNotEmpty(phoneByErp)) {
                        phoneList.add(phoneByErp);
                    }
                }
            }
        }
        dockCodeAndPhone.setPhoneList(phoneList);
        return dockCodeAndPhone;
    }

    /**
     * 通过员工erp获取员工电话
     *
     * @param erp
     * @return
     */
    private UserNameAndPhone getPhoneByErp(String erp) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffIgnoreIsResignByErp(erp);
        if (ObjectHelper.isEmpty(baseStaffSiteOrgDto)) {
            return null;
        }
        UserNameAndPhone userNameAndPhone = new UserNameAndPhone();
        userNameAndPhone.setUserName(baseStaffSiteOrgDto.getStaffName());
        userNameAndPhone.setPhone(baseStaffSiteOrgDto.getMobilePhone1());
        return userNameAndPhone;
    }

}

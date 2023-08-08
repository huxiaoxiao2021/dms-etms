package com.jd.bluedragon.distribution.workStation.impl;


import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.domain.DockCodeAndPhone;
import com.jd.bluedragon.common.domain.DockCodeAndPhoneQuery;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigEntity;
import com.jd.bluedragon.distribution.jy.dao.config.JyWorkMapFuncConfigDao;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordFlowDao;
import com.jd.bluedragon.distribution.station.dao.WorkStationDao;
import com.jd.bluedragon.distribution.station.dao.WorkStationGridDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.workStation.DockCodeAndPhoneService;
import com.jd.bluedragon.utils.BaseContants;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.print.utils.ObjectHelper;
import com.jdl.basic.api.service.workStation.WorkGridFlowDirectionJsfService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service("dockCodeAndPhoneService")
public class DockCodeAndPhoneServiceImpl implements DockCodeAndPhoneService {


    @Autowired
    private JyWorkMapFuncConfigDao jyWorkMapFuncConfigDao;

    @Autowired
    private WorkStationDao workStationDao;

    @Autowired
    private WorkGridFlowDirectionJsfService workGridFlowDirectionJsfService;

    @Autowired
    private WorkStationGridDao workStationGridDao;

    @Autowired
    private UserSignRecordFlowDao userSignRecordFlowDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 获取运输月台号和联系人
     *
     * @param
     * @return
     */
    @Override
    public DockCodeAndPhone queryDockCodeAndPhone(DockCodeAndPhoneQuery dockCodeAndPhoneQuery) {
        if (StringUtils.isEmpty(dockCodeAndPhoneQuery.getStartSiteID()) || StringUtils.isEmpty(dockCodeAndPhoneQuery.getEndSiteID()) || null != dockCodeAndPhoneQuery.getSiteType()) {
            throw new IllegalArgumentException("传入的必要参数为空，dockCodeAndPhoneQuery" + JSONObject.toJSON(dockCodeAndPhoneQuery));
        }
        DockCodeAndPhone dockCodeAndPhone = new DockCodeAndPhone();
        List<String> refStationKeyList = new ArrayList<>();
        //1、根据传入的类型去jy_work_map_func_config 拣运APP功能和工序映射表中查询出ref_work_key
        List<JyWorkMapFuncConfigEntity> jyWorkMapFuncConfigEntities = new ArrayList<JyWorkMapFuncConfigEntity>();
        JyWorkMapFuncConfigEntity jyWorkMapFuncConfigEntity = new JyWorkMapFuncConfigEntity();
        if (dockCodeAndPhoneQuery.getSiteType().equals(BaseContants.NUMBER_ONE)) {
            jyWorkMapFuncConfigEntity.setFuncCode(JyFuncCodeEnum.SEND_CAR_POSITION.getCode());
            jyWorkMapFuncConfigEntities = jyWorkMapFuncConfigDao.queryByCondition(jyWorkMapFuncConfigEntity);
        } else {
            List<String> FuncCodeList = new ArrayList<>();
            FuncCodeList.add(JyFuncCodeEnum.UNSEAL_CAR_POSITION.getCode());
            FuncCodeList.add(JyFuncCodeEnum.UNLOAD_CAR_POSITION.getCode());
            jyWorkMapFuncConfigEntities = jyWorkMapFuncConfigDao.queryByConditionList(FuncCodeList);
        }
        if (CollectionUtils.isNotEmpty(jyWorkMapFuncConfigEntities)) {
            refStationKeyList = jyWorkMapFuncConfigEntities.stream().map(JyWorkMapFuncConfigEntity::getRefWorkKey).distinct().collect(Collectors.toList());
        }
            //2、根据ref_work_key去work_station 网格工序信息表中查询area_code，business_key
//        ArrayList<WorkStation> workStationlist = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(jyWorkMapFuncConfigEntities)) {
//            for (JyWorkMapFuncConfigEntity workMapFuncConfigEntity : jyWorkMapFuncConfigEntities) {
//                WorkStation workStation = workStationDao.queryByRealBusinessKey(workMapFuncConfigEntity.getRefWorkKey());
//                workStationlist.add(workStation);
//            }
//        }
//        List<String> refStationKeyList = workStationlist.stream().distinct().map(WorkStation::getBusinessKey).collect(Collectors.toList());
            //3、根据传入的流向：发货地ID及名称、目的地ID及名称去work_grid_flow_direction  场地网格流向表 获取 ref_work_grid_key
            List<String> refWorkGridKeyList = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = new BaseStaffSiteOrgDto();
            if (dockCodeAndPhoneQuery.getSiteType().equals(BaseContants.NUMBER_ONE)) {
                baseStaffSiteOrgDto= baseMajorManager.getBaseSiteByDmsCode(dockCodeAndPhoneQuery.getStartSiteID());
            }else {
                baseStaffSiteOrgDto= baseMajorManager.getBaseSiteByDmsCode(dockCodeAndPhoneQuery.getEndSiteID());
            }
            if (!ObjectHelper.isEmpty(baseStaffSiteOrgDto)) {
                refWorkGridKeyList = workGridFlowDirectionJsfService.queryFlowDataForFlowSiteCode(dockCodeAndPhoneQuery.getSiteType(), baseStaffSiteOrgDto.getSiteCode()).getData();
            }
            //4、根据business_key和ref_work_grid_key去work_station_grid 场地网格工序信息表查询 获取到月台号 和 business_key
            //获取月台号
            List<WorkStationGrid> workStationGrids = new ArrayList<WorkStationGrid>();
            List<WorkStationGrid> workStationGridList = new ArrayList<WorkStationGrid>();
            List<String> dockCodes = new ArrayList<String>();
            List<String> businessKeys = new ArrayList<String>();
            List<String> finallyPhones = new ArrayList<String>();
            if (CollectionUtils.isNotEmpty(refStationKeyList)) {
                workStationGrids = workStationGridDao.queryByRefStationKey(refStationKeyList);
                if (CollectionUtils.isNotEmpty(workStationGrids)) {
                    workStationGrids.forEach(item -> {
                        dockCodes.add(item.getDockCode());
                        businessKeys.add(item.getBusinessKey());
                        finallyPhones.add(item.getOwnerUserErp());
                    });
                }
            }
            if (!CollectionUtils.isEmpty(refWorkGridKeyList)) {
                workStationGridList = workStationGridDao.queryByRefGridKey(refWorkGridKeyList);
                if (CollectionUtils.isNotEmpty(workStationGridList)) {
                    workStationGridList.forEach(item -> {
                        dockCodes.add(item.getDockCode());
                        businessKeys.add(item.getBusinessKey());
                        finallyPhones.add(item.getOwnerUserErp());
                    });
                }
            }
            if (!CollectionUtils.isEmpty(dockCodes)) {
                List<String> dockCodeList = dockCodes.stream().distinct().collect(Collectors.toList());
                if (dockCodeList.size()>BaseContants.NUMBER_FIVE){
                    dockCodeAndPhone.setDockCodeList(dockCodeList.subList(BaseContants.NUMBER_ONE, BaseContants.NUMBER_FIVE));
                }else {
                    dockCodeAndPhone.setDockCodeList(dockCodeList);
                }
            }
            //5、根据business_key去user_sign_record 查询 erp
            List<UserSignRecordFlow> userSignRecordFlows = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(businessKeys)) {
                List<String> businessKeyList = businessKeys.stream().distinct().collect(Collectors.toList());
                userSignRecordFlows = userSignRecordFlowDao.queryByRefGridKey(businessKeyList);
            }

            //6、根据erp查询电话号码
            List<Map<String, String>> phoneList = new ArrayList<>();
            Map<String, String> map = new HashMap<String, String>();
            if (CollectionUtils.isNotEmpty(userSignRecordFlows)) {
                for (UserSignRecordFlow userSignRecordFlow : userSignRecordFlows) {
                    BaseStaffSiteOrgDto baseStaffIgnoreIsResignByErp = baseMajorManager.getBaseStaffIgnoreIsResignByErp(userSignRecordFlow.getUserCode());
                    map.put(userSignRecordFlow.getUserCode(), baseStaffIgnoreIsResignByErp.getMobilePhone1());
                }
            }
            // 兜底逻辑：如果在岗人员为0人，则取网格对应的负责人姓名+手机号
            if (CollectionUtils.isEmpty(dockCodeAndPhone.getPhoneList())) {
                if (CollectionUtils.isNotEmpty(finallyPhones)) {
                    List<String> finallyPhoneList = finallyPhones.stream().distinct().collect(Collectors.toList());
                    for (String finallyPhone : finallyPhoneList) {
                        BaseStaffSiteOrgDto baseStaffIgnoreIsResignByErp = baseMajorManager.getBaseStaffIgnoreIsResignByErp(finallyPhone);
                        map.put(finallyPhone, baseStaffIgnoreIsResignByErp.getMobilePhone1());
                    }
                }
            }
            phoneList.add(map);
            if (phoneList.size()>BaseContants.NUMBER_THREE){
                dockCodeAndPhone.setPhoneList(phoneList.subList(BaseContants.NUMBER_ONE, BaseContants.NUMBER_THREE));
            }else {
                dockCodeAndPhone.setPhoneList(phoneList);
            }

        return dockCodeAndPhone;
    }

}

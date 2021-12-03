package com.jd.bluedragon.distribution.dock.convert;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.distribution.dock.domain.DockBaseInfoPo;
import com.jd.bluedragon.distribution.dock.entity.AllowedVehicleEntity;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.dock.enums.DockAttributeEnums;
import com.jd.bluedragon.distribution.dock.enums.DockTypeEnums;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.convert
 * @ClassName: DockInfoConverter
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/12/1 17:46
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class DockInfoConverter {

    /**
     * 将数据库的对象转化为通用实体对象
     * @param dockBaseInfoPo
     * @return
     */
    public static DockInfoEntity convertToEntity(DockBaseInfoPo dockBaseInfoPo) {
        DockInfoEntity entity = new DockInfoEntity();
        entity.setId(dockBaseInfoPo.getId());
        entity.setDockCode(dockBaseInfoPo.getDockCode());
        entity.setSiteCode(dockBaseInfoPo.getSiteCode());
        entity.setSiteName(dockBaseInfoPo.getSiteName());
        entity.setOrgId(dockBaseInfoPo.getOrgId());
        entity.setOrgName(dockBaseInfoPo.getOrgName());
        entity.setDockType(DockTypeEnums.getEnumsByType(dockBaseInfoPo.getDockType()));
        entity.setDockAttribute(DockAttributeEnums.getEnumsByType(dockBaseInfoPo.getDockAttribute()));
        entity.setAllowedVehicleTypes(getListEntity(dockBaseInfoPo.getAllowedVehicleType()));
        entity.setImmediately(dockBaseInfoPo.getImmediately());
        entity.setHasDockLeveller(dockBaseInfoPo.getHasDockLeveller());
        entity.setHasScales(dockBaseInfoPo.getHasScales());
        entity.setHeight(dockBaseInfoPo.getHeight());
        entity.setCreateUserName(dockBaseInfoPo.getCreateUserName());
        entity.setCreateTime(dockBaseInfoPo.getCreateTime());
        entity.setUpdateUserName(dockBaseInfoPo.getUpdateUserName());
        entity.setUpdateTime(dockBaseInfoPo.getUpdateTime());
        entity.setIsDelete(dockBaseInfoPo.getIsDelete());
        entity.setTs(dockBaseInfoPo.getTs());
        return entity;
    }


    /**
     * 将通用实体对象转化为数据库的对象
     * @param dockInfoEntity
     * @return
     */
    public static DockBaseInfoPo convertToPo(DockInfoEntity dockInfoEntity) {
        DockBaseInfoPo po = new DockBaseInfoPo();
        po.setId(dockInfoEntity.getId());
        po.setDockCode(dockInfoEntity.getDockCode());
        po.setSiteCode(dockInfoEntity.getSiteCode());
        po.setSiteName(dockInfoEntity.getSiteName());
        po.setOrgId(dockInfoEntity.getOrgId());
        po.setOrgName(dockInfoEntity.getOrgName());
        if (!Objects.equals(dockInfoEntity.getDockType(), null)) {
            po.setDockType(dockInfoEntity.getDockType().getType());
        }
        if (!Objects.equals(dockInfoEntity.getDockAttribute(), null)) {
            po.setDockAttribute(dockInfoEntity.getDockAttribute().getAttr());
        }
        po.setAllowedVehicleType(getListCodeString(dockInfoEntity.getAllowedVehicleTypes()));
        po.setImmediately(dockInfoEntity.getImmediately());
        po.setHasDockLeveller(dockInfoEntity.getHasDockLeveller());
        po.setHasScales(dockInfoEntity.getHasScales());
        po.setHeight(dockInfoEntity.getHeight());
        po.setCreateUserName(dockInfoEntity.getCreateUserName());
        po.setCreateTime(dockInfoEntity.getCreateTime());
        po.setUpdateUserName(dockInfoEntity.getUpdateUserName());
        po.setUpdateTime(dockInfoEntity.getUpdateTime());
        po.setIsDelete(dockInfoEntity.getIsDelete());
        po.setTs(dockInfoEntity.getTs());
        return po;
    }

    private static String getListCodeString(List<AllowedVehicleEntity> allowedVehicleEntities) {
        if (CollectionUtils.isEmpty(allowedVehicleEntities)) {
            return "";
        }
        List<String> codes = new ArrayList<>();
        for(AllowedVehicleEntity entity : allowedVehicleEntities) {
            codes.add(entity.getCode());
        }

        return JsonHelper.toJson(codes);
    }

    private static List<AllowedVehicleEntity> getListEntity(String listCode) {
        List<AllowedVehicleEntity> allowedVehicleEntities = new ArrayList<>();
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(listCode)) {
            list = JsonHelper.fromJsonUseGson(listCode, new TypeToken<List<String>>(){}.getType());
        }
        if (!CollectionUtils.isEmpty(list)) {
            for (String code : list) {
                AllowedVehicleEntity entity = new AllowedVehicleEntity();
                entity.setCode(code);
                allowedVehicleEntities.add(entity);
            }
        }
        return allowedVehicleEntities;

    }

}

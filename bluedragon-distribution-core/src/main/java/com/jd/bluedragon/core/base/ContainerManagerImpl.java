package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.ContainerQueryService;
import com.jd.ql.shared.services.sorting.api.ContainerService;
import com.jd.ql.shared.services.sorting.api.dto.*;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 中台容器相关的服务
 *
 * @author shipeilin
 * @date 2019年05月14日 14时:55分
 */
@Service("containerManager")
public class ContainerManagerImpl implements ContainerManager{

    private Log log = LogFactory.getLog(ContainerManagerImpl.class);

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ContainerQueryService containerQueryService;

    @Autowired
    private BaseMajorManager baseMajorManager;


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ContainerManagerImpl.createContainers", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<Box> createContainers(Box param, BoxSystemTypeEnum systemType) throws Exception{
        log.info("中台创建容器入参：" + JsonHelper.toJson(param));

        UserEnv userEnv = buildUserEnv(param.getCreateUser(), param.getCreateUser(), param.getCreateSiteCode());
        Flow flow = buildFlow(param);

        ContainerSource containerSource = ContainerSource.CLIENT;
        if(BoxSystemTypeEnum.AUTO_SORTING_MACHINE.equals(systemType)){
            containerSource = ContainerSource.MACHINE;
        }

        SortingAttributes attributes = buildSortingAttributes(param, containerSource);
        int count = param.getQuantity();

        ApiResult<List<Container>> apiResult = containerService.createContainers(flow, attributes, count, userEnv);
        log.info("中台创建容器结果：" + JsonHelper.toJson(apiResult));

        if(!Constants.INTEGER_FLG_TRUE.equals(apiResult.getCode())){
            log.warn("通过中台创建箱号失败：" + JsonHelper.toJson(apiResult));
            throw new Exception("通过中台创建箱号失败：" + apiResult.getMessage());
        }
        List<Container> containers = apiResult.getData();
        List<Box> boxes = new ArrayList<>();
        if(containers != null && !containers.isEmpty()){
            for (Container container : containers){
                Box box = new Box();
                BeanHelper.copyProperties(box, param);
                box.setCode(container.getCode());
                box.setStatus(Box.STATUS_PRINT);
                boxes.add(box);
            }
        }

        return boxes;
    }

    /**
     * 暂时不接入，只有龙门架会量体积并更新，但是目前消息体没有体积数据，估计业务下线了
     * @param boxCode 箱号
     * @param userErp 更新人erp
     * @param length 长厘米
     * @param width 宽厘米
     * @param height 高厘米
     * @return
     * @throws Exception
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ContainerManagerImpl.updateVolumeByContainerCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean updateVolumeByContainerCode(String boxCode, String userErp, String userName, Integer createSiteCode,
                                               Double length, Double width, Double height) throws Exception{
        //体积为0不用更新直接返回成功
        if(length == null || width == null || height == null){
            log.warn("箱号体积为0，不再更新中台容器体积：" + boxCode);
            return true;
        }
        log.info("中台更新容器体积：" + boxCode);
        UserEnv userEnv = buildUserEnv(userErp, userName, createSiteCode);

        Volume volume = new Volume();
        volume.setLength(BigDecimal.valueOf(length));
        volume.setWidth(BigDecimal.valueOf(width));
        volume.setHeight(BigDecimal.valueOf(height));
        volume.setUnit(VolumeUnit.CM3);
        volume.setVolume(BigDecimal.valueOf(length * width * height));

        ApiResult<Void> apiResult = containerService.measure(boxCode, volume, null, userEnv);
        log.info("中台更新体积结果：" + JsonHelper.toJson(apiResult));

        if(!Constants.INTEGER_FLG_TRUE.equals(apiResult.getCode())){
            log.warn("通过中台更新箱号体积失败：" + JsonHelper.toJson(apiResult));
            throw new Exception("通过中台更新箱号体积失败：" + apiResult.getMessage());
        }
        return true;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ContainerManagerImpl.updateBoxSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean updateBoxSend(String boxCode, String userErp, String userName, Integer createSiteCode) throws Exception{
        log.info("中台更新容器状态为发货：" + boxCode);

        UserEnv userEnv = buildUserEnv(userErp, userName, createSiteCode);

        ApiResult<Void> apiResult = containerService.send(userEnv, boxCode);
        log.info("中台更新容器状态为发货结果：" + JsonHelper.toJson(apiResult));

        if(!Constants.INTEGER_FLG_TRUE.equals(apiResult.getCode())){
            log.warn("通过中台更新箱号发货状态失败：" + JsonHelper.toJson(apiResult));
            throw new Exception("通过中台更新箱号发货状态失败：" + apiResult.getMessage());
        }
        return true;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ContainerManagerImpl.updateBoxSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean updateBoxCancelSend(String boxCode, String userErp, String userName, Integer createSiteCode) throws Exception{
        log.info("中台更新容器状态为取消发货：" + boxCode);

        UserEnv userEnv = buildUserEnv(userErp, userName, createSiteCode);
        ApiResult<Void> apiResult = containerService.reopenContainer(boxCode, userEnv);
        log.info("中台更新容器状态为取消发货结果：" + JsonHelper.toJson(apiResult));

        if(!Constants.INTEGER_FLG_TRUE.equals(apiResult.getCode())){
            log.warn("通过中台更新箱号发货状态失败：" + JsonHelper.toJson(apiResult));
            throw new Exception("通过中台更新箱号发货状态失败：" + apiResult.getMessage());
        }
        return true;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ContainerManagerImpl.findBoxByCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Box findBoxByCode(String boxCode) throws Exception{
        log.info("中台查询容器入参：" + boxCode);

        Box box = null;
        ApiResult<Container> apiResult = containerQueryService.getContainerByCode(tenantCode, boxCode);
        log.info("中台查询容器结果：" + JsonHelper.toJson(apiResult));

        if(Constants.INTEGER_FLG_TRUE.equals(apiResult.getCode())){
            box = container2Box(apiResult.getData());
        }else{
            log.warn("通过中台查询箱号失败：" + JsonHelper.toJson(apiResult));
            throw new Exception("通过中台查询箱号失败：" + apiResult.getMessage());
        }
        return box;
    }

    /**
     * 将容器转换为箱子
     * @param container
     * @return
     */
    private Box container2Box(Container container){
        Box box = new Box();
        if(container != null){
            box.setCode(container.getCode());

            //获取流向
            Flow flow = container.getFlow();
            BaseStaffSiteOrgDto fromSite = baseMajorManager.getBaseSiteBySiteId(flow.getFromSiteId());
            BaseStaffSiteOrgDto toSite = baseMajorManager.getBaseSiteBySiteId(flow.getToSiteId());

            //设置始发目的
            box.setCreateSiteCode(flow.getFromSiteId());
            box.setCreateSiteName(fromSite.getSiteName());
            box.setReceiveSiteCode(flow.getToSiteId());
            box.setReceiveSiteName(toSite.getSiteName());

            //设置箱子状态已打印、已发货（已打印为初始态）
            if(ContainerStatus.OPEN.equals(container.getStatus())
                    || ContainerStatus.PRINT.equals(container.getStatus())
                    || ContainerStatus.RE_OPEN.equals(container.getStatus())){
                box.setStatus(Box.STATUS_PRINT);
            }else if(ContainerStatus.HAS_SENT.equals(container.getStatus())){
                box.setStatus(Box.BOX_STATUS_SEND);
            }

            //设置是否可以混装
            if(container.getAttributes().isMixContainer()){
                box.setMixBoxType(Constants.INTEGER_FLG_TRUE);
            }else{
                box.setMixBoxType(Constants.INTEGER_FLG_FALSE);
            }

            //设置预计发货时间
            Object predictSendTime = container.getAttributes().getCustomAttributes().get(predictSendTimeKey);
            if(predictSendTime != null ){
                box.setPredictSendTime(new Date((Long) predictSendTime));
            }

            //设置路由信息
            box.setRouter(flow.getSimpleRouteCodes());
            box.setRouterName(flow.getSimpleRouteNames());

            //设置承运类型
            box.setTransportType(container.getAttributes().getTransportType().type());

            //设置箱子类型
            box.setType(container.getAttributes().getContainerBusinessType().type());
        }

        return box;
    }

    /**
     * 构建userEnv
     * @param userErp 用户erp
     * @param userName 用户名称
     * @param createSiteCode 所属站点ID
     * @return UserEnv
     */
    private UserEnv buildUserEnv(String userErp, String userName, Integer createSiteCode){
        if(StringUtils.isBlank(userErp)){
            userErp = defaultUserErp;
        }
        if(StringUtils.isBlank(userName)){
            userName = defaultUserName;
        }
        UserEnv userEnv = new UserEnv();
        userEnv.setTenantCode(tenantCode);
        userEnv.setOperateSortingCenterId(createSiteCode);
        User user = new User(userErp, UserSource.JD_ERP);
        user.setUserName(userName);
        userEnv.setUser(user);
        return userEnv;
    }

    /**
     * 构建容器流向:场地类型统一为SITE
     * @param box 箱子
     * @return Flow
     */
    private Flow buildFlow(Box box){
        Flow flow = new Flow();
        flow.setFromSiteId(box.getCreateSiteCode());
        flow.setFromSiteType(SiteType.SITE);
        flow.setToSiteId(box.getReceiveSiteCode());
        flow.setToSiteType(SiteType.SITE);
        flow.setSimpleRouteCodes(box.getRouter());
        flow.setSimpleRouteNames(box.getRouterName());
        return flow;
    }

    /**
     * 构建容器属性
     * @param box 箱子
     * @return 容器属性
     */
    private SortingAttributes buildSortingAttributes(Box box, ContainerSource containerSource){
        SortingAttributes attributes = new SortingAttributes();
        attributes.setContainerSource(containerSource);
        attributes.setContainerBusinessType(getContainerBusinessType(box.getType()));
        //承运类型
        TransportType transportType = null;
        if(TransportType.LAND.type().equals(box.getTransportType())){
            transportType = TransportType.LAND;
        }else if(TransportType.AIR.type().equals(box.getTransportType())){
            transportType = TransportType.AIR;
        }else if(TransportType.RAILWAY.type().equals(box.getTransportType())){
            transportType = TransportType.AIR;
        }else if(TransportType.CITY.type().equals(box.getTransportType())){
            transportType = TransportType.CITY;
        }
        attributes.setTransportType(transportType );
        attributes.setMixContainer(Constants.INTEGER_FLG_TRUE.equals(box.getMixBoxType()));
        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put(predictSendTimeKey, box.getPredictSendTime().getTime());
        attributes.setCustomAttributes(customAttributes);
        return attributes;
    }

    /**
     * 获取容器业务类型枚举
     * @param type 类型编码
     * @return 容器业务类型
     */
    private ContainerBusinessType getContainerBusinessType(String type){
        ContainerBusinessType result = null;
        for(ContainerBusinessType containerBusinessType : ContainerBusinessType.values()){
            if(containerBusinessType.type().equalsIgnoreCase(type)){
                result = containerBusinessType;
                break;
            }
        }

        return result;
    }

}

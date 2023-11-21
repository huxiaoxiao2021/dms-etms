package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.base.RequestProfile;
import com.jd.bluedragon.distribution.api.request.box.BoxPrintReq;
import com.jd.bluedragon.distribution.api.request.box.CreateBoxReq;
import com.jd.bluedragon.distribution.api.response.box.BoxPrintInfo;
import com.jd.bluedragon.distribution.api.response.box.CreateBoxInfo;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.constants.BoxSubTypeEnum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.dms.utils.BoxCodeUtil;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * 箱号打印相关服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-11-03 10:14:16 周五
 */
@Service("boxPrintService")
public class BoxPrintServiceImpl implements BoxPrintService{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoxService boxService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private CrossBoxService crossBoxService;

    /**
     * 创建箱号
     *
     * @param createBoxReq 创建箱号入参
     * @return 箱号数据
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    @Override
    public Result<CreateBoxInfo> createBoxPrintInfo(CreateBoxReq createBoxReq) {
        if(log.isInfoEnabled()){
            log.info("BoxPrintServiceImpl.createBoxPrintInfo param {}", JsonHelper.toJson(createBoxReq));
        }
        Result<CreateBoxInfo> result = Result.success();
        final CreateBoxInfo createBoxInfo = new CreateBoxInfo();
        result.setData(createBoxInfo);
        try {

            final Result<Void> checkResult = this.checkParam4CreateBoxPrintInfo(createBoxReq);
            if (checkResult.isFail()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            final BaseStaffSiteOrgDto operateUserStaffInfo = baseMajorManager.getBaseStaffIgnoreIsResignByErp(createBoxReq.getOperateUser().getUserCode());
            if(operateUserStaffInfo == null){
                return result.toFail(String.format("未找到员工为%s的数据", createBoxReq.getOperateUser().getUserCode()));
            }
            createBoxReq.getOperateUser().setUserId(operateUserStaffInfo.getStaffNo().longValue());

            final BaseStaffSiteOrgDto createSiteInfo = baseMajorManager.getBaseSiteBySiteId(createBoxReq.getCreateSiteCode());

            List<Box> availableBoxes;
            availableBoxes = boxService.batchAddNew(this.toBox(createBoxReq, createSiteInfo), BoxSystemTypeEnum.AUTO_SORTING_MACHINE.getCode());

            List<String> boxCodeList = new ArrayList<>(availableBoxes.size());
            for (Box item : availableBoxes) {
                boxCodeList.add(item.getCode());
            }
            createBoxInfo.setBoxCodes(boxCodeList);

            if (createBoxReq.getTransportType() != null && createBoxReq.getTransportType() == 2) {// 只有公路运输的支持路由信息查询2014.3.10
                // 获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
                try {
                    CrossBoxResult<CrossBox> resData = crossBoxService.getCrossDmsBoxByOriAndDes(createBoxReq.getCreateSiteCode(), createBoxReq.getReceiveSiteCode());
                    if (log.isInfoEnabled()) {
                        this.log.info("调用跨箱号中转获取箱号路由:{}", com.jd.bluedragon.utils.JsonHelper.toJson(resData));
                    }
                    List<AbstractMap.SimpleEntry<Integer, String>> routerMapList = new ArrayList<AbstractMap.SimpleEntry<Integer, String>>();
                    if (null != resData.getData()) {
                        routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getOriginalDmsId(), resData.getData().getOriginalDmsName()));
                        if (null != resData.getData().getTransferOneId()) {
                            routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getTransferOneId(), resData.getData().getTransferOneName()));
                        }
                        if (null != resData.getData().getTransferTwoId()) {
                            routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getTransferTwoId(), resData.getData().getTransferTwoName()));
                        }
                        if (null != resData.getData().getTransferThreeId()) {
                            routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getTransferThreeId(), resData.getData().getTransferThreeName()));
                        }
                        routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getDestinationDmsId(), resData.getData().getDestinationDmsName()));
                    } else {
                        routerMapList.add(new AbstractMap.SimpleEntry<>(createBoxReq.getCreateSiteCode(), createBoxReq.getCreateSiteName()));
                        routerMapList.add(new AbstractMap.SimpleEntry<>(createBoxReq.getReceiveSiteCode(), createBoxReq.getReceiveSiteName()));
                    }
                    createBoxInfo.setRouter(getRouterNameList(routerMapList));
                    createBoxInfo.setRouterText(getRouterText(routerMapList));
                } catch (Exception e) {
                    this.log.error("获得站点路由信息失败： ", e);
                }
            }
            int routerCount = createBoxInfo.getRouter() == null ? 0 : createBoxInfo.getRouter().size();
            createBoxInfo.setTemplateName(BoxCodeUtil.getTemplateName(routerCount));
            createBoxInfo.setTemplateVersion(BoxCodeUtil.getTemplateVersion(createBoxInfo.getTemplateName()));
            createBoxInfo.setBoxType(BoxCodeUtil.getBoxTypeName(createBoxReq.getType()));
            createBoxInfo.setCategoryText(BoxCodeUtil.getCategoryText(createBoxReq.getTransportType()));
            createBoxInfo.setMixBoxTypeText(BoxCodeUtil.getMixBoxTypeText(createBoxReq.getMixBoxType()));
            createBoxInfo.setCreateSiteName(createBoxReq.getCreateSiteName());
            createBoxInfo.setReceiveSiteName(createBoxReq.getReceiveSiteName());
        } catch (Exception e) {
            log.error("BoxPrintServiceImpl.createBoxPrintInfo exception {}", JsonHelper.toJson(createBoxReq), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4CreateBoxPrintInfo(CreateBoxReq createBoxReq) {
        Result<Void> checkResult = Result.success();
        final OperateUser operateUser = createBoxReq.getOperateUser();
        if (operateUser == null) {
            return checkResult.toFail("参数错误，operateUser不能为空");
        }
        if (operateUser.getSiteCode() == null) {
            return checkResult.toFail("参数错误，operateUser.siteCode不能为空");
        }
        if (StringUtils.isBlank(operateUser.getUserCode())) {
            return checkResult.toFail("参数错误，operateUser.userCode不能为空");
        }

        final RequestProfile requestProfile = createBoxReq.getRequestProfile();
        if(requestProfile == null){
            return checkResult.toFail("参数错误，requestProfile不能为空");
        }

        if (StringUtils.isBlank(createBoxReq.getType())) {
            return checkResult.toFail("参数错误，type不能为空");
        }

        if (createBoxReq.getCreateSiteCode() == null) {
            return checkResult.toFail("参数错误，createSiteCode不能为空");
        }
        if (StringUtils.isBlank(createBoxReq.getCreateSiteName())) {
            return checkResult.toFail("参数错误，createSiteName不能为空");
        }

        if (createBoxReq.getReceiveSiteCode() == null) {
            return checkResult.toFail("参数错误，receiveSiteCode不能为空");
        }
        if (StringUtils.isBlank(createBoxReq.getReceiveSiteName())) {
            return checkResult.toFail("参数错误，receiveSiteName不能为空");
        }

        if (createBoxReq.getQuantity() == null) {
            return checkResult.toFail("参数错误，quantity不能为空");
        }
        if (createBoxReq.getQuantity() <= 0) {
            return checkResult.toFail("参数错误，quantity不合法");
        }

        if (createBoxReq.getTransportType() == null) {
            return checkResult.toFail("参数错误，transportType不能为空");
        }
        if (createBoxReq.getMixBoxType() == null) {
            return checkResult.toFail("参数错误，mixBoxType不能为空");
        }

        // 排除非法箱号类型
        if (this.boxTypeCheckSwitchOn() && !BoxSubTypeEnum.ENUM_LIST.contains(createBoxReq.getSubType())) {
            return checkResult.toFail("箱号子类型不合法!", CreateBoxInfo.Code_boxTypeIllegal);
        }
        return checkResult;
    }

    /**
     * 箱号类型校验开关
     * @return
     */
    private Boolean boxTypeCheckSwitchOn(){
        Boolean sign = Boolean.FALSE;
        try {
            SysConfig config = sysConfigService.findConfigContentByConfigName(Constants.BOX_TYPE_CHECK_SWITCH);
            if(config != null && Constants.STRING_FLG_TRUE.equals(config.getConfigContent())){
                sign = true;
            }
        }catch (Exception e){
            log.error("获取箱号类型开关异常",e);
        }
        return sign;
    }

    private Box toBox(CreateBoxReq request, BaseStaffSiteOrgDto createSiteInfo) {

        Box box = new Box();
        box.setType(request.getType());
        box.setBoxSubType(request.getSubType());
        box.setQuantity(request.getQuantity());
        box.setCreateSiteCode(request.getCreateSiteCode());
        box.setCreateSiteName(request.getCreateSiteName());
        box.setReceiveSiteCode(request.getReceiveSiteCode());
        box.setReceiveSiteName(request.getReceiveSiteName());
        final OperateUser operateUser = request.getOperateUser();
        box.setCreateUser(operateUser.getUserName());
        if (operateUser.getUserId() != null) {
            box.setCreateUserCode(operateUser.getUserId().intValue());
        }
        box.setTransportType(request.getTransportType());
        box.setMixBoxType(request.getMixBoxType());
        //临时占用字段处理站点商家重复
        box.setStatuses(String.valueOf(createSiteInfo.getSiteType()));
        box.setUpdateUser(operateUser.getUserCode());
        //设置状态和当前节点
        box.setStatus(com.jd.bluedragon.distribution.external.constants.BoxStatusEnum.OPEN.getStatus());
        box.setLastNodeType(OpBoxNodeEnum.PRINTBOXCODE.getNodeCode());
        return box;
    }

    private List<String> getRouterNameList(List<AbstractMap.SimpleEntry<Integer, String>> routerInfo){
        List<String> routerNameList = new ArrayList<>();
        if (routerInfo == null || routerInfo.isEmpty() || routerInfo.size() <= 2) {
            return routerNameList;
        }
        //移除目的地
        routerInfo.remove(routerInfo.get(routerInfo.size() - 1));
        //移除始发
        routerInfo.remove(0);
        for(AbstractMap.SimpleEntry<Integer, String> simpleEntry : routerInfo){
            routerNameList.add(BoxCodeUtil.replaceSortingName(simpleEntry.getValue()));
        }
        return routerNameList;
    }
    private  String getRouterText(List<AbstractMap.SimpleEntry<Integer, String>> routerInfo){
        if(routerInfo == null || routerInfo.isEmpty()){
            return "";
        }

        StringBuilder routerbf = new StringBuilder();
        for(int i = 0; i < routerInfo.size(); i++){
            routerbf.append(routerInfo.get(i).getValue());
            if(i != routerInfo.size() - 1){
                routerbf.append("-");
            }
        }

        return routerbf.toString();
    }

    /**
     * 获取箱号打印数据
     *
     * @param boxPrintReq 箱号打印入参
     * @return 箱号数据
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    @Override
    public Result<BoxPrintInfo> getBoxPrintInfo(BoxPrintReq boxPrintReq) {
        if(log.isInfoEnabled()){
            log.info("BoxPrintServiceImpl.getBoxPrintInfo param {}", JsonHelper.toJson(boxPrintReq));
        }
        Result<BoxPrintInfo> result = Result.success();
        final BoxPrintInfo boxPrintInfo = new BoxPrintInfo();
        result.setData(boxPrintInfo);
        try {

            final Result<Void> checkResult = this.checkParam4GetBoxPrintInfo(boxPrintReq);
            if (checkResult.isFail()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            final OperateUser operateUser = boxPrintReq.getOperateUser();

            Box boxParam = new Box();
            boxParam.setCode(boxPrintReq.getBoxCode());
            boxParam.setCreateSiteCode(operateUser.getSiteCode());
            final Box boxExist = boxService.findBoxByBoxCode(boxParam);

            if (boxExist == null) {
                return result.toFail(String.format("未查询到箱号为%s的箱号数据", boxPrintReq.getBoxCode()));
            }
            if (boxExist.getTransportType() != null && boxExist.getTransportType() == 2) {// 只有公路运输的支持路由信息查询2014.3.10
                // 获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
                try {
                    CrossBoxResult<CrossBox> resData = crossBoxService.getCrossDmsBoxByOriAndDes(boxExist.getCreateSiteCode(), boxExist.getReceiveSiteCode());
                    if (log.isInfoEnabled()) {
                        this.log.info("调用跨箱号中转获取箱号路由:{}", com.jd.bluedragon.utils.JsonHelper.toJson(resData));
                    }
                    List<AbstractMap.SimpleEntry<Integer, String>> routerMapList = new ArrayList<AbstractMap.SimpleEntry<Integer, String>>();
                    if (null != resData.getData()) {
                        routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getOriginalDmsId(), resData.getData().getOriginalDmsName()));
                        if (null != resData.getData().getTransferOneId()) {
                            routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getTransferOneId(), resData.getData().getTransferOneName()));
                        }
                        if (null != resData.getData().getTransferTwoId()) {
                            routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getTransferTwoId(), resData.getData().getTransferTwoName()));
                        }
                        if (null != resData.getData().getTransferThreeId()) {
                            routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getTransferThreeId(), resData.getData().getTransferThreeName()));
                        }
                        routerMapList.add(new AbstractMap.SimpleEntry<>(resData.getData().getDestinationDmsId(), resData.getData().getDestinationDmsName()));
                    } else {
                        routerMapList.add(new AbstractMap.SimpleEntry<>(boxExist.getCreateSiteCode(), boxExist.getCreateSiteName()));
                        routerMapList.add(new AbstractMap.SimpleEntry<>(boxExist.getReceiveSiteCode(), boxExist.getReceiveSiteName()));
                    }
                    boxPrintInfo.setRouter(getRouterNameList(routerMapList));
                    boxPrintInfo.setRouterText(getRouterText(routerMapList));
                } catch (Exception e) {
                    this.log.error("获得站点路由信息失败： ", e);
                }
            }

            boxPrintInfo.setBoxCode(boxPrintReq.getBoxCode());
            int routerCount = boxPrintInfo.getRouter() == null ? 0 : boxPrintInfo.getRouter().size();
            boxPrintInfo.setTemplateName(BoxCodeUtil.getTemplateName(routerCount));
            boxPrintInfo.setTemplateVersion(BoxCodeUtil.getTemplateVersion(boxPrintInfo.getTemplateName()));
            boxPrintInfo.setBoxType(BoxCodeUtil.getBoxTypeName(boxExist.getType()));
            boxPrintInfo.setCategoryText(BoxCodeUtil.getCategoryText(boxExist.getTransportType()));
            boxPrintInfo.setMixBoxTypeText(BoxCodeUtil.getMixBoxTypeText(boxExist.getMixBoxType()));
            boxPrintInfo.setCreateSiteName(boxExist.getCreateSiteName());
            boxPrintInfo.setReceiveSiteName(boxExist.getReceiveSiteName());
        } catch (Exception e) {
            log.error("BoxPrintServiceImpl.getBoxPrintInfo exception {}", JsonHelper.toJson(boxPrintReq), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4GetBoxPrintInfo(BoxPrintReq boxPrintReq) {
        Result<Void> checkResult = Result.success();
        final OperateUser operateUser = boxPrintReq.getOperateUser();
        if (operateUser == null) {
            return checkResult.toFail("参数错误，operateUser不能为空");
        }
        if (operateUser.getSiteCode() == null) {
            return checkResult.toFail("参数错误，operateUser.siteCode不能为空");
        }
        if (StringUtils.isBlank(operateUser.getUserCode())) {
            return checkResult.toFail("参数错误，operateUser.userCode不能为空");
        }

        final RequestProfile requestProfile = boxPrintReq.getRequestProfile();
        if(requestProfile == null){
            return checkResult.toFail("参数错误，requestProfile不能为空");
        }

        if (StringUtils.isBlank(boxPrintReq.getBoxCode())) {
            return checkResult.toFail("参数错误，boxCode不能为空");
        }

        return checkResult;
    }
}

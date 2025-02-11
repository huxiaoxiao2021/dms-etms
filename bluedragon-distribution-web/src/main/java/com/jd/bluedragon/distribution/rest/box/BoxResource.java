package com.jd.bluedragon.distribution.rest.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.AutoSortingBoxResult;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.constants.BoxSubTypeEnum;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.constants.BoxTypeV2Enum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.box.service.GroupBoxService;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.external.constants.BoxStatusEnum;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.impl.FuncSwitchConfigServiceImpl;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.common.annotation.CacheMethod;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.domain.SortCrossDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import com.jdl.basic.common.enums.CollectClaimEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static com.jd.bluedragon.Constants.CHECK_BOX_END_SITE_MATCH_SWITCH;
import static com.jd.bluedragon.distribution.api.response.BoxResponse.*;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jy.service.collectpackage.JyCollectPackageServiceImpl.bxBoxEndSiteTypeCheck;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isReverseSite;
import static com.jdl.basic.api.enums.WorkSiteTypeEnum.RETURN_CENTER;
import static com.jd.bluedragon.distribution.jy.enums.MixBoxTypeEnum.MIX_DISABLE;
import static com.jd.bluedragon.distribution.jy.enums.SiteTypeLevel.SiteTypeOneLevelEnum.TERMINAL_SITE;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isReverseSite;
import static com.jdl.basic.api.enums.WorkSiteTypeEnum.DMS_TYPE;
import static com.jdl.basic.api.enums.WorkSiteTypeEnum.RWMS_TYPE;


@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BoxResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 箱号类型字符长度，默认2
     * */
    private static final int BOX_TYPE_LENGTH = 2;

    private static final List<Integer> siteTypes = Arrays.asList(4,10);//营业部,自营京东派 显示特殊类型

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CrossBoxService crossBoxService;

    @Autowired
    private GroupBoxService groupBoxService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Resource(name="siteBoxTypeMap")
    private Map<String,String> siteBoxTypeMap;

    @Resource(name="sortingBoxTypeMap")
    private Map<String,String> sortingBoxTypeMap;

    @Resource(name="sortingBoxSubTypeMap")
    private Map<String,String> sortingBoxSubTypeMap;
    @Resource(name="siteBoxSubTypeMap")
    private Map<String,String> siteBoxSubTypeMap;

    @Autowired
    private CycleBoxService cycleBoxService;

    @Autowired
    private FuncSwitchConfigServiceImpl funcSwitchConfigService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private BoxLimitConfigManager boxLimitConfigManager;

    @Autowired
    @Qualifier("basicPrimaryWS")
    private BasicPrimaryWS basicPrimaryWS;

    @GET
    @Path("/boxes/{boxCode}")
    @JProfiler(jKey = "DMS.WEB.BoxResource.get", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse get(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.log.info("box code's {}", boxCode);

        Box box = this.boxService.findBoxByCode(boxCode);
        if (box == null) {
            return this.boxNoFound();
        }

        BoxResponse response = this.toBoxResponse(box);

        if (StringHelper.isNotEmpty(box.getRouter())) {
            return response;
        }
        //箱子没有路由信息的话 查一遍
        //获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
        try {
            //  BaseResult<String> routInfoRes = this.basicSafInterfaceManager.getCrossDmsBox(box.getCreateSiteCode(), box.getReceiveSiteCode());
            CrossBoxResult<String[]> routInfoRes = crossBoxService.getBoxRouter(box.getCreateSiteCode(), box.getReceiveSiteCode(), box.getPredictSendTime(), box.getTransportType());
            this.log.info("BasicSaf getCrossDmsBox Routerinfo:{} ResultCode:{} Message:{}"
                    ,routInfoRes.getData(),routInfoRes.getResultCode(), routInfoRes.getMessage());

            if (CrossBoxResult.SUCCESS == routInfoRes.getResultCode() && routInfoRes.getData() != null && routInfoRes.getData().length == 2) {
                //没超过5个站点，用这个选择模板打印
                response.setRouterInfo(routInfoRes.getData()[0].split("\\-\\-"));
                response.setRouterFullId(routInfoRes.getData()[ 1].split("\\-\\-"));
                //超过5个站点，打印系统直接用他打印
                response.setRouterText(routInfoRes.getData()[0].replace("--", "-"));
            }
        } catch (Exception e) {
            this.log.error("获得站点路由信息失败： ", e);
        }
        this.buildBoxPrintInfo(box.getCreateSiteCode(), box.getReceiveSiteCode(), response);
        this.buildBoxExtraInfo(box, response);
        return response;
    }

    /**
	 * 根据boxCode得到box信息,供周转箱(笼车)发货使用
	 * @param request
	 * @return
	 */
	@POST
    @Path("/boxes/getBoxByBoxCode")
    @JProfiler(jKey = "DMS.WEB.BoxResource.getBoxByBoxCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse getBoxByBoxCode(BoxRequest request) {

		Assert.notNull(request.getBoxCode(), "BoxRequest's code must not be null");
        this.log.info("BoxRequest's {}", request);
        String boxCode = request.getBoxCode();
        Box box = this.boxService.findBoxByCode(boxCode);
        if (box == null) {
            return this.boxNoFound();
        }

        BoxResponse response = this.toBoxResponse(box);

        return response;
    }

    @POST
    @Path("/boxes/reprint")
    @JProfiler(jKey = "DMS.WEB.BoxResource.reprint", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse reprint(BoxRequest request) {
        Assert.notNull(request.getBoxCode(), "BoxRequest's code must not be null");
        this.log.info("BoxRequest's {}", request);
        this.boxService.reprint(this.toBox2(request));
        return this.ok();
    }

    @GET
    @Path("/boxes/validation")
    @JProfiler(jKey = "DMS.WEB.BoxResource.validation", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse validation(@QueryParam("boxCode") String boxCode, @QueryParam("operateType") Integer operateType) {
        Assert.notNull(boxCode, "boxCode must not be null");
        Assert.notNull(operateType, "operateType must not be null");

        this.log.info("boxCode's {}", boxCode);
        this.log.info("operateType's {}", operateType);
        Integer siteCodeType = 0;

        Box box = this.boxService.findBoxByCode(boxCode);
        if (Constants.OPERATE_TYPE_SORTING.equals(operateType) || Constants.OPERATE_TYPE_INSPECTION.equals(operateType)) {
            if (box == null) {
                return this.boxNoFound();
            } else if (Box.STATUS_DEFALUT.intValue() == box.getStatus().intValue()) {
                return this.boxNoPrint();
            }

            //判断箱子是否已发货，包含先从缓存中取 edited by hanjiaxing3 2018.10.28
//            SendM sendM = new SendM();
//            sendM.setBoxCode(boxCode);
//            sendM.setCreateSiteCode(box.getCreateSiteCode());
//            List<SendM> sendMList = sendMDao.findSendMByBoxCode(sendM);
//            if (!sendMList.isEmpty()) {// 如果箱子已经发货，则不能再使用了
            if (boxService.checkBoxIsSent(boxCode, box.getCreateSiteCode())) {
                return this.boxHasBeanSended();
            } else {
                try{
                BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(box.getReceiveSiteCode().toString());
                if (dto == null) {
                    log.info("boxes/validation :{} baseService.queryDmsBaseSiteByCode 获取目的地信息 NULL",box.getReceiveSiteCode().toString());
                    return this.paramSiteError();
                } else {
                    siteCodeType = dto != null && dto.getSiteType() != null ? dto.getSiteType() : null;
                    box.setSiteType(siteCodeType);
                    log.info("boxes = {} 目的地：{} siteType={}",boxCode,box.getReceiveSiteCode().toString(),siteCodeType);
                }

                return this.toBoxResponse(box);
                }
                catch (Exception ex){
                    log.error("validation:{}",boxCode, ex);
                }
            }
        }

        return this.paramError();
    }

    @POST
    @Path("/boxes")
    @JProfiler(jKey = "DMS.WEB.BoxResource.add", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Deprecated
    public BoxResponse add(BoxRequest request) {
        return boxService.commonGenBox(request, BoxSystemTypeEnum.PRINT_CLIENT.getCode(),false);
    }

    /**
     * 新版 打印客户端获取箱号
     * @param request
     * @return
     */
    @POST
    @Path("/printClient/boxes")
    @JProfiler(jKey = "DMS.WEB.BoxResource.printClientBoxes", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse printClientBoxes(BoxRequest request) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(request.getSubType())){
            final BoxSubTypeEnum boxSubTypeEnum = BoxSubTypeEnum.getFromCode(request.getSubType());
            if (boxSubTypeEnum == null) {
                return new BoxResponse(BoxResponse.CODE_BOX_NOT_FOUND, "箱号子类型错误");
            }
            request.setType(boxSubTypeEnum.getParentTypeCode());
            //校验箱号类型与目的地站点类型是否匹配
            BoxResponse boxResponse = checkBoxEndSiteMatch(request);
            if (!BoxResponse.CODE_OK.equals(boxResponse.getCode())) {
                return boxResponse;
            }
        }
        BoxResponse boxResponse = boxService.commonGenBox(request, BoxSystemTypeEnum.PRINT_CLIENT.getCode(), true);

        boxResponse.setReceiveSiteCode(request.getReceiveSiteCode());
        boxResponse.setCreateSiteCode(request.getCreateSiteCode());
        boxResponse.setMixBoxType(request.getMixBoxType());
        // 获取其他打印信息
        assemblyBoxResponseInfo(boxResponse);
        return boxResponse;
    }

    /**
     * 打印对象
     * @param boxResponse
     */
    public void assemblyBoxResponseInfo(BoxResponse boxResponse) {
        try {
            CollectBoxFlowDirectionConf flowConf = getCollectBoxFlowDirectionConf(boxResponse.getCreateSiteCode(), boxResponse.getReceiveSiteCode());
            // 始发地处理 去除接货仓 分拣中心字样
            boxResponse.setCreateSiteName(getStartName(boxResponse.getCreateSiteCode()));
            // 处理目的地字段
            BaseStaffSiteOrgDto receiveSiteInfo = baseMajorManager.getBaseSiteBySiteId(boxResponse.getReceiveSiteCode());
            boxResponse.setReceiveSiteName(getEndSiteName(flowConf, receiveSiteInfo));

            // 获取集包要求
            if (flowConf != null && flowConf.getCollectClaim() != null) {
                boxResponse.setCollectClaimDesc(CollectClaimEnum.getName(flowConf.getCollectClaim()));
                boxResponse.setCollectClaim(flowConf.getCollectClaim());
                boxResponse.setMixBoxTypeText(CollectClaimEnum.getName(flowConf.getCollectClaim()));
            }
            boxResponse.setCreateTime(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));

            // 滑道笼车信息
            setCrossCodeTabletrolleyCode(boxResponse);

            // 路由字段去除 分拣中心 接货仓 营业部字样
            String[] routers = boxResponse.getRouterInfo();
            if (routers != null) {
                for (int i = 0; i < routers.length; i++) {
                    routers[i] = getReplaceName(routers[i]);
                }
            }
            // 多流向路由处理
            if (!StringUtils.isEmpty(boxResponse.getRouterText())) {
                boxResponse.setRouterText(getReplaceName(boxResponse.getRouterText()));
            }
        }catch (Exception e) {
            log.error("箱号打印获取免单信息异常：{}", JsonHelper.toJson(boxResponse), e);
        }
    }

    /**
     * 查询滑道笼车信息
     * @param boxResponse
     */
    private void setCrossCodeTabletrolleyCode(BoxResponse boxResponse) {
        if (StringUtils.isEmpty(boxResponse.getDestinationCrossCode()) && StringUtils.isEmpty(boxResponse.getDestinationTabletrolleyCode())) {
            SortCrossDetail sortCrossDetail = basicPrimaryWS
                    .getCrossCodeDetailByDmsID(boxResponse.getCreateSiteCode(), String.valueOf(boxResponse.getReceiveSiteCode()));
            if (sortCrossDetail != null) {
                boxResponse.setDestinationCrossCode(sortCrossDetail.getCrossCode());
                boxResponse.setDestinationTabletrolleyCode(sortCrossDetail.getTabletrolleyCode());
            }
        }
    }

    private String getStartName(Integer createSiteCode) {
        BaseStaffSiteOrgDto createSiteInfo = baseMajorManager.getBaseSiteBySiteId(createSiteCode);
        if (createSiteInfo != null && !StringUtils.isEmpty(createSiteInfo.getSiteName())) {
            return createSiteInfo.getSiteName().replace(RWMS_TYPE.getName(), "").replace(DMS_TYPE.getName(), "");
        }
        return "";
    }

    /**
     * 处理目的地字段
     *
     * @param flowConf
     * @param receiveSiteInfo
     */
    private String getEndSiteName(CollectBoxFlowDirectionConf flowConf, BaseStaffSiteOrgDto receiveSiteInfo) {
        // 目的地处理  营业部去除营业部字段；逆向打印全称；干、传、摆取集包规则-包牌名称
        if (receiveSiteInfo != null) {
            String receiveSiteName = receiveSiteInfo.getSiteName();
            // 如果是营业部
            if (BusinessHelper.isSiteType(receiveSiteInfo.getSiteType())) {
                receiveSiteName = receiveSiteName.replace(TERMINAL_SITE.getName(), "");
            } else if (!isReverseSite(receiveSiteInfo.getSiteType())) {
                // 获取包牌名称
                if (flowConf != null && !StringUtils.isEmpty(flowConf.getBoxPkgName())) {
                    receiveSiteName = flowConf.getBoxPkgName();
                }
            }
            return receiveSiteName;
        }
        return "";
    }

    private static String getReplaceName(String router) {
        if (StringUtils.isEmpty(router)) {
            return "";
        }
        return router.replace(TERMINAL_SITE.getName(), "")
                .replace(RWMS_TYPE.getName(), "")
                .replace(DMS_TYPE.getName(), "");
    }

    /**
     * 获取一条集包规则
     * @param startSiteCode
     * @param endSiteCode
     * @return
     */
    @CacheMethod(key="BoxResource.getCollectBoxFlowDirectionConf-{0}-{1}", cacheBean="redisCache", nullTimeout = 1000 * 60 * 10, timeout = 1000 * 60 * 5)
    private CollectBoxFlowDirectionConf getCollectBoxFlowDirectionConf(Integer startSiteCode, Integer endSiteCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.BoxResource.getCollectBoxFlowDirectionConf", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            CollectBoxFlowDirectionConf con = assembleCollectBoxFlowDirectionConf(startSiteCode, endSiteCode);
            List<CollectBoxFlowDirectionConf> flowDirectionConfList = boxLimitConfigManager.listCollectBoxFlowDirection(con, null);
            // 获取包牌名称
            if (CollectionUtils.isNotEmpty(flowDirectionConfList)) {
                return flowDirectionConfList.get(0);
            }
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    /**
     * 组装获取集包规则参数
     * @param startSiteId
     * @param endSiteId
     * @return
     */
    private CollectBoxFlowDirectionConf assembleCollectBoxFlowDirectionConf(Integer startSiteId, Integer endSiteId) {
        CollectBoxFlowDirectionConf conf = new CollectBoxFlowDirectionConf();
        conf.setStartSiteId(startSiteId);
        conf.setBoxReceiveId(endSiteId);
        conf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        return conf;
    }

    private BoxResponse checkBoxEndSiteMatch(BoxRequest request) {
        if (!sysConfigService.getByListContainOrAllConfig(CHECK_BOX_END_SITE_MATCH_SWITCH, String.valueOf(request.getCreateSiteCode()))) {
            return new BoxResponse(BoxResponse.CODE_OK, BoxResponse.MESSAGE_OK);
        }
        if (Objects.isNull(request.getReceiveSiteCode())) {
            return new BoxResponse(BoxResponse.CODE_NO_BOX_END_SITE, MESSAGE_NO_BOX_END_SITE);
        }
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(request.getReceiveSiteCode());
        if (siteInfo == null) {
            return new BoxResponse(BoxResponse.CODE_NO_BOX_END_SITE, MESSAGE_NO_BOX_END_SITE);
        }
        BoxTypeV2Enum boxType = BoxTypeV2Enum.getFromCode(request.getType());
        // TC 开头的箱号，目的地只能是 备件库、仓储、退货组、逆向仓、售后仓
        if (BoxTypeV2Enum.TYPE_TC.equals(boxType)
                && !(isReverseSite(siteInfo.getSiteType())
                || Objects.equals(RETURN_CENTER.getFirstTypesOfThird(), siteInfo.getSortType()))) {
            return new BoxResponse(BoxResponse.CODE_TC_BOX_END_SITE_TYPE_NOT_MATCH, MESSAGE_TC_BOX_END_SITE_TYPE_NOT_MATCH);
        }
        // BX 开头的箱号，目的地只能是 三方配送公司
        if (BoxTypeV2Enum.TYPE_BX.equals(boxType)
                && !bxBoxEndSiteTypeCheck(siteInfo)) {
            return new BoxResponse(BoxResponse.CODE_BX_BOX_END_SITE_TYPE_NOT_MATCH, MESSAGE_BX_BOX_END_SITE_TYPE_NOT_MATCH);
        }
        return new BoxResponse(BoxResponse.CODE_OK, BoxResponse.MESSAGE_OK);
    }

    /**
     * 构建目的地打印属性
     *
     * @param createSiteCode
     * @param receiveSiteCode
     * @param response
     */
    private void buildBoxPrintInfo(Integer createSiteCode, Integer receiveSiteCode, BoxResponse response) {
        CrossPackageTagNew crossPackageTag = baseMinorManager.queryNonDmsSiteCrossPackageTagForPrint(receiveSiteCode, createSiteCode);
        if (crossPackageTag != null) {
            response.setDestinationCrossCode(crossPackageTag.getDestinationCrossCode());
            response.setDestinationTabletrolleyCode(crossPackageTag.getDestinationTabletrolleyCode());
        }
    }

    /**
     * 构建箱子额外信息
     *
     * @param box
     * @param response
     */
    private void buildBoxExtraInfo(Box box, BoxResponse response) {
        try {
            BaseStaffSiteOrgDto createSite = baseService.getSiteBySiteID(box.getCreateSiteCode());
            BaseStaffSiteOrgDto receiveSite = baseService.getSiteBySiteID(box.getReceiveSiteCode());
            response.setCreateSiteType(createSite==null?null:String.valueOf(createSite.getSiteType()));
            response.setReceiveSiteType(receiveSite==null?null:String.valueOf(receiveSite.getSiteType()));
            if(createSite != null
                    && Constants.THIRD_ENET_SITE_TYPE.equals(createSite.getSiteType())){
                response.setWhetherZyBoxCode(true);
            }
        }catch (Exception e){
            log.error("获取箱号:【{}】的始发目站点类型异常,异常信息:【{}】",box.getCode(),e.getMessage(),e);
        }
    }

    /**
     * 存储分组箱号
     * @param list
     * @return
     */
    @POST
    @Path("/groupBoxes/batchAdd")
    @JProfiler(jKey = "DMS.WEB.BoxResource.batch", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void batch(List<BoxRequest> list) {

        List<Box> groupList = new ArrayList<Box>();
        String flage = UUID.randomUUID().toString().replace("-", "");
        for(BoxRequest boxRequest : list){
            Box groupBox = new Box();
            groupBox.setGroupName(boxRequest.getGroupName());
            groupBox.setGroupSendCode(flage);
            groupBox.setCode(boxRequest.getBoxCode());
            groupList.add(groupBox);
        }
        groupBoxService.batchAdd(groupList);
    }

    /**
     * 根据箱号获取分组下所有箱号
     * @param boxCode
     * @return
     */
    @GET
    @Path("/groupBoxes/getAllGroupBoxes/{boxCode}")
    @JProfiler(jKey = "DMS.WEB.BoxResource.getAllGroupBoxes", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<String>> getAllGroupBoxes(@PathParam("boxCode") String boxCode) {

        this.log.info("boxCode is {}", boxCode);
        InvokeResult<List<String>> result = new InvokeResult<List<String>>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<String> boxCodeList = new ArrayList<String>();
        List<Box> boxList = groupBoxService.getAllBoxByBoxCode(boxCode);
        for(Box box : boxList){
            boxCodeList.add(box.getCode());
        }
        result.setData(boxCodeList);
        return result;
    }


    /**
     * 为自动分拣机生成箱号
     *
     * @param request
     * @return
     */
    @POST
    @Path("/boxes/create")
    @JProfiler(jKey = "DMS.WEB.BoxResource.create", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Deprecated
    public com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> create(BoxRequest request) {
        return create(request,BoxSystemTypeEnum.AUTO_SORTING_MACHINE.getCode(),false);
    }

    /**
     * 为自动分拣机生成箱号 新版 // todo
     *
     * @param request
     * @return
     */
    @POST
    @Path("/autoSorting/boxes")
    @JProfiler(jKey = "DMS.WEB.BoxResource.autoSortingBoxes", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> autoSortingBoxes(BoxRequest request) {
        return create(request,BoxSystemTypeEnum.AUTO_SORTING_MACHINE.getCode(),true);
    }

    /**
     * 为自动分拣机生成箱号
     *
     * @param request
     * @return
     */
    @POST
    @Path("/autoSorting/create")
    @JProfiler(jKey = "DMS.WEB.BoxResource.autoSortingCreate", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> autoSortingCreate(BoxRequest request) {
        com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> result =  create(request,BoxSystemTypeEnum.AUTO_SORTING_MACHINE.getCode(),true);
        //计算滑道号和笼车号
        if(RESULT_SUCCESS_CODE == result.getCode() && result.getData() != null){
            boxService.computeRouter(result.getData().getRouterInfo());
            // 组装打印参数
            assemblyAutoSortingBoxResult(result.getData(), request);
        }
        return result;
    }

    /**
     * 自动化箱号打印参数组装
     * @param autoSortingBoxResult
     * @param request
     */
    private void assemblyAutoSortingBoxResult(AutoSortingBoxResult autoSortingBoxResult, BoxRequest request) {
        try {
            CollectBoxFlowDirectionConf flowConf = getCollectBoxFlowDirectionConf(request.getCreateSiteCode(), request.getReceiveSiteCode());
            // 始发地处理 去除接货仓 分拣中心字样
            autoSortingBoxResult.setCreateSiteName(getStartName(request.getCreateSiteCode()));

            // 目的地处理  营业部去除营业部字段；逆向打印全称；干、传、摆取集包规则-包牌名称
            BaseStaffSiteOrgDto receiveSiteInfo = baseMajorManager.getBaseSiteBySiteId(request.getReceiveSiteCode());
            autoSortingBoxResult.setReceiveSiteName(getEndSiteName(flowConf, receiveSiteInfo));

            // 滑道笼车信息
            setCrossCodeTabletrolleyCode(autoSortingBoxResult, receiveSiteInfo, request);

            // 集包要求集包规则-集包要求
            if (flowConf != null && flowConf.getCollectClaim() != null) {
                autoSortingBoxResult.setMixBoxTypeText(CollectClaimEnum.getName(flowConf.getCollectClaim()));
            }
            autoSortingBoxResult.setCreateTime(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));

            // 路由字段去除 分拣中心 接货仓 营业部字样
            List<Map.Entry<Integer,String>> routers = autoSortingBoxResult.getRouterInfo();
            if (CollectionUtils.isNotEmpty(routers)) {
                for (Map.Entry<Integer, String> router : routers) {
                    router.setValue(getReplaceName(router.getValue()));
                }
            }
        }catch (Exception e) {
            log.error("自动化箱号打印获取免单信息异常：{}", JsonHelper.toJson(autoSortingBoxResult), e);
        }
    }

    /**
     * 查询滑道笼车信息
     *
     * @param autoSortingBoxResult
     * @param receiveSiteInfo
     * @param request
     */
    private void setCrossCodeTabletrolleyCode(AutoSortingBoxResult autoSortingBoxResult, BaseStaffSiteOrgDto receiveSiteInfo, BoxRequest request) {
        if (receiveSiteInfo == null) {
            return;
        }
        if (!DMS_TYPE.getFirstTypesOfThird().equals(receiveSiteInfo.getSortType())) {
            CrossPackageTagNew crossPackageTag = baseMinorManager
                    .queryNonDmsSiteCrossPackageTagForPrint(request.getCreateSiteCode(), request.getReceiveSiteCode());
            if (crossPackageTag != null) {
                autoSortingBoxResult.setDestinationCrossCode(crossPackageTag.getDestinationCrossCode());
                autoSortingBoxResult.setDestinationTabletrolleyCode(crossPackageTag.getDestinationTabletrolleyCode());
            }
        }else {
            SortCrossDetail sortCrossDetail = basicPrimaryWS.getCrossCodeDetailByDmsID(request.getCreateSiteCode(), String.valueOf(request.getReceiveSiteCode()));
            if (sortCrossDetail != null) {
                autoSortingBoxResult.setDestinationCrossCode(sortCrossDetail.getCrossCode());
                autoSortingBoxResult.setDestinationTabletrolleyCode(sortCrossDetail.getTabletrolleyCode());
            }
        }
    }

    private com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> create(BoxRequest request, String systemType,boolean isNew) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getType(), "request type must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        Assert.notNull(request.getQuantity(), "request quantity must not be null");
        this.log.info("BoxRequest's {}", request.toString());
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.boxResource.boxes.create",
                Constants.UMP_APP_NAME_DMSWEB, false, true);
        //排除非法箱号类型
        if(boxTypeCheckSwitchOn() && request.getType().length() > BOX_TYPE_LENGTH){
            com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> result
                    = new com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult>();
            result.customMessage(600,"箱号类型不合法!");
            return result;
        }

        if(dmsConfigManager.getPropertyConfig().getBoxTypeNewVersionSwitch()){
            if(StringUtils.isEmpty(request.getSubType())){
                request.setSubType(BoxSubTypeEnum.PARENT_ASSOCIATE_NORMAL_SBU_TYPE_MAP.get(request.getType()));
            }
        }

        List<Box> availableBoxes;
        if(isNew){
            availableBoxes = this.boxService.batchAddNew(this.toBox(request),systemType);
        }else {
            availableBoxes = this.boxService.batchAdd(this.toBox(request));
        }

        com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> result=new com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult>();
        AutoSortingBoxResult boxResult=new AutoSortingBoxResult();
        List<String> boxs=new ArrayList<String>(availableBoxes.size());
        for (Box item:availableBoxes){
            boxs.add(item.getCode());
        }
        boxResult.setBoxCode(boxs);
        result.setData(boxResult);

        if (request.getTransportType() != null && request.getTransportType() == 2) {//只有公路运输的支持路由信息查询2014.3.10
            //获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
            try {
                //BaseResult<CrossDmsBox> resData = this.basicSafInterfaceManager.getCrossDmsBoxByOriAndDes(request.getCreateSiteCode(), request.getReceiveSiteCode());
                CrossBoxResult<CrossBox> resData = crossBoxService.getCrossDmsBoxByOriAndDes(request.getCreateSiteCode(), request.getReceiveSiteCode());
                if (log.isInfoEnabled()) {
                    this.log.info("调用跨箱号中转获取箱号路由:{}", JsonHelper.toJson(resData));
                }
                List<Map.Entry<Integer, String>> router = new ArrayList<Map.Entry<Integer, String>>();
                if (null != resData.getData()) {
                    router.add(new AbstractMap.SimpleEntry<Integer, String>(resData.getData().getOriginalDmsId(), resData.getData().getOriginalDmsName()));
                    if (null != resData.getData().getTransferOneId()) {
                        router.add(new AbstractMap.SimpleEntry<Integer, String>(resData.getData().getTransferOneId(), resData.getData().getTransferOneName()));
                    }
                    if (null != resData.getData().getTransferTwoId()) {
                        router.add(new AbstractMap.SimpleEntry<Integer, String>(resData.getData().getTransferTwoId(), resData.getData().getTransferTwoName()));
                    }
                    if (null != resData.getData().getTransferThreeId()) {
                        router.add(new AbstractMap.SimpleEntry<Integer, String>(resData.getData().getTransferThreeId(), resData.getData().getTransferThreeName()));
                    }
                    router.add(new AbstractMap.SimpleEntry<Integer, String>(resData.getData().getDestinationDmsId(), resData.getData().getDestinationDmsName()));
                } else {
                    router.add(new AbstractMap.SimpleEntry<Integer, String>(request.getCreateSiteCode(), request.getCreateSiteName()));
                    router.add(new AbstractMap.SimpleEntry<Integer, String>(request.getReceiveSiteCode(), request.getReceiveSiteName()));
                }
                boxResult.setRouterInfo(router);
            } catch (Exception e) {
                this.log.error("获得站点路由信息失败： ", e);
                Profiler.functionError(callerInfo);
            }
        }
        Profiler.registerInfoEnd(callerInfo);
        return result;
    }

    @POST
    @Path("/boxes/getRouterInfo")
    @JProfiler(jKey = "DMS.WEB.BoxResource.getRouterInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse getRouterInfo(BoxRequest request) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        this.log.info("getRouterInfo BoxRequest's {}", request);

        BoxResponse response = this.ok();
        if (request.getTransportType() != null && request.getTransportType() == 2) {//只有公路运输的支持路由信息查询2014.3.10
            //获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
            try {
                CrossBoxResult<String> routInfoRes = crossBoxService.getCrossDmsBox(request.getCreateSiteCode(), request.getReceiveSiteCode());
               // BaseResult<String> routInfoRes = basicSafInterfaceManager.getCrossDmsBox(request.getCreateSiteCode(),request.getReceiveSiteCode());
                this.log.info("BasicSaf getCrossDmsBox Routerinfo:{} ResultCode:{} Message:{}"
                        ,routInfoRes.getData(), routInfoRes.getResultCode(), routInfoRes.getMessage());

                if (StringHelper.isNotEmpty(routInfoRes.getData())) {
                    response.setRouterInfo(routInfoRes.getData().split("\\-\\-"));
                }

            } catch (Exception e) {
                this.log.error("获得站点路由信息失败： ", e);
            }
        }

        return response;
    }


    /**
     * 向box表中写入数据
     * @param request
     * @return
     */
    @POST
    @Path("/addBox")
    @JProfiler(jKey = "DMS.WEB.BoxResource.addBox", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse addBox(BoxRequest request) {
        Box box = new Box();
        box.setCode(request.getBoxCode());
        box.setType(request.getType());
        box.setCreateSiteCode(request.getCreateSiteCode());
        box.setCreateSiteName(request.getCreateSiteName());
        box.setReceiveSiteName(request.getReceiveSiteName());
        box.setReceiveSiteCode(request.getReceiveSiteCode());
        box.setMixBoxType(request.getMixBoxType());

        Integer result = this.boxService.add(box);
        BoxResponse response = this.ok();
        response.setBoxCodes(request.getBoxCode());
        response.setMessage("result:"+result);
        return response;
    }

    private BoxResponse toBoxResponse(Box box) {
        BoxResponse response = this.ok();
        response.setSiteType(box.getSiteType());
        response.setBoxCode(box.getCode());
        response.setCreateSiteCode(box.getCreateSiteCode());
        response.setCreateSiteName(box.getCreateSiteName());
        response.setReceiveSiteCode(box.getReceiveSiteCode());
        response.setReceiveSiteName(box.getReceiveSiteName());
        response.setType(box.getType());
        response.setTransportType(box.getTransportType());
        response.setPredictSendTime(box.getPredictSendTime());
        if (StringHelper.isNotEmpty(box.getRouterName())){
            response.setRouterInfo(box.getRouterName().split("\\-\\-"));
            response.setRouterText(box.getRouterName().replace("--","-"));
        }
        response.setMixBoxType(box.getMixBoxType());
        response.setPackageNum(box.getPackageNum());
        if(!StringUtils.isEmpty(box.getMaterialCode())){
            response.setMaterialCode(box.getMaterialCode());
        }
        response.setBoxSubTypes(BoxSubTypeEnum.ENUM_MAP);
        if (!StringUtils.isEmpty(box.getBoxSubType())) {
            response.setSubType(box.getBoxSubType());
        }
        return response;
    }

    private Box toBox(BoxRequest request) {
        Box box = new Box();
        box.setType(request.getType());
        box.setQuantity(request.getQuantity());
        box.setCreateSiteCode(request.getCreateSiteCode());
        box.setCreateSiteName(request.getCreateSiteName());
        box.setReceiveSiteCode(request.getReceiveSiteCode());
        box.setReceiveSiteName(request.getReceiveSiteName());
        box.setCreateUser(request.getUserName());
        box.setCreateUserCode(request.getUserCode());
        box.setTransportType(request.getTransportType());
        box.setMixBoxType(request.getMixBoxType());
        box.setPredictSendTime(request.getPredictSendTime());
        //临时占用字段处理站点商家重复
        box.setStatuses(request.getCreateSiteType());
        box.setUpdateUser(request.getReceiveSiteType());
        //设置状态和当前节点
        box.setStatus(BoxStatusEnum.OPEN.getStatus());
        box.setLastNodeType(OpBoxNodeEnum.PRINTBOXCODE.getNodeCode());
        return box;
    }

    private Box toBox2(BoxRequest request) {
        Box box = new Box();
        box.setCode(request.getBoxCode());
        box.setUpdateUser(request.getUserName());
        box.setUpdateUserCode(request.getUserCode());
        return box;
    }

    private BoxResponse boxNoFound() {
        return new BoxResponse(BoxResponse.CODE_BOX_NOT_FOUND, HintService.getHint(HintCodeConstants.BOX_NOT_EXIST));
    }

    private BoxResponse boxNoBingDing(){
        return  new BoxResponse(BoxResponse.CODE_BC_BOX_NO_BINDING, HintService.getHint(HintCodeConstants.BOX_UNBIND_RECYCLE_BAG));
    }


    private BoxResponse boxHasBeanSended() {
        return new BoxResponse(BoxResponse.CODE_BOX_SENDED, HintService.getHint(HintCodeConstants.BOX_SENT));
    }

    private BoxResponse boxNoPrint() {
        return new BoxResponse(BoxResponse.CODE_BOX_NO_PRINT, HintService.getHint(HintCodeConstants.BOX_NO_USE));
    }

    private BoxResponse paramError() {
        return new BoxResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
    }

    private BoxResponse ok() {
        return new BoxResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    private BoxResponse paramSiteError() {
        return new BoxResponse(BoxResponse.CODE_SITE_SENDED, BoxResponse.MESSAGE_SITE_SENDED);
    }

    @GET
    @Path("/boxes/cache/{boxCode}")
    @JProfiler(jKey = "DMS.WEB.BoxResource.getboxCodeCache", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse getboxCodeCache(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.log.info("box code's {}", boxCode);

        Box box = this.boxService.findBoxCacheByCode(boxCode);
        if (box == null) {
            return this.boxNoFound();
        }

        BoxResponse response = this.toBoxResponse(box);

        return response;
    }

    @GET
    @Path("/boxes/delcache/{boxCode}")
    @JProfiler(jKey = "DMS.WEB.BoxResource.delboxCodeCache", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Long delboxCodeCache(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.log.info("box code's {}", boxCode);

        Long resulte = this.boxService.delboxCodeCache(boxCode);

        return resulte;
    }

    @GET
    @Path("/boxes/availability/{boxCode}")
    @JProfiler(jKey = "DMS.WEB.BoxResource.statusValidation", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> statusValidation(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.log.info("boxCode's {}", boxCode);

        InvokeResult<Boolean> invokeResult = new InvokeResult<Boolean>();
        invokeResult.success();
        Box box = this.boxService.findBoxByCode(boxCode);
        if (box != null) {
            if (boxService.checkBoxIsSent(boxCode, box.getCreateSiteCode())) {
                invokeResult.setData(false);
            } else {
                invokeResult.setData(true);
            }
        } else {
            invokeResult.customMessage(BoxResponse.CODE_BOX_NOT_FOUND, BoxResponse.MESSAGE_BOX_NOT_FOUND);
        }

        return invokeResult;
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

    /**
     * 登录人ERP所属部门类型为 【营业部】或【自营京东派】 类型
     * 箱子类型返回部分类型【文件，航空件，同城混包，快递混包，其他混包，退货普通，售后件】
     * 其余登录人返回全部类型
     * @param request
     * @return
     */
    @POST
    @Path("/boxes/getBoxType")
    public BoxResponse getBoxType(BoxRequest request) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getOperateUserErp(), "request receiveSiteCode must not be null");
        BoxResponse response = this.ok();

        //获取登录人信息
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(request.getOperateUserErp());
        if (null == baseStaffSiteOrgDto){
            log.warn("获取登录人信息失败，erp: " + request.getOperateUserErp());
            return null;
        }
        //营业部,自营京东派 人员使用部分箱型
        if (siteTypes.contains(baseStaffSiteOrgDto.getSubType())){
            response.setBoxTypes(siteBoxTypeMap);
            if(dmsConfigManager.getPropertyConfig().getBoxTypeNewVersionSwitch()){
                Map<String, String> boxSubTypeShowMap = this.getBoxSubTypeShowMap(siteBoxSubTypeMap);
                response.setBoxSubTypes(boxSubTypeShowMap);
            }
            return response;
        }
        //分拣中心
        response.setBoxTypes(sortingBoxTypeMap);
        if(dmsConfigManager.getPropertyConfig().getBoxTypeNewVersionSwitch()){
            Map<String, String> boxSubTypeShowMap = this.getBoxSubTypeShowMap(sortingBoxSubTypeMap);
            response.setBoxSubTypes(boxSubTypeShowMap);
        }
        return response;
    }

    private Map<String, String> getBoxSubTypeShowMap(Map<String, String> siteBoxTypeMap) {
        Map<String, String> boxSubTypeShowMap = new LinkedHashMap<>();
        for (String code : siteBoxTypeMap.keySet()) {
            final BoxSubTypeEnum boxSubTypeEnum = BoxSubTypeEnum.getFromCode(code);
            if (boxSubTypeEnum == null) {
                continue;
            }
            boxSubTypeShowMap.put(code, String.format("%s-%s", boxSubTypeEnum.getName(), boxSubTypeEnum.getParentTypeCode()));
        }
        return boxSubTypeShowMap;
    }

    /**
     * 校验箱号信息-以及绑定循环集包袋情况
     * @param boxCode
     * @param operateType
     * @param siteCode 当前登录人归属站点
     * @return
     */
    @GET
    @Path("/boxes/validationAndCheck")
    @JProfiler(jKey = "DMS.WEB.BoxResource.validationAndCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse validationAndCheck(@QueryParam("boxCode") String boxCode, @QueryParam("operateType") Integer operateType,@QueryParam("siteCode")Integer siteCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        Assert.notNull(operateType, "operateType must not be null");
        Assert.notNull(siteCode, "siteCode must not be null");

        this.log.info("boxCode's {}", boxCode);
        this.log.info("operateType's {}", operateType);
        this.log.info("siteCode's {}", siteCode);

        // 始发地类型
        Integer siteCodeType = 0;
        Box box = this.boxService.findBoxByCode(boxCode);
        if (Constants.OPERATE_TYPE_SORTING.equals(operateType) || Constants.OPERATE_TYPE_INSPECTION.equals(operateType)) {
            if (box == null) {
                return this.boxNoFound();
            } else if (Box.STATUS_DEFALUT.intValue() == box.getStatus().intValue()) {
                return this.boxNoPrint();
            }

            //判断箱子是否已发货
            if (boxService.checkBoxIsSent(boxCode, box.getCreateSiteCode())) {
                return this.boxHasBeanSended();
            } else {
                try{
                    BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(box.getReceiveSiteCode().toString());
                    if (dto == null) {
                        log.info("boxes/validation :{} baseService.queryDmsBaseSiteByCode 获取目的地信息 NULL",box.getReceiveSiteCode().toString());
                        return this.paramSiteError();
                    } else {
                        siteCodeType = dto != null && dto.getSiteType() != null ? dto.getSiteType() : null;
                        box.setSiteType(siteCodeType);
                        log.info("boxes = {} 目的地：{} siteType={}",boxCode,box.getReceiveSiteCode().toString(),siteCodeType);
                    }

                    // 获取循环集包袋绑定信息
                    String materialCode =  cycleBoxService.getBoxMaterialRelation(boxCode);
                    // 决定是否绑定循环集包袋
                    if(!checkHaveBinDing(materialCode,box.getType(),siteCode)){
                        return this.boxNoBingDing();
                    }
                    box.setMaterialCode(materialCode);
                    return this.toBoxResponse(box);
                }
                catch (Exception ex){
                    log.error("validation:{}",boxCode, ex);
                }
            }
        }
        return this.paramError();
    }

    /**
     * true 绑定了  false 未绑定
     * @param materialCode
     * @param boxType
     * @param siteCode
     * @return
     */
    private boolean checkHaveBinDing(String materialCode,String boxType,Integer siteCode){
        // 不是BC类型的不拦截
        if(!BusinessHelper.isBCBoxType(boxType)){
            return true;
        }

        // 开关关闭不拦截
        if(!funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(),siteCode)){
            return  true;
        }

        //有集包袋不拦截
        if(!StringUtils.isEmpty(materialCode)){
            return true;
        }
        return false;
    }


    /**
     * 获取箱号信息
     * 支持按箱号 包裹号查询
     * @param barCode
     * @return
     */
    @GET
    @Path("/boxes/getByBarcode/{barCode}")
    @JProfiler(jKey = "DMS.WEB.BoxResource.getByBarCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoxResponse getByBarCode(@PathParam("barCode") String barCode) {
        if (StringUtils.isEmpty(barCode) && !WaybillUtil.isPackageCode(barCode) && !BusinessUtil.isBoxcode(barCode)) {
            log.info("只支持按包裹号和箱号操作补打{}",barCode);
            return new BoxResponse(JdResponse.CODE_PARAM_ERROR, "只支持按包裹号和箱号操作补打!");
        }
        if (WaybillUtil.isPackageCode(barCode)) {
            // 根据包裹号获取箱号
            SortingDto sortingDto = sortingService.getLastSortingInfoByPackageCode(barCode);
            if (sortingDto == null || StringUtils.isEmpty(sortingDto.getBoxCode())) {
                log.info("未获取到包裹号绑定的箱号信息{}",barCode);
                return new BoxResponse(JdResponse.CODE_PARAM_ERROR, "未获取到包裹号绑定的箱号信息！");
            }
            barCode = sortingDto.getBoxCode();
        }
        BoxResponse boxResponse = this.get(barCode);
        if (null != boxResponse) {
            boxResponse.setBoxTypes(BoxTypeEnum.getMap());
            boxResponse.setBoxSubTypes(BoxSubTypeEnum.ENUM_MAP);
        }
        // 获取其他打印信息
        assemblyBoxResponseInfo(boxResponse);
        return boxResponse;
    }

}

package com.jd.bluedragon.distribution.rest.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.AutoSortingBoxResult;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
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
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;


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

    @Autowired
    private CycleBoxService cycleBoxService;

    @Autowired
    private FuncSwitchConfigServiceImpl funcSwitchConfigService;

    @GET
    @Path("/boxes/{boxCode}")
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
    public BoxResponse reprint(BoxRequest request) {
        Assert.notNull(request.getBoxCode(), "BoxRequest's code must not be null");
        this.log.info("BoxRequest's {}", request);
        this.boxService.reprint(this.toBox2(request));
        return this.ok();
    }

    @GET
    @Path("/boxes/validation")
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
    public BoxResponse printClientBoxes(BoxRequest request) {
        return boxService.commonGenBox(request, BoxSystemTypeEnum.PRINT_CLIENT.getCode(),true);
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
    @Deprecated
    public com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> create(BoxRequest request) {
        return create(request,BoxSystemTypeEnum.AUTO_SORTING_MACHINE.getCode(),false);
    }

    /**
     * 为自动分拣机生成箱号 新版
     *
     * @param request
     * @return
     */
    @POST
    @Path("/autoSorting/boxes")
    public com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> autoSortingBoxes(BoxRequest request) {
        return create(request,BoxSystemTypeEnum.AUTO_SORTING_MACHINE.getCode(),true);
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
        return new BoxResponse(BoxResponse.CODE_BOX_NOT_FOUND, BoxResponse.MESSAGE_BOX_NOT_FOUND);
    }

    private BoxResponse boxNoBingDing(){
        return  new BoxResponse(BoxResponse.CODE_BC_BOX_NO_BINDING,BoxResponse.MESSAGE_BC_NO_BINDING);
    }


    private BoxResponse boxHasBeanSended() {
        return new BoxResponse(BoxResponse.CODE_BOX_SENDED, BoxResponse.MESSAGE_BOX_SENDED);
    }

    private BoxResponse boxNoPrint() {
        return new BoxResponse(BoxResponse.CODE_BOX_NO_PRINT, BoxResponse.MESSAGE_BOX_NO_PRINT);
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
    public Long delboxCodeCache(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.log.info("box code's {}", boxCode);

        Long resulte = this.boxService.delboxCodeCache(boxCode);

        return resulte;
    }

    @GET
    @Path("/boxes/availability/{boxCode}")
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
            return response;
        }
        //分拣中心
        response.setBoxTypes(sortingBoxTypeMap);
        return response;
    }

    @GET
    @Path("/boxes/validationAndCheck")
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

}

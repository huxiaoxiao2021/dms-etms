package com.jd.bluedragon.distribution.rest.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.AutoSortingBoxResult;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.box.service.GroupBoxService;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BoxResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * 箱号类型字符长度，默认2
     * */
    private static final int BOX_TYPE_LENGTH = 2;

    @Autowired
    private BoxService boxService;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CrossBoxService crossBoxService;

    @Autowired
    private GroupBoxService groupBoxService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @GET
    @Path("/boxes/{boxCode}")
    public BoxResponse get(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.logger.info("box code's " + boxCode);

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
            this.logger.info("BasicSaf getCrossDmsBox Routerinfo:" + routInfoRes.getData() + " ResultCode:" + routInfoRes.getResultCode() + " Message:" + routInfoRes.getMessage());

            if (CrossBoxResult.SUCCESS == routInfoRes.getResultCode() && routInfoRes.getData() != null && routInfoRes.getData().length == 2) {
                //没超过5个站点，用这个选择模板打印
                response.setRouterInfo(routInfoRes.getData()[0].split("\\-\\-"));
                //超过5个站点，打印系统直接用他打印
                response.setRouterText(routInfoRes.getData()[0].replace("--", "-"));
            }
        } catch (Exception e) {
            this.logger.error("获得站点路由信息失败： ", e);
        }
        this.buildBoxPrintInfo(box.getCreateSiteCode(), box.getReceiveSiteCode(), response);
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
        this.logger.info("BoxRequest's " + request);
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
        this.logger.info("BoxRequest's " + request);
        this.boxService.reprint(this.toBox2(request));
        return this.ok();
    }

    @GET
    @Path("/boxes/validation")
    public BoxResponse validation(@QueryParam("boxCode") String boxCode, @QueryParam("operateType") Integer operateType) {
        Assert.notNull(boxCode, "boxCode must not be null");
        Assert.notNull(operateType, "operateType must not be null");

        this.logger.info("boxCode's " + boxCode);
        this.logger.info("operateType's " + operateType);
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
                    logger.info("boxes/validation :"+box.getReceiveSiteCode().toString() +" baseService.queryDmsBaseSiteByCode 获取目的地信息 NULL");
                    return this.paramSiteError();
                } else {
                    siteCodeType = dto != null && dto.getSiteType() != null ? dto.getSiteType() : null;
                    box.setSiteType(siteCodeType);
                    logger.info("boxes = "+boxCode+ " 目的地："+box.getReceiveSiteCode().toString() +" siteType="+siteCodeType);
                }

                return this.toBoxResponse(box);
                }
                catch (Exception ex){
                    logger.error(ex);
                }
            }
        }

        return this.paramError();
    }

    @POST
    @Path("/boxes")
    @Deprecated
    public BoxResponse add(BoxRequest request) {
        return add(request,BoxSystemTypeEnum.PRINT_CLIENT,false);
    }

    /**
     * 新版 打印客户端获取箱号
     * @param request
     * @return
     */
    @POST
    @Path("/printClient/boxes")
    public BoxResponse printClientBoxes(BoxRequest request) {
        return add(request,BoxSystemTypeEnum.PRINT_CLIENT,true);
    }

    private BoxResponse add(BoxRequest request, BoxSystemTypeEnum systemType, boolean isNew) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getType(), "request type must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        Assert.notNull(request.getQuantity(), "request quantity must not be null");
        if (Box.BOX_TRANSPORT_TYPE_CITY.equals(request.getTransportType())) {
            Assert.notNull(request.getPredictSendTime(), "request predictSendTime must not be null");
        }
        this.logger.info("BoxRequest's " + request.toString());
        BoxResponse response = this.ok();
        // 先生成路由信息
        // 获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
        CrossBoxResult<String[]> routInfoRes = null;
        try {
            routInfoRes = crossBoxService.getBoxRouter(request.getCreateSiteCode(), request.getReceiveSiteCode(), request.getPredictSendTime(), request.getTransportType());
            if (routInfoRes != null) {
                this.logger.info("BasicSaf getCrossDmsBox RouterInfo:" + routInfoRes.getData() + " ResultCode:" + routInfoRes.getResultCode() + " Message:" + routInfoRes.getMessage());
                if (logger.isInfoEnabled()) {
                    this.logger.info("调用跨箱号中转获取箱号路由" + JsonHelper.toJson(routInfoRes));
                }
                if (CrossBoxResult.SUCCESS == routInfoRes.getResultCode() && routInfoRes.getData() != null && routInfoRes.getData().length == 2) {
                    // 没超过5个站点，用这个选择模板打印
                    response.setRouterInfo(routInfoRes.getData()[0].split("\\-\\-"));
                    // 超过5个站点，打印系统直接用他打印
                    response.setRouterText(routInfoRes.getData()[0].replace("--", "-"));
                }
            } else {
                logger.warn("获得站点路由信息结果为空,参数信息：" + JsonHelper.toJson(request));
            }
        } catch (Exception e) {
            this.logger.error("获得站点路由信息失败： ", e);
        }
        // 生成箱号
        List<Box> availableBoxes;
        if (isNew) {
            availableBoxes = this.boxService.batchAddNew(this.toBoxWithRouter(request, routInfoRes), systemType);
        } else {
            availableBoxes = this.boxService.batchAdd(this.toBoxWithRouter(request, routInfoRes));
        }
        response.setBoxCodes(StringHelper.join(availableBoxes, "getCode", Constants.SEPARATOR_COMMA));

        this.buildBoxPrintInfo(request.getCreateSiteCode(), request.getReceiveSiteCode(), response);
        return response;
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

        this.logger.info("boxCode is " + boxCode);
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
        return create(request,BoxSystemTypeEnum.AUTO_SORTING_MACHINE,false);
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
        return create(request,BoxSystemTypeEnum.AUTO_SORTING_MACHINE,true);
    }

    private com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> create(BoxRequest request,BoxSystemTypeEnum systemType,boolean isNew) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getType(), "request type must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        Assert.notNull(request.getQuantity(), "request quantity must not be null");
        this.logger.info("BoxRequest's " + request.toString());

        //排除非法箱号类型
        if(request.getType().length() > BOX_TYPE_LENGTH){
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
                if (logger.isInfoEnabled()) {
                    this.logger.info("调用跨箱号中转获取箱号路由" + JsonHelper.toJson(resData));
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
                this.logger.error("获得站点路由信息失败： ", e);
            }
        }

        return result;
    }

    @POST
    @Path("/boxes/getRouterInfo")
    public BoxResponse getRouterInfo(BoxRequest request) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        this.logger.info("getRouterInfo BoxRequest's " + request);

        BoxResponse response = this.ok();
        if (request.getTransportType() != null && request.getTransportType() == 2) {//只有公路运输的支持路由信息查询2014.3.10
            //获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
            try {
                CrossBoxResult<String> routInfoRes = crossBoxService.getCrossDmsBox(request.getCreateSiteCode(), request.getReceiveSiteCode());
               // BaseResult<String> routInfoRes = basicSafInterfaceManager.getCrossDmsBox(request.getCreateSiteCode(),request.getReceiveSiteCode());
                this.logger.info("BasicSaf getCrossDmsBox Routerinfo:" + routInfoRes.getData() + " ResultCode:" + routInfoRes.getResultCode() + " Message:" + routInfoRes.getMessage());

                if (StringHelper.isNotEmpty(routInfoRes.getData())) {
                    response.setRouterInfo(routInfoRes.getData().split("\\-\\-"));
                }

            } catch (Exception e) {
                this.logger.error("获得站点路由信息失败： ", e);
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
        return box;
    }

    private Box toBox2(BoxRequest request) {
        Box box = new Box();
        box.setCode(request.getBoxCode());
        box.setUpdateUser(request.getUserName());
        box.setUpdateUserCode(request.getUserCode());
        return box;
    }

    /**
     * 对象转换，包括路由信息
     * @param request
     * @param crossBoxResult
     * @return
     */
    private Box toBoxWithRouter(BoxRequest request, CrossBoxResult<String[]> crossBoxResult){
        Box box=toBox(request);
        if (crossBoxResult != null && CrossBoxResult.SUCCESS==crossBoxResult.getResultCode() && crossBoxResult.getData()!=null && crossBoxResult.getData().length==2){
            box.setRouterName(crossBoxResult.getData()[0]);
            box.setRouter(crossBoxResult.getData()[1]);
        }
        return box;
    }
    private BoxResponse boxNoFound() {
        return new BoxResponse(BoxResponse.CODE_BOX_NOT_FOUND, BoxResponse.MESSAGE_BOX_NOT_FOUND);
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
        this.logger.info("box code's " + boxCode);

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
        this.logger.info("box code's " + boxCode);

        Long resulte = this.boxService.delboxCodeCache(boxCode);

        return resulte;
    }

    @GET
    @Path("/boxes/availability/{boxCode}")
    public InvokeResult<Boolean> statusValidation(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.logger.info("boxCode's " + boxCode);

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
}

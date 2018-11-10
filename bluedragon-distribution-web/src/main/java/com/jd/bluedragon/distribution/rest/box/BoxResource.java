package com.jd.bluedragon.distribution.rest.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.AutoSortingBoxResult;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
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


@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BoxResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BoxService boxService;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CrossBoxService crossBoxService;

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

        if(StringHelper.isNotEmpty(box.getRouter())){
            return response;
        }
        //箱子没有路由信息的话 查一遍
        //获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
        try {
            //  BaseResult<String> routInfoRes = this.basicSafInterfaceManager.getCrossDmsBox(box.getCreateSiteCode(), box.getReceiveSiteCode());
            CrossBoxResult<String[]> routInfoRes = crossBoxService.getBoxRouter(box.getCreateSiteCode(),box.getReceiveSiteCode(),box.getPredictSendTime(),box.getTransportType());
            this.logger.info("BasicSaf getCrossDmsBox Routerinfo:" + routInfoRes.getData() + " ResultCode:" + routInfoRes.getResultCode() + " Message:" + routInfoRes.getMessage());

            if (CrossBoxResult.SUCCESS==routInfoRes.getResultCode() && routInfoRes.getData()!=null && routInfoRes.getData().length==2) {
                //没超过5个站点，用这个选择模板打印
                response.setRouterInfo(routInfoRes.getData()[0].split("\\-\\-"));
                //超过5个站点，打印系统直接用他打印
                response.setRouterText(routInfoRes.getData()[0].replace("--","-"));
            }
        } catch (Exception e) {
            this.logger.error("获得站点路由信息失败： ", e);
        }
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
            SendM sendM = new SendM();
            sendM.setBoxCode(boxCode);
            sendM.setCreateSiteCode(box.getCreateSiteCode());
            List<SendM> sendMList = sendMDao.findSendMByBoxCode(sendM);
            if (!sendMList.isEmpty()) {// 如果箱子已经发货，则不能再使用了
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
    public BoxResponse add(BoxRequest request) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getType(), "request type must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        Assert.notNull(request.getQuantity(), "request quantity must not be null");
        //Assert.notNull(request.getSiteType(), "request SiteType must not be null");
        if (Box.BOX_TRANSPORT_TYPE_CITY.equals(request.getTransportType())){
            Assert.notNull(request.getPredictSendTime(), "request predictSendTime must not be null");
        }
        this.logger.info("BoxRequest's " + request.toString());
        BoxResponse response = this.ok();
        //先生成路由信息
        //获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
        CrossBoxResult<String[]> routInfoRes=null;
        try {
            routInfoRes = crossBoxService.getBoxRouter(request.getCreateSiteCode(), request.getReceiveSiteCode(), request.getPredictSendTime(),request.getTransportType());
            this.logger.info("BasicSaf getCrossDmsBox Routerinfo:" + routInfoRes.getData() + " ResultCode:" + routInfoRes.getResultCode() + " Message:" + routInfoRes.getMessage());
            if (logger.isInfoEnabled()) {
                this.logger.info("调用跨箱号中转获取箱号路由" + JsonHelper.toJson(routInfoRes));
            }
            if (CrossBoxResult.SUCCESS==routInfoRes.getResultCode() && routInfoRes.getData()!=null && routInfoRes.getData().length==2) {
                //没超过5个站点，用这个选择模板打印
                response.setRouterInfo(routInfoRes.getData()[0].split("\\-\\-"));
                //超过5个站点，打印系统直接用他打印
                response.setRouterText(routInfoRes.getData()[0].replace("--","-"));
            }
        } catch (Exception e) {
            this.logger.error("获得站点路由信息失败： ", e);
        }
        //生成箱号
        List<Box> availableBoxes = this.boxService.batchAdd(this.toBoxWithRouter(request,routInfoRes));

        response.setBoxCodes(StringHelper.join(availableBoxes, "getCode", Constants.SEPARATOR_COMMA));
        return response;
    }

    /**
     * 为自动分拣机生成箱号
     *
     * @param request
     * @return
     */
    @POST
    @Path("/boxes/create")
    public com.jd.bluedragon.distribution.jsf.domain.InvokeResult<AutoSortingBoxResult> create(BoxRequest request) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getType(), "request type must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        Assert.notNull(request.getQuantity(), "request quantity must not be null");
        this.logger.info("BoxRequest's " + request.toString());

        List<Box> availableBoxes = this.boxService.batchAdd(this.toBox(request));
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
        if (CrossBoxResult.SUCCESS==crossBoxResult.getResultCode() && crossBoxResult.getData()!=null && crossBoxResult.getData().length==2){
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
}

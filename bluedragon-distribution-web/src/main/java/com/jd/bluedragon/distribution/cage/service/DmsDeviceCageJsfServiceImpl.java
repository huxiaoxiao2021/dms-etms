package com.jd.bluedragon.distribution.cage.service;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.board.domain.Response;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.cage.DmsDeviceCageJsfService;
import com.jd.bluedragon.distribution.cage.request.CollectPackageReq;
import com.jd.bluedragon.distribution.cage.response.CollectPackageResp;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyCollectPackageService;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service("dmsDeviceCageJsfService")
public class DmsDeviceCageJsfServiceImpl implements DmsDeviceCageJsfService {
    private static final Logger log = LoggerFactory.getLogger(DmsDeviceCageJsfServiceImpl.class);

    @Autowired
    @Qualifier("jyCollectLoadingService")
    private JyCollectPackageService jyCollectPackageService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private BoxService boxService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DeviceCageJsfServiceImpl.cage", mState = JProEnum.TP)
    public InvokeResult<CollectPackageResp> cage(CollectPackageReq req) {

        //校验

        InvokeResult<CollectPackageResp> response = new InvokeResult<CollectPackageResp>();
        com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq request = new com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq();
        request.setBarCode(req.getBarCode());
        request.setBoxCode(req.getBoxCode());
        CurrentOperate currentOperate = new CurrentOperate();
        Site site = siteService.get(req.getSiteCode().intValue());
        if (site!=null){
            currentOperate.setSiteCode(req.getSiteCode().intValue());
            currentOperate.setSiteName(site.getName());
        }
        request.setCurrentOperate(currentOperate);
        User user = new User();
        user.setUserErp(req.getUserErp());
        user.setUserName(req.getUserName());
        request.setUser(user);
        Box box = boxService.findBoxByCode(request.getBoxCode());
        if (box == null) {
            throw new JyBizException("该箱号不存在或者已过期！");
        }
        if (Box.STATUS_DEFALUT.intValue() == box.getStatus().intValue()) {
            throw new JyBizException("该箱号未打印！");
        }
        if (box.getCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
            throw new JyBizException("箱号超长！");
        }
        request.setBoxReceiveId(new Long(box.getReceiveSiteCode()));
        request.setBoxReceiveName(box.getReceiveSiteName());
        InvokeResult<com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp> res;
        try {
            if (BusinessUtil.isBoxcode(req.getBarCode())){
                res = jyCollectPackageService.collectBoxForMachine(request);
            }else{
                res = jyCollectPackageService.collectPackageForMachine(request);
            }

            log.info("分拣机装笼调参数:{}，返回值:{}", JsonHelper.toJson(req), JsonHelper.toJson(res));
            if(res.getCode() != 200){
                response.parameterError(MessageFormat.format("调板服务组板发货接口失败code:{0}，message:{1}", res.getCode(),
                        res.getMessage()));
                log.warn("分拣机装笼接口失败code:{}，message:{},请求参数:{}", res.getCode(), res.getMessage(),
                        JsonHelper.toJson(req));
                return response;
            }
            CollectPackageResp data = JsonHelper.fromJsonUseGson(JsonHelper.toJson(res.getData()), new TypeToken<Response<CollectPackageResp>>() {
            }.getType());
            response.setData(data);
            response.success();
            return response;
        }catch (Exception e){
            log.error("分拣机装笼操作异常，装笼信息：{}", JsonHelper.toJson(request),e);
            response.parameterError("分拣机装笼操作异常");
            return response;
        }
    }

}

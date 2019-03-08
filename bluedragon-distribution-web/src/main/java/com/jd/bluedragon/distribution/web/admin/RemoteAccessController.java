package com.jd.bluedragon.distribution.web.admin;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.admin.service.RemoteAccessService;
import com.jd.bluedragon.distribution.api.domain.YtWaybillSync;
import com.jd.bluedragon.distribution.api.request.RemoteAccessRequest;
import com.jd.bluedragon.distribution.api.response.RemoteAccessResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.uim.annotation.Authorization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by yangbo7 on 2016/3/28.
 * 访问本地分拣中心的数据
 */
@Controller
@RequestMapping("/admin/remote-access")
public class RemoteAccessController {

    private static final Logger logger = Logger.getLogger(RemoteAccessController.class);

    @Autowired
    private RemoteAccessService remoteAccessService;

    @Authorization(Constants.DMS_WEB_SORTING_ADDRESSCHANGE_R)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            model.addAttribute("erpUser", erpUser);
        } catch (Exception e) {
            logger.error("index error!", e);
        }
        return "admin/remote-access/remote-access-index";
    }

    @Authorization(Constants.DMS_WEB_SORTING_ADDRESSCHANGE_R)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RemoteAccessResponse<List<YtWaybillSync>> doQueryWaybill(RemoteAccessRequest request) {
        RemoteAccessResponse<List<YtWaybillSync>> response = new RemoteAccessResponse<List<YtWaybillSync>>();
        try {
            if (null == request || StringHelper.isAnyEmpty(request.getWaybillOrPackageCode(), request.getMachineCode(), request.getLocalDmsUrl())) {
                response.setCode(RemoteAccessResponse.CODE_PARAM_ERROR);
                response.setMessage("参数[运单或包裹号,分拣机代码]不能为空！");
                return response;
            }
            logger.info(MessageFormat.format("RemoteAccessController.doQueryWaybill请求参数为{0}", JsonHelper.toJson(request)));
            RemoteAccessResponse<List<YtWaybillSync>> remoteResponse = remoteAccessService.findListByWaybillCode(request);
            if (remoteResponse == null || remoteResponse.getCode() == null || remoteResponse.getCode().intValue() != RemoteAccessResponse.CODE_OK) {
                response.setCode(RemoteAccessResponse.CODE_SERVICE_ERROR);
                response.setMessage(RemoteAccessResponse.MESSAGE_SERVICE_ERROR);
                response.setData(null);
            }else{
                response.setData(remoteResponse.getData());
                response.setCode(RemoteAccessResponse.CODE_OK);
                response.setMessage(RemoteAccessResponse.MESSAGE_OK);
            }
        } catch (Exception e) {
            logger.error("RemoteAccessController.doQueryWaybill-error", e);
            response.setCode(CommonDto.CODE_EXCEPTION);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public static void main(String[] args) {
        RemoteAccessRequest request = new RemoteAccessRequest();
        request.setLocalDmsUrl("123123");
        String s = MessageFormat.format("RemoteAccessController.doQueryWaybill-error, 请求参数为{0}", JsonHelper.toJson(request));
        System.out.println(s);
    }

}

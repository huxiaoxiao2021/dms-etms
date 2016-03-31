package com.jd.bluedragon.distribution.web.admin;

import com.jd.bluedragon.distribution.admin.service.RemoteAccessService;
import com.jd.bluedragon.distribution.api.domain.YtWaybillSync;
import com.jd.bluedragon.distribution.api.request.RemoteAccessRequest;
import com.jd.bluedragon.distribution.api.response.RemoteAccessResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.erp.service.dto.CommonDto;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.Date;
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
            RemoteAccessResponse<List<YtWaybillSync>> remoteResponse = remoteAccessService.findListByWaybillCode(request);
            if (remoteResponse == null || remoteResponse.getCode() == null || remoteResponse.getCode().intValue() != RemoteAccessResponse.CODE_OK) {
                response.setCode(RemoteAccessResponse.CODE_SERVICE_ERROR);
                response.setMessage(RemoteAccessResponse.MESSAGE_SERVICE_ERROR);
                response.setData(null);
            }
            response.setData(remoteResponse.getData());
            response.setCode(RemoteAccessResponse.CODE_OK);
            response.setMessage(RemoteAccessResponse.MESSAGE_OK);
        } catch (Exception e) {
            logger.error("doQueryWorker-error!", e);
            response.setCode(CommonDto.CODE_EXCEPTION);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }


}

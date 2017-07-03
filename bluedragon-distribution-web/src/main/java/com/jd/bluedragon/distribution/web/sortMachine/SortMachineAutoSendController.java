package com.jd.bluedragon.distribution.web.sortMachine;

import IceInternal.Ex;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupConfig;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineSendGroup;
import com.jd.bluedragon.distribution.sendGroup.service.SortMachineSendGroupService;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.annotation.meta.TypeQualifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
@Controller
@RequestMapping("/sortMachineAutoSend")
public class SortMachineAutoSendController {
    private static final Log logger = LogFactory.getLog(SortMachineAutoSendController.class);
    private final static String prefixKey = "localdmsIp$";
    private final static String HTTP = "http://";
    @Autowired
    BaseMajorManager baseMajorManager;
    @Resource
    private SortSchemeService sortSchemeService;
    @Resource
    private SortMachineSendGroupService sortMachineSendGroupService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "sortMachine/sortMachineAutoSendIndex";
    }

    /**
     * 根据用户erp加载该用户所在分拣中心的分拣机
     * @return
     */
    @RequestMapping(value = "/findSortMachineByErp", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<String>> findSortMachineByErp(){
        InvokeResult<List<String>> response = new InvokeResult<List<String>>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
        //获取分拣站点失败
        if(bssod == null || bssod.getSiteCode() == null){
            response.parameterError("根据erp活动分拣中心失败！");
            return response;
        }
        //站点类型不是分拣中心
        if(bssod.getSiteType() != 64){
            response.parameterError("该站点不是分拣中心！");
            return response;
        }
        //获取分拣中心本地服务url
        String url = PropertiesHelper.newInstance().getValue(prefixKey + bssod.getSiteCode());
        if (StringUtils.isBlank(url)) {
            response.parameterError("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
            return response;
        }
        //构建查询分拣机的model
        SortSchemeRequest sortSchemeRequest = new SortSchemeRequest();
        sortSchemeRequest.setSiteNo(String.valueOf(bssod.getSiteCode()));
        sortSchemeRequest.setPageNo(1);
        sortSchemeRequest.setPageSize(Integer.MAX_VALUE);
        SortSchemeResponse<Pager<List<SortScheme>>> remoteResponse = sortSchemeService.
                pageQuerySortScheme(sortSchemeRequest, HTTP + url + "/autosorting/sortScheme/list");
        if(remoteResponse == null
                || remoteResponse.getData() == null
                || remoteResponse.getData().getData() == null
                || remoteResponse.getData().getData().isEmpty()){
            response.parameterError("获取分拣机失败,或当前站点无分拣机！");
            return response;
        }
        List<SortScheme> sortSchemes = remoteResponse.getData().getData();
        List<String> machineCodes = new ArrayList<String>(sortSchemes.size());
        for(SortScheme sortScheme : sortSchemes){
            machineCodes.add(sortScheme.getMachineCode());
        }
        response.setData(machineCodes);

        return response;
    }

    /**
     * 根据分拣机编号查询发货组
     * @param machineCode
     * @return
     */
    @RequestMapping(value = "/findSendGroupByMachineCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<SortMachineSendGroup>> findSendGroupByMachineCode(String machineCode){
        InvokeResult<List<SortMachineSendGroup>> response = new InvokeResult<List<SortMachineSendGroup>>();
        try{
            List<SortMachineSendGroup> sortMachineSendGroups = sortMachineSendGroupService.
                    findSendGroupByMachineCode(machineCode);
            response.setData(sortMachineSendGroups);
        }catch (Exception e){
            e.printStackTrace();
            response.error(null);
        }
        return response;

    }

    /**
     * 根据发货组Id查询该组关联的滑道
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/findSendGroupConfigByGroupId", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<SortMachineGroupConfig>> findSendGroupConfigByGroupId(Integer groupId){
        InvokeResult<List<SortMachineGroupConfig>> response = new InvokeResult<List<SortMachineGroupConfig>>();
        try{
            List<SortMachineGroupConfig> sendGroupConfigs = sortMachineSendGroupService.
                    findSendGroupConfigByGroupId(groupId);
            response.setData(sendGroupConfigs);
        }catch (Exception e){
            e.printStackTrace();
            response.error(null);
        }
        return response;
    }

    /**
     * 根据分拣机号查询滑槽信息
     * @param groupId
     * @return
     */
    //todo
    @RequestMapping(value = "/queryChuteBySortMachineCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<SortMachineGroupConfig>> queryChuteBySortMachineCode(Integer groupId){
//        InvokeResult<List> response = new InvokeResult<List>();
//
//        return response;
        return null;
    }

    /**
     * 添加发货组
     * @param machineCode
     * @param groupName
     * @param chuteCodes
     * @return
     */
    @RequestMapping(value = "/addSendGroup", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult addSendGroup(String machineCode,
                                     String groupName,
                                     @RequestParam(value = "chuteCodes[]") String[] chuteCodes){
        InvokeResult respone = new InvokeResult();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        try{
            InvokeResult addResult = sortMachineSendGroupService.addSendGroup(machineCode,
                    groupName, chuteCodes, erpUser.getStaffNo(), erpUser.getUserName());
        }catch (Exception e){
            e.printStackTrace();
            respone.customMessage(500, "添加发货组时系统异常！");
        }

        return respone;
    }
}

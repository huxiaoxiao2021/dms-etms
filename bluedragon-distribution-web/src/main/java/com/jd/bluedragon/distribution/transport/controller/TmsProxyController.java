package com.jd.bluedragon.distribution.transport.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.JdiSelectWSManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.transport.domain.TmsProxyCondition;
import com.jd.bluedragon.distribution.transport.domain.TransBookBillDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.dto.PageDto;
import com.jd.tms.jdi.dto.TransBookBillQueryDto;
import com.jd.tms.jdi.dto.TransBookBillResultDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.POST;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年09月20日 10时:39分
 */
@Controller
@RequestMapping("transport/tmsProxy")
public class TmsProxyController extends DmsBaseController{

    private static final Logger log = LoggerFactory.getLogger(TmsProxyController.class);

    @Autowired
    private JdiSelectWSManager jdiSelectWSManager;

    @Autowired
    private SiteService siteService;

    @Value("${transBook.bill.printUrl:http://tfc.tms.jd.com/tfc/trans-book-bill/print}")
    private String printBaseUrl;

    /**
     * 打印委托书参数
     */
    private static String BASE_PARAM = "?transBookCode=";

    /**
     * 首页
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_TMSPROXY_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex(Model model) {
        return "/transport/tmsProxy";
    }

    @Authorization(Constants.DMS_WEB_SORTING_TMSPROXY_R)
    @ResponseBody
    @RequestMapping("/getAllSiteList")
    public Object getAllSiteList() {
        List<BaseStaffSiteOrgDto> allDms = new ArrayList<BaseStaffSiteOrgDto>();
        try {
            allDms.addAll(siteService.getAllDmsSite());
        } catch (Exception e) {
            log.error("加载站点失败：", e);
        }
        return allDms;
    }

    /**
     * 分页查询
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_TMSPROXY_R)
    @RequestMapping(value = "/listData")
    @POST
    public @ResponseBody
    PagerResult<TransBookBillDto> listData(@RequestBody TmsProxyCondition condition) {
        PagerResult<TransBookBillDto> result = new PagerResult<TransBookBillDto>();
        TransBookBillQueryDto queryDto = new TransBookBillQueryDto();
        try {
            queryDto = convertQueryDto(condition);
            CommonDto<com.jd.tms.jdi.dto.PageDto<com.jd.tms.jdi.dto.TransBookBillResultDto>> resultDtoPageDto
                    = jdiSelectWSManager.getTransBookBill(queryDto, convertPageDto(condition));
            if(log.isDebugEnabled()){
                log.debug("运输委托书查询结果：{}", JsonHelper.toJson(resultDtoPageDto));
            }
            if(Constants.INTEGER_FLG_TRUE.equals(resultDtoPageDto.getCode())){
                if(resultDtoPageDto.getData() != null){
                    result.setTotal(resultDtoPageDto.getData().getTotalRow());
                    result.setRows(dealResult(resultDtoPageDto.getData().getResult()));
                }
            }else{
                log.warn("运输委托书查询失败：：{} , {}", JsonHelper.toJson(queryDto) , JsonHelper.toJson(resultDtoPageDto));
            }
        }catch (Exception e){
            log.error("运输委托书查询失败：{} , {}", JsonHelper.toJson(queryDto) , JsonHelper.toJson(condition), e);
        }
        return result;
    }

    private List<TransBookBillDto> dealResult(List<TransBookBillResultDto> tmsDatas){
        if(tmsDatas == null || tmsDatas.isEmpty()){
            return new ArrayList<TransBookBillDto>();
        }
        List<TransBookBillDto> data = new ArrayList<TransBookBillDto>(tmsDatas.size());
        for(TransBookBillResultDto tmsData : tmsDatas){
            TransBookBillDto dto = new TransBookBillDto();
            dto.setTransBookCode(tmsData.getTransBookCode());
            dto.setStatus(tmsData.getStatus());
            dto.setStatusName(tmsData.getStatusName());
            dto.setBeginCityId(tmsData.getBeginCityId());
            dto.setBeginCityName(tmsData.getBeginCityName());
            dto.setBeginNodeCode(tmsData.getBeginNodeCode());
            dto.setBeginNodeName(tmsData.getBeginNodeName());
            dto.setEndCityId(tmsData.getEndCityId());
            dto.setEndCityName(tmsData.getEndCityName());
            dto.setEndNodeCode(tmsData.getEndNodeCode());
            dto.setEndNodeName(tmsData.getEndNodeName());
            dto.setRequirePickupTime(tmsData.getRequirePickupTime());
            dto.setCarrierCode(tmsData.getCarrierCode());
            dto.setCarrierName(tmsData.getCarrierName());
            dto.setVehicleNumber(tmsData.getVehicleNumber());
            String uri = BASE_PARAM + tmsData.getTransBookCode();
            dto.setPrintUrl(printBaseUrl + uri);
            data.add(dto);
        }
        return data;
    }

    /**
     * 查询条件转换
     * @param condition
     * @return
     */
    private TransBookBillQueryDto convertQueryDto(TmsProxyCondition condition){
        TransBookBillQueryDto dto = new TransBookBillQueryDto();
        LoginUser loginUser = getLoginUser();
        if(StringUtils.isNotBlank(condition.getTransBookCode())){
            dto.setTransBookCode(condition.getTransBookCode().trim());
        }
        if(StringUtils.isNotBlank(condition.getBillCode())){
            dto.setBillCode(condition.getBillCode().trim());
        }
        if(StringUtils.isNotBlank(condition.getVehicleNumber())){
            dto.setVehicleNumber(condition.getVehicleNumber().trim());
        }
        dto.setBeginNodeCode(loginUser.getDmsSiteCode());
        dto.setBeginNodeName(loginUser.getSiteName());
        dto.setEndNodeCode(condition.getEndNodeCode());
        dto.setEndNodeName(condition.getEndNodeName());
        dto.setRequirePickupTimeBegin(condition.getRequirePickupTimeBegin());
        dto.setRequirePickupTimeEnd(condition.getRequirePickupTimeEnd());

        return dto;
    }

    /**
     * 分页查询条件转换
     * @param condition
     * @return
     */
    private PageDto<TransBookBillQueryDto> convertPageDto(BasePagerCondition condition){
        PageDto<TransBookBillQueryDto> pageDto = new PageDto<TransBookBillQueryDto>();
        //设置分页条件
        pageDto.setCurrentPage(condition.getOffset()/condition.getLimit() + 1);
        pageDto.setPageSize(condition.getLimit());
        return pageDto;
    }

}

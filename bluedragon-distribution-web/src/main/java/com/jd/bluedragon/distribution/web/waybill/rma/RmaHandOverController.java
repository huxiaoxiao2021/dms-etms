package com.jd.bluedragon.distribution.web.waybill.rma;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.RmaHandoverQueryRequest;
import com.jd.bluedragon.distribution.api.response.RmabillInfoResponse;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.common.web.LoginContext;
import com.jd.etms.erp.service.dto.CommonDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghao141 on 2018/9/18.
 */
@Controller
@RequestMapping("/waybill/rma")
public class RmaHandOverController {

    @Autowired
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    private static final Logger logger = Logger.getLogger(RmaHandOverController.class);
    /**
     * 跳转到主界面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "waybill/rma/list";
    }
    /**
     * 查询方法
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public RmabillInfoResponse<Pager<List<RmaHandoverWaybill>>> query(RmaHandoverQueryRequest rmaHandoverQueryRequest, Pager page) {
        RmabillInfoResponse<Pager<List<RmaHandoverWaybill>>> rmabillInfoResponse = new RmabillInfoResponse<Pager<List<RmaHandoverWaybill>>>();
        rmabillInfoResponse.setCode(RmabillInfoResponse.CODE_NORMAL);
        try {

            if(rmaHandoverQueryRequest==null){
                rmabillInfoResponse.setCode(RmabillInfoResponse.CODE_EXCEPTION);
                rmabillInfoResponse.setData(null);
                rmabillInfoResponse.setMessage("查询条件不能为空");
                return rmabillInfoResponse;
            }


            if(rmaHandoverQueryRequest.getSendDateStart()==null && rmaHandoverQueryRequest.getSendDateEnd()==null){
                rmabillInfoResponse.setCode(RmabillInfoResponse.CODE_EXCEPTION);
                rmabillInfoResponse.setData(null);
                rmabillInfoResponse.setMessage("查询时间条件不能为空");
                return rmabillInfoResponse;
            }
            if(rmaHandoverQueryRequest.getReceiverAddress()==null){
                rmabillInfoResponse.setCode(RmabillInfoResponse.CODE_EXCEPTION);
                rmabillInfoResponse.setData(null);
                rmabillInfoResponse.setMessage("查询地址不能为空");
                return rmabillInfoResponse;
            }
            RmaHandoverQueryParam rmaHandoverQueryParam=new RmaHandoverQueryParam();
            LoginContext loginContext=LoginContext.getLoginContext();
            rmaHandoverQueryParam.setCreateSiteCode(rmaHandoverQueryRequest.getCreateSiteCode());
            rmaHandoverQueryParam.setReceiverAddress(rmaHandoverQueryRequest.getReceiverAddress());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            rmaHandoverQueryParam.setSendDateStart(sdf.parse(rmaHandoverQueryRequest.getSendDateStart()));
            rmaHandoverQueryParam.setSendDateEnd(sdf.parse(rmaHandoverQueryRequest.getSendDateEnd()));
            rmaHandoverQueryParam.setHasPrint(rmaHandoverQueryRequest.getHasPrint());

//                Pager<List<RmaHandoverWaybill>> pager=rmaHandOverWaybillService.getListWithoutDetail(rmaHandoverQueryRequest,page);

//            mock 数据
            List<RmaHandoverWaybill> rmaHandoverWaybillList=new ArrayList<RmaHandoverWaybill>();

            RmaHandoverWaybill rmaHandoverWaybill=new  RmaHandoverWaybill();
            rmaHandoverWaybill.setId(1L);
            rmaHandoverWaybill.setWaybillCode("asdfg");
            rmaHandoverWaybill.setReceiverAddress("XXXX地址");
            rmaHandoverWaybill.setHasPrint(1);
            rmaHandoverWaybillList.add(rmaHandoverWaybill);
            RmaHandoverWaybill rmaHandoverWaybill1=new  RmaHandoverWaybill();
            rmaHandoverWaybill1.setId(2L);
            rmaHandoverWaybill1.setWaybillCode("asdfg11111");
            rmaHandoverWaybill1.setReceiverAddress("XXXX地址22222");
            rmaHandoverWaybill1.setHasPrint(1);

            rmaHandoverWaybillList.add(rmaHandoverWaybill1);

        // 设置分页对象
            if (page == null) {
                page = new Pager<List<RmaHandoverWaybill>>(Pager.DEFAULT_PAGE_NO);
            }
            page.setData(rmaHandoverWaybillList);
            rmabillInfoResponse.setData(page);

            if (page == null) {
                page = new Pager<List<RmaHandoverWaybill>>(Pager.DEFAULT_PAGE_NO);
            }
            rmabillInfoResponse.setData(page);
            rmabillInfoResponse.setCode(RmabillInfoResponse.CODE_NORMAL);

        } catch (Exception e) {
            logger.error("根据查询条件获取路由信息失败.",e);
            rmabillInfoResponse.setCode(RmabillInfoResponse.CODE_EXCEPTION);
            rmabillInfoResponse.setData(null);
            rmabillInfoResponse.setMessage("根据查询条件获取数据失败："+e.getMessage());
        }
        return rmabillInfoResponse;
    }
    /**
     * 查询地址方法
     * @return
     * @param waybillCode
     */
    @RequestMapping("/receiverAddressQuery")
    @ResponseBody
    public CommonDto<String> queryAddstrBybillNo(@RequestBody String waybillCode) {
        CommonDto<String> rmabillInfoResponse = new CommonDto<String>();
        rmabillInfoResponse.setCode(CommonDto.CODE_FAIL);

        try {
            String receiverAddress=rmaHandOverWaybillService.getReceiverAddressByWaybillCode(waybillCode);
            rmabillInfoResponse.setData(receiverAddress);
            rmabillInfoResponse.setCode(CommonDto.CODE_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            rmabillInfoResponse.setMessage("查询地址失败，单号无效");
        }
        return rmabillInfoResponse;
    }
    /**
     * 打印
     * @return
     * @param idList
     */
    @RequestMapping("/printWaybillRma")
    @ResponseBody
    public CommonDto<String> printWaybillRma(@RequestBody List<Integer> idList) {
        CommonDto<String> rmabillInfoResponse = new CommonDto<String>();
        rmabillInfoResponse.setCode(CommonDto.CODE_FAIL);

        try {
            //todo 准备打印方法
            rmabillInfoResponse.setData("xxxx地址");
        } catch (Exception e) {
            e.printStackTrace();
            rmabillInfoResponse.setCode(CommonDto.CODE_NORMAL);
            rmabillInfoResponse.setMessage("查询地址失败，单号无效");
        }
        return rmabillInfoResponse;
    }
}

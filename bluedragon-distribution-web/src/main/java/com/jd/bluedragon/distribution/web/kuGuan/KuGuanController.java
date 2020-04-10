package com.jd.bluedragon.distribution.web.kuGuan;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.ChuguanExportManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/kuGuan")
public class KuGuanController {
	
	private final Logger log = LoggerFactory.getLogger(KuGuanController.class);
	
	@Autowired
	private StockExportManager stockExportManager;

    @Autowired
    private ChuguanExportManager chuguanExportManager;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;


    @Autowired
    private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;

	@Authorization(Constants.DMS_WEB_QUERY_KUGUANINIT)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListpage(Model model) {
		model.addAttribute("errorMesage", "0");
		return "kuguan/kuguan";
	}

	@Authorization(Constants.DMS_WEB_QUERY_KUGUANLIST)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.WEB.KuGuanController.queryOperateLog", mState = JProEnum.TP)
	public String queryOperateLog(KuGuanDomain kuGuanDomain, Model model) {

		Map<String, Object> params = ObjectMapHelper
				.makeObject2Map(kuGuanDomain);
		if (params.get("waybillCode") == null) {
			log.warn("根据订单号获取库管单信息参数错误");
			return "kuguan/kuguan";
		}
        ErpUserClient.ErpUser user = null;
        try {
            user = ErpUserClient.getCurrUser();
            if(log.isInfoEnabled()){
				log.info("库管单查询-queryOperateLog-user[{}]kuGuanDomain[{}]", JsonHelper.toJson(user), JsonHelper.toJson(kuGuanDomain));
			}
		} catch (Exception e) {
            log.error("库管单查询-queryOperateLog-kuGuanDomain[{}]",JsonHelper.toJson(kuGuanDomain),e);
        }
        String orderCode = kuGuanDomain.getWaybillCode();
        String lKdanhao = kuGuanDomain.getlKdanhao();

		try {
			if(log.isInfoEnabled()){
				log.info("根据订单号获取库管单信息:{}", params.toString());
			}
			kuGuanDomain = this.queryByOrderCode(orderCode,lKdanhao);
			
		} catch (Exception e) {
			kuGuanDomain = new KuGuanDomain(); 
			kuGuanDomain.setWaybillCode(null);
			model.addAttribute("errorMesage", "1");
			log.error("根据订单号获取库管单信息服务异常",e);
		}
		if(kuGuanDomain == null){
            kuGuanDomain = new KuGuanDomain();
            kuGuanDomain.setWaybillCode(null);
            model.addAttribute("errorMesage", "未获取库管信息");
        }else{
            model.addAttribute("kuguanLists", kuGuanDomain.getStockDetails());
            model.addAttribute("kuGuanDomain", kuGuanDomain);
        }
		return "kuguan/kuguan";
	}

	@Authorization(Constants.DMS_WEB_QUERY_KUGUANLIST)
	@RequestMapping(value = "/listForYanfa", method = RequestMethod.GET)
	public String listForYanfa(KuGuanDomain kuGuanDomain, Model model) {
		return queryOperateLog(kuGuanDomain, model);
	}

    private KuGuanDomain queryByOrderCode(String orderCode,String lKdanhao){
        if(uccPropertyConfiguration.isChuguanNewPageQuerySwitch()){
            return chuguanExportManager.queryByOrderCode(orderCode,lKdanhao);
        }
        return stockExportManager.queryByOrderCode(orderCode,lKdanhao);
    }

    @RequestMapping(value = "/reprocessChuguanPage", method = RequestMethod.GET)
    public String reprocessChuguanPage() {
        return "kuguan/reprocessChuguanPage";
    }

    @RequestMapping(value="/reprocessChuguan",method=RequestMethod.POST)
    @ResponseBody
    public String reprocessChuguan(String orderIds) {
        if(StringUtils.isEmpty(orderIds)){
            return "orderIds is null";
        }
        int error=0;
        int succ=0;
        try {
            String[] orderIdArray = orderIds.replaceAll("\n","").split(",");
            for (String id : orderIdArray){
                Boolean aBoolean = reverseReceiveNotifyStockService.nodifyStock(Long.valueOf(id));
                if(aBoolean !=null && aBoolean){
                    succ++;
                }else{
                    error++;
                }
                log.info("订单号id[{}]处理结果aBoolean[]",id,aBoolean);
            }
        } catch (Exception e) {
            log.error("根据订单号处理出管报错",e);
            return e.getMessage();
        }
        return "succ["+succ+"[error["+error+"]";
    }

}

package com.jd.bluedragon.distribution.web.kuGuan;

import java.util.Map;

import com.jd.bluedragon.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.uim.annotation.Authorization;

@Controller
@RequestMapping("/kuGuan")
public class KuGuanController {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private StockExportManager stockExportManager;
	
	@Authorization(Constants.DMS_WEB_QUERY_KUGUANINIT)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListpage(Model model) {
		model.addAttribute("errorMesage", "0");
		return "kuguan/kuguan";
	}

	@Authorization(Constants.DMS_WEB_QUERY_KUGUANLIST)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String queryOperateLog(KuGuanDomain kuGuanDomain, Model model) {

		Map<String, Object> params = ObjectMapHelper
				.makeObject2Map(kuGuanDomain);
		if (params.get("waybillCode") == null) {
			logger.error("根据订单号获取库管单信息参数错误");
			return "kuguan/kuguan";
		}

		try {
			logger.info("根据订单号获取库管单信息"+params.toString());
			kuGuanDomain = stockExportManager.queryByParams(params);
			
		} catch (Exception e) {
			kuGuanDomain = new KuGuanDomain(); 
			kuGuanDomain.setWaybillCode(null);
			model.addAttribute("errorMesage", "1");
			logger.error("根据订单号获取库管单信息服务异常"+e);
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
}

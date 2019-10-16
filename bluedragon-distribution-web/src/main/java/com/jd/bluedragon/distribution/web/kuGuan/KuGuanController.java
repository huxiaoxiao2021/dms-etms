package com.jd.bluedragon.distribution.web.kuGuan;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.ChuguanExportManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/kuGuan")
public class KuGuanController {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private StockExportManager stockExportManager;

    @Autowired
    private ChuguanExportManager chuguanExportManager;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;
	
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
			logger.error("根据订单号获取库管单信息参数错误");
			return "kuguan/kuguan";
		}

        String orderCode = kuGuanDomain.getWaybillCode();
        String lKdanhao = kuGuanDomain.getlKdanhao();

		try {
			logger.info("根据订单号获取库管单信息"+params.toString());
			kuGuanDomain = this.queryByOrderCode(orderCode,lKdanhao);
			
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

    private KuGuanDomain queryByOrderCode(String orderCode,String lKdanhao){
        if(uccPropertyConfiguration.isChuguanNewInterfaceQuerySwitch()){
            return chuguanExportManager.queryByOrderCode(orderCode,lKdanhao);
        }
        return stockExportManager.queryByOrderCode(orderCode,lKdanhao);
    }
}

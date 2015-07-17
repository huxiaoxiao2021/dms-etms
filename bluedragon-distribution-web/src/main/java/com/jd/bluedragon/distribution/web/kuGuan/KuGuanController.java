package com.jd.bluedragon.distribution.web.kuGuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.kuguan.service.KuGuanService;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.common.hrm.HrmPrivilege;

@Controller
@RequestMapping("/kuGuan")
public class KuGuanController {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	KuGuanService tKuGuanService;
	
	@HrmPrivilege("DMS-WEB-QUERY-KUGUANINIT")
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListpage(Model model) {
		model.addAttribute("ddlType", "1");
		model.addAttribute("errorMesage", "0");
		return "kuguan/kuguan";
	}

	@HrmPrivilege("DMS-WEB-QUERY-KUGUANLIST")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String queryOperateLog(KuGuanDomain kuGuanDomain, Model model) {

		Map<String, Object> params = ObjectMapHelper
				.makeObject2Map(kuGuanDomain);
		String ddlType = (String) params.get("ddlType");
		if (params.get("waybillCode") == null || params.get("ddlType") == null) {
			logger.error("根据订单号获取库管单信息参数错误");
			return "kuguan/kuguan";
		}
		List<KuGuanDomain> domains = new ArrayList<KuGuanDomain>();
		try {
			logger.error("根据订单号获取库管单信息参数错误-queryByParams");
			kuGuanDomain = tKuGuanService.queryByParams(params);
			if(kuGuanDomain==null)
				model.addAttribute("errorMesage", "1");
			else if (kuGuanDomain.getlKdanhao() != null){
				logger.error("根据订单号获取库管单信息参数错误-queryMingxi");
				domains = tKuGuanService.queryMingxi(kuGuanDomain.getlKdanhao());
			}
			logger.error("根据订单号获取库管单信息参数错误-1");
			if (domains != null && !domains.isEmpty()) {
				double zj = 0;
				int znum= 0;
				for (KuGuanDomain domain : domains) {
					String jine = domain.getLbljine();
					String num = domain.getLblNum();
					if (jine != null && !jine.equals(""))
						zj += Double.parseDouble(jine);
					if (num != null && !num.equals(""))
						znum += Integer.parseInt(num);
				}
				kuGuanDomain.setLblstatistics("合计：" + znum
						+ "件商品，共计"+zj+"元");
			}
		} catch (Exception e) {
			kuGuanDomain = new KuGuanDomain(); 
			kuGuanDomain.setDdlType(ddlType);
			kuGuanDomain.setWaybillCode(null);
			model.addAttribute("errorMesage", "1");
			logger.error("根据订单号获取库管单信息服务异常"+e);
		}
		model.addAttribute("kuguanLists", domains);
		model.addAttribute("kuGuanDomain", kuGuanDomain);
		return "kuguan/kuguan";
	}
	
	
}

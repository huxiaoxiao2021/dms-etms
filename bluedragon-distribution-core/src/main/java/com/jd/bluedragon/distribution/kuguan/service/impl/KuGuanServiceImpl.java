package com.jd.bluedragon.distribution.kuguan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.kuguan.service.KuGuanService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.iwmss.stock.client.ArrayOfStock;
import com.jd.iwmss.stock.client.ArrayOfStockDetail;
import com.jd.iwmss.stock.client.Stock;
import com.jd.iwmss.stock.client.StockDetail;
import com.jd.iwmss.stock.client.StockParamter;
import com.jd.iwmss.stock.client.StockWebServiceSoap;

@Service
public class KuGuanServiceImpl implements KuGuanService {
	
	@Autowired
	private StockWebServiceSoap stockWebService;
	
	@Override
	@Profiled(tag = "KuGuanService.queryByParams")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public KuGuanDomain queryByParams(Map<String, Object> params) {
		String ddlType = (String) params.get("ddlType");
		String waybillCode = (String) params.get("waybillCode");
		StockParamter stock = new StockParamter();
		if (ddlType.equals("1")) {
			if (BusinessHelper.isNumeric(waybillCode))
				stock.setOrderid(Long.parseLong(waybillCode));
		} else {
			if (BusinessHelper.isNumeric(waybillCode))
				stock.setKdanhao(Integer.parseInt(waybillCode));
		}
		ArrayOfStock stocks = stockWebService.getStocks(stock);
		KuGuanDomain domain = null;
		List<Stock> list = stocks.getStock();
		List<String> kglist = new ArrayList<String>();
		if (list != null && !list.isEmpty()) {
			domain = new KuGuanDomain();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				Stock nstock = list.get(i);
				String kdanhao = String.valueOf(nstock.getKdanhao());
				kglist.add(kdanhao);
				if (i == 0) {
					// 订单类型
					domain.setDdlType(ddlType);
					// 库管单号
					domain.setlKdanhao(String.valueOf(nstock.getKdanhao()));
					// 方式
					if (nstock.getChuru() != null)
						domain.setLblWay(nstock.getChuru());
					// 分类
					if (nstock.getFeilei() != null)
						domain.setLblType(nstock.getFeilei());
					// 其他方式
					if (nstock.getQtfs() != null)
						domain.setLblOtherWay(nstock.getQtfs());
					// 经办人
					if (nstock.getJingban() != null)
						domain.setLblJingban(nstock.getJingban());
					// 日期
					if (nstock.getCity() != null)
						domain.setLblDate(nstock.getCity());
					// 来源
					if (nstock.getLaiyuan() != null)
						domain.setLblFrom(nstock.getLaiyuan());
					// 款项
					if (nstock.getKuanx() != null)
						domain.setLblKuanx(nstock.getKuanx());
					// 优惠
					if (nstock.getYouhui() != null)
						domain.setLblYouhui(String.valueOf(nstock.getYouhui()));
					// 运费
					if (nstock.getYun() != null)
						domain.setLblYun(String.valueOf(nstock.getYun()));
					// 其他
					if (nstock.getQite() != null)
						domain.setLblOther(String.valueOf(nstock.getQite()));
					// 总金额
					if (nstock.getZjine() != null)
						domain.setLblZjine(String.valueOf(nstock.getZjine()));
					// 财务单号
					domain.setLblCdanhao1(String.valueOf(nstock.getCdanhao()));
					// 订单号
					domain.setLblOrderid(String.valueOf(nstock.getOrderid()));
					// 机构
					if (nstock.getJigou() != null)
						domain.setLblOrg(String.valueOf(nstock.getJigou()));
					// 仓库
					domain.setLblStock(String.valueOf(nstock.getSId()));
					// 录入员
					if (nstock.getLuru() != null)
						domain.setLblLuru(String.valueOf(nstock.getLuru()));
					// 自提点
					domain.setLblStation("");
					// 是否签字
					if (nstock.getQianzi().equals(1))
						domain.setLblSure("已签");
					else
						domain.setLblSure("未签");
					// 原单号
					if (nstock.getYuandanhao() != null)
						domain.setLblYdanhao(String.valueOf(nstock
								.getYuandanhao()));
					// 备注
					if (nstock.getRemark() != null)
						domain.setLblRemark(String.valueOf(nstock.getRemark()));
				}
			}
			domain.setWaybillCode(waybillCode);
			// 超链接库管单号
			if (ddlType.equals("1") && kglist != null && !kglist.isEmpty()) {
				for (int i = 0; i < kglist.size(); i++) {
					String kudanhao = kglist.get(i);
					buf.append("<a href=list?ddlType=2&waybillCode=" + kudanhao
							+ " >" + kudanhao + "</a>&nbsp;");
				}
				domain.setLblKdanhao(buf.toString());
			}
		}

		return domain;
	}
	
	@Override
	@Profiled(tag = "KuGuanService.queryMingxi")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<KuGuanDomain> queryMingxi(String kdanhao) {
		// TODO Auto-generated method stub
		List<KuGuanDomain> listKG = new ArrayList<KuGuanDomain>();
		if (kdanhao != null && !kdanhao.equals("")) {
			ArrayOfStockDetail lists = stockWebService
					.getStockDetailsByKdanhaos(kdanhao);
			if (lists != null) {
				List<StockDetail> stockDetail = lists.getStockDetail();
				for (StockDetail nstock : stockDetail) {
					KuGuanDomain domain = new KuGuanDomain();
					// 财务单-列表
					if (nstock.getCdanhao() != null)
						domain.setLblCdanhao(String.valueOf(nstock.getCdanhao()));
					// 金额-列表
					if (nstock.getZjine() != null)
						domain.setLbljine(String.valueOf(nstock.getZjine()));
					// 数量-列表
					domain.setLblNum(String.valueOf(nstock.getNum()));
					// 单价-列表
					domain.setLblPrice(String.valueOf(nstock.getJiage()));
					// 商品名称-列表
					if (nstock.getWare() != null)
						domain.setLblWare(nstock.getWare());
					// 商品id-列表
					domain.setLblWareId(String.valueOf(nstock.getWareid()));
					listKG.add(domain);
				}
			}
		}
		return listKG;
	}
}

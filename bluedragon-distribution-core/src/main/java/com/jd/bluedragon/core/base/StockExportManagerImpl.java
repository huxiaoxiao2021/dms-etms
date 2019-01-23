package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.stock.iwms.export.StockExportService;
import com.jd.stock.iwms.export.param.CallerParam;
import com.jd.stock.iwms.export.param.StockVOParam;
import com.jd.stock.iwms.export.result.QueryResult;
import com.jd.stock.iwms.export.vo.StockDetailVO;
import com.jd.stock.iwms.export.vo.StockVO;

@Service("stockExportManager")
public class StockExportManagerImpl implements StockExportManager {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private StockExportService stockExportService;
	
	@Override
	@SuppressWarnings("rawtypes")
	public long insertStockVirtualIntOut(StockVOParam stockVOParam0, StockVOParam stockVOParam1) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.StockExportManagerImpl.insertStockVirtualIntOut", false, true);
		try{
			com.jd.stock.iwms.export.result.BaseResult result = stockExportService.insertStockVirtualIntOut(stockVOParam0, stockVOParam1);
			
			if(result!=null){
				if(!result.isResultFlag()){
					this.logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常：result:"+result.getMessage());
					Profiler.functionError(info);
					return 0;
				}else{
					this.logger.info("调用库管接口stockExportManager.insertStockVirtualIntOut成功：resultCode:"+result.getResultCode()+" resultMessage:"+result.getMessage());
					return 1;//表示推送成功
				}
			}else{
				this.logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常: result为空!");
				Profiler.functionError(info);
				return 0;
			}
			
		}catch(Exception e){
			logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常", e);
			Profiler.functionError(info);
			return 0;
		}finally {
			Profiler.registerInfoEnd(info);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public QueryResult<StockVO> getFullStockByBusiNo(String businessNo, Integer businessType, Boolean isQueryHis) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.StockExportManagerImpl.getFullStockByBusiNo", false, true);
		QueryResult<StockVO> result = null;
		try{
			
			CallerParam callerParam = new CallerParam();
			callerParam.setSystemName("ql.dms");
			result = stockExportService.queryStockData(callerParam, businessNo, businessType);
			
			if(result==null||result.getQueryList()==null||result.getQueryList().isEmpty()){
				this.logger.info("调用库管接口stockExportManager.getFullStockByBusiNo queryStockData未获得数据,改调用queryStockHisData方法,本次查询结果:"+ JsonHelper.toJson(result));
				result = stockExportService.queryStockHisData(callerParam, businessNo, businessType);
			}
			
			if(result!=null){
				if(!result.isResultFlag()){
					this.logger.error("调用库管接口stockExportManager.getFullStockByBusiNo queryStockData()异常：result:"+result.getMessage());
					Profiler.functionError(info);
					result = null;
				}else{
					this.logger.info("调用库管接口stockExportManager.getFullStockByBusiNo queryStockData()成功: resultMessage:"+result.getMessage());
				}
			}else{
				this.logger.error("调用库管接口stockExportManager.getFullStockByBusiNo queryStockData()异常: result为空!");
				Profiler.functionError(info);
			}
			
		}catch(Exception e){
			logger.error("调用库管接口stockExportManager.getFullStockByBusiNo queryStockData()异常", e);
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
		}
		return result;
	}

	@Override
	public KuGuanDomain queryByParams(Map<String, Object> paramMap) {

		String waybillCode = (String) paramMap.get("waybillCode");
		String kdanhao = (String) paramMap.get("lKdanhao");

		//根据参数进行查询
		QueryResult<StockVO> stocks = getFullStockByBusiNo(waybillCode, null, true);
		KuGuanDomain domain = null;
		List<StockVO> list = stocks.getQueryList();
		List<String> kglist = new ArrayList<String>();
		if (list != null && !list.isEmpty()) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				StockVO nstock = list.get(i);
				String stockKudanhao = String.valueOf(nstock.getKdanhao().toString());
				kglist.add(stockKudanhao);
				if(StringHelper.isEmpty(kdanhao)){
					if (i == 0) {
						domain = convert2KuGuanDomain(nstock);
					}
				}else{
					if(kdanhao.equals(stockKudanhao)){
						domain = convert2KuGuanDomain(nstock);
					}
				}
				
				//组装库管单号超链接
				if(stockKudanhao.equals(kdanhao)){
					buf.append("<a href=list?waybillCode=" + waybillCode
							+ "&lKdanhao="+stockKudanhao+" style='color:red;text-align:center' >" + stockKudanhao + "</a>&nbsp;");
				}else{
					buf.append("<a href=list?waybillCode=" + waybillCode
							+ "&lKdanhao="+stockKudanhao+" >" + stockKudanhao + "</a>&nbsp;");
				}
			}

			if(domain!=null) 
				domain.setLblKdanhao(buf.toString());
		}

		return domain;
	}
	
	@Override
	public KuGuanDomain queryByWaybillCode(String waybillCode) {
		KuGuanDomain kuGuanDomain = new KuGuanDomain();
		kuGuanDomain.setWaybillCode(waybillCode);

		KuGuanDomain result = null;
		
		Map<String, Object> params = ObjectMapHelper
				.makeObject2Map(kuGuanDomain);

		try {
			logger.info("根据订单号获取库管单信息参数错误-queryByParams");
			result = this.queryByParams(params);
		} catch (Exception e) {
			logger.info("根据订单号获取库管单信息服务异常", e);
		}
		return result;
	}
	
	/**
	 * 将获得的nstock对象转化为前台可以使用的对象,需要保证传入对象不为空
	 * @param nstock
	 * @return
	 */
	private KuGuanDomain convert2KuGuanDomain(StockVO nstock){
		KuGuanDomain domain = new KuGuanDomain();
		domain.setWaybillCode(nstock.getOrderId().toString());
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
		domain.setLblOrderid(String.valueOf(nstock.getOrderId()));
		// 机构
		if (nstock.getJigou() != null)
			domain.setLblOrg(String.valueOf(nstock.getJigou()));
		// 仓库
		domain.setLblStock(String.valueOf(nstock.getSid()));
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
		
		//处理商品明细
		double zj = 0;
		int znum= 0;
		List<KuGuanDomain> listKG = new ArrayList<KuGuanDomain>();
		List<StockDetailVO> stockDetail = nstock.getStockDetails();
		for (StockDetailVO sdv : stockDetail) {
			KuGuanDomain sdvDomain = new KuGuanDomain();
			// 财务单-列表
			if (sdv.getCdanhao() != null)
				sdvDomain.setLblCdanhao(String.valueOf(sdv.getCdanhao()));
			// 金额-列表
			if (sdv.getZjine() != null)
				sdvDomain.setLbljine(String.valueOf(sdv.getZjine()));
			// 数量-列表
			sdvDomain.setLblNum(String.valueOf(sdv.getNum()));
			// 单价-列表
			sdvDomain.setLblPrice(String.valueOf(sdv.getJiage()));
			// 商品名称-列表
			if (sdv.getWare() != null)
				sdvDomain.setLblWare(sdv.getWare());
			// 商品id-列表
			sdvDomain.setLblWareId(String.valueOf(sdv.getWareId()));
			listKG.add(sdvDomain);
			
			//计算商品数量、总价
			String jine = sdvDomain.getLbljine();
			String num = sdvDomain.getLblNum();
			if (jine != null && !jine.equals(""))
				zj += Double.parseDouble(jine);
			if (num != null && !num.equals(""))
				znum += Integer.parseInt(num);
		}
		domain.setStockDetails(listKG);
		domain.setLblstatistics("合计：" + znum
				+ "件商品，共计"+zj+"元");
		
		return domain;
		
	}
}

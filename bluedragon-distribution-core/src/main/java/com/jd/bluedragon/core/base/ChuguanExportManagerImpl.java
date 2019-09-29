package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.stock.base.CallerParam;
import com.jd.stock.iwms.export.ChuguanExport;
import com.jd.stock.iwms.export.StockExportService;
import com.jd.stock.iwms.export.param.ChuguanParam;
import com.jd.stock.iwms.export.param.ChuguanQueryParam;
import com.jd.stock.iwms.export.result.ChuguanDataResult;
import com.jd.stock.iwms.export.result.ChuguanResult;
import com.jd.stock.iwms.export.vo.ChuguanVo;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xumigen
 * @date : 2019/9/27
 */
public class ChuguanExportManagerImpl implements ChuguanExportManager{


    private final static Logger logger = LoggerFactory.getLogger(StockExportManagerImpl.class);

    @Autowired
    private StockExportService stockExportService;

    @Autowired
    private ChuguanExport chuguanExport;

    @Override
    public long insertStockVirtualIntOut(List<ChuguanParam> chuguanParamList) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.StockExportManagerImpl.insertChuguan", false, true);
        try{
            CallerParam callerParam = getCallerParam();
            ChuguanResult result = chuguanExport.insertChuguan(chuguanParamList, callerParam);
            if(result == null){
                logger.error("调用库管接口insertChuguan返回null-chuguanParamList[{}][{}]", JsonHelper.toJson(chuguanParamList),JsonHelper.toJson(callerParam));
                return 0;
            }
            if(result.getCode() != 1){
                logger.error("调用库管接口insertChuguan返回失败result[{}][{}]",result);
                return 0;
            }
//            com.jd.stock.iwms.export.result.BaseResult
//			if(result!=null){
//				if(!result.isResultFlag()){//todo isResultFlag 是什么业务
//					this.logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常：result:"+result.getMessage());
//					Profiler.functionError(info);
//					return 0;
//				}else{
//					this.logger.info("调用库管接口stockExportManager.insertStockVirtualIntOut成功：resultCode:"+result.getResultCode()+" resultMessage:"+result.getMessage());
//					return 1;//表示推送成功
//				}
//			}else{
//				this.logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常: result为空!");
//				Profiler.functionError(info);
//				return 0;
//			}
            return 1;//表示推送成功
        }catch(Exception e){
            logger.error("调用库管接口stockExportManager.insertStockVirtualIntOut异常", e);
            Profiler.functionError(info);
            return 0;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private CallerParam getCallerParam(){
        CallerParam callerParam = new CallerParam();
        callerParam.setBuId(303);
        callerParam.setSystemName("ql.dms");
        callerParam.setNationId("CN");//todo 是否正确
        callerParam.setTimezone("GMT+8");
        callerParam.setLanguage("zh");
        callerParam.setUseDefaultTenant(1);
        try {
            callerParam.setSysIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
           logger.error("获取ip错误");
        }
        Map<String , String> extMap = new HashMap<>();
        extMap.put("currency","CNY");
        callerParam.setExtMap(extMap);
        return callerParam;
    }

    private List<ChuguanVo> getFullStockByBusiNo(String businessNo, Integer businessType, Boolean isQueryHis) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.StockExportManagerImpl.queryChuGuan", false, true);
        List<ChuguanVo> result = null;
        try{
            ChuguanQueryParam chuguanQueryParam = new ChuguanQueryParam();
            chuguanQueryParam.setBusiNo(businessNo);
            chuguanQueryParam.setTypeId(businessType);//todo 现在是空 需要确认
            chuguanQueryParam.setNeedTransData(Boolean.TRUE);
            ChuguanDataResult chuguanDataResult = chuguanExport.queryChuGuan(chuguanQueryParam, getCallerParam());
            if(chuguanDataResult == null || chuguanDataResult.getCode() != 1){
                return null;
            }
            result = chuguanDataResult.getChuguanVoList();
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
        List<ChuguanVo> chuguanVos = getFullStockByBusiNo(waybillCode, null, true);
        KuGuanDomain domain = null;
        List<String> kglist = new ArrayList<String>();
        if (chuguanVos != null && !chuguanVos.isEmpty()) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < chuguanVos.size(); i++) {
                ChuguanVo chuguanVo = chuguanVos.get(i);
                String stockKudanhao = String.valueOf(chuguanVo.getKdanhao().toString());
                kglist.add(stockKudanhao);
                if(StringHelper.isEmpty(kdanhao)){
                    if (i == 0) {
                        domain = convert2KuGuanDomain(chuguanVo);
                    }
                }else{
                    if(kdanhao.equals(stockKudanhao)){
                        domain = convert2KuGuanDomain(chuguanVo);
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

            if(domain!=null) {
                domain.setLblKdanhao(buf.toString());
            }
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
    private KuGuanDomain convert2KuGuanDomain(ChuguanVo nstock){
        KuGuanDomain domain = new KuGuanDomain();
        domain.setWaybillCode(nstock.getOrderId().toString());
        // 库管单号
        domain.setlKdanhao(String.valueOf(nstock.getKdanhao()));
        // 方式
        if (nstock.getChuru() != null){
            domain.setLblWay(nstock.getChuru());
        }
        // 分类
        if (nstock.getFenlei() != null){
            domain.setLblType(nstock.getFenlei());
        }
        // 其他方式
        if (nstock.getQiTaFangShi() != null){
            domain.setLblOtherWay(nstock.getQiTaFangShi());
        }
        // 经办人
        if (nstock.getJingBan() != null){
            domain.setLblJingban(nstock.getJingBan());
        }
        // 业务日期
//		if (nstock.getBusinessTime() != null){todo 老对象 是date
//			domain.setLblDate(nstock.getBusinessTime());
//        }
        // 来源
        if (nstock.getLaiYuan() != null){
            domain.setLblFrom(nstock.getLaiYuan());
        }
        // 款项
        if (nstock.getKuanXiang() != null){
            domain.setLblKuanx(nstock.getKuanXiang());
        }
        // 优惠
        if (nstock.getYouHui() != null){
            domain.setLblYouhui(String.valueOf(nstock.getYouHui()));
        }
        // 运费
        if (nstock.getYunFei() != null){
            domain.setLblYun(String.valueOf(nstock.getYunFei()));
        }
        // 其他
        if (nstock.getQiTaFeiYong() != null){
            domain.setLblOther(String.valueOf(nstock.getQiTaFeiYong()));
        }
        // 总金额
        if (nstock.getZongJinE() != null){
            domain.setLblZjine(String.valueOf(nstock.getZongJinE()));
        }
        // 财务单号
//		domain.setLblCdanhao1(String.valueOf(nstock.getCdanhao()));//todo 新对象没有
        // 订单号
        domain.setLblOrderid(String.valueOf(nstock.getOrderId()));
        // 机构
        if (nstock.getOrgName() != null){
            domain.setLblOrg(String.valueOf(nstock.getOrgName()));
        }
        // 仓库
        domain.setLblStock(String.valueOf(nstock.getSid()));
        // 录入员
        if (nstock.getLuru() != null){
            domain.setLblLuru(String.valueOf(nstock.getLuru()));
        }
        // 自提点
        domain.setLblStation("");
        // 是否签字
        if (nstock.getQianZi().equals(1)){//todo 跟老逻辑不一样
            domain.setLblSure("已签");
        }else{
            domain.setLblSure("未签");
        }
        // 原单号
        if (nstock.getYuanDanHao() != null){
            domain.setLblYdanhao(String.valueOf(nstock.getYuanDanHao()));
        }
        // 备注
        if (nstock.getRemark() != null){
            domain.setLblRemark(String.valueOf(nstock.getRemark()));
        }

        //处理商品明细
//		double zj = 0;
//		int znum= 0;
//		List<KuGuanDomain> listKG = new ArrayList<KuGuanDomain>();
//		List<StockDetailVO> stockDetail = nstock.getStockDetails();//todo 新对象没有
//		for (StockDetailVO sdv : stockDetail) {
//			KuGuanDomain sdvDomain = new KuGuanDomain();
//			// 财务单-列表
//			if (sdv.getCdanhao() != null)
//				sdvDomain.setLblCdanhao(String.valueOf(sdv.getCdanhao()));
//			// 金额-列表
//			if (sdv.getZjine() != null)
//				sdvDomain.setLbljine(String.valueOf(sdv.getZjine()));
//			// 数量-列表
//			sdvDomain.setLblNum(String.valueOf(sdv.getNum()));
//			// 单价-列表
//			sdvDomain.setLblPrice(String.valueOf(sdv.getJiage()));
//			// 商品名称-列表
//			if (sdv.getWare() != null)
//				sdvDomain.setLblWare(sdv.getWare());
//			// 商品id-列表
//			sdvDomain.setLblWareId(String.valueOf(sdv.getWareId()));
//			listKG.add(sdvDomain);
//
//			//计算商品数量、总价
//			String jine = sdvDomain.getLbljine();
//			String num = sdvDomain.getLblNum();
//			if (jine != null && !jine.equals(""))
//				zj += Double.parseDouble(jine);
//			if (num != null && !num.equals(""))
//				znum += Integer.parseInt(num);
//		}
//		domain.setStockDetails(listKG);
//		domain.setLblstatistics("合计：" + znum
//				+ "件商品，共计"+zj+"元");

        return domain;

    }
}

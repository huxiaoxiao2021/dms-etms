package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.utils.ConstantEnums;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.stock.base.CallerParam;
import com.jd.stock.iwms.export.ChuguanExport;
import com.jd.stock.iwms.export.param.ChuguanParam;
import com.jd.stock.iwms.export.param.ChuguanQueryParam;
import com.jd.stock.iwms.export.result.ChuguanDataResult;
import com.jd.stock.iwms.export.result.ChuguanResult;
import com.jd.stock.iwms.export.vo.ChuguanDetailVo;
import com.jd.stock.iwms.export.vo.ChuguanVo;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service("chuguanExportManager")
public class ChuguanExportManagerImpl implements ChuguanExportManager{

    private final static Logger log = LoggerFactory.getLogger(ChuguanExportManagerImpl.class);

    @Autowired
    private ChuguanExport chuguanExport;

    private static final String QUERY_FIELD_WAYBILLCODE = "waybillCode";
    private static final String QUERY_FIELD_LKDANHAO = "lKdanhao";

    @Override
    public int insertChuguan(List<ChuguanParam> chuguanParamList) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.ChuguanExportManagerImpl.insertChuguan", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try{
            CallerParam callerParam = getCallerParam();
            ChuguanResult result = chuguanExport.insertChuguan(chuguanParamList, callerParam);
            if(result == null){
                log.warn("新出管接口写入返回null-chuguanParamList[{}][{}]", JsonHelper.toJson(chuguanParamList),JsonHelper.toJson(callerParam));
                return 0;
            }
            if(result.getCode() != 1){
                log.warn("新出管接口写入返回失败chuguanParamList[{}]callerParam[{}]result[{}]"
                        ,JsonHelper.toJson(chuguanParamList),JsonHelper.toJson(callerParam),JsonHelper.toJson(result));
                return 0;
            }
            if(log.isDebugEnabled()){
                log.debug("新出管接口写入成功-chuguanParamList[{}]result[{}]",JsonHelper.toJson(chuguanParamList),JsonHelper.toJson(result));
            }
            return 1;//表示推送成功
        }catch(Exception e){
            log.error("新出管接口写入报错chuguanParamList[{}]",JsonHelper.toJson(chuguanParamList),e);
            Profiler.functionError(info);
            return 0;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 出管国际化参数
     * https://cf.jd.com/pages/viewpage.action?pageId=165577572
     * @return
     */
    private CallerParam getCallerParam(){
        CallerParam callerParam = new CallerParam();
        callerParam.setBuId(301);//http://git.jd.com/mall-tp/tp-csr/blob/master/docs/common_parameters.md
        callerParam.setNationId("CN");//国家码
        callerParam.setTimezone("GMT+8");//时区
        callerParam.setLanguage("zh");//语言
        callerParam.setSystemName("ql.dms");
        callerParam.setUseDefaultTenant(1);//1使用默认租户，不需要传递商品租户和订单租户
        try {
            callerParam.setSysIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
           log.error("获取ip错误",e);
        }
        Map<String , String> extMap = new HashMap<>();
        extMap.put("currency","CNY");
        callerParam.setExtMap(extMap);
        return callerParam;
    }

    /**
     * 查询出管数据；查询逆向物流的数据。
     * @param businessNo 业务单号 可能是订单号
     * @return
     */
    private List<ChuguanVo> getFullStockByBusinNo(String businessNo,ConstantEnums.ChuGuanTypeId chuGuanTypeId) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.ChuguanExportManagerImpl.queryChuGuan", Constants.UMP_APP_NAME_DMSWEB, false, true);
        List<ChuguanVo> result = null;
            ChuguanQueryParam chuguanQueryParam = new ChuguanQueryParam();
        try{
            chuguanQueryParam.setBusiNo(businessNo);

            // 此字对应于com.jd.stock.iwms.export.param.ChuguanParam 的 typeId业务类型Id；
            // 暂时用ContantsEnum.ChuGuanTypeId.REVERSE_LOGISTICS_OUT 这个是写入出管的时候 出使用typeid值。
            // 理论上用入和出typeId字段值都能查到数据。详情参看 insertChuguan方法的调用时传入的参数
            chuguanQueryParam.setTypeId(chuGuanTypeId.getType());
            chuguanQueryParam.setNeedTransData(Boolean.TRUE);
            ChuguanDataResult chuguanDataResult = chuguanExport.queryChuGuan(chuguanQueryParam, getCallerParam());
            if(chuguanDataResult == null || chuguanDataResult.getCode() != 1){
                log.warn("新出管接口调用返回失败chuguanQueryParam[{}]chuguanDataResult[{}]",
                        JsonHelper.toJson(chuguanQueryParam),JsonHelper.toJson(chuguanDataResult));
                return null;
            }
            result = chuguanDataResult.getChuguanVoList();
            if(result != null){
                log.warn("新出管接口调用成功-有数据chuguanQueryParam[{}]chuguanDataResult[{}]"
                        ,JsonHelper.toJson(chuguanQueryParam),JsonHelper.toJson(chuguanDataResult));
            }else {
                log.warn("新出管接口调用成功-无返回数据chuguanQueryParam[{}]",JsonHelper.toJson(chuguanQueryParam));
            }

        }catch(Exception e){
            log.error("新出管接口调用chuguanQueryParam[{}]",JsonHelper.toJson(chuguanQueryParam),e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    @Override
    public KuGuanDomain queryByOrderCode(String orderCode,String lKdanhao) {

        //由于出管限制了 typeId业务类型Id。如果需要查询逆向物流场景的数据，那就把所有的typeId 枚举的场景都查询一遍。因为不确定 写入出管 的TypeId 是什么
        List<ChuguanVo> chuguanVos = getFullStockByBusinNo(orderCode,ConstantEnums.ChuGuanTypeId.REVERSE_LOGISTICS_GOODS_REJECTION);
        if(chuguanVos == null){
            chuguanVos = getFullStockByBusinNo(orderCode,ConstantEnums.ChuGuanTypeId.REVERSE_LOGISTICS_MONEY_REJECTION);
        }

        if(chuguanVos == null){//如果逆向物流的业务类型没有数据，那就查询 内配出库的数据
            chuguanVos = getFullStockByBusinNo(orderCode,ConstantEnums.ChuGuanTypeId.NEIPEI_OUT);
        }
        KuGuanDomain domain = null;
        if (chuguanVos != null && !chuguanVos.isEmpty()) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < chuguanVos.size(); i++) {
                ChuguanVo chuguanVo = chuguanVos.get(i);
                String stockKudanhao = String.valueOf(chuguanVo.getKdanhao().toString());
                if(StringHelper.isEmpty(lKdanhao)){
                    if (i == 0) {
                        domain = convert2KuGuanDomain(chuguanVo);
                    }
                }else{
                    if(lKdanhao.equals(stockKudanhao)){
                        domain = convert2KuGuanDomain(chuguanVo);
                    }
                }

                //组装库管单号超链接
                if(stockKudanhao.equals(lKdanhao)){
                    buf.append("<a href=list?waybillCode=" + orderCode
                            + "&lKdanhao="+stockKudanhao+" style='color:red;text-align:center' >" + stockKudanhao + "</a>&nbsp;");
                }else{
                    buf.append("<a href=list?waybillCode=" + orderCode
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

        KuGuanDomain result = null;
        try {
            result = this.queryByOrderCode(waybillCode,null);
            if(log.isDebugEnabled()){
                log.debug("根据订单号获取库管单信息-结束waybillCode[{}]result[{}]",waybillCode, JsonHelper.toJson(result));
            }
        } catch (Exception e) {
            log.error("根据订单号获取库管单信息服务异常waybillCode[{}]",waybillCode, e);
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
        //	业务类型Id
        domain.setTypeId(nstock.getTypeId());
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
		if (nstock.getBusinessTime() != null){
			domain.setLblDate(DateHelper.formatDate(nstock.getBusinessTime(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        }
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
//		domain.setLblCdanhao1(String.valueOf(nstock.getCdanhao()));//出管不给了
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
        if (nstock.getQianZi().equals(1)){
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
		double zj = 0;
		int znum= 0;
		List<KuGuanDomain> listKG = new ArrayList<KuGuanDomain>();
		List<ChuguanDetailVo> stockDetail = nstock.getChuguanDetailVos();
		for (ChuguanDetailVo sdv : stockDetail) {
			KuGuanDomain sdvDomain = new KuGuanDomain();
			// 财务单-列表
//			if (sdv.getCdanhao() != null)
//				sdvDomain.setLblCdanhao(String.valueOf(sdv.getCdanhao()));
			// 金额-列表
			if (sdv.getZongJinE() != null){
				sdvDomain.setLbljine(sdv.getZongJinE().toString());
            }
			// 数量-列表
			sdvDomain.setLblNum(String.valueOf(sdv.getNum()));
			// 单价-列表
			sdvDomain.setLblPrice(StringHelper.getStringValue(sdv.getJiaGe()));
			// 商品名称-列表
			if (sdv.getSkuName() != null){
			    sdvDomain.setLblWare(sdv.getSkuName());
            }
			// 商品id-列表
			sdvDomain.setLblWareId(StringHelper.getStringValue(sdv.getSkuId()));
			listKG.add(sdvDomain);

			//计算商品数量、总价
			String jine = sdvDomain.getLbljine();
			String num = sdvDomain.getLblNum();
			if (jine != null && !jine.equals("")){
				zj += Double.parseDouble(jine);
            }
			if (num != null && !num.equals("")){
				znum += Integer.parseInt(num);
            }
		}
		domain.setStockDetails(listKG);
		domain.setLblstatistics("合计：" + znum
				+ "件商品，共计"+zj+"元");

        return domain;

    }
}

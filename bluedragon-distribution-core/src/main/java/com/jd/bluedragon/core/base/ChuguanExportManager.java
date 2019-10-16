package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.stock.iwms.export.param.ChuguanParam;

import java.util.List;

/**
 * 出管新接口代理
 * 相关cf
 * https://cf.jd.com/pages/viewpage.action?pageId=165577089 国际化-查询出管数据
 * https://cf.jd.com/pages/viewpage.action?pageId=165577134 国际化-写入出管数据
 * https://cf.jd.com/pages/viewpage.action?pageId=165578101 国际化-写入出管数据-新老接口对应关系以及属性映射关系
 * https://cf.jd.com/pages/viewpage.action?pageId=165577572 国际化-CallerParam
 * 此接口是国际化接口，需要通过公司藏经阁审批后才能调用
 * 藏经阁接入 https://cf.jd.com/pages/viewpage.action?pageId=180381121
 * @author : xumigen
 * @date : 2019/9/27
 */
public interface ChuguanExportManager {

    /**
     * 出管写入接口
     * 参考新接口 https://cf.jd.com/pages/viewpage.action?pageId=165577134
     * 与老接口属性 映射 https://cf.jd.com/pages/viewpage.action?pageId=165578101
     * @param chuguanParamList
     * @return
     */
    int insertChuguan(List<ChuguanParam> chuguanParamList);

    /*******************方法扩展区*********************/
    KuGuanDomain queryByOrderCode(String orderCode,String lKdanhao);

    /**
     * 从出管查询逆向物流类型的数据
     * @param waybillCode 订单号
     * @return 返回一个KuGuanDomain对象,如果查不到对应运单的库管对象,则返回null
     */
    KuGuanDomain queryByWaybillCode(String waybillCode);
}

package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.stock.iwms.export.param.ChuguanParam;

import java.util.List;
import java.util.Map;

/**
 * 出管新接口代理
 * 相关cf
 * https://cf.jd.com/pages/viewpage.action?pageId=165577089
 * https://cf.jd.com/pages/viewpage.action?pageId=165577134
 * https://cf.jd.com/pages/viewpage.action?pageId=165578101
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
    long insertChuguan(List<ChuguanParam> chuguanParamList);

    /*******************方法扩展区*********************/
    KuGuanDomain queryByParams(Map<String, Object> paramMap);

    /**
     * 已经做了异常处理,只以运单号做库查询条件
     * @param waybillCode
     * @return 返回一个KuGuanDomain对象,如果查不到对应运单的库管对象,则返回null
     */
    KuGuanDomain queryByWaybillCode(String waybillCode);
}

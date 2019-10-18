package com.jd.bluedragon.external.crossbow.pdd.manager;

import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDRequest;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 *     拼多多接口调用的引用类，通过物流组件 crossbow进行连接，
 *     所有拼多多的接口应该统一由该接口实例化：
 *     例如：
 *          拼多多有两个提供给京东两个接口：1.面单获取接口； 2.面单余额查询接口.
 *          因为他们由拼多多公司统一提供，所以其接口的request对象是统一的，可以共用，可由该类统一实例化
 *          实例化的过程可由spring代理，表现为配置多个bean
 *
 *          PDDBusinessManager interface1 = new PDDBusinessManager();
 *          interface1.setWpCode("wpCode1");
 *          interface1.setSecret("secret1");
 *          interface1.setCrossbowConfig(crossbowConfig1);
 *          interface1.doRestInterface(condition1);
 *
 *          PDDBusinessManager interface2 = new PDDBusinessManager();
 *          interface2.setWpCode("wpCode2");
 *          interface2.setSecret("secret2");
 *          interface2.setCrossbowConfig(crossbowConfig2);
 *          interface2.doRestInterface(condition2);
 *
 *
 *     目前对接接口
 *     1. 拼多多面单信息获取接口地址：
 *          测试环境：http://express-channel-api.test.yiran.com/waybill/detail/query
 *          正式环境：http://express.pinduoduo.com/waybill/detail/query
 *
 * @author wuzuxiang
 * @since 2019/10/17
 **/
public class PDDBusinessManager extends AbstractCrossbowManager<PDDRequest, PDDResponse<PDDWaybillDetailDto>> {

    /**
     * 拼多多公司给京东物流公司定义的物流公司编码，用于接口的合法调用
     */
    private String wpCode;

    /**
     * 用于生成拼多多公司的请求前面的秘钥
     */
    private String secret;

    @Override
    protected PDDRequest getMyRequestBody(Object parameter) {
        PDDRequest pddRequest = new PDDRequest();
        pddRequest.setWpCode(this.wpCode);
        pddRequest.setLogisticsInterface(JsonHelper.toJson(parameter));
        String dataDigest = new String(Base64.encodeBase64(Md5Helper.getMD5(pddRequest.getLogisticsInterface() + secret)), StandardCharsets.UTF_8);
        pddRequest.setDataDigest(dataDigest);
        return pddRequest;
    }

    public String getWpCode() {
        return wpCode;
    }

    public void setWpCode(String wpCode) {
        this.wpCode = wpCode;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

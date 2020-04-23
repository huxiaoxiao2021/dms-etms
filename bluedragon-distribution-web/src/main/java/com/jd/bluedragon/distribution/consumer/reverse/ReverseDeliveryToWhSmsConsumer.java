package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.whems.Ems4JingDongPortType;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by wuzuxiang on 2017/2/7.
 */
@Service("reverseDeliveryToWhSmsConsumer")
@Deprecated
public class ReverseDeliveryToWhSmsConsumer extends MessageBaseConsumer{

    private static final Log logger = LogFactory.getLog(ReverseDeliveryToWhSmsConsumer.class);

    @Autowired
    private Ems4JingDongPortType whemsClientService;

    @Autowired
    ReverseDeliveryService reverseDelivery;

    @Override
    public void consume(Message message) throws Exception {
        this.logger.info("反向推送武汉邮政的自消费，内容为：" + message.getText());
        if(message == null || "".equals(message.getText()) || null == message.getText()){
            this.logger.warn("推送武汉邮政的消息体内容为空");
            return;
        }
        String body = message.getText();
        String whEmsKey = PropertiesHelper.newInstance().getValue(
                "encpKey");
        if (StringHelper.isEmpty(whEmsKey))
            return;
        String emsstring = null;
        String md5tempstring = encrypt(body + whEmsKey.trim());
        CallerInfo info = Profiler.registerInfo("dms.web.consumer.ReverseDeliveryToWhSmsConsumer",false,true);//方法监控开始
        try {
            emsstring = whemsClientService
                    .sendMsg("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                            + "<Response><ActionCode>03</ActionCode><ParternCode>WHEMS</ParternCode>"
                            + "<ProductProviderID>360BUY</ProductProviderID><ValidationData>"
                            + md5tempstring
                            + "</ValidationData>"
                            + body + "</Response>");
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("推送武汉邮政运单数据，接口异常",e);
            if (e instanceof SocketException){
                logger.error("推送武汉邮政运单数据，接口超时 :socket time out");
                reverseDelivery.pushWhemsWaybill(message.getBusinessId());//消息的业务主键就是运单号
            }
        }
        if (logger.isInfoEnabled()){
            logger.info("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                    + "<Response><ActionCode>03</ActionCode><ParternCode>WHEMS</ParternCode>"
                    + "<ProductProviderID>360BUY</ProductProviderID><ValidationData>"
                    + md5tempstring
                    + "</ValidationData>"
                    + body + "</Response>");
        }
        Profiler.registerInfoEnd(info);//方法监控结束
        if (null == emsstring || "".equals(emsstring.trim())) {
            this.logger
                    .warn("DmsToTmsTaskImpl!ReverseDeliveryToWhSmsConsumer WuHan CXF return null :" + message.getBusinessId());
            return;
        }
        this.logger.info(message.getBusinessId() + "武汉邮政返回" + emsstring);
        String str = emsstring.substring(emsstring.indexOf("<ResultCode>")==-1?0:emsstring.indexOf("<ResultCode>"));
        long keyword4 = -1;//接口返回的是否成功的标识
        if (str.indexOf("<ResultCode>000</ResultCode>") != -1) {
            this.logger
                    .info("reverseDeliveryToWhSmsConsumer! 接受邮政返回的数据 000=交易成功 :" + message.getBusinessId());
            keyword4 = 000;
        }else if (str.indexOf("<ResultCode>001</ResultCode>") == -1) {
            this.logger
                    .warn("reverseDeliveryToWhSmsConsumer! 接受邮政返回的数据 001=验证失败 :" + message.getBusinessId());
            keyword4 = 001;
        }else if(str.indexOf("<ResultCode>002</ResultCode>") != -1){
            String strCode = str.substring(str.indexOf("<ResultCode>") + "<ResultCode>".length(),str.indexOf("</ResultCode>"));
            this.logger
                    .warn("reverseDeliveryToWhSmsConsumer! 接受邮政返回的数据 002=接受数据失败 :" + message.getBusinessId()
                            + findReason(strCode)) ;
            keyword4 = 002;
        }else if(str.indexOf("<ResultCode>003</ResultCode>") != -1){
            this.logger
                    .warn("reverseDeliveryToWhSmsConsumer! 接受邮政返回的数据 003=没有可接受的数据 :" + message.getBusinessId());
            keyword4 = 003;
        }
        //记录systemLog 方便查询 参数顺序依次为 1.waybillCode，2.推送给武汉邮政的数据报文，3.mq的topic，4.接口返回的code，5.武汉邮政返回的报文，6.自定义的type
        SystemLogUtil.log(message.getBusinessId(),body,"bd_dms_whSms_mq",keyword4,emsstring,89757L);

    }

    public static String encrypt(String mingwen) {
        Base64 base64=new Base64();
        String result="";
        try {
            result = new String(base64.encode(md5(mingwen).getBytes("utf-8")), Charset.forName("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String md5(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "1";
        }
        return new BigInteger(1, md.digest()).toString(16);
    }

    private String findReason(String resultCode){
        String resultText = "";
        Integer code = 0;
        try{
            code = Integer.parseInt(resultCode);
        }catch (NumberFormatException e){
            code = 006;//resultCode解析失败
        }
        switch (code){
            case 0000 : resultText = "订单不存在";break;
            case 0001 : resultText = "订单不属于贵公司";break;
            case 0002 : resultText = "订单已经配送成功无法操作";break;
            case 0003 : resultText = "内部处理异常";break;
            case 0004 : resultText = "运单号已经存在";break;
            case 0005 : resultText = "增加订单运单关联失败";break;
            default   : resultText = "Unknown Reason";break;
        }
        return resultText;
    }

}

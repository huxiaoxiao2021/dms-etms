package com.jd.bluedragon.external.crossbow.pdd.domain;

/**
 * <p>
 *     拼多多服务的请求参数
 *
 * @author wuzuxiang
 * @since 2019/10/14
 **/
public class PDDRequest {

    /**
     * 物流公司编码; 京东为JD
     */
    private String wpCode;

    /**
     * <p>
     * 为保证数据的正确性和安全性，在对接接口前，拼多多将会与每一家物流公司约定一个密钥（secret），
     * 在数据传输时附上利用JSON字符串格式的数据体（logisticsInterface）和密钥计算得到的摘要（dataDigest）。
     * 双方接收到请求后，都需要先验证摘要的正确性，验证通过后再进行相应的业务处理。
     * </p>
     *
     * <doc>
     * 摘要的计算方法
     * 先将logisticsInterface与secret(测试环境secret:TEST_SECRET_KEY)拼接，再对拼接后的字符串先后进行md5加密和base64编码，得到长度为24字节的base64字符串。
     * </doc>
     *
     * <doc>
     *      example:
     *          假设，logisticsInterface内容为:JSON，secret为:123456，
     *          那么，用于计算摘要的plainText字符串为:JSON123456，
     *          计算结果为:yhmQhg2ZiWCMc91nH0/vsg==
     *
     * </doc>
     *
     * @see
     */
    private String dataDigest;

    /**
     * 用于实际业务方法执行的参数对象，为json对象
     */
    private String logisticsInterface;

    public String getWpCode() {
        return wpCode;
    }

    public void setWpCode(String wpCode) {
        this.wpCode = wpCode;
    }

    public String getDataDigest() {
        return dataDigest;
    }

    public void setDataDigest(String dataDigest) {
        this.dataDigest = dataDigest;
    }

    public String getLogisticsInterface() {
        return logisticsInterface;
    }

    public void setLogisticsInterface(String logisticsInterface) {
        this.logisticsInterface = logisticsInterface;
    }
}

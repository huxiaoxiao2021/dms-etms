package com.jd.bluedragon.dms.utils;

import com.commons.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 包括 head(头部)，reqInfo(请求部分)，respInfo(响应部分)
 */
public class SecurityLog {

    private static final Logger securityLog = LoggerFactory.getLogger("security.log");

    public static final String LOCALHOST = "127.0.0.1";
    public static final String ACCOUNTNAME = "pin";
    private final static String SYSTEMNAME = "QLFJZXJT";
    private final static String APPNAME = "dms.etms";

    private HeadLogSecurityInfo head;
    private ReqLogSecurityInfo reqInfo;
    private RespLogSecurityInfo respInfo;

    public SecurityLog(HeadLogSecurityInfo head, ReqLogSecurityInfo reqInfo, RespLogSecurityInfo respInfo) {
        this.head = head;
        this.reqInfo = reqInfo;
        this.respInfo = respInfo;
    }

    /**
     * 操作枚举值
     * 0：添加; 1：删除; 2：更新; 3：查询; 4：导出 必填；
     */
    public enum OpTypeEnum{
        ADD,DELETE,UPDATE,QUERY,EXPORT;
    }

    /**
     * 账户类型 ：1：erp; 2：passport; 3：selfCreated（自建）;4: JSF调用者;5: 定时任务
     */
    public enum AccountTypeEnum{
        NULL,ERP,PASSPORT,SELFCREATED,JSFCALL,SCHEDULE;
    }
    public HeadLogSecurityInfo getHead() {
        return head;
    }

    public ReqLogSecurityInfo getReqInfo() {
        return reqInfo;
    }

    public RespLogSecurityInfo getRespInfo() {
        return respInfo;
    }

    public void setHead(HeadLogSecurityInfo head) {
        this.head = head;
    }

    public void setReqInfo(ReqLogSecurityInfo reqInfo) {
        this.reqInfo = reqInfo;
    }

    public void setRespInfo(RespLogSecurityInfo respInfo) {
        this.respInfo = respInfo;
    }

    /**
     * 头域：包含系统名称、应用名称、接口名称、接口名称、时间、用户标识、操作行为等通用信息
     */
    public static  class HeadLogSecurityInfo {

        //本日志格式的版本号,必填 例如 V2；；
        private String version;
        // 系统英文名称；必填；与jone或jdos一致。jone系统名称，对应系统信息中的系统英文名称字段。jdos系统名称，对应基本信息中的所属系统字段。；
        private String systemName;
        // 应用英文名称； 必填；jone应用名称，对应基本信息中的应用英文名称字段。jdos应用名称，对应应用信息中的应用标识字段。
        private String appName;
        // 接口名称； 必填；例如/xxx/yyy/zzz
        private String interfaceName;
        //唯一ID； 保证同一个应用是唯一的。当接口分为多个记录返回同一个查询信息时，可通过唯一ID的方式做关联 例如 b0d34ac3-0d10-4278-8ee4-8a9722ea25a5
        private String uuid;
        //当前业务操作时间，长度为10位的数值型（单位：秒）---必填
        private Long time;
        //部署机器ip，点分制；必填；
        private String serverIp;
        //客户端ip,点分制；非必填;
        private String clientIp;
        //账号类型；1：erp; 2：passport; 3：selfCreated（自建）必填；
        private Integer accountType;
        //账号名称；必填；中文的；
        private String accountName;
        //具体操作行为：0：添加; 1：删除; 2：更新; 3：查询; 4：导出 必填；
        private Integer op;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getSystemName() {
            return systemName;
        }

        public void setSystemName(String systemName) {
            this.systemName = systemName;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getInterfaceName() {
            return interfaceName;
        }

        public void setInterfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public String getServerIp() {
            return serverIp;
        }

        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }

        public String getClientIp() {
            return clientIp;
        }

        public void setClientIp(String clientIp) {
            this.clientIp = clientIp;
        }

        public Integer getAccountType() {
            return accountType;
        }

        public void setAccountType(Integer accountType) {
            this.accountType = accountType;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public Integer getOp() {
            return op;
        }

        public void setOp(Integer op) {
            this.op = op;
        }

    }

    /**
     * 请求日志类
     * 添加、删除、更新、查询、导出的应用场景不同，请求部分内容亦不相同，现对请求部分做约定如下：
     * 1) 查询、导出、删除、更新的请求部分填写搜索条件，如订单系统，请求部分可能包含手机号、搜索时间范围、订单类型等
     * 例如："reqInfo":{"telephone":"13301288089",‘“timeFrom”:0123456789, ‘“timeTO”:0123456789};
     * 2) 添加的请求部分填写为空，即不用填写任何信息。
     */
    public static class ReqLogSecurityInfo {
        //员工账号 必填；
        private String erpId;
        //操作开始时间（北京时间），数值型10位.；单位：秒；
        private Long timeFrom;
        //操作终止时间（北京时间），数值型10位.；单位：秒；
        private long timeTo;
        //京东用户账号 NO MD5
        private String pin;
        //订单号 NO MD5
        private String orderId;
        //运单号 NO MD5
        private String carryBillId;
        //商家ID NO MD5
        private Integer venderId;
        //收货地址省份编号 NO MD5
        private Integer addrProvinceId;
        //收货地址城市编号 NO MD5
        private Integer addrCityId;
        //收货地址区县编号 NO MD5
        private Integer addrCountyId;
        //仓库编号 NO MD5
        private Integer storeId;
        //商品编号 NO MD5
        private String skuId;
        //订单类型 NO MD5
        private String orderCd;
        //配送方式 NO MD5
        private String carryBillCd;
        //售后单号 NO MD5
        private String afsOrdId;
        //商品类型 NO MD5
        private String itemCate;
        //手机号  MD5
        private String telephone;
        //邮箱  MD5
        private String email;

        public String getErpId() {
            return erpId;
        }

        public void setErpId(String erpId) {
            this.erpId = erpId;
        }

        public Long getTimeFrom() {
            return timeFrom;
        }

        public void setTimeFrom(Long timeFrom) {
            this.timeFrom = timeFrom;
        }

        public long getTimeTo() {
            return timeTo;
        }

        public void setTimeTo(long timeTo) {
            this.timeTo = timeTo;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getCarryBillId() {
            return carryBillId;
        }

        public void setCarryBillId(String carryBillId) {
            this.carryBillId = carryBillId;
        }

        public Integer getVenderId() {
            return venderId;
        }

        public void setVenderId(Integer venderId) {
            this.venderId = venderId;
        }

        public Integer getAddrProvinceId() {
            return addrProvinceId;
        }

        public void setAddrProvinceId(Integer addrProvinceId) {
            this.addrProvinceId = addrProvinceId;
        }

        public Integer getAddrCityId() {
            return addrCityId;
        }

        public void setAddrCityId(Integer addrCityId) {
            this.addrCityId = addrCityId;
        }

        public Integer getAddrCountyId() {
            return addrCountyId;
        }

        public void setAddrCountyId(Integer addrCountyId) {
            this.addrCountyId = addrCountyId;
        }

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getOrderCd() {
            return orderCd;
        }

        public void setOrderCd(String orderCd) {
            this.orderCd = orderCd;
        }

        public String getCarryBillCd() {
            return carryBillCd;
        }

        public void setCarryBillCd(String carryBillCd) {
            this.carryBillCd = carryBillCd;
        }

        public String getAfsOrdId() {
            return afsOrdId;
        }

        public void setAfsOrdId(String afsOrdId) {
            this.afsOrdId = afsOrdId;
        }

        public String getItemCate() {
            return itemCate;
        }

        public void setItemCate(String itemCate) {
            this.itemCate = itemCate;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

    /**
     *  应答部分是查询、导出、更新、删除、添加等操作返回的内容或状态。为了数据安全，不可记录数据的详细内容
     * （必须严格遵守），只记录本次操作的唯一标识，
     */
    public static  class RespLogSecurityInfo  {

        //记录操作是否成功：0 ：成功 ;-1：失败
        private Integer status;

        //若返回成功，记录条数 如返回失败，填为0 必填；
        private Long recordCnt = 0L;

        // 记录的唯一标识
        //uniqueIdentifier唯一标识
        //字段约定如下：
        // 1）通过唯一标识必须能够反查出本记录的详细信息，如通过订单号可以获取到商品名称、订购人、订购时间等信息。
        // 2）如唯一标识不在uniqueIdentifier 列表中，业务方应联系安全接口人，由安全部统一分配相关字段。
        // 3）查询、导出、删除、更新的响应部分填写唯一标识。
        // 4）添加的响应部分亦填写唯一标识。
        // 5）批量查询应返回全部记录，每条记录可以包含多个唯一标识，
        // 例如示例填写了accountName和orderID字段，导出或查询出来共10条记录，只取第一条和最后一条记录(最终uniqueIdentifier只保留2条记录)："uniqueIdentifier": [{"accountName": "张三"," orderID ": "1238765876"},{"accountName": "李四","orderNumber": "1238765876"}]
        private List<UniqueIdentifier> uniqueIdentifier = new ArrayList<UniqueIdentifier>();

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Long getRecordCnt() {
            return recordCnt;
        }

        public void setRecordCnt(Long recordCnt) {
            this.recordCnt = recordCnt;
        }

        public List<UniqueIdentifier> getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public void setUniqueIdentifier(List<UniqueIdentifier> uniqueIdentifier) {
            this.uniqueIdentifier = uniqueIdentifier;
        }

        public static class UniqueIdentifier{

            public UniqueIdentifier() {
            }

            /**
             * 运单号
             */
            private String carryBillId;
            /**
             * 订单号
             */
            private String orderId;

            public String getCarryBillId() {
                return carryBillId;
            }

            public void setCarryBillId(String carryBillId) {
                this.carryBillId = carryBillId;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }
        }
    }


    /**
     * 上报安全日志
     * @param interfaceName
     * @param erpOp
     * @param carryBill
     */
    public static void reportSecurityLog(String interfaceName,String erpOp,String carryBill) throws UnknownHostException {
        String log = makeParamForSecurityLog(interfaceName,erpOp,carryBill);
        securityLog.info(log);
    }

    /**
     * 构建日志信息
     * @param interfaceName 接口信息
     * @param erpOp 操作人ERP
     * @param carryBill 运单号
     * @return
     * @throws UnknownHostException
     */
    public static String makeParamForSecurityLog(String interfaceName,String erpOp,String carryBill) throws UnknownHostException {
        //头部信息
        SecurityLog.HeadLogSecurityInfo head = new SecurityLog.HeadLogSecurityInfo();
        head.setOp(SecurityLog.OpTypeEnum.QUERY.ordinal());
        head.setInterfaceName(interfaceName);
        head.setTime(new Date().getTime()/1000);
        head.setServerIp(InetAddress.getLocalHost().getHostAddress());
        head.setSystemName(SYSTEMNAME);
        head.setAppName(APPNAME);
        head.setClientIp(SecurityLog.LOCALHOST);
        head.setVersion("V1.0");
        head.setAccountName(SecurityLog.ACCOUNTNAME);
        head.setAccountType(SecurityLog.AccountTypeEnum.ERP.ordinal());

        //请求信息
        SecurityLog.ReqLogSecurityInfo reqInfo = new SecurityLog.ReqLogSecurityInfo();
        reqInfo.setErpId(erpOp);
        reqInfo.setTimeFrom(new Date().getTime()/1000);
        reqInfo.setTimeTo(new Date().getTime()/1000);


        //返回信息
        SecurityLog.RespLogSecurityInfo respInfo = new SecurityLog.RespLogSecurityInfo();
        respInfo.setStatus(0);
        respInfo.setRecordCnt(1L);
        SecurityLog.RespLogSecurityInfo.UniqueIdentifier uniqueIdentifier = new SecurityLog.RespLogSecurityInfo.UniqueIdentifier();
        uniqueIdentifier.setCarryBillId(carryBill);
        respInfo.setUniqueIdentifier(Arrays.asList(uniqueIdentifier));

        SecurityLog securityLog = new SecurityLog(head,reqInfo,respInfo);

        return JsonHelper.toJson(securityLog);
    }
}


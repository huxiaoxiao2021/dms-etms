package com.jd.bluedragon.utils.log;

/**
 * business.log.jd.com 记录日志使用的系统编码，业务类型，操作类型 枚举类
 * 配置指引：https://cf.jd.com/pages/viewpage.action?pageId=146201361
 */
public class BusinessLogConstans {

    /**
     * 是在http://business.log.jd.com/》配置管理》系统接入 中定义的系统编码；
     * 配置指引：https://cf.jd.com/pages/viewpage.action?pageId=146201361
     */
    public enum SourceSys{

        /**
         * 应用场景：
         *      rest请求入参出参记录，接口交互日志，菜单点击量。。。
         *      只面向研发看的日志场景。在这里查询到http://business.log.jd.com/#
         */
        DMS_WEB(1,"DMS.WEB"),

        /**
         * 应用场景：
         *      离线日志，分拣实操日志，打印数据
         *      使用分拣web的人可以搜索到的日志。换句话说可以在http://dms.etms.jd.com/businesslog/goListPage 这里看到。(在这里也能查询到http://business.log.jd.com/#)
         *      需要用户看到的日志可以使用此系统编码。可以认为与DMS_WEB是两个系统。
         * tips:此系统编码是为了统一分拣原先的SystemLog、cassandrar日志、OperationLog、OfflineLog而定义的。 因为这些日志在分拣web有挂功能菜单，有用户使用。
         * 所以需要区别出来。
         */
        DMS_OPERATE(112,"实操日志");

        /**
         * http://business.log.jd.com/》配置管理》系统接入 中定义的系统编码；
         */
        private int code;
        private String text;

        SourceSys(int code, String text) {
            this.code = code;
            this.text = text;
        }

        public int getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * 添加枚举前 要定义好使用的 com.jd.bluedragon.utils.log.BusinessLogConstans.SourceSys
     * http://business.log.jd.com/》配置管理》业务类型配置； 中定义的业务类型；
     * 配置指引：https://cf.jd.com/pages/viewpage.action?pageId=146201361
     */
    public enum BizTypeEnum{
        SEND(SourceSys.DMS_OPERATE,100,"发货"),
        SORTING(SourceSys.DMS_OPERATE,700,"分拣"),
        OUTER_WAYBILL_EXCHANGE(SourceSys.DMS_OPERATE,1900,"外单换单"),
        TASK(SourceSys.DMS_OPERATE,2006,"任务"),
        BOARD(SourceSys.DMS_OPERATE,2005,"组板"),
        WEIGH_WAYBILL(SourceSys.DMS_OPERATE,1901,"快运称重"),
        SEAL(SourceSys.DMS_OPERATE,1011,"封车"),
        DE_SEAL(SourceSys.DMS_OPERATE,1012,"解封车"),
        RETURNS(SourceSys.DMS_OPERATE,2007,"退货"),
        CAR(SourceSys.DMS_OPERATE,2009,"车辆"),
        TRANSFER(SourceSys.DMS_OPERATE,2010,"交接"),
        RECEIVE(SourceSys.DMS_OPERATE,2008,"收货"),
        INSPECTION(SourceSys.DMS_OPERATE,500,"验货"),
        PRINT(SourceSys.DMS_OPERATE,2001,"打印"),
        EXCEPTIONS(SourceSys.DMS_OPERATE,2011,"异常"),
        WAREHOUSING_REVERSE(SourceSys.DMS_OPERATE,2012,"仓储"),
        REVERSE_SPARE(SourceSys.DMS_OPERATE,2019,"备件库"),
        LOAD_CAN(SourceSys.DMS_OPERATE,2013,"装载"),
        AFTER_SALE(SourceSys.DMS_OPERATE,2014,"售后"),
        OTHER_OTHER(SourceSys.DMS_OPERATE,2015,"其他");

        /**
         * 所属系统；
         * 请确定所使用的系统编码；
         */
        private SourceSys sourceSys;
        /**
         * 业务类型编码；
         * 与BizTypeEnum#sourceSys 的映射关系需要在
         * http://business.log.jd.com/》配置管理》业务类型配置;中配置
         */
        private int code;
        private String text;

        BizTypeEnum(SourceSys sourceSys, int code, String text) {
            this.sourceSys = sourceSys;
            this.code = code;
            this.text = text;
        }

        public SourceSys getSourceSys() {
            return sourceSys;
        }

        public int getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * 添加枚举前，首先要定好要使用的com.jd.bluedragon.utils.log.BusinessLogConstans.BizTypeEnum
     * http://business.log.jd.com/》配置管理》操作类型配置；
     * 理论上每一条写日志的地方operateType值都不一样；这样方便查找
     * 配置指引：https://cf.jd.com/pages/viewpage.action?pageId=146201361
      */
    public enum OperateTypeEnum{
        SEND_SEND(BizTypeEnum.SEND,1009,"发货"),
        SEND_OFFLINE(BizTypeEnum.SEND,1012,"离线发货"),
        SEND_CANCEL_SEND(BizTypeEnum.SEND,1010,"取消发货"),
        SEND_SENDREVERSE_SEND(BizTypeEnum.SEND,1008,"逆向发货"),
        SEND_ONECAR_SEND(BizTypeEnum.SEND,1001,"一车一单发货"),
        SEND_ONECAR_OFFLINE(BizTypeEnum.SEND,1013,"离线一车一单发货"),
        SEND_REVERSE_SEND(BizTypeEnum.SEND,1008,"逆向发货"),
        SEND_REVERSE_GETSENDDETIAL(BizTypeEnum.SEND,1014,"亚一逆向获得发货明细"),
        SEND_REVERSE_MOBILE_SEND(BizTypeEnum.SEND,1015,"移动仓内配单发货"),
        SEND_REVERSE_WMS_GETSENDDETIAL(BizTypeEnum.SEND,1016,"WMS逆向获得发货明细"),
        SEND_REVERSE_SPECIAL_SEND(BizTypeEnum.SEND,1017,"逆向发货迷你仓"),
        SEND_REVERSE_ECLP_SEND(BizTypeEnum.SEND,1018,"逆向发货ECLP"),
        SEND_REVERSE_CLPS_SEND(BizTypeEnum.SEND,1019,"逆向发货CLPS"),
        SEND_REVERSE_ECLP_SPWMS_SEND(BizTypeEnum.SEND,1020,"逆向发货ECLP_SPWMS"),
        SEND_REVERSE_SPWMS_SEND(BizTypeEnum.SEND,1021,"逆向发货SPWMS"),
        SEND_PARTNER_WAY_BILL(BizTypeEnum.SEND,1011,"运单号关联包裹信息"),
        SEND_COLDCHAIN_SMS(BizTypeEnum.SEND,1022,"冷链卡班暂存计费发短信"),

        OUTER_WAYBILL_EXCHANGE_WAYBILL(BizTypeEnum.OUTER_WAYBILL_EXCHANGE,1900002,"触发外单换单"),
        TASK_CONSUME_FAIL(BizTypeEnum.TASK,20062,"消费失败落库"),
        TASK_REDIS_TASK(BizTypeEnum.TASK,20063,"Redis任务数据处理"),

        SORTING_CANCEL_SORTING(BizTypeEnum.SORTING,60017,"取消分拣"),
        SORTING_SORTING(BizTypeEnum.SORTING,60016,"分拣"),
        SORTING_PRE_SITE_CHANGE(BizTypeEnum.SORTING,60014,"预分拣站点变更"),
        SORTING_BOXCACHECLEAR(BizTypeEnum.SORTING,60015,"预分拣站点变更"),
        SORTING_REVERSE_SORTING_100SCORE(BizTypeEnum.SORTING,60013,"回传退款100分逆向分拣"),
        SORTING_SORTING_INTERCEPT(BizTypeEnum.SORTING,60018,"分拣拦截"),
        SORTING_OFFLINE_SORTING(BizTypeEnum.SORTING,60011,"离线分拣"),
        SORTING_SORTING_SEAL(BizTypeEnum.SORTING,60012,"分拣封箱"),
        SORTING_SORTING_SEAL_OFFLINE(BizTypeEnum.SORTING,60019,"离线分拣封箱"),

        BOARD_BOARDCOMBINATION(BizTypeEnum.BOARD,20053,"组板"),
        BOARD_DEBOARDCOMBINATION(BizTypeEnum.BOARD,20054,"取消组板"),

        WEIGH_WAYBILL_VALIDWAYBILL(BizTypeEnum.WEIGH_WAYBILL,1901003,"当运单经校验存在时"),
        WEIGH_WAYBILL_INVALIDWAYBILL(BizTypeEnum.WEIGH_WAYBILL,1901004,"当运单经校验不存在时"),
        WEIGH_WAYBILL_OPERATEEXCEPTION(BizTypeEnum.WEIGH_WAYBILL,1901001,"记录操作人引起的异常"),
        WEIGH_WAYBILL_WEIGHTINFOTOWAYBILL(BizTypeEnum.WEIGH_WAYBILL,1901002,"向运单回传包裹称重信息"),

        REVERSE_SPARE_CHUGUAN(BizTypeEnum.REVERSE_SPARE,20091,"推出管"),
        REVERSE_SPARE_SPWMS_REJECT(BizTypeEnum.REVERSE_SPARE,20092,"逆向仓储驳回"),

        SEAL_SEAL(BizTypeEnum.SEAL,1011,"封车"),
        SEAL_FERRY_SEAL(BizTypeEnum.SEAL,1014,"传摆封车"),
        SEAL_OFFLINE_SEAL(BizTypeEnum.SEAL,101104,"离线封车"),

        DE_SEAL_DE_SEAL(BizTypeEnum.DE_SEAL,1012,"解封车"),

        RETURNS_REFUND100(BizTypeEnum.RETURNS,20073,"退款100分"),
        RETURNS_FASTREFUND(BizTypeEnum.RETURNS,20074,"先货先款退款"),
        RETURNS_OVER_AREA_RETURN(BizTypeEnum.RETURNS,20071,"三方超区退货"),
        RETURNS_OVER_AREA_RETURN_OFFLINE(BizTypeEnum.RETURNS,20075,"离线三方超区退货"),

        CAR_IN(BizTypeEnum.CAR,20091,"进出记录进"),
        CAR_OUT(BizTypeEnum.CAR,20092,"进出记录出"),
        CAR_DEPARTURE(BizTypeEnum.CAR,1012,"发车"),

        TRANSFER_TRANSFER(BizTypeEnum.TRANSFER,20101,"交接"),

        RECEIVE_RECEIVE(BizTypeEnum.RECEIVE,20081,"收货"),
        RECEIVE_RECEIVE_OFFLINE(BizTypeEnum.RECEIVE,20087,"离线收货"),
        RECEIVE_REVERSE_AMS_RECEIVE(BizTypeEnum.RECEIVE,20085,"逆向售后收货"),
        RECEIVE_REVERSE_WMS_RECEIVE(BizTypeEnum.RECEIVE,20081,"逆向仓储收货"),
        RECEIVE_REVERSE_RECEIPT(BizTypeEnum.RECEIVE,20082,"逆向收货"),
        RECEIVE_SPARE_REVERSE_RECEIPT(BizTypeEnum.RECEIVE,20083,"逆向备件库收货"),

        INSPECTION_INSPECTION(BizTypeEnum.INSPECTION,5001,"验货"),
        INSPECTION_REVERSE_SPARE_INSPECTION(BizTypeEnum.INSPECTION,5002,"逆向备件库驳回验货"),
        INSPECTION_REVERSE_WMS_REJECT_INSPECTION(BizTypeEnum.INSPECTION,5003,"逆向仓储驳回验货"),
        INSPECTION_SALE_REVERSE_REJECT(BizTypeEnum.INSPECTION,5004,"逆向售后驳回验货"),
        INSPECTION_SORT_CENTER_INSPECTION(BizTypeEnum.INSPECTION,50012,"分拣中心验货"),
        INSPECTION_SORT_CENTER_INSPECTION_OFFLINE(BizTypeEnum.INSPECTION,50013,"离线分拣中心验货"),

        PRINT_PRINT(BizTypeEnum.PRINT,20011,"打印"),
        PRINT_PACKAGE_LABEL_PRINT(BizTypeEnum.PRINT,20112,"包裹标签打印"),
        EXCEPTIONS_DATA_EXCEPTIONS(BizTypeEnum.EXCEPTIONS,20111,"数据异常"),
        WAREHOUSING_REVERSE_WAREHOUSING_REJECT(BizTypeEnum.WAREHOUSING_REVERSE,20121,"逆向仓储驳回"),

        LOAD_CAN_GLOBAL(BizTypeEnum.LOAD_CAN,20131,"取消预装载"),
        AFTER_SALE_REVERSE_REJECT(BizTypeEnum.AFTER_SALE,20141,"逆向售后驳回"),
        RECEIVE_POP_RECEIVE(BizTypeEnum.RECEIVE,20086,"pop上门接货"),
        RECEIVE_POP_RECEIVE_OFFLINE(BizTypeEnum.RECEIVE,20088,"离线pop上门接货"),
        OTHER_OTHER(BizTypeEnum.OTHER_OTHER,20151,"其他"),
        OTHER_OTHER_OFFLINE(BizTypeEnum.OTHER_OTHER,20152,"离线其他");
        /**
         * 业务类型编码；
         */
        private BizTypeEnum bizType;
        /**
         * 操作类型编码；
         * 与OperateTypeEnum#bizType 的映射关系需要在
         * http://business.log.jd.com/》配置管理》操作类型配置；中配置
         */
        private int code;
        private String text;

        OperateTypeEnum(BizTypeEnum bizType, int code, String text) {
            this.bizType = bizType;
            this.code = code;
            this.text = text;
        }

        public BizTypeEnum getBizType() {
            return bizType;
        }

        public int getBizTypeCode(){
            return bizType.getCode();
        }

        public SourceSys getSourceSys(){
            return bizType.getSourceSys();
        }
        public int getSourceSysCode(){
            return bizType.getSourceSys().getCode();
        }

        public int getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }
}

package com.jd.bluedragon.utils;

import java.util.Objects;

/**
 * 枚举 定义
 * @author : xumigen
 * @date : 2019/10/12
 */
public class ConstantEnums {

    /**
     * Iot business字段枚举
     */
    public enum IotBusiness{
        B2C("B2C"),C2C("C2C");
        private String type;

        IotBusiness(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }


    /**
     * 国际化-写入出管数据；
     * 字段 fenLeiId 库存分类编号；枚举
     * https://cf.jd.com/pages/viewpage.action?pageId=165577134
     */
    public enum ChuGuanFenLei{
        PUT_GOODS(1,"放货"),
        RETURN_GOODS(2,"退货"),
        SALE(3,"销售"),
        OTHER(6,"其它");
        private Integer type;
        private String text;

        private ChuGuanFenLei(Integer type, String text) {
            this.type = type;
            this.text = text;
        }

        public Integer getType() {
            return type;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * 出管国际业务类型映射；
     * 字段
     * typeId 业务类型 定义来自 出管
     * text 业务类型描述 定义来自 出管
     * logistics 自定义 是否逆向物流业务
     * https://cf.jd.com/pages/viewpage.action?pageId=175378878
     */
    public enum ChuGuanTypeId{
        REVERSE_LOGISTICS_GOODS_REJECTION(1401,"逆向物流-先货拒收(虚入",true),
        REVERSE_LOGISTICS_MONEY_REJECTION(1402,"逆向物流-先款拒收(虚入",true),
        REVERSE_LOGISTICS_OUT(1403,"逆向物流-虚出",true),
        ORDER_MONEY_OUT(202,"订单出库-先款",false);
        private Integer type;
        private String text;
        private boolean logistics;

        private ChuGuanTypeId(Integer type, String text,boolean logistics) {
            this.type = type;
            this.text = text;
            this.logistics = logistics;
        }

        /**
         *  判断是否是逆向物流 业务类型
         * @param typeValue
         * @return
         */
        public static boolean iSLogisticsTypeId(Integer typeValue){
            for(ChuGuanTypeId item : ChuGuanTypeId.values()){
                if(item.isLogistics() && Objects.equals(item.getType(),typeValue)){
                    return true;
                }
            }
            return false;
        }

        public Integer getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public boolean isLogistics() {
            return logistics;
        }
    }

    /**
     * 国际化-写入出管数据；
     * 字段  rfType 业务类型（财务相关）	枚举
     * https://cf.jd.com/pages/viewpage.action?pageId=165577134
     */
    public enum ChuGuanRfType {
        /**
         * 入
         */
        IN(6),

        /**
         * 出
         */
        Out(2);
        private Integer type;

        ChuGuanRfType(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }

    /**
     * 国际化-写入出管数据；
     * 字段  churuId 库存出入类型编号 枚举
     * https://cf.jd.com/pages/viewpage.action?pageId=165577134
     */
    public enum ChuGuanChuruId{
        IN_KU(1,"入库"),
        OUT_KU(2,"出库");
        private Integer type;
        private String text;

        private ChuGuanChuruId(Integer type, String text) {
            this.type = type;
            this.text = text;
        }

        public Integer getType() {
            return type;
        }

        public String getText() {
            return text;
        }
    }


    /**
     * 国际化-写入出管数据；
     * 分拣自定义的枚举；用于生成 RfId，区别 入库和出库
     */
    public enum ChuGuanRfId{
        IN("IN"),
        OUT("OUT");
        private String text;

        ChuGuanRfId(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}

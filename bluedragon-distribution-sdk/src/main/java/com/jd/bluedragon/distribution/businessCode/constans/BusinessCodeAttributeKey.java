package com.jd.bluedragon.distribution.businessCode.constans;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public class BusinessCodeAttributeKey {

    /**
     * 批次号的属性key枚举类
     */
    public enum SendCodeAttributeKeyEnum {

        /**
         * 始发站点编号
         */
        from_site_code("始发站点"),

        /**
         * 目的站点编号
         */
        to_site_code("目的站点"),

        /**
         * 是否生鲜：
         * true 标识具有生鲜属性
         * false 标识不具有生鲜属性
         */
        is_fresh("生鲜属性");

        private String mark;

        SendCodeAttributeKeyEnum(String mark) {
            this.mark = mark;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }
    }

    public enum BoardCodeAttributeKeyEnum {

    }
}

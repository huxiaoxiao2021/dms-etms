package com.jd.bluedragon.distribution.businessCode;

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

    /**
     * 拣运集齐能力域设计-待集齐集合的属性字段
     * 除了在枚举中的key需要落code>business_code_attribute</code>表以外，在<code>collectionConditionKeyEnum</code>中的枚举也需要落库
     */
    public enum JQCodeAttributeKeyEnum {

        /**
         * 值所在的枚举请见
         * @link CollectionBusinessTypeEnum
         */
        collection_business_type("集齐业务类型"),

        /**
         * 值所在的枚举请见
         * @link CollectionConditionKeyEnum
         */
        collection_condition("集齐条件"),

        ;

        private final String mark;

        JQCodeAttributeKeyEnum(String mark) {
            this.mark = mark;
        }

        public String getMark() {
            return mark;
        }

    }
}

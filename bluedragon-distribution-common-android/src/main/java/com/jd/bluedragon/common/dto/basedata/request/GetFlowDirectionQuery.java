package com.jd.bluedragon.common.dto.basedata.request;

import com.jd.bluedragon.common.dto.base.request.BaseRequest;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取流向入参
 *
 * @author ext.lishaotan5
 * @copyright jd.com 京东物流JDL
 * @time 2023-08-25 10:53:04 周五
 */
public class GetFlowDirectionQuery extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 2930511914936043262L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 查询参数
     */
    private String searchStr;

    /**
     * 查询类型
     */
    private List<Integer> supportQueryType = new ArrayList<Integer>();


    public enum SupportQueryTypeEnum {

        /**
         * 场地ID
         */
        SITE_ID(1, "场地ID"),

        /**
         * 场地名称
         */
        SITE_NAME(2, "场地名称"),

        /**
         * 运单号
         */
        WAYBILL_CODE(3, "运单号"),

        /**
         * 包裹号
         */
        PACKAGE_CODE(4, "包裹号"),

        UNKNOWN(-1, "未知"),

        ;

        public static Map<Integer, String> ENUM_MAP;

        public static List<Integer> ENUM_LIST;

        private Integer code;

        private String name;

        static {
            //将所有枚举装载到map中
            ENUM_MAP = new HashMap<Integer, String>();
            ENUM_LIST = new ArrayList<Integer>();
            for (SupportQueryTypeEnum enumItem : SupportQueryTypeEnum.values()) {
                ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
                ENUM_LIST.add(enumItem.getCode());
            }
        }

        /**
         * 通过code获取name
         *
         * @param code 编码
         * @return string
         */
        public static SupportQueryTypeEnum getEnumNameByCode(Integer code) {
            for (SupportQueryTypeEnum value : SupportQueryTypeEnum.values()) {
                if (value.getCode() == code) {
                    return value;
                }
            }
            return UNKNOWN;
        }

        SupportQueryTypeEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public List<Integer> getSupportQueryType() {
        return supportQueryType;
    }

    public void setSupportQueryType(List<Integer> supportQueryType) {
        this.supportQueryType = supportQueryType;
    }
}

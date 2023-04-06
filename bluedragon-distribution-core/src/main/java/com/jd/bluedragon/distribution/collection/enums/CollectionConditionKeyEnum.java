package com.jd.bluedragon.distribution.collection.enums;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.enums
 * @ClassName: CollectionConditionKeyEnum
 * @Description: 拣运集齐能力域-集齐条件的key值组装枚举
 * <p>
 *     我们希望condition的在组装的时候保证一致性，所以请后续的添加者严格控制枚举加在最后，并不要改变已经存在的枚举的上下位置，从而保证顺序性
 * </p>
 * @Author： wuzuxiang
 * @CreateDate 2023/2/27 14:57
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum CollectionConditionKeyEnum {

    site_code("站点编码"),

    seal_car_code("封车编码"),

    /**
     * 精确到日
     * 2023-03-13
     */
    date_time("日期时间")

    ;

    private final String name;

    CollectionConditionKeyEnum(String name) {
        this.name = name;
    }

    public static String getCondition(Map<CollectionConditionKeyEnum, Object> collectionElements, CollectionBusinessTypeEnum businessTypeEnum) {
        if (businessTypeEnum == null || MapUtils.isEmpty(collectionElements)) {
            return "";
        }

        StringBuilder conditionString = new StringBuilder();
        /*
         ！！！重要：我们依赖Enums.values()的方法来保证生成的format的顺序性
         如果在未来发行的JDK版本中不能够保证Enums.values()的顺序，此处将会成为未来的一个风险。需要重新显示的去保证顺序
         */
        for (CollectionConditionKeyEnum conditionKeyEnum : CollectionConditionKeyEnum.values()){
            if (businessTypeEnum.getCollectionConditionKeys().contains(conditionKeyEnum)) {
                /*
                事实上，某个businessType下定义的CollectionCondition的key需要有值去对应，如果没有值我们将使用"null"来代替，
                我们在上层定义businessType的时候，应该严格避免null值的出现，如果有null值请重新考虑businessType下的CollectionCondition的key组合方式
                */
                conditionString.append(conditionKeyEnum.name());
                conditionString.append(":");
                conditionString.append(collectionElements.getOrDefault(conditionKeyEnum, "null"));
                conditionString.append(",");
            }
        }
        /* 去除末尾的"," */
        conditionString.deleteCharAt(conditionString.length() - 1);

        return conditionString.toString();

    }

    public String getName() {
        return name;
    }

}

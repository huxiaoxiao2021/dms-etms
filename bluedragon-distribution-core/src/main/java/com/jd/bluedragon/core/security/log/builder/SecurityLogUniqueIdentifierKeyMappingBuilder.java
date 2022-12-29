package com.jd.bluedragon.core.security.log.builder;

import com.jd.bluedragon.core.security.log.enums.SecurityLogUniqueIdentifierKeyEnums;

import java.util.*;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log.builder
 * @ClassName: SecurityLogUniqueIdentifierKeyMappingBuilder
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/29 10:27
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityLogUniqueIdentifierKeyMappingBuilder {

    private Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> uniqueIdentifierKeyEnumsStringHashMap = new HashMap<>();

    /**
     * 返回值对象的映射关系，为了兼容一种值多字段的问题，
     * 比较典型的场景是打印
     *  在众多的打印场景中，每种打印场景的返回值可能不太一样，那么在取某一个值的时候，可能出现在不同的字段中，
     *  这时候就可以在一个List列表中将所有可能的字段列上，读到有值的字段就停止
     * @param keyEnums
     * @param keyMapping
     * @return
     */
    public SecurityLogUniqueIdentifierKeyMappingBuilder addKey(SecurityLogUniqueIdentifierKeyEnums keyEnums, String keyMapping) {
        if (uniqueIdentifierKeyEnumsStringHashMap.containsKey(keyEnums)) {
            uniqueIdentifierKeyEnumsStringHashMap.get(keyEnums).add(keyMapping);
        } else {
            List<String> values = new ArrayList<>();
            values.add(keyMapping);
            uniqueIdentifierKeyEnumsStringHashMap.put(keyEnums, values);
        }
        return this;
    }

    public Map<SecurityLogUniqueIdentifierKeyEnums, List<String>> builder() {
        return this.uniqueIdentifierKeyEnumsStringHashMap;
    }

}

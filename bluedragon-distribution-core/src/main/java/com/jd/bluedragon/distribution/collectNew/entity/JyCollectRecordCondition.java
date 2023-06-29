package com.jd.bluedragon.distribution.collectNew.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/5/30 17:48
 * @Description
 */

@Data
public class JyCollectRecordCondition extends JyCollectRecordPo {

    private Integer pageSize;

    private Integer offset;

    private List<String> collectionCodeList;

}

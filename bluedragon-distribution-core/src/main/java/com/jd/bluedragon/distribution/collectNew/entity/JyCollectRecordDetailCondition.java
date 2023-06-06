package com.jd.bluedragon.distribution.collectNew.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/5/30 20:16
 * @Description
 */

@Data
public class JyCollectRecordDetailCondition extends JyCollectRecordDetailPo {

    private Integer pageSize;

    private Integer offset;

    private List<String> collectionCodeList;

    private Date startOperateTime;

    private Date endOperateTime;
}

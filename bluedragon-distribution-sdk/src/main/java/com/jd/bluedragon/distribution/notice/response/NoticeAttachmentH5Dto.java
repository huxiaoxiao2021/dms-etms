package com.jd.bluedragon.distribution.notice.response;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知附件
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-02-28 09:57:06 周日
 */
public class NoticeAttachmentH5Dto implements Serializable {
    private static final long serialVersionUID = 8696170512763399442L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 通知ID
     */
    private Long noticeId;

    /**
     * 附件大小
     */
    private String size;

    /**
     * 附件类型
     */
    private String type;

    /**
     * 附件名称
     */
    private String fileName;

    /**
     * 文件系统中key
     */
    private String keyName;

    /**
     * 地址
     */
    private String url;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

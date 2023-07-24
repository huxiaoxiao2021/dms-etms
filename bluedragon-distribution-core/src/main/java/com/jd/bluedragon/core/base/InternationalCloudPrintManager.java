package com.jd.bluedragon.core.base;

import com.jdl.print.dto.render.RenderResultDTO;
import com.jdl.print.dto.render.ReprintQueryRenderDTO;

import java.util.List;

/**
 * 国际化云打印包装类接口
 *
 * @author hujiping
 * @date 2023/7/19 2:10 PM
 */
public interface InternationalCloudPrintManager {

    /**
     * 生成pdf链接
     * 
     * @param renderQuery
     * @return
     */
    List<RenderResultDTO> internationalCloudPrint(ReprintQueryRenderDTO renderQuery);
}

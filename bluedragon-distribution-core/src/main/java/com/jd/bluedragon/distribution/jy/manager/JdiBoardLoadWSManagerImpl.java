package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.jdi.dto.BoardLoadDto;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.ws.JdiBoardLoadWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liwenji
 * @description 运输组板相关数据
 * @date 2023-09-27 14:16
 */
@Component
public class JdiBoardLoadWSManagerImpl implements JdiBoardLoadWSManager{
    private static final Logger log = LoggerFactory.getLogger(JdiBoardLoadWSManagerImpl.class);

    @Autowired
    private JdiBoardLoadWS jdiBoardLoadWS;
    
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JdiBoardLoadWSManagerImpl.queryBoardLoad", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BoardLoadDto> queryBoardLoad(BoardLoadDto boardLoadDto) {
        List<BoardLoadDto> boardList= new ArrayList<>();
        try {
            CommonDto<List<BoardLoadDto>> commonDto = jdiBoardLoadWS.queryBoardLoad(boardLoadDto);
            if (commonDto == null || !commonDto.isSuccess() || CollectionUtils.isEmpty(commonDto.getData())) {
                return boardList;
            }
            return commonDto.getData();
        }catch (Exception e) {
            log.error("通过运输接口获取板数据异常：{}", JsonHelper.toJson(boardLoadDto),e);
            return boardList;
        }
    }
}

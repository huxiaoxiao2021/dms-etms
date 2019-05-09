package com.jd.bluedragon.core.base;

        import com.jd.bluedragon.Constants;
        import com.jd.etms.monitor.dto.vos.DifferenceType;
        import com.jd.etms.monitor.dto.vos.ScanDto;
        import com.jd.etms.monitor.jsf.VosScanCodeDifferenceQuery;
        import com.jd.ump.annotation.JProEnum;
        import com.jd.ump.annotation.JProfiler;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;

/**
 * Created by guoyongzhi on 2016/7/29.
 * 调用监控系统提供的jsf
 */
@Service("monitorManager")
public class MonitorManagerImpl implements MonitorManager {

    @Autowired
    private VosScanCodeDifferenceQuery monitorJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.monitorJsfService.query",mState={JProEnum.TP,JProEnum.FunctionError})
    public ScanDto query(String sendCode,Integer Start,Integer Size) {
        return monitorJsfService.query(DifferenceType.SortingSendVosNotReceive,sendCode,Start,Size);
    }
}

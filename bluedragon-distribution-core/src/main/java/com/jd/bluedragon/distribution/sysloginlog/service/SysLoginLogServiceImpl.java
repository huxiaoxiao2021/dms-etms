package com.jd.bluedragon.distribution.sysloginlog.service;

import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.sysloginlog.dao.SysLoginLogDao;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.distribution.sysloginlog.domain.SysLoginLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 登录日志服务类
 * Created by shipeilin on 2018/1/16.
 */
@Service("SysLoginLogService")
public class SysLoginLogServiceImpl implements SysLoginLogService{

    @Autowired
    private SysLoginLogDao sysLoginLogDao;

    private static final Integer DEFAULT_MATCHFLAG = 1; //默认版本最新
    private static final Integer ERROR_MATCHFLAG = 0; //版本不匹配

    @Override
    @JProfiler(jKey = "DMSWEB.SysLoginLogService.insert", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SysLoginLog insert(PdaStaff siteInfo, ClientInfo clientInfo) {
        SysLoginLog sysLoginLog = toSysLoginLog(siteInfo, clientInfo);
        sysLoginLogDao.insert(sysLoginLog);
        return sysLoginLog;
    }

    /**
     * 转为登录日志
     * @param siteInfo
     * @param clientInfo
     * @return
     */
    private SysLoginLog toSysLoginLog(PdaStaff siteInfo, ClientInfo clientInfo) {
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setLoginUserErp(clientInfo.getLoginUserErp());
        sysLoginLog.setLoginUserCode(siteInfo.getStaffId().longValue());
        sysLoginLog.setLoginUserName(siteInfo.getStaffName());
        sysLoginLog.setLoginTime(new Date());
        sysLoginLog.setDmsSiteCode(siteInfo.getSiteId().longValue());
        sysLoginLog.setDmsSiteName(siteInfo.getSiteName());
//        sysLoginLog.setDmsSiteType(request.getDmsSiteType());
//        sysLoginLog.setDmsSiteSubtype(request.getDmsSiteSubtype());
//        sysLoginLog.setSiteCode(request.getSiteCode().longValue());
//        sysLoginLog.setSiteName(request.getSiteName());
        sysLoginLog.setProgramType(clientInfo.getProgramType());
        sysLoginLog.setVersionCode(clientInfo.getVersionCode());
        sysLoginLog.setVersionName(clientInfo.getVersionName());
        String fileVersions = "";
        if(clientInfo.getFiles() != null && clientInfo.getFiles().size() > 0){
            fileVersions = clientInfo.getFiles().toString();
        }
        sysLoginLog.setFileVersions(fileVersions);
        sysLoginLog.setMachineName(clientInfo.getMachineName());
        sysLoginLog.setMatchFlag(getMatchFlag(clientInfo.getVersionCode(), clientInfo));
        sysLoginLog.setIpv4(clientInfo.getIpv4());
        sysLoginLog.setIpv6(clientInfo.getIpv6());
        sysLoginLog.setMacAdress(clientInfo.getMacAdress());

        return sysLoginLog;
    }

    /**
     * 判断是否匹配
     * @param versionCode
     * @param clientInfo
     * @return
     */
    private Integer getMatchFlag(String versionCode,ClientInfo clientInfo){
        List<ClientInfo.FileVersion> files = clientInfo.getFiles();
        if(StringUtils.isBlank(versionCode) || files == null || files.size() == 0){
            return ERROR_MATCHFLAG;
        }
        Integer matchFlag = DEFAULT_MATCHFLAG;
        String fileVersionCode = null;
        for (ClientInfo.FileVersion fileVersion:files){
            if(fileVersionCode == null || fileVersionCode.equals(fileVersion.getVersionCode())){
                fileVersionCode = fileVersion.getVersionCode();
            }else{
                matchFlag = ERROR_MATCHFLAG;
                break;
            }
        }
        if(fileVersionCode != null){
            String fileVersion = "";
            String[] fields = fileVersionCode.split("\\.");
            for(String temp : fields){
                if(temp.length() == 1){
                    temp = "0"+temp;
                }
                fileVersion = fileVersion + temp;
            }
            fileVersionCode = fileVersion;
        }
        if(fileVersionCode == null){
            matchFlag = ERROR_MATCHFLAG;
        }else if(matchFlag.equals(DEFAULT_MATCHFLAG) && !versionCode.contains(fileVersionCode)){
            matchFlag = ERROR_MATCHFLAG;
        }

        return matchFlag;
    }
}

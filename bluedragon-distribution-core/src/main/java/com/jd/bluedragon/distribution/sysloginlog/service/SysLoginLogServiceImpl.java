package com.jd.bluedragon.distribution.sysloginlog.service;

import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.sysloginlog.dao.SysLoginLogDao;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.distribution.sysloginlog.domain.SysLoginLog;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger log = LoggerFactory.getLogger(SysLoginLogServiceImpl.class);
    @Autowired
    private SysLoginLogDao sysLoginLogDao;

    @Override
    @JProfiler(jKey = "DMSWEB.SysLoginLogService.insert", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SysLoginLog insert(PdaStaff siteInfo, ClientInfo clientInfo) {
        SysLoginLog sysLoginLog = toSysLoginLog(siteInfo, clientInfo);
        try {
			sysLoginLogDao.insert(sysLoginLog);
		} catch (Exception e) {
			log.error("插入用户登录日志失败！",e);
		}
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
        //避免ip长度过长
        if(StringHelper.isNotEmpty(clientInfo.getIpv4()) && clientInfo.getIpv4().length()<=20){
        	sysLoginLog.setIpv4(clientInfo.getIpv4());
        }else{
        	log.warn("客户端登录用户-ipv4:{}", clientInfo.getIpv4());
        }
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
    	//版本校验不通过，登录失败
    	if(SysLoginLog.MATCHFLAG_LOGIN_FAIL.equals(clientInfo.getMatchFlag())){
    		return SysLoginLog.MATCHFLAG_LOGIN_FAIL;
    	}
        List<ClientInfo.FileVersion> files = clientInfo.getFiles();
        if(StringUtils.isBlank(versionCode) || files == null || files.size() == 0){
            return SysLoginLog.MATCHFLAG_FAIL;
        }
        Integer matchFlag = SysLoginLog.MATCHFLAG_SUC;
        String fileVersionCode = null;
        for (ClientInfo.FileVersion fileVersion:files){
            if(fileVersionCode == null || fileVersionCode.equals(fileVersion.getVersionCode())){
                fileVersionCode = fileVersion.getVersionCode();
            }else{
                matchFlag = SysLoginLog.MATCHFLAG_FAIL;
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
            matchFlag = SysLoginLog.MATCHFLAG_FAIL;
        }else if(matchFlag.equals(SysLoginLog.MATCHFLAG_SUC) && !versionCode.contains(fileVersionCode)){
            matchFlag = SysLoginLog.MATCHFLAG_FAIL;
        }

        return matchFlag;
    }
}

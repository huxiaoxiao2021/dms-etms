package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.external.service.DmsBaseService;
import com.jd.bluedragon.distribution.rest.base.BaseResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsBaseService")
public class DmsBaseServiceImpl implements DmsBaseService {

    @Autowired
    @Qualifier("baseResource")
    private BaseResource baseResource;
    
	@Autowired
	private UserService userService;

    @Override
    public BaseResponse login(LoginRequest request) {
        return userService.jsfLogin(request);
    }

    @Override
    public BaseResponse getSite(String code) {
        return baseResource.getSite(code);
    }

    @Override
    public BaseResponse getServerDate(String arg) {
        return baseResource.getServerDate();
    }

    @Override
    public List<BaseResponse> getErrorList(String arg) {
        return baseResource.getErrorList();
    }

    @Override
    public List<BaseResponse> getRunNumber(String arg) {
        return baseResource.getRunNumber();
    }

	@Override
	public JdResult<LoginUserResponse> getLoginUser(LoginRequest request) {
		return userService.getLoginUser(request);
	}
}

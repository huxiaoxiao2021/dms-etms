package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service("jyComboardSendVehicleService")
public class JyComboardSendVehicleServiceImpl extends JySendVehicleServiceImpl{

  @Override
  public InvokeResult<List<SendDestDetail>> sendDestDetail(SendDetailRequest request) {
    return super.sendDestDetail(request);
  }
}

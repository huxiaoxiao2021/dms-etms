package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.es.comboard.ComboardScanedDto;
import com.jdl.jy.realtime.model.es.comboard.JyComboardPackageDetail;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;


public interface IJyComboardJsfManager {

    Pager<ComboardScanedDto> queryScannedDetail(Pager<JyComboardPackageDetail> query);

    Pager<ComboardScanedDto> queryInterceptDetail(Pager<JyComboardPackageDetail> query);

    Pager<ComboardScanedDto> queryWaitScanDetail(Pager<JyComboardPackageDetail> query);
}

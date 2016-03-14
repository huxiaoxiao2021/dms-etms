package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import org.springframework.stereotype.Service;

/**
 * Created by wangtingwei on 2016/3/10.
 */
@Service("scannerFrameDispatchService")
public class SimpleScannerFrameDispatchServiceImpl implements ScannerFrameDispatchService {

    @Override
    public boolean dispatch(UploadData domain) {
        return false;
    }

    
}

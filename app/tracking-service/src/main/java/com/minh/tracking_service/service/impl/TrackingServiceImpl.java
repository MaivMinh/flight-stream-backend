package com.minh.tracking_service.service.impl;

import com.minh.common.response.ResponseData;
import com.minh.tracking_service.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    @Override
    public ResponseData getTargetDetails(Integer targetId) {
        return null;
    }
}
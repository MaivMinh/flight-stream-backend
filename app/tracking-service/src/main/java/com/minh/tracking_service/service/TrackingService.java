package com.minh.tracking_service.service;

import com.minh.common.response.ResponseData;

public interface TrackingService {

    ResponseData getTargetDetails(Integer targetId, Integer day);
}

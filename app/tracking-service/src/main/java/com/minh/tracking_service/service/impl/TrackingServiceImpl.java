package com.minh.tracking_service.service.impl;

import com.minh.common.model.Target;
import com.minh.common.response.ResponseData;
import com.minh.tracking_service.repository.TrackingRepository;
import com.minh.tracking_service.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {
    private static final int DEFAULT_HISTORY_DAYS = 7;
    private static final int DEFAULT_LIMIT = 1000;
    private final TrackingRepository trackingRepository;

    @Override
    public ResponseData getTargetDetails(Integer targetId) {
        List<Target> history = trackingRepository.findRecentHistoryByTargetId(targetId, DEFAULT_HISTORY_DAYS, DEFAULT_LIMIT);
        return ResponseData.builder()
                .status(200)
                .message("Success")
                .data(history)
                .build();
    }
}

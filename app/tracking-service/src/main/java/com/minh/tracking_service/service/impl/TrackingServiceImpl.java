package com.minh.tracking_service.service.impl;

import com.minh.common.model.Target;
import com.minh.common.response.ResponseData;
import com.minh.tracking_service.repository.TrackingRepository;
import com.minh.tracking_service.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {
    private final TrackingRepository trackingRepository;

    @Override
    public ResponseData getTargetDetails(Integer targetId, Integer day) {
        if (Objects.isNull(targetId) || targetId < 0) {
            return ResponseData.builder()
                    .status(400)
                    .message("Invalid targetId")
                    .data(List.of())
                    .build();
        }
        if (day == null || day <= 0) {
            day = 0;
        } else if (day > 7) {
            day = 7;
        }

        LocalDate targetDate = Instant.now()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .minusDays(day);
        Timestamp fromTimeStamp = Timestamp.valueOf(targetDate.atStartOfDay());
        Timestamp toTimeStamp = Timestamp.valueOf(targetDate.plusDays(1).atStartOfDay());

        List<Target> history = trackingRepository.findRecentHistoryByTargetIdAndDays(targetId, fromTimeStamp, toTimeStamp);
        return ResponseData.builder()
                .status(200)
                .message("Success")
                .data(history)
                .build();
    }
}

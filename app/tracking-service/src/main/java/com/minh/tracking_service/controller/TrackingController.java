package com.minh.tracking_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.tracking_service.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/tracking")
@RequiredArgsConstructor
@Validated
public class TrackingController {
    private final TrackingService trackingService;

    @GetMapping(value = "/targets/{targetId}")
    public ResponseEntity<ResponseData> getTargetDetails(@PathVariable(name = "targetId") Integer targetId,
                                                         @RequestParam(name = "day") Integer day) {
        ResponseData response = trackingService.getTargetDetails(targetId, day);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }
}
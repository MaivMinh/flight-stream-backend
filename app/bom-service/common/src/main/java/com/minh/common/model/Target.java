package com.minh.common.model;


import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Target implements Serializable {
    public Integer id;
    public Double lat;
    public Double lon;
    public Double alt;
    public Double velocity;
    public Double angularVelocity;
    public String type;
    public Integer currentWaypointIndex = 0;
    public String status;
    public Long timestamp;
    public Double centerLat;
    public Double centerLon;
    public Double radius;
    public Double endLat;
    public Double endLon;
    public Double nextLat;
    public Double nextLon;
}
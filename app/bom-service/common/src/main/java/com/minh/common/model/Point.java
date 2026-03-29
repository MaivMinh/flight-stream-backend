package com.minh.common.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Point implements Serializable {
    private Double lat;
    private Double lon;
    private Double alt;
}

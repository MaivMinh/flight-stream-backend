package com.minh.common.utils;

import com.github.f4b6a3.uuid.UuidCreator;

public class AppUtils {


    public static String generateUUIDv7() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}

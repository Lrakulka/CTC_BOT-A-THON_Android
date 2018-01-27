package com.google.android.gms.samples.vision.barcodereader.dto;

/**
 * Created by List on 1/27/2018.
 */

public class UserData {
    private Long userId;

    private String startDate;

    private Integer timeUnitAmount;

    private String timeUnit;

    public UserData(Long userId, String startDate, Integer timeUnitAmount, String timeUnit) {
        this.userId = userId;
        this.startDate = startDate;
        this.timeUnitAmount = timeUnitAmount;
        this.timeUnit = timeUnit;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public Integer getTimeUnitAmount() {
        return timeUnitAmount;
    }

    public String getTimeUnit() {
        return timeUnit;
    }
}

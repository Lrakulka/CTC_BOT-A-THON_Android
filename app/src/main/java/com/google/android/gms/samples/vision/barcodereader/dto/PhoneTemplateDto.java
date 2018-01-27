package com.google.android.gms.samples.vision.barcodereader.dto;

import java.util.Collection;

public class PhoneTemplateDto {

    private Long userId;

    private String startDate;

    private Integer timeUnitAmount;

    private String timeUnit;

    private Collection<PhoneTemplateItem> barCodes;

    public static class PhoneTemplateItem {

        private String barCode;

        private Integer quantity;

        public String getBarCode() {
            return barCode;
        }

        public void setBarCode(final String barCode) {
            this.barCode = barCode;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(final Integer quantity) {
            this.quantity = quantity;
        }

        public PhoneTemplateItem(String barCode, Integer quantity) {
            this.barCode = barCode;
            this.quantity = quantity;
        }
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

    public Collection<PhoneTemplateItem> getBarCodes() {
        return barCodes;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate;
    }

    public void setTimeUnitAmount(final Integer timeUnitAmount) {
        this.timeUnitAmount = timeUnitAmount;
    }

    public void setTimeUnit(final String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setBarCodes(final Collection<PhoneTemplateItem> barCodes) {
        this.barCodes = barCodes;
    }
}

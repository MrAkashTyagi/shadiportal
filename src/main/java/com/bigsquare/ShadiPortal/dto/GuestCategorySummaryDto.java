package com.bigsquare.ShadiPortal.dto;

public class GuestCategorySummaryDto {

    private String category;
    private Long count;

    public GuestCategorySummaryDto(String category, Long count) {
        this.category = category;
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public Long getCount() {
        return count;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

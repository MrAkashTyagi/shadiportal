package com.bigsquare.ShadiPortal.dto;

public class FamilySummaryDto {

    private Long totalFamilies;
    private Long totalFamilyMembers;
    private Integer largestFamilySize;

    public FamilySummaryDto(
            Long totalFamilies,
            Long totalFamilyMembers,
            Integer largestFamilySize
    ) {
        this.totalFamilies = totalFamilies;
        this.totalFamilyMembers = totalFamilyMembers;
        this.largestFamilySize = largestFamilySize;
    }

    public Long getTotalFamilies() {
        return totalFamilies;
    }

    public void setTotalFamilies(Long totalFamilies) {
        this.totalFamilies = totalFamilies;
    }

    public Long getTotalFamilyMembers() {
        return totalFamilyMembers;
    }

    public void setTotalFamilyMembers(Long totalFamilyMembers) {
        this.totalFamilyMembers = totalFamilyMembers;
    }

    public Integer getLargestFamilySize() {
        return largestFamilySize;
    }

    public void setLargestFamilySize(Integer largestFamilySize) {
        this.largestFamilySize = largestFamilySize;
    }
}

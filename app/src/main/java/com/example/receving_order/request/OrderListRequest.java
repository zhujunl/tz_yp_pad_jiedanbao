package com.example.receving_order.request;

public class OrderListRequest {
    private Integer shopId;

    private Integer page;


    private String deviceId;

    private Integer branchId;

    private Integer employeeId;

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "OrderListRequest{" +
                "shopId=" + shopId +
                ", page=" + page +
                ", deviceId='" + deviceId + '\'' +
                ", branchId=" + branchId +
                ", employeeId=" + employeeId +
                '}';
    }
}

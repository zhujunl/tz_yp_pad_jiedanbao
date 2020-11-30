package com.example.receving_order.response;


import java.util.List;

/**
 * Created by 18682 on 2018/11/11.
 */

public class InitResponse {
    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean{
        private Integer shopId;
        private Integer cashierDeskId;
        private Integer cashAllow;
        private Integer branchId;
        private Integer employeeId;
        private List<BranchVO> branchList;
        private String shopName;

        public Integer getShopId() {
            return shopId;
        }

        public Integer getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Integer employeeId) {
            this.employeeId = employeeId;
        }

        public void setShopId(Integer shopId) {
            this.shopId = shopId;
        }

        public Integer getCashierDeskId() {
            return cashierDeskId;
        }

        public void setCashierDeskId(Integer cashierDeskId) {
            this.cashierDeskId = cashierDeskId;
        }

        public Integer getCashAllow() {
            return cashAllow;
        }

        public void setCashAllow(Integer cashAllow) {
            this.cashAllow = cashAllow;
        }

        public Integer getBranchId() {
            return branchId;
        }

        public void setBranchId(Integer branchId) {
            this.branchId = branchId;
        }

        public List<BranchVO> getBranchList() {
            return branchList;
        }

        public void setBranchList(List<BranchVO> branchList) {
            this.branchList = branchList;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
    }

    @Override
    public String toString() {
        return "InitResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

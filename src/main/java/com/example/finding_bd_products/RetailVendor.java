package com.example.finding_bd_products;

public class RetailVendor {
    private String vendorId;
    private String ownerName;
    private String shopName;
    private String email;
    private String password;
    private String phoneNumber;
    private String businessRegistrationNumber;
    private String tradeLicenseNumber;
    private String shopAddress;
    private String tinNumber;
    private String shopLogo;
    private String accountStatus; // "pending", "approved", "rejected"

    public RetailVendor() {
    }

    public RetailVendor(String vendorId, String ownerName, String shopName, String email, String password,
                       String phoneNumber, String businessRegistrationNumber, String tradeLicenseNumber,
                       String shopAddress, String tinNumber, String accountStatus) {
        this.vendorId = vendorId;
        this.ownerName = ownerName;
        this.shopName = shopName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.tradeLicenseNumber = tradeLicenseNumber;
        this.shopAddress = shopAddress;
        this.tinNumber = tinNumber;
        this.accountStatus = accountStatus;
    }

    // Getters and Setters
    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getTradeLicenseNumber() {
        return tradeLicenseNumber;
    }

    public void setTradeLicenseNumber(String tradeLicenseNumber) {
        this.tradeLicenseNumber = tradeLicenseNumber;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}

package com.example.finding_bd_products;

public class CompanyVendor {
    private String vendorId;
    private String fullName;
    private String designation;
    private String companyName;
    private String email;
    private String password;
    private String phoneNumber;
    private String companyRegistrationNumber;
    private String bstiCertificateNumber;
    private String companyAddress;
    private String tinNumber;
    private String companyLogo;
    private String accountStatus; // "pending", "approved", "rejected"

    public CompanyVendor() {
    }

    public CompanyVendor(String vendorId, String fullName, String designation, String companyName,
                        String email, String password, String phoneNumber, String companyRegistrationNumber,
                        String bstiCertificateNumber, String companyAddress, String tinNumber, String accountStatus) {
        this.vendorId = vendorId;
        this.fullName = fullName;
        this.designation = designation;
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.bstiCertificateNumber = bstiCertificateNumber;
        this.companyAddress = companyAddress;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
        this.companyRegistrationNumber = companyRegistrationNumber;
    }

    public String getBstiCertificateNumber() {
        return bstiCertificateNumber;
    }

    public void setBstiCertificateNumber(String bstiCertificateNumber) {
        this.bstiCertificateNumber = bstiCertificateNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}

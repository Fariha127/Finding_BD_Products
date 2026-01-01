package com.example.finding_bd_products;

public class VendorSession {
    private static VendorSession instance;
    private CompanyVendor currentCompanyVendor;
    private RetailVendor currentRetailVendor;
    private String vendorType; // "company" or "retail"

    private VendorSession() {
    }

    public static VendorSession getInstance() {
        if (instance == null) {
            instance = new VendorSession();
        }
        return instance;
    }

    public void loginCompanyVendor(CompanyVendor vendor) {
        this.currentCompanyVendor = vendor;
        this.currentRetailVendor = null;
        this.vendorType = "company";
    }

    public void loginRetailVendor(RetailVendor vendor) {
        this.currentRetailVendor = vendor;
        this.currentCompanyVendor = null;
        this.vendorType = "retail";
    }

    public void logout() {
        this.currentCompanyVendor = null;
        this.currentRetailVendor = null;
        this.vendorType = null;
    }

    public boolean isLoggedIn() {
        return currentCompanyVendor != null || currentRetailVendor != null;
    }

    public String getVendorType() {
        return vendorType;
    }

    public String getCurrentVendorId() {
        if (currentCompanyVendor != null) {
            return currentCompanyVendor.getVendorId();
        } else if (currentRetailVendor != null) {
            return currentRetailVendor.getVendorId();
        }
        return null;
    }

    public String getCurrentVendorName() {
        if (currentCompanyVendor != null) {
            return currentCompanyVendor.getCompanyName();
        } else if (currentRetailVendor != null) {
            return currentRetailVendor.getShopName();
        }
        return null;
    }

    public CompanyVendor getCurrentCompanyVendor() {
        return currentCompanyVendor;
    }

    public RetailVendor getCurrentRetailVendor() {
        return currentRetailVendor;
    }
}

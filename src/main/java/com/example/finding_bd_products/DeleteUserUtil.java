package com.example.finding_bd_products;

public class DeleteUserUtil {
    public static void main(String[] args) {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        String emailToDelete = "fariha1032004@gmail.com";
        boolean deleted = dbManager.deleteUserByEmail(emailToDelete);
        
        if (deleted) {
            System.out.println("User account with email '" + emailToDelete + "' has been successfully deleted.");
        } else {
            System.out.println("No user found with email '" + emailToDelete + "' or deletion failed.");
        }
    }
}

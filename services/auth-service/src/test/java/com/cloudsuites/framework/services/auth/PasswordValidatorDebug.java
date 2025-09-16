package com.cloudsuites.framework.services.auth;

import java.util.List;

public class PasswordValidatorDebug {
    public static void main(String[] args) {
        String[] testPasswords = {
            "ComplicatedP@ssword!",  // Test this one specifically
            "MyStr0ng!"             // Test simple special char case
        };
        
        for (String password : testPasswords) {
            System.out.println("Testing password: " + password);
            System.out.println("Valid: " + PasswordValidator.isValidPassword(password));
            System.out.println("Strength: " + PasswordValidator.calculatePasswordStrength(password));
            List<String> errors = PasswordValidator.getValidationErrors(password);
            System.out.println("Errors: " + errors);
            System.out.println("---");
        }
    }
}

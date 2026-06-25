/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapppoe;

import java.util.regex.Pattern;

/**
 *
 * @author tsits
 */
public class Login {
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellPhone;
    private String firstName;
    private String lastName;

    public String getRegisteredUsername() {
        return registeredUsername;
    }

    public String getRegisteredPassword() {
        return registeredPassword;
    }

    public String getRegisteredCellPhone() {
        return registeredCellPhone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    
    // Registration method
    public String registerUser(String username, String password, String cellPhone, 
                               String firstName, String lastName) {
        boolean usernameOk = checkUserName(username);
        boolean passwordOk = checkPasswordComplexity(password);
        boolean phoneOk = checkCellPhoneNumber(cellPhone);

        if (!usernameOk) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        } else if (!passwordOk) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        } else if (!phoneOk) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        } else {
            // Save valid registration data
            this.registeredUsername = username;
            this.registeredPassword = password;
            this.registeredCellPhone = cellPhone;
            this.firstName = firstName;
            this.lastName = lastName;
            return "User successfully registered.";
        }
    }

    // Username validation
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    // Password complexity validation
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasCapital = true;
            else if (Character.isDigit(c)) hasNumber = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    // Phone number validation
    public boolean checkCellPhoneNumber(String cellPhone) {
        if (cellPhone == null) return false;
        // Attribution: pattern adapted from Oracle regex tutorial and common SA number format
        Pattern pattern = Pattern.compile("^\\+27[0-9]{9,10}$");
        return pattern.matcher(cellPhone).matches();
    }

    // Login verification
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return registeredUsername != null && registeredUsername.equals(enteredUsername)
                && registeredPassword != null && registeredPassword.equals(enteredPassword);
    }

    // Returns login status message
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            // First name and last name are stored during registration
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}

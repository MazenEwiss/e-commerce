package com.mazen.ecommerce.wallet_service.dto;

import java.util.List;

import com.mazen.ecommerce.wallet_service.model.AccountStatus;
import com.mazen.ecommerce.wallet_service.model.UserRole;

public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private List<Long> walletId;
    private UserRole role;
    private AccountStatus accountStatus;
    
    public UserResponseDto(Long id, String firstName, String lastName, String userName, String email, List<Long> walletId ,UserRole role, AccountStatus accountStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.walletId = walletId;
        this.role = role;
        this.accountStatus = accountStatus;
    }
    public UserResponseDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    public Long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUserName() {
        return userName;
    }
    public String getEmail() {
        return email;
    }
    public List<Long> getWalletId() {
        return walletId;
    }

    public UserRole getRole() {
        return role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

}

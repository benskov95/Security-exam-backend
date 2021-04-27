package dto;

import entities.User;

import java.util.List;

public class UserDTO {

    private String email;
    private String username;
    private String role;
    private String password;
    private String oldPassword;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole().getRoleName();
    }

    public UserDTO(){}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

package org.example.security.dto;

public class UserDtoForSaving {

    private String username;

    private String password;

    private String email;

    private Integer age;

    public UserDtoForSaving() {
    }

    public UserDtoForSaving(String username, String password, String email, Integer age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

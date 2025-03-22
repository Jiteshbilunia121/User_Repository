package dto;

import lombok.Getter;
import lombok.Setter;

//@Setter
//@Getter
public class LoginRequest {
    // Getters & Setters
    private String email;
    private String password;


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

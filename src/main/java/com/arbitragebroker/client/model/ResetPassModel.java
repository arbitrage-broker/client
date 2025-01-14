package com.arbitragebroker.client.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Data
public class ResetPassModel {
    @NotBlank
    @NotEmpty
    private String login;
    @NotBlank
    @NotEmpty
    private String otp;
    @NotBlank
    @NotEmpty
    private String newPassword;


    @Override
    public int hashCode() {
        return Objects.hash(getLogin(),getOtp());
    }
}

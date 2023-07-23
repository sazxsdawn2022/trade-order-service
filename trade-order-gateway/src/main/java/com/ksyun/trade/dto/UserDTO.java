package com.ksyun.trade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String email;

    private String phone;

    private String address;

}
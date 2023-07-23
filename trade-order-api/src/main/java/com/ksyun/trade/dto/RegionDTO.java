package com.ksyun.trade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;

    private String name;

}


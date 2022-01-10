package com.dmlab.cita.server.config;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CitaConfig {
    private String addr;
    private String name;
    private String contractAddress;
    private String algo;
    private String privateKey;
    private Long timeoutHeight;
}

package com.unilex.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TermSearchRequest {
    private List<String> products;
    private String dataType;
    private String keyword;
    private List<String> projects;
    private String confirm;
}

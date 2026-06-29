package com.unilex.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class LangPackDiffDto {
    private String zhContent;
    private String enContent;
    private String jaContent;
    private List<String> products;
    private String scope;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DiffItem {
        private String fullKey;
        private String shortKey;
        private String dirPath;
        private String zhCn;
        private String enUs;
        private String jaJp;
        /** MISSING / PRODUCT_MISSING / EXISTS / DIFF */
        private String status;
        private String diffDetail;
        private String existZhCn;
        private String existEnUs;
        private String existJaJp;
        /** PRODUCT_MISSING 时：已有术语的 ID */
        private Long existingTermId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DiffResult {
        private int total;
        private int missing;
        private int productMissing;
        private int exists;
        private int diff;
        private List<DiffItem> items;
    }
}

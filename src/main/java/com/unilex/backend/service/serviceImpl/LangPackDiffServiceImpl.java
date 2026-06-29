package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.unilex.backend.dto.LangPackDiffDto;
import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.service.LangPackDiffService;
import com.unilex.backend.service.TermService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangPackDiffServiceImpl implements LangPackDiffService {

    private final TermService termService;
    private final DirectoryService directoryService;

    private static final Pattern KV_PATTERN = Pattern.compile(
            "['\"]?([\\w.\\-]+)['\"]?\\s*[:=]\\s*['\"]([^'\"]*?)['\"]");
    private static final Pattern FUNC_PATTERN = Pattern.compile(
            "(?:t|i18n|intl|translate)\\s*\\(\\s*['\"]([\\w.\\-]+)['\"]");

    @Override
    public LangPackDiffDto.DiffResult diff(LangPackDiffDto req) {
        Map<String, String> zhMap = parseToMap(req.getZhContent());
        Map<String, String> enMap = parseToMap(req.getEnContent());
        Map<String, String> jaMap = parseToMap(req.getJaContent());

        Set<String> allKeys = new LinkedHashSet<>();
        allKeys.addAll(zhMap.keySet());
        allKeys.addAll(enMap.keySet());
        allKeys.addAll(jaMap.keySet());
        if (allKeys.isEmpty()) return buildEmpty();

        List<CatDirectory> allDirs = directoryService.lambdaQuery()
                .orderByAsc(CatDirectory::getParentId, CatDirectory::getSortOrder).list();

        LambdaQueryChainWrapper<TermEntry> filteredQw = termService.lambdaQuery();
        applyProductFilter(filteredQw, req.getProducts());
        applyScopeFilter(filteredQw, req.getScope());
        Map<String, TermEntry> filteredIndex = buildTermIndex(filteredQw.list());

        Map<String, TermEntry> fullIndex = null;
        if (req.getProducts() != null && !req.getProducts().isEmpty()) {
            fullIndex = buildTermIndex(termService.lambdaQuery().list());
        }

        List<LangPackDiffDto.DiffItem> items = new ArrayList<>();
        int missingCount = 0, existsCount = 0, diffCount = 0, productMissingCount = 0;

        for (String fullKey : allKeys) {
            String zh = zhMap.get(fullKey);
            String en = enMap.get(fullKey);
            String ja = jaMap.get(fullKey);
            String[] segments = fullKey.split("\\.");
            if (segments.length < 2) continue;
            List<String> segList = new ArrayList<>(Arrays.asList(segments));
            if ("All".equalsIgnoreCase(segList.get(0))) segList.remove(0);
            if (segList.size() < 2) continue;
            String shortKey = segList.get(segList.size() - 1);
            List<String> dirSegs = segList.subList(0, segList.size() - 1);
            String dirPath = String.join(" / ", dirSegs);
            Long dirId = resolveDirPath(dirSegs, allDirs);
            String status = null;
            String diffDetail = null;
            String existZhCn = null, existEnUs = null, existJaJp = null;
            Long existingTermId = null;

            if (dirId != null) {
                TermEntry inFiltered = filteredIndex.get(dirId + ":" + shortKey.toLowerCase());
                if (inFiltered != null) {
                    existZhCn = inFiltered.getZhCn();
                    existEnUs = inFiltered.getEnUs();
                    existJaJp = inFiltered.getJaJp();
                    List<String> diffs = new ArrayList<>();
                    if (zh != null && !nullSafeEquals(zh, existZhCn)) diffs.add("\u4e2d\u6587");
                    if (en != null && !nullSafeEquals(en, existEnUs)) diffs.add("\u82f1\u6587");
                    if (ja != null && !nullSafeEquals(ja, existJaJp)) diffs.add("\u65e5\u6587");
                    if (!diffs.isEmpty()) {
                        status = "DIFF";
                        diffDetail = String.join(", ", diffs) + " \u4e0d\u540c";
                        diffCount++;
                    } else {
                        status = "EXISTS";
                        existsCount++;
                    }
                } else if (fullIndex != null) {
                    TermEntry inFull = fullIndex.get(dirId + ":" + shortKey.toLowerCase());
                    if (inFull != null) {
                        status = "PRODUCT_MISSING";
                        existingTermId = inFull.getId();
                        existZhCn = inFull.getZhCn();
                        existEnUs = inFull.getEnUs();
                        existJaJp = inFull.getJaJp();
                        productMissingCount++;
                    } else {
                        status = "MISSING";
                        missingCount++;
                    }
                } else {
                    status = "MISSING";
                    missingCount++;
                }
            } else {
                status = "MISSING";
                missingCount++;
            }

            LangPackDiffDto.DiffItem.DiffItemBuilder builder = LangPackDiffDto.DiffItem.builder()
                    .fullKey(fullKey).shortKey(shortKey).dirPath(dirPath)
                    .zhCn(zh).enUs(en).jaJp(ja)
                    .status(status).diffDetail(diffDetail)
                    .existZhCn(existZhCn).existEnUs(existEnUs).existJaJp(existJaJp);
            if (existingTermId != null) builder.existingTermId(existingTermId);
            items.add(builder.build());
        }

        items.sort((a, b) -> orderOf(a.getStatus()) - orderOf(b.getStatus()));

        return LangPackDiffDto.DiffResult.builder()
                .total(items.size()).missing(missingCount)
                .exists(existsCount).diff(diffCount)
                .productMissing(productMissingCount)
                .items(items).build();
    }

    private int orderOf(String status) {
        if ("MISSING".equals(status)) return 0;
        if ("PRODUCT_MISSING".equals(status)) return 1;
        if ("DIFF".equals(status)) return 2;
        return 3;
    }

    private void applyProductFilter(LambdaQueryChainWrapper<TermEntry> qw, List<String> products) {
        if (products == null || products.isEmpty()) return;
        qw.and(w -> {
            for (String p : products) {
                if ("SmartOM".equalsIgnoreCase(p)) w.or().eq(TermEntry::getProductSmartom, 1);
                if ("EMS".equalsIgnoreCase(p)) w.or().eq(TermEntry::getProductEms, 1);
                if ("OnePoint".equalsIgnoreCase(p)) w.or().eq(TermEntry::getProductOnepoint, 1);
            }
        });
    }

    private void applyScopeFilter(LambdaQueryChainWrapper<TermEntry> qw, String scope) {
        if ("confirmed".equals(scope)) qw.eq(TermEntry::getConfirmed, 1);
        else if ("unconfirmed".equals(scope)) qw.eq(TermEntry::getConfirmed, 0);
    }

    private Map<String, TermEntry> buildTermIndex(List<TermEntry> terms) {
        Map<String, TermEntry> index = new HashMap<>();
        for (TermEntry t : terms) {
            if (t.getDirId() != null && t.getShortKey() != null)
                index.put(t.getDirId() + ":" + t.getShortKey().toLowerCase(), t);
        }
        return index;
    }

    private Map<String, String> parseToMap(String content) {
        Map<String, String> result = new LinkedHashMap<>();
        if (content == null || content.trim().isEmpty()) return result;
        String trimmed = content.trim();
        if (trimmed.contains("\t")) {
            String[] lines = trimmed.split("\n");
            int start = 0;
            for (String line : lines) { if (line.contains("\t")) break; start++; }
            for (int i = start; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                String[] cols = line.split("\t", -1);
                if (cols.length < 2) continue;
                int keyCol = -1;
                for (int c = 0; c < cols.length; c++) {
                    if (cols[c].contains(".") && !hasCJK(cols[c])) { keyCol = c; break; }
                }
                if (keyCol >= 0 && keyCol < cols.length - 1) {
                    result.put(cleanCol(cols[keyCol]), cleanCol(cols[keyCol + 1]));
                } else if (cols.length >= 2) {
                    result.put(cleanCol(cols[cols.length - 1]), cleanCol(cols[cols.length - 2]));
                }
            }
            if (!result.isEmpty()) return result;
        }
        if (trimmed.startsWith("{")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map<String, Object> json = mapper.readValue(trimmed,
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                flattenJson("", json, result);
                if (!result.isEmpty()) return result;
            } catch (Exception ignored) {}
        }
        Matcher kv = KV_PATTERN.matcher(content);
        while (kv.find()) {
            String key = kv.group(1), value = kv.group(2);
            if (key != null && !key.isEmpty()) result.put(key, value);
        }
        if (result.isEmpty()) {
            Matcher func = FUNC_PATTERN.matcher(content);
            while (func.find()) {
                String key = func.group(1);
                if (key != null && !key.isEmpty()) result.put(key, "");
            }
        }
        return result;
    }

    private void flattenJson(String prefix, Map<String, Object> map, Map<String, String> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nested = (Map<String, Object>) value;
                flattenJson(key, nested, result);
            } else if (value instanceof String) {
                result.put(key, (String) value);
            } else if (value != null) {
                result.put(key, String.valueOf(value));
            }
        }
    }

    private boolean nullSafeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.trim().equals(b.trim());
    }

    private boolean hasCJK(String s) {
        return s != null && s.chars().anyMatch(c ->
                (c >= 0x4E00 && c <= 0x9FFF) || (c >= 0x3040 && c <= 0x30FF));
    }

    private String cleanCol(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("'") && s.endsWith("'")) s = s.substring(1, s.length() - 1);
        if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length() - 1);
        return s;
    }

    private Long resolveDirPath(List<String> segments, List<CatDirectory> allDirs) {
        Long curParent = 0L;
        for (String seg : segments) {
            CatDirectory found = null;
            for (CatDirectory d : allDirs) {
                if (d.getParentId().equals(curParent) && d.getDirKey() != null
                        && d.getDirKey().equalsIgnoreCase(seg)) { found = d; break; }
            }
            if (found == null) return null;
            curParent = found.getId();
        }
        return curParent;
    }

    private LangPackDiffDto.DiffResult buildEmpty() {
        return LangPackDiffDto.DiffResult.builder()
                .total(0).missing(0).exists(0).diff(0).productMissing(0)
                .items(Collections.emptyList()).build();
    }
}


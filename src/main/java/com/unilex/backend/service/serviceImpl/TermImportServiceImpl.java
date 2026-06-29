package com.unilex.backend.service.serviceImpl;

import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.service.TermImportService;
import com.unilex.backend.service.TermService;
import com.unilex.backend.service.TermVersionLogService;
import com.unilex.backend.utils.SecurityUtil;
import com.unilex.backend.vo.TermImportPreviewVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TermImportServiceImpl implements TermImportService {

    private final TermService termService;
    private final DirectoryService directoryService;
    private final TermVersionLogService versionLogService;

    private static final Pattern KEY_PATTERN = Pattern.compile("'([^']+)'");

    private final Map<String, Long> dirCache = new HashMap<>();
    private final Map<String, Boolean> dirExistsCache = new HashMap<>();

    @Override
    public List<TermImportPreviewVo> parseExcel(MultipartFile file) {
        List<TermImportPreviewVo> result = new ArrayList<>();
        dirCache.clear();
        dirExistsCache.clear();

        // 预加载全部目录
        List<CatDirectory> allDirs = directoryService.lambdaQuery()
                .orderByAsc(CatDirectory::getParentId, CatDirectory::getSortOrder)
                .list();
        Map<Long, CatDirectory> dirById = allDirs.stream()
                .collect(Collectors.toMap(CatDirectory::getId, d -> d));

        // 预加载全部术语：(dirId:shortKey) → TermEntry（大小写不敏感）
        List<TermEntry> allTerms = termService.lambdaQuery().list();
        Map<String, TermEntry> termIndex = new HashMap<>();
        for (TermEntry t : allTerms) {
            if (t.getDirId() != null && t.getShortKey() != null) {
                termIndex.put(t.getDirId() + ":" + t.getShortKey().toLowerCase(), t);
            }
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            for (int i = 3; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int excelRowNum = i + 1;
                String zhCn    = getCellStr(row, 1);
                String enUs    = getCellStr(row, 2);
                String jaJp    = getCellStr(row, 3);
                String keyRaw  = getCellStr(row, 4);
                String remark  = getCellStr(row, 5);

                if (zhCn.isEmpty() && enUs.isEmpty() && jaJp.isEmpty() && keyRaw.isEmpty()) continue;

                String fullKey = extractKey(keyRaw);

                boolean valid = true;
                String reason = "";
                String shortKey = "";
                String resolvedDirPath = "";
                Long dirId = null;
                Boolean dirExists = null;
                Boolean keyExists = false;
                Long existingTermId = null;
                Boolean existSmartom = null;
                Boolean existEms = null;
                Boolean existOnepoint = null;
                String existProjectName = null;

                if (fullKey.isEmpty()) {
                    valid = false;
                    reason = "Key 为空或格式无法解析";
                } else {
                    String[] segments = fullKey.split("\\.");
                    if (segments.length < 2) {
                        valid = false;
                        reason = "Key 路径至少需要 2 段";
                    } else {
                        List<String> segList = new ArrayList<>(Arrays.asList(segments));

                        // 去掉 "All" 前缀 — All 只是 Excel 命名空间，不是数据库目录
                        if ("All".equalsIgnoreCase(segList.get(0))) {
                            segList.remove(0);
                        }

                        if (segList.size() < 2) {
                            valid = false;
                            reason = "去掉 All 后路径不足 2 段";
                        } else {
                            // 最后一段 = shortKey
                            shortKey = segList.get(segList.size() - 1);
                            // 前面 = 目录路径
                            List<String> dirSegs = segList.subList(0, segList.size() - 1);

                            // 显示路径
                            resolvedDirPath = String.join(" / ", dirSegs);

                            try {
                                // 从 parentId=0 开始，大小写不敏感匹配
                                dirId = resolveDirPath(dirSegs, 0L, dirById, dirExistsCache);
                                String existsKey = "0:" + String.join(".", dirSegs).toLowerCase();
                                dirExists = Boolean.TRUE.equals(dirExistsCache.get(existsKey));

                                // 检测 key 重复（大小写不敏感）
                                if (dirId != null) {
                                    TermEntry existingTerm = termIndex.get(dirId + ":" + shortKey.toLowerCase());
                                    if (existingTerm != null) {
                                        keyExists = true;
                                        existingTermId = existingTerm.getId();
                                        existSmartom = existingTerm.getProductSmartom() == 1;
                                        existEms = existingTerm.getProductEms() == 1;
                                        existOnepoint = existingTerm.getProductOnepoint() == 1;
                                        existProjectName = existingTerm.getProjectName();
                                    }
                                }
                            } catch (Exception e) {
                                valid = false;
                                reason = "目录解析失败：" + e.getMessage();
                            }
                        }
                    }
                }

                result.add(TermImportPreviewVo.builder()
                        .rowNum(excelRowNum)
                        .shortKey(shortKey)
                        .fullPath(fullKey)
                        .resolvedDirPath(resolvedDirPath)
                        .dirId(dirId)
                        .dirExists(dirExists)
                        .keyExists(keyExists)
                        .existingTermId(existingTermId)
                        .existSmartom(existSmartom)
                        .existEms(existEms)
                        .existOnepoint(existOnepoint)
                        .existProjectName(existProjectName)
                        .zhCn(zhCn)
                        .enUs(enUs)
                        .jaJp(jaJp)
                        .definition(remark)
                        .valid(valid)
                        .reason(reason)
                        .build());
            }
        } catch (Exception e) {
            log.error("解析 Excel 失败", e);
            throw new RuntimeException("Excel 解析失败：" + e.getMessage(), e);
        }

        log.info("Excel 解析完成，共 {} 条记录", result.size());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImportByPath(List<TermImportPreviewVo> list, String globalProjectName) {
        if (list == null || list.isEmpty()) return 0;

        Long userId = SecurityUtil.currentUserId();

        List<CatDirectory> allDirs = directoryService.lambdaQuery()
                .orderByAsc(CatDirectory::getParentId, CatDirectory::getSortOrder)
                .list();
        Map<Long, CatDirectory> dirById = allDirs.stream()
                .collect(Collectors.toMap(CatDirectory::getId, d -> d));
        dirCache.clear();
        dirExistsCache.clear();

        Map<Long, Integer> dirMaxSort = new HashMap<>();
        int insertCount = 0;

        // 批次内去重：防止同一批中有重复的 (dirId, shortKey)
        Set<String> insertedKeys = new HashSet<>();
        int updateCount = 0;

        for (TermImportPreviewVo vo : list) {
            if (!Boolean.TRUE.equals(vo.getValid())) continue;

            Long targetDirId = vo.getDirId();
            if (targetDirId == null) {
                try {
                    String[] dirSegs = vo.getResolvedDirPath().split(" / ");
                    targetDirId = resolveOrCreateDirPath(dirSegs, 0L, dirById);
                    vo.setDirId(targetDirId);
                } catch (Exception e) {
                    log.warn("导入时目录解析失败: path={}", vo.getResolvedDirPath(), e);
                    continue;
                }
            }

            String projectName = vo.getProjectName();
            if ((projectName == null || projectName.isEmpty()) && globalProjectName != null) {
                projectName = globalProjectName;
            }

            int smartom = Boolean.TRUE.equals(vo.getProductSmartom()) ? 1 : 0;
            int ems = Boolean.TRUE.equals(vo.getProductEms()) ? 1 : 0;
            int onepoint = Boolean.TRUE.equals(vo.getProductOnepoint()) ? 1 : 0;

            if (Boolean.TRUE.equals(vo.getKeyExists()) && vo.getExistingTermId() != null) {
                /* 已存在：更新产品标记 + 版本记录 */
                TermEntry update = new TermEntry();
                update.setId(vo.getExistingTermId());
                boolean changed = false;

                // 产品标记：0 → 1
                if (smartom == 1 && !Boolean.TRUE.equals(vo.getExistSmartom())) {
                    update.setProductSmartom(1); changed = true;
                }
                if (ems == 1 && !Boolean.TRUE.equals(vo.getExistEms())) {
                    update.setProductEms(1); changed = true;
                }
                if (onepoint == 1 && !Boolean.TRUE.equals(vo.getExistOnepoint())) {
                    update.setProductOnepoint(1); changed = true;
                }

                // 所属项目：记录新版本
                String oldVer = vo.getExistProjectName() != null ? vo.getExistProjectName() : "";
                String newVer = (projectName != null && !projectName.isEmpty()) ? projectName : "";
                if (!newVer.isEmpty() && !newVer.equals(oldVer)) {
                    update.setProjectName(newVer);
                    changed = true;
                    versionLogService.logVersionChange(
                            vo.getExistingTermId(), targetDirId, vo.getShortKey(),
                            oldVer, newVer, "UPDATE");
                }

                if (changed) {
                    termService.updateById(update);
                    updateCount++;
                }
            } else {
                /* 不存在：新增 */
                // 批次内去重检查
                String batchKey = targetDirId + ":" + vo.getShortKey().toLowerCase();
                if (insertedKeys.contains(batchKey)) {
                    log.debug("批次内重复跳过: dirId={}, shortKey={}", targetDirId, vo.getShortKey());
                    continue;
                }
                int nextSort = dirMaxSort.compute(targetDirId, (k, cached) -> {
                    if (cached != null) return cached + 1;
                    TermEntry last = termService.lambdaQuery()
                            .eq(TermEntry::getDirId, k)
                            .orderByDesc(TermEntry::getSortOrder)
                            .last("LIMIT 1")
                            .one();
                    return (last != null ? last.getSortOrder() : 0) + 1;
                });

                TermEntry entry = new TermEntry();
                entry.setDirId(targetDirId);
                entry.setShortKey(vo.getShortKey());
                entry.setZhCn(vo.getZhCn());
                entry.setEnUs(vo.getEnUs());
                entry.setJaJp(vo.getJaJp());
                entry.setDefinition(vo.getDefinition());
                entry.setProjectName(projectName);
                entry.setProductSmartom(smartom);
                entry.setProductEms(ems);
                entry.setProductOnepoint(onepoint);
                entry.setCreator(userId);
                entry.setIsPredefined(0);
                entry.setConfirmed(0);
                entry.setSortOrder(nextSort);

                try {
                    termService.save(entry);
                    insertedKeys.add(batchKey);
                    insertCount++;
                    // 记录版本创建
                    if (projectName != null && !projectName.isEmpty()) {
                        versionLogService.logVersionChange(
                                entry.getId(), targetDirId, vo.getShortKey(),
                                "", projectName, "CREATE");
                    }
                } catch (org.springframework.dao.DuplicateKeyException e) {
                    log.warn("术语已存在，跳过: dirId={}, shortKey={}", targetDirId, vo.getShortKey());
                }
            }
        }

        log.info("批量导入完成，新增 {} 条，更新 {} 条", insertCount, updateCount);
        return insertCount + updateCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImport(Long dirId, List<TermImportPreviewVo> list) {
        return batchImportByPath(list, null);
    }

    /* ========== 目录解析（大小写不敏感） ========== */

    /**
     * 从指定 parentId 开始逐级查找目录路径（大小写不敏感匹配 dirKey）
     */
    private Long resolveDirPath(List<String> segments, Long parentId,
                                Map<Long, CatDirectory> dirById,
                                Map<String, Boolean> existsCache) {
        String cacheKey = parentId + ":" + String.join(".", segments).toLowerCase();
        if (dirCache.containsKey(cacheKey)) return dirCache.get(cacheKey);

        Long curParent = parentId;
        boolean allExist = true;

        for (String seg : segments) {
            // 查找当前父目录下的所有子目录，做大小写不敏感比较
            List<CatDirectory> children = directoryService.lambdaQuery()
                    .eq(CatDirectory::getParentId, curParent)
                    .list();

            CatDirectory found = null;
            for (CatDirectory child : children) {
                if (child.getDirKey() != null && child.getDirKey().equalsIgnoreCase(seg)) {
                    found = child;
                    break;
                }
            }

            if (found != null) {
                curParent = found.getId();
            } else {
                allExist = false;
                curParent = null;
                break;
            }
        }

        if (allExist) dirCache.put(cacheKey, curParent);
        existsCache.put(cacheKey, allExist && curParent != null);
        return curParent;
    }

    /**
     * 从指定 parentId 开始逐级查找或创建目录（大小写不敏感匹配）
     */
    private Long resolveOrCreateDirPath(String[] segments, Long startParentId,
                                        Map<Long, CatDirectory> dirById) {
        String cacheKey = startParentId + ":" + String.join(".", segments).toLowerCase();
        if (dirCache.containsKey(cacheKey)) return dirCache.get(cacheKey);

        Long curParent = startParentId;
        int level = 1;

        for (String seg : segments) {
            final Long curParentId = curParent;
            final int curLevel = level;

            // 大小写不敏感查找
            List<CatDirectory> children = directoryService.lambdaQuery()
                    .eq(CatDirectory::getParentId, curParentId)
                    .list();

            CatDirectory found = null;
            for (CatDirectory child : children) {
                if (child.getDirKey() != null && child.getDirKey().equalsIgnoreCase(seg)) {
                    found = child;
                    break;
                }
            }

            if (found != null) {
                curParent = found.getId();
            } else {
                CatDirectory newDir = new CatDirectory();
                newDir.setParentId(curParentId);
                newDir.setDirKey(seg);
                newDir.setDirNameZh(seg);
                newDir.setDirNameEn(seg);
                newDir.setDirType(curLevel);
                newDir.setIsSystem(0);

                CatDirectory lastSibling = directoryService.lambdaQuery()
                        .eq(CatDirectory::getParentId, curParentId)
                        .eq(CatDirectory::getDirType, curLevel)
                        .orderByDesc(CatDirectory::getSortOrder)
                        .last("LIMIT 1")
                        .one();
                newDir.setSortOrder(lastSibling != null ? lastSibling.getSortOrder() + 1 : 1);

                directoryService.save(newDir);
                curParent = newDir.getId();
                dirById.put(newDir.getId(), newDir);
                log.info("自动创建目录: level={}, key={}, parentId={}", curLevel, seg, curParentId);
            }
            level++;
        }

        dirCache.put(cacheKey, curParent);
        return curParent;
    }

    /* ========== 工具 ========== */

    private String extractKey(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        Matcher m = KEY_PATTERN.matcher(raw);
        if (m.find()) return m.group(1);
        return raw.trim();
    }

    private String getCellStr(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC:
                double d = cell.getNumericCellValue();
                return d == Math.floor(d) ? String.valueOf((long) d) : String.valueOf(d);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue().trim(); }
                catch (Exception e) {
                    try { return String.valueOf(cell.getNumericCellValue()); }
                    catch (Exception e2) { return ""; }
                }
            default: return "";
        }
    }
}

package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.dto.LangPackDiffDto;
import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.security.OpLog;
import com.unilex.backend.security.ReqPerm;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.service.LangPackDiffService;
import com.unilex.backend.service.TermService;
import com.unilex.backend.utils.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lang-pack")
@RequiredArgsConstructor
public class LangPackDiffController {

    private final LangPackDiffService diffService;
    private final TermService termService;
    private final DirectoryService directoryService;

    @PostMapping("/diff")
    @ReqPerm("term:add")
    public R<LangPackDiffDto.DiffResult> diff(@RequestBody LangPackDiffDto req) {
        return R.ok(diffService.diff(req));
    }

    @Data
    public static class ImportMissingReq {
        private List<LangPackDiffDto.DiffItem> items;
        private String projectName;
        private List<String> products;
    }

    @PostMapping("/import-missing")
    @ReqPerm("term:add")
    @OpLog(module = "term", operation = "CREATE", description = "语言包对比导入")
    public R<Map<String, Object>> importMissing(@RequestBody ImportMissingReq req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return R.error(400, "没有可导入的术语");
        }

        Long userId = SecurityUtil.currentUserId();
        Map<String, Long> dirCache = new HashMap<>();
        int insertCount = 0;
        int updateCount = 0;

        for (LangPackDiffDto.DiffItem item : req.getItems()) {
            if ("EXISTS".equals(item.getStatus())) continue;

            if ("PRODUCT_MISSING".equals(item.getStatus()) && item.getExistingTermId() != null) {
                /* 术语已存在，但没有选中该产品 → 更新产品标记 */
                TermEntry update = new TermEntry();
                update.setId(item.getExistingTermId());
                boolean changed = false;

                if (req.getProducts() != null) {
                    if (req.getProducts().contains("SmartOM")) { update.setProductSmartom(1); changed = true; }
                    if (req.getProducts().contains("EMS")) { update.setProductEms(1); changed = true; }
                    if (req.getProducts().contains("OnePoint")) { update.setProductOnepoint(1); changed = true; }
                }

                if (changed) { termService.updateById(update); updateCount++; }
                continue;
            }

            if ("DIFF".equals(item.getStatus())) {
                /* 术语已存在且产品已选，但值不同 → 更新三语值 */
                // 需要找到该术语的 ID：按 dirId + shortKey 查
                String dirPath = item.getDirPath();
                Long dirId = dirCache.get(dirPath);
                if (dirId == null) {
                    dirId = resolveOrCreateDir(dirPath.split(" / "));
                    dirCache.put(dirPath, dirId);
                }
                if (dirId == null) continue;

                TermEntry existing = termService.lambdaQuery()
                        .eq(TermEntry::getDirId, dirId)
                        .eq(TermEntry::getShortKey, item.getShortKey())
                        .last("LIMIT 1").one();
                if (existing == null) continue;

                TermEntry update = new TermEntry();
                update.setId(existing.getId());
                if (item.getZhCn() != null) update.setZhCn(item.getZhCn());
                if (item.getEnUs() != null) update.setEnUs(item.getEnUs());
                if (item.getJaJp() != null) update.setJaJp(item.getJaJp());
                termService.updateById(update);
                updateCount++;
                continue;
            }

            if (!"MISSING".equals(item.getStatus())) continue;

            /* 全新术语 → 创建 */
            String dirPath = item.getDirPath();
            Long dirId = dirCache.get(dirPath);
            if (dirId == null) {
                dirId = resolveOrCreateDir(dirPath.split(" / "));
                dirCache.put(dirPath, dirId);
            }
            if (dirId == null) continue;

            try {
                TermEntry entry = new TermEntry();
                entry.setDirId(dirId);
                entry.setShortKey(item.getShortKey());
                entry.setZhCn(item.getZhCn());
                entry.setEnUs(item.getEnUs());
                entry.setJaJp(item.getJaJp());
                entry.setCreator(userId);
                entry.setIsPredefined(1);
                entry.setConfirmed(1);

                entry.setProductSmartom(req.getProducts() != null && req.getProducts().contains("SmartOM") ? 1 : 0);
                entry.setProductEms(req.getProducts() != null && req.getProducts().contains("EMS") ? 1 : 0);
                entry.setProductOnepoint(req.getProducts() != null && req.getProducts().contains("OnePoint") ? 1 : 0);

                TermEntry last = termService.lambdaQuery()
                        .eq(TermEntry::getDirId, dirId)
                        .orderByDesc(TermEntry::getSortOrder).last("LIMIT 1").one();
                entry.setSortOrder(last != null ? last.getSortOrder() + 1 : 1);

                if (req.getProjectName() != null && !req.getProjectName().isEmpty()) {
                    entry.setProjectName(req.getProjectName());
                }

                termService.save(entry);
                insertCount++;
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // skip
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("imported", insertCount);
        result.put("updated", updateCount);
        result.put("updated", updateCount);
        return R.ok(result);
    }

    private Long resolveOrCreateDir(String[] segments) {
        Long curParent = 0L;
        int level = 1;
        for (String seg : segments) {
            final Long cp = curParent;
            final int lv = level;
            CatDirectory found = null;
            for (CatDirectory d : directoryService.lambdaQuery()
                    .eq(CatDirectory::getParentId, cp).list()) {
                if (d.getDirKey() != null && d.getDirKey().equalsIgnoreCase(seg)) {
                    found = d; break;
                }
            }
            if (found != null) {
                curParent = found.getId();
            } else {
                CatDirectory nd = new CatDirectory();
                nd.setParentId(cp); nd.setDirKey(seg);
                nd.setDirNameZh(seg); nd.setDirNameEn(seg);
                nd.setDirType(lv); nd.setIsSystem(0);
                CatDirectory sib = directoryService.lambdaQuery()
                        .eq(CatDirectory::getParentId, cp)
                        .eq(CatDirectory::getDirType, lv)
                        .orderByDesc(CatDirectory::getSortOrder).last("LIMIT 1").one();
                nd.setSortOrder(sib != null ? sib.getSortOrder() + 1 : 1);
                directoryService.save(nd);
                curParent = nd.getId();
            }
            level++;
        }
        return curParent;
    }
}

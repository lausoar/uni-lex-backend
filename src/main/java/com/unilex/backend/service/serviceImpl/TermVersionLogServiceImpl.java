package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.TermVersionLog;
import com.unilex.backend.mapper.TermVersionLogMapper;
import com.unilex.backend.service.TermVersionLogService;
import com.unilex.backend.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TermVersionLogServiceImpl extends ServiceImpl<TermVersionLogMapper, TermVersionLog>
        implements TermVersionLogService {

    @Override
    public void logVersionChange(Long termId, Long dirId, String shortKey,
                                 String oldVersion, String newVersion, String changeType) {
        Long userId = SecurityUtil.currentUserId();
        String username = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.unilex.backend.entity.SysUser) {
            username = ((com.unilex.backend.entity.SysUser) auth.getPrincipal()).getUsername();
        }

        TermVersionLog logEntry = TermVersionLog.builder()
                .termId(termId)
                .dirId(dirId)
                .shortKey(shortKey)
                .oldVersion(oldVersion != null ? oldVersion : "")
                .newVersion(newVersion != null ? newVersion : "")
                .changeType(changeType)
                .operatorId(userId)
                .operatorName(username)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            this.save(logEntry);
        } catch (Exception e) {
            log.error("记录版本日志失败", e);
        }
    }

    @Override
    public List<TermVersionLog> listByTermId(Long termId) {
        return lambdaQuery()
                .eq(TermVersionLog::getTermId, termId)
                .orderByDesc(TermVersionLog::getCreatedAt)
                .list();
    }

    @Override
    public List<TermVersionLog> listByShortKey(String shortKey) {
        return lambdaQuery()
                .eq(TermVersionLog::getShortKey, shortKey)
                .orderByDesc(TermVersionLog::getCreatedAt)
                .list();
    }
}

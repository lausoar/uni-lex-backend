package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.OperationLog;
import com.unilex.backend.mapper.OperationLogMapper;
import com.unilex.backend.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
        implements OperationLogService {

    @Async
    @Override
    public void saveLog(OperationLog opLog) {
        try {
            this.save(opLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
}

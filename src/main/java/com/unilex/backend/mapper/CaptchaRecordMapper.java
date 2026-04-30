package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.CaptchaRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 验证码记录Mapper，提供验证码校验状态的更新操作。
 */
@Mapper
public interface CaptchaRecordMapper extends BaseMapper<CaptchaRecord> {

    /**
     * 将指定UUID的验证码记录标记为已校验。
     */
    @Update("UPDATE captcha_records SET validated = 1 WHERE uuid = #{uuid}")
    void markValidated(String uuid);
}

package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.CaptchaRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CaptchaRecordMapper extends BaseMapper<CaptchaRecord> {

    @Update("UPDATE captcha_records SET validated = 1 WHERE uuid = #{uuid}")
    void markValidated(String uuid);
}

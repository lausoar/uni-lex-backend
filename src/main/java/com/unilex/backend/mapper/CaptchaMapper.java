package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.Captcha;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CaptchaMapper extends BaseMapper<Captcha> {

    @Select("SELECT * FROM captcha WHERE uuid=#{uuid} AND expire_time>=#{now} LIMIT 1")
    Optional<Captcha> valid(String uuid, LocalDateTime now);

    @Delete("DELETE FROM captcha WHERE expire_time < #{now}")
    void cleanExpired(LocalDateTime now);
}

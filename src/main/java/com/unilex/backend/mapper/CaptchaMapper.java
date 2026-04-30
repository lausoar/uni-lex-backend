package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.Captcha;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 验证码Mapper，提供验证码的查询及过期清理操作。
 */
public interface CaptchaMapper extends BaseMapper<Captcha> {

    /**
     * 根据UUID查询未过期的有效验证码。
     */
    @Select("SELECT * FROM captcha WHERE uuid=#{uuid} AND expire_time>=#{now} LIMIT 1")
    Optional<Captcha> valid(String uuid, LocalDateTime now);

    /**
     * 清理已过期的验证码记录。
     */
    @Delete("DELETE FROM captcha WHERE expire_time < #{now}")
    void cleanExpired(LocalDateTime now);
}

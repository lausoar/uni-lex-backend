package com.unilex.backend.service.serviceImpl;

import com.unilex.backend.entity.Captcha;
import com.unilex.backend.mapper.CaptchaMapper;
import com.unilex.backend.service.CaptchaService;
import com.unilex.backend.utils.CaptchaGenerator;
import com.unilex.backend.vo.CaptchaVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * 图形验证码服务实现
 * <p>负责生成普通图形验证码、校验用户输入及定时清理过期记录</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaMapper captchaMapper;

    @Override
    @Transactional
    public CaptchaVo generate() {
        // 1. 生成随机验证码图片
        CaptchaGenerator.Captcha gen = CaptchaGenerator.generate();
        String uuid = UUID.randomUUID().toString();
        // 2. 持久化验证码记录（5分钟有效期）
        Captcha po = new Captcha();
        po.setUuid(uuid);
        po.setCode(gen.code);
        po.setExpireTime(LocalDateTime.now().plusMinutes(5));
        captchaMapper.insert(po);

        // 3. 图片转Base64返回
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(gen.image, "png", os);
            String base64 = Base64.getEncoder().encodeToString(os.toByteArray());
            return new CaptchaVo(uuid, "data:image/png;base64," + base64);
        } catch (Exception e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

    @Override
    public boolean validate(String uuid, String userInput) {
        if (uuid == null || userInput == null) return false;

        return captchaMapper.valid(uuid, LocalDateTime.now())
                .map(captcha -> {
                    boolean ok = captcha.getCode()
                            .equalsIgnoreCase(userInput.trim());
                    log.info("库中码={}, 用户输入={}, 比对结果={}",
                            captcha.getCode(), userInput.trim(), ok);
                    log.warn("校验结果={}", ok);
                    return ok;
                })
                .orElseGet(() -> {
                    log.warn("验证码已过期或 uuid 不存在");
                    return false;
                });
    }


    // 每 10 分钟清一次过期验证码
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void clean() {
        captchaMapper.cleanExpired(LocalDateTime.now());
    }
}
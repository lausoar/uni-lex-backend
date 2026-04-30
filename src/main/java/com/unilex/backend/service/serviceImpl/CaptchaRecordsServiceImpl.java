package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.unilex.backend.entity.CaptchaRecord;
import com.unilex.backend.mapper.CaptchaRecordMapper;
import com.unilex.backend.service.CaptchaRecordsService;
import com.unilex.backend.vo.CaptchaRecordsVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 滑块验证码服务实现
 * <p>负责生成拼图滑块验证码、校验用户拖拽位置及定时清理过期记录</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaRecordsServiceImpl implements CaptchaRecordsService {

    private final CaptchaRecordMapper captchaRecordMapper;

    @Value("${captcha.width:400}")
    private int width; // 背景图宽度

    @Value("${captcha.height:200}")
    private int height;

    @Value("${captcha.puzzle-width:50}")
    private int puzzleWidth;

    @Value("${captcha.puzzle-height:50}")
    private int puzzleHeight;

    @Value("${captcha.tolerance:5}")
    private int tolerance;

    // ================= 核心方法 =================

    @Override
    public CaptchaRecordsVo generateCaptcha() {
        try {
            String uuid = IdWorker.get32UUID();

            // 随机生成拼图块X坐标（留出右边距）
            int maxX = width - puzzleWidth - 10;
            int puzzleX = ThreadLocalRandom.current().nextInt(50, maxX);

            // 创建图片
            BufferedImage background = createBackgroundImage();
            BufferedImage puzzleImage = createPuzzleImage(background, puzzleX);
            drawPuzzleSlot(background, puzzleX);

            // 保存到数据库
            saveCaptchaRecord(uuid, puzzleX);

            // 计算滑块参数（必须与前端的轨道宽度一致）
            // 前端 Modal 宽度 450px，左右 padding 20px*2 = 40px
            // 实际轨道宽度 = 450 - 40 = 410px
            // 滑块按钮宽度 40px
            int sliderMaxX = 410 - 40 - 20; // 350px（留点边距）

            return CaptchaRecordsVo.builder()
                    .uuid(uuid)
                    .backgroundImage(toBase64(background))
                    .puzzleImage(toBase64(puzzleImage))
                    .puzzleWidth(puzzleWidth)
                    .puzzleHeight(puzzleHeight)
                    .puzzleX(puzzleX)  // 返回真实位置
                    .build();

        } catch (Exception e) {
            log.error("生成滑块验证码失败", e);
            throw new RuntimeException("生成验证码失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean validate(String uuid, Integer userX) {
        // 参数检查
        if (uuid == null || userX == null || userX < 0) {
            log.warn("验证参数非法: uuid={}, userX={}", uuid, userX);
            return false;
        }

        // 查询验证记录
        CaptchaRecord record = captchaRecordMapper.selectOne(
                new QueryWrapper<CaptchaRecord>()
                        .eq("uuid", uuid)
                        .eq("validated", 0)
        );

        if (record == null) {
            log.warn("验证码记录不存在或已验证: uuid={}", uuid);
            return false;
        }

        // 将数据库存储的 puzzleX 转换为滑块坐标系下的值
        int expectedPuzzleX = record.getPuzzleX(); // 数据库中的值，如：65

        // 坐标系转换参数（必须与前端完全一致）
        int trackWidth = 410; // 前端轨道实际宽度
        int sliderButtonWidth = 40;
        int sliderMaxX = trackWidth - sliderButtonWidth - 20; // 350，留边距

        int puzzleMaxX = width - puzzleWidth - 10; // 340

        // 将 puzzleX 转换为对应的滑块位置
        int expectedSliderX = (int) ((expectedPuzzleX * 1.0 / puzzleMaxX) * sliderMaxX);

        // 计算误差
        int diff = Math.abs(expectedSliderX - userX);
        boolean isValid = diff <= tolerance;

        log.info("滑块验证详情: uuid={}, 数据库puzzleX={}, 期望滑块X={}, 实际滑块X={}, 误差={}, 容忍度={}, 结果={}",
                uuid, expectedPuzzleX, expectedSliderX, userX, diff, tolerance, isValid);

        if (isValid) {
            captchaRecordMapper.markValidated(uuid);
            return true;
        }

        return false;
    }

    // ================= 辅助方法 =================

    private void saveCaptchaRecord(String uuid, int puzzleX) {
        CaptchaRecord record = new CaptchaRecord();
        record.setUuid(uuid);
        record.setPuzzleX(puzzleX);
        record.setValidated(0);
        captchaRecordMapper.insert(record);
    }

    private BufferedImage createBackgroundImage() {
        // 你的现有代码...
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getRandomColor(200, 250));
        g2d.fillRect(0, 0, width, height);

        // 绘制干扰线...
        g2d.setColor(getRandomColor(150, 200));
        for (int i = 0; i < 15; i++) {
            int x1 = ThreadLocalRandom.current().nextInt(width);
            int y1 = ThreadLocalRandom.current().nextInt(height);
            int x2 = ThreadLocalRandom.current().nextInt(width);
            int y2 = ThreadLocalRandom.current().nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();
        return image;
    }

    private BufferedImage createPuzzleImage(BufferedImage background, int puzzleX) {
        BufferedImage puzzle = new BufferedImage(puzzleWidth, puzzleHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = puzzle.createGraphics();

        int puzzleY = height / 2 - puzzleHeight / 2;
        BufferedImage subImage = background.getSubimage(puzzleX, puzzleY, puzzleWidth, puzzleHeight);
        g2d.drawImage(subImage, 0, 0, null);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(0, 0, puzzleWidth - 1, puzzleHeight - 1);

        g2d.dispose();
        return puzzle;
    }

    private void drawPuzzleSlot(BufferedImage background, int puzzleX) {
        Graphics2D g2d = background.createGraphics();
        int puzzleY = height / 2 - puzzleHeight / 2;

        // 绘制灰色缺口
        g2d.setColor(new Color(180, 180, 180, 180));
        g2d.fillRect(puzzleX, puzzleY, puzzleWidth, puzzleHeight);

        // 绘制边框
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(puzzleX, puzzleY, puzzleWidth, puzzleHeight);

        g2d.dispose();
    }

    private String toBase64(BufferedImage image) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
    }

    private Color getRandomColor(int min, int max) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Color(
                random.nextInt(min, max),
                random.nextInt(min, max),
                random.nextInt(min, max)
        );
    }

    // 每30分钟清理过期验证码
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void cleanExpiredCaptcha() {
        try {
            int deleted = captchaRecordMapper.delete(
                    new QueryWrapper<CaptchaRecord>()
                            .lt("created_at", LocalDateTime.now().minusMinutes(30))
            );
            if (deleted > 0) {
                log.info("清理过期验证码记录: {}条", deleted);
            }
        } catch (Exception e) {
            log.error("清理过期验证码失败", e);
        }
    }
}
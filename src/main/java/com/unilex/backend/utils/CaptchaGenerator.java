package com.unilex.backend.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaGenerator {

    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int WIDTH  = 120;
    private static final int HEIGHT = 40;
    private static final int LENGTH = 5;

    public static class Captcha {
        public String code;
        public BufferedImage image;
    }

    public static Captcha generate() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
        String code = sb.toString();

        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.BLACK);
        g.drawString(code, 15, 28);
        // 干扰线
        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
            g.drawLine(r.nextInt(WIDTH), r.nextInt(HEIGHT), r.nextInt(WIDTH), r.nextInt(HEIGHT));
        }
        g.dispose();
        Captcha c = new Captcha();
        c.code = code;
        c.image = img;
        return c;
    }
}
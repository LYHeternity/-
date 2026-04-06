package com.entrepreneur.platform.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类
 */
public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int FONT_SIZE = 20;
    private static final int CODE_LENGTH = 4;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 生成验证码
     * @return 验证码对象，包含验证码图片和验证码文本
     */
    public static Captcha generateCaptcha() {
        // 创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        Random random = new Random();
        g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        for (int i = 0; i < 5; i++) {
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), 
                      random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        // 生成验证码文本
        StringBuilder code = new StringBuilder();
        g.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        for (int i = 0; i < CODE_LENGTH; i++) {
            char c = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            code.append(c);
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            g.drawString(String.valueOf(c), 20 + i * 20, 28);
        }

        // 释放资源
        g.dispose();

        // 将图片转换为Base64
        String imageBase64 = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Captcha(code.toString(), imageBase64);
    }

    /**
     * 验证码对象
     */
    public static class Captcha {
        private String code;
        private String imageBase64;

        public Captcha(String code, String imageBase64) {
            this.code = code;
            this.imageBase64 = imageBase64;
        }

        public String getCode() {
            return code;
        }

        public String getImageBase64() {
            return imageBase64;
        }
    }
}

package com.example.shortlink.service.impl;

import com.example.shortlink.dto.CaptchaResponse;
import com.example.shortlink.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final int CAPTCHA_EXPIRE_MINUTES = 5;
    private static final int IMAGE_WIDTH = 120;
    private static final int IMAGE_HEIGHT = 40;
    private static final String CAPTCHA_CHARS = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    @Override
    public CaptchaResponse generateCaptcha() {
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        String code = generateRandomCode(4);

        String imageBase64 = generateImage(code);

        String key = CAPTCHA_KEY_PREFIX + captchaId;
        redisTemplate.opsForValue().set(key, code.toLowerCase(), CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        CaptchaResponse response = new CaptchaResponse();
        response.setCaptchaId(captchaId);
        response.setImageBase64(imageBase64);
        response.setExpireTime(LocalDateTime.now().plusMinutes(CAPTCHA_EXPIRE_MINUTES));

        return response;
    }

    @Override
    public boolean validateCaptcha(String captchaId, String captchaCode) {
        if (captchaId == null || captchaCode == null) {
            return false;
        }

        String key = CAPTCHA_KEY_PREFIX + captchaId;
        Object storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false;
        }

        boolean valid = storedCode.toString().equalsIgnoreCase(captchaCode.trim());

        if (valid) {
            redisTemplate.delete(key);
        }

        return valid;
    }

    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return code.toString();
    }

    private String generateImage(String code) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        Random random = new Random();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        int x = (IMAGE_WIDTH - fm.stringWidth(code)) / 2;
        int y = (IMAGE_HEIGHT + fm.getAscent()) / 2 - 5;
        g.drawString(code, x, y);

        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(200) + 50));
            g.fillRect(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT), 2, 2);
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        byte[] imageBytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}

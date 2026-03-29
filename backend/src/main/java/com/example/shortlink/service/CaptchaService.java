package com.example.shortlink.service;

import com.example.shortlink.dto.CaptchaResponse;

public interface CaptchaService {
    CaptchaResponse generateCaptcha();
    boolean validateCaptcha(String captchaId, String captchaCode);
}

package com.unilex.backend.service;

import com.unilex.backend.vo.CaptchaVo;

public interface CaptchaService {
    CaptchaVo generate();
    boolean validate(String uuid, String userInput);
}


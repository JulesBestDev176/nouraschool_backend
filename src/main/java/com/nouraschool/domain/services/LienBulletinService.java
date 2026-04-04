package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.viescolaire.LienBulletinParentDto;

import java.util.UUID;

public interface LienBulletinService {

    LienBulletinParentDto generate(UUID bulletinId, UUID parentId);

    LienBulletinParentDto findByToken(String token);

    LienBulletinParentDto verifierOtp(String token, String otp);
}

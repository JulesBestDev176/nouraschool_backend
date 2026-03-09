package com.nouraschool.domain.dtos.viescolaire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LienBulletinParentDto {

    private UUID id;
    private UUID bulletinId;
    private UUID parentId;
    private String token;
    private Boolean otpVerified;
    private Instant expiresAt;
    private Instant ouvertLe;
}

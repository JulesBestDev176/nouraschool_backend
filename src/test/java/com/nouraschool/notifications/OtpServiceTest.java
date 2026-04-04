package com.nouraschool.notifications;

import com.nouraschool.domain.services.OtpService;
import com.nouraschool.domain.services.RedisService;
import com.nouraschool.domain.services.impl.OtpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires OtpService — Mock OTP (task.md FOLDER 11).
 */
@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    RedisService redisService;

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpServiceImpl(redisService);
    }

    @Test
    @DisplayName("generate crée un code 6 chiffres et le stocke en Redis avec TTL")
    void generate_storesCodeInRedis() {
        String code = otpService.generate("reset:user@test.com");
        assertThat(code).hasSize(6).matches("\\d{6}");
        verify(redisService).set(eq("otp:reset:user@test.com"), eq(code), eq(300L));
    }

    @Test
    @DisplayName("verify retourne true quand le code est correct")
    void verify_correctCode_returnsTrue() {
        String key = "reset:user@test.com";
        String code = "123456";
        when(redisService.get("otp:" + key)).thenReturn(code);
        when(redisService.get("otp:attempts:" + key)).thenReturn(null);

        assertThat(otpService.verify(key, code)).isTrue();
        verify(redisService).delete("otp:" + key);
    }

    @Test
    @DisplayName("verify retourne false quand le code est incorrect")
    void verify_incorrectCode_returnsFalse() {
        String key = "reset:user@test.com";
        when(redisService.get("otp:" + key)).thenReturn("123456");
        when(redisService.get("otp:attempts:" + key)).thenReturn(null);

        assertThat(otpService.verify(key, "999999")).isFalse();
        verify(redisService).set(eq("otp:attempts:" + key), anyString(), anyLong());
    }

    @Test
    @DisplayName("invalidate supprime les clés Redis")
    void invalidate_removesKeys() {
        otpService.invalidate("reset:user@test.com");
        verify(redisService).delete("otp:reset:user@test.com");
        verify(redisService).delete("otp:attempts:reset:user@test.com");
    }
}

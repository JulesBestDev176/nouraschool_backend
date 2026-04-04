package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.inscription.LienPaiementCreateDto;
import com.nouraschool.domain.dtos.inscription.LienPaiementParentDto;

import java.util.List;

public interface LienPaiementService {

    List<LienPaiementParentDto> generate(List<LienPaiementCreateDto> dtos);

    LienPaiementParentDto findByToken(String token);

    LienPaiementParentDto verifierOtp(String token, String otp);

    LienPaiementParentDto payer(String token);
}

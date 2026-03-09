package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.EnseignantCreateDto;
import com.nouraschool.domain.dtos.EnseignantDto;
import com.nouraschool.domain.entities.EnseignantEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.EnseignantMapper;
import com.nouraschool.domain.repositories.EnseignantRepository;
import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminEnseignantUseCaseImpl implements AdminEnseignantUseCase {

    @Inject
    EnseignantRepository enseignantRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    TenantRepository tenantRepository;
    @Inject
    EnseignantMapper enseignantMapper;
    @Inject
    PasswordEncoder passwordEncoder;

    @Override
    public List<EnseignantDto> findAll() {
        return enseignantRepository.findAll().stream()
                .map(enseignantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EnseignantDto findById(UUID id) {
        EnseignantEntity entity = enseignantRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Enseignant not found: " + id);
        }
        return enseignantMapper.toDto(entity);
    }

    @Override
    @Transactional
    public EnseignantDto create(EnseignantCreateDto dto) {
        if (userRepository.findByUsername(dto.getUsername()) != null) {
            throw new InvalidRequestException("EMAIL_DEJA_UTILISE");
        }
        if (userRepository.findByUsernameOrEmail(dto.getEmail()) != null) {
            throw new InvalidRequestException("EMAIL_DEJA_UTILISE");
        }
        EnseignantEntity entity = new EnseignantEntity();
        entity.tenant = tenantRepository.findDefault();
        entity.username = dto.getUsername();
        entity.email = dto.getEmail();
        entity.password = passwordEncoder.encode(dto.getPassword());
        entity.firstName = dto.getFirstName();
        entity.lastName = dto.getLastName();
        entity.telephone = dto.getTelephone();
        entity.adresse = dto.getAdresse();
        entity.role = UserRole.ENSEIGNANT;
        entity.mustChangePassword = true;
        entity.matricule = dto.getMatricule();
        entity.specialite = dto.getSpecialite();
        entity.dateEmbauche = dto.getDateEmbauche();
        entity.numeroCNPS = dto.getNumeroCNPS();
        entity.numeroSecuriteSociale = dto.getNumeroSecuriteSociale();
        return enseignantMapper.toDto(enseignantRepository.persist(entity));
    }

    @Override
    @Transactional
    public EnseignantDto update(UUID id, EnseignantDto dto) {
        EnseignantEntity entity = enseignantRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Enseignant not found: " + id);
        }
        if (dto.getUsername() != null) entity.username = dto.getUsername();
        if (dto.getEmail() != null) entity.email = dto.getEmail();
        if (dto.getFirstName() != null) entity.firstName = dto.getFirstName();
        if (dto.getLastName() != null) entity.lastName = dto.getLastName();
        if (dto.getTelephone() != null) entity.telephone = dto.getTelephone();
        if (dto.getAdresse() != null) entity.adresse = dto.getAdresse();
        if (dto.getActive() != null) entity.active = dto.getActive();
        if (dto.getMatricule() != null) entity.matricule = dto.getMatricule();
        if (dto.getSpecialite() != null) entity.specialite = dto.getSpecialite();
        if (dto.getDateEmbauche() != null) entity.dateEmbauche = dto.getDateEmbauche();
        if (dto.getNumeroCNPS() != null) entity.numeroCNPS = dto.getNumeroCNPS();
        if (dto.getNumeroSecuriteSociale() != null) entity.numeroSecuriteSociale = dto.getNumeroSecuriteSociale();
        return enseignantMapper.toDto(enseignantRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EnseignantEntity entity = enseignantRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Enseignant not found: " + id);
        }
        enseignantRepository.delete(entity);
    }
}

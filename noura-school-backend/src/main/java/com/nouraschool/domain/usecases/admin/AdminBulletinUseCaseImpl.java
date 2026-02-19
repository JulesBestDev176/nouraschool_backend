package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.dtos.PageRequest;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.exception.errors.ServiceException;
import com.nouraschool.domain.mappers.BulletinMapper;
import com.nouraschool.domain.repositories.BulletinRepository;
import com.nouraschool.domain.repositories.EleveRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminBulletinUseCaseImpl implements AdminBulletinUseCase {

    @Inject
    BulletinRepository bulletinRepository;
    @Inject
    EleveRepository eleveRepository;
    @Inject
    BulletinMapper bulletinMapper;

    @Override
    public List<BulletinDto> findAll() {
        return bulletinRepository.findAll().stream().map(bulletinMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public PageDto<BulletinDto> findAll(PageRequest pageRequest) {
        try {
            var entities = bulletinRepository.findAll(pageRequest);
            var total = bulletinRepository.count();
            var content = entities.stream().map(bulletinMapper::toDto).collect(Collectors.toList());
            return PageDto.of(content, pageRequest.getPage(), pageRequest.getSize(), total);
        } catch (Exception e) {
            throw new ServiceException("Erreur pagination bulletins: " + e.getMessage(), 500);
        }
    }

    @Override
    public BulletinDto findById(UUID id) {
        var entity = bulletinRepository.findById(id);
        if (entity == null) throw new NotFoundException("Bulletin not found: " + id);
        return bulletinMapper.toDto(entity);
    }

    @Override
    @Transactional
    public BulletinDto create(BulletinDto dto) {
        try {
            var entity = bulletinMapper.toEntity(dto);
            if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
            return bulletinMapper.toDto(bulletinRepository.persist(entity));
        } catch (Exception e) {
            throw new ServiceException("Erreur création bulletin: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public BulletinDto update(UUID id, BulletinDto dto) {
        try {
            var entity = bulletinRepository.findById(id);
            if (entity == null) throw new NotFoundException("Bulletin not found: " + id);
            if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
            if (dto.getTrimestre() != null) entity.trimestre = dto.getTrimestre();
            if (dto.getAnneeScolaire() != null) entity.anneeScolaire = dto.getAnneeScolaire();
            if (dto.getMoyenne() != null) entity.moyenne = dto.getMoyenne();
            if (dto.getMoyenneClasse() != null) entity.moyenneClasse = dto.getMoyenneClasse();
            if (dto.getRang() != null) entity.rang = dto.getRang();
            if (dto.getTotalEleves() != null) entity.totalEleves = dto.getTotalEleves();
            if (dto.getAppreciation() != null) entity.appreciation = dto.getAppreciation();
            if (dto.getNombreAbsences() != null) entity.nombreAbsences = dto.getNombreAbsences();
            if (dto.getNombreRetards() != null) entity.nombreRetards = dto.getNombreRetards();
            if (dto.getFichierPdfUrl() != null) entity.fichierPdfUrl = dto.getFichierPdfUrl();
            return bulletinMapper.toDto(bulletinRepository.persist(entity));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur mise à jour bulletin: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            var entity = bulletinRepository.findById(id);
            if (entity == null) throw new NotFoundException("Bulletin not found: " + id);
            bulletinRepository.delete(entity);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur suppression bulletin: " + e.getMessage(), 500);
        }
    }
}

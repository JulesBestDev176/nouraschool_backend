package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.runtime.tenant.TenantFilterEnabler;
import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Inject
    JPAStreamer jpaStreamer;

    @Inject
    EntityManager entityManager;

    @Inject
    TenantFilterEnabler tenantFilterEnabler;

    @Override
    public List<UserEntity> findAll() {
        tenantFilterEnabler.enableTenantFilter(entityManager);
        return UserEntity.listAll();
    }

    @Override
    public UserEntity findById(UUID id) {
        tenantFilterEnabler.enableTenantFilter(entityManager);
        return UserEntity.findById(id);
    }

    @Override
    public UserEntity findByUsernameOrEmail(String identifier) {
        tenantFilterEnabler.enableTenantFilter(entityManager);
        return jpaStreamer.stream(UserEntity.class)
                .filter(u -> identifier.equals(u.username) || identifier.equals(u.email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserEntity findByUsernameOrEmailOrPhone(String login) {
        if (login == null || login.isBlank()) return null;
        tenantFilterEnabler.enableTenantFilter(entityManager);
        String raw = login.trim();
        String lower = raw.toLowerCase();

        // Ensure tenant is eagerly loaded for authentication checks (tenant.actif).
        List<UserEntity> byLogin = entityManager.createQuery(
                        "select u from UserEntity u left join fetch u.tenant "
                                + "where lower(u.username) = :login or lower(u.email) = :login",
                        UserEntity.class)
                .setParameter("login", lower)
                .setMaxResults(1)
                .getResultList();
        if (!byLogin.isEmpty()) {
            return byLogin.get(0);
        }

        String normalized = normalizePhone(raw);
        List<UserEntity> byPhone = entityManager.createQuery(
                        "select u from UserEntity u left join fetch u.tenant where u.telephone is not null",
                        UserEntity.class)
                .getResultList();
        return byPhone.stream()
                .filter(u -> normalized.equals(normalizePhone(u.telephone)))
                .findFirst()
                .orElse(null);
    }

    /** Normalise pour comparaison (espaces supprimés, uniquement chiffres et +). */
    private static String normalizePhone(String s) {
        if (s == null) return "";
        return s.replaceAll("[\\s\\-.()]", "").trim();
    }

    @Override
    public UserEntity findByUsername(String username) {
        tenantFilterEnabler.enableTenantFilter(entityManager);
        return jpaStreamer.stream(UserEntity.class)
                .filter(u -> username.equals(u.username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserEntity persist(UserEntity user) {
        user.persist();
        return user;
    }

    @Override
    public void delete(UserEntity user) {
        user.delete();
    }
}

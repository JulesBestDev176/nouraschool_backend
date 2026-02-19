package com.nouraschool.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@MappedSuperclass
public abstract class AbstractUuidEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    public UUID id;
}

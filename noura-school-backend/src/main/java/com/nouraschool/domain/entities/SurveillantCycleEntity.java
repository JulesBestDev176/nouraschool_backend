package com.nouraschool.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "surveillant_cycle")
@IdClass(SurveillantCycleEntity.SurveillantCycleId.class)
public class SurveillantCycleEntity extends PanacheEntityBase {

    @Id
    @Column(name = "surveillant_id", nullable = false)
    public UUID surveillantId;

    @Id
    @Column(name = "cycle_id", nullable = false)
    public UUID cycleId;

    @Column(name = "assigned_at", nullable = false)
    public Instant assignedAt = Instant.now();

    @Column(name = "assigned_by")
    public UUID assignedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveillant_id", insertable = false, updatable = false)
    public UserEntity surveillant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", insertable = false, updatable = false)
    public CycleEntity cycle;

    public static class SurveillantCycleId implements java.io.Serializable {
        public UUID surveillantId;
        public UUID cycleId;

        public SurveillantCycleId() {}

        public SurveillantCycleId(UUID surveillantId, UUID cycleId) {
            this.surveillantId = surveillantId;
            this.cycleId = cycleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SurveillantCycleId that)) return false;
            return java.util.Objects.equals(surveillantId, that.surveillantId)
                    && java.util.Objects.equals(cycleId, that.cycleId);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(surveillantId, cycleId);
        }
    }
}

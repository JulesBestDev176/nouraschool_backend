CREATE TABLE surveillant_cycle (
    surveillant_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cycle_id       UUID NOT NULL REFERENCES cycle(id) ON DELETE CASCADE,
    assigned_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    assigned_by    UUID REFERENCES users(id) ON DELETE SET NULL,
    PRIMARY KEY (surveillant_id, cycle_id)
);
CREATE INDEX idx_surveillant_cycle_surveillant ON surveillant_cycle(surveillant_id);
CREATE INDEX idx_surveillant_cycle_cycle ON surveillant_cycle(cycle_id);

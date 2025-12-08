-- Trainers (one per account)
CREATE TABLE trainer (
                         id              BIGSERIAL PRIMARY KEY,
                         email           VARCHAR(255) NOT NULL UNIQUE,
                         password_hash   VARCHAR(255) NOT NULL,
                         name            VARCHAR(255) NOT NULL,
                         pix_key         VARCHAR(255) NOT NULL,
                         created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                         updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Students (belong to a trainer)
CREATE TABLE student (
                         id              BIGSERIAL PRIMARY KEY,
                         trainer_id      BIGINT       NOT NULL REFERENCES trainer(id) ON DELETE CASCADE,
                         name            VARCHAR(255) NOT NULL,
                         contact_info    VARCHAR(255),
                         status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / PAUSED
                         created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                         updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Plans (mensalidades) defined by a trainer
CREATE TABLE plan (
                      id              BIGSERIAL PRIMARY KEY,
                      trainer_id      BIGINT       NOT NULL REFERENCES trainer(id) ON DELETE CASCADE,
                      name            VARCHAR(255) NOT NULL,
                      amount_in_cents INTEGER      NOT NULL,
                      interval_days   INTEGER      NOT NULL, -- for MVP: 30
                      created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                      updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Subscriptions: student <-> plan
CREATE TABLE subscription (
                              id                  BIGSERIAL PRIMARY KEY,
                              student_id          BIGINT       NOT NULL REFERENCES student(id) ON DELETE CASCADE,
                              plan_id             BIGINT       NOT NULL REFERENCES plan(id) ON DELETE CASCADE,
                              status              VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / CANCELED
                              next_payment_date   DATE         NOT NULL,
                              created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                              updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Payments for each cycle
CREATE TABLE payment (
                         id                      BIGSERIAL PRIMARY KEY,
                         subscription_id         BIGINT       NOT NULL REFERENCES subscription(id) ON DELETE CASCADE,
                         amount_in_cents         INTEGER      NOT NULL,
                         due_date                DATE         NOT NULL,
                         paid_date               TIMESTAMPTZ,
                         status                  VARCHAR(32)  NOT NULL DEFAULT 'PENDING', -- PENDING / PAID / CANCELED
                         pix_provider            VARCHAR(64),
                         pix_payment_id          VARCHAR(255), -- ID from provider
                         pix_qr_code             TEXT,
                         pix_copy_paste          TEXT,
                         created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                         updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Basic indexes
CREATE INDEX idx_student_trainer ON student(trainer_id);
CREATE INDEX idx_plan_trainer ON plan(trainer_id);
CREATE INDEX idx_sub_student ON subscription(student_id);
CREATE INDEX idx_sub_plan ON subscription(plan_id);
CREATE INDEX idx_payment_subscription ON payment(subscription_id);
CREATE INDEX idx_payment_status_due ON payment(status, due_date);

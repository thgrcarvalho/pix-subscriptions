-- Change plan.amount_in_cents from INT to BIGINT
ALTER TABLE plan
ALTER COLUMN amount_in_cents TYPE BIGINT;

-- If you also created payment.amount_in_cents as INT, fix that too:
ALTER TABLE payment
ALTER COLUMN amount_in_cents TYPE BIGINT;
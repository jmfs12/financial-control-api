# financial-control-api
User(id, email, password_hash, status, created_at)

Role(id, name) — USER, ADMIN; join table user_roles

Account(id, user_id, name, type[checking|savings|credit], currency, balance_snapshot, institution, created_at)

Category(id, user_id, parent_id?, name, type[income|expense], color)

Transaction(id, user_id, account_id, category_id?, type[income|expense|transfer], amount, currency, description, merchant?, tags[], posted_at, cleared_at?, status[pending|cleared], external_id?, idempotency_key, created_at)

Transfer(id, user_id, from_account_id, to_account_id, amount, currency, fee_amount?, posted_at)

RecurringRule(id, user_id, schedule[cron/rrule], next_run_at, template[txn fields])

Budget(id, user_id, month, category_id, limit_amount, currency)

Attachment(id, user_id, txn_id, filename, media_type, size, storage_key)

AuditLog(id, user_id, action, entity, entity_id, at, metadata)
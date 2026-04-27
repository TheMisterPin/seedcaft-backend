-- `product.category_id` was varchar (numeric id and/or category code). Convert to bigint FK to category.id.
-- Subqueries are not allowed in ALTER USING, so: add temp column, UPDATE, drop/rename.
-- Digits only → id; else resolve via category.code; unknown code → NULL.

DO $migration$
DECLARE
  r record;
BEGIN
  IF to_regclass('public.product') IS NULL THEN
    RETURN;
  END IF;

  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'product'
      AND column_name = 'category_id'
      AND udt_name IN ('varchar', 'text', 'bpchar')
  ) THEN
    RETURN;
  END IF;

  FOR r IN
    SELECT c.conname
    FROM pg_constraint c
    JOIN pg_class rel ON c.conrelid = rel.oid
    JOIN pg_class ref ON c.confrelid = ref.oid
    WHERE c.contype = 'f'
      AND rel.relname = 'product'
      AND ref.relname = 'category'
  LOOP
    EXECUTE format('ALTER TABLE product DROP CONSTRAINT %I', r.conname);
  END LOOP;

  ALTER TABLE product ADD COLUMN category_id_fk_new bigint;

  UPDATE product p
  SET category_id_fk_new = CASE
    WHEN p.category_id IS NULL OR btrim(p.category_id::text) = '' THEN NULL
    WHEN btrim(p.category_id::text) ~ '^\d+$' THEN btrim(p.category_id::text)::bigint
    ELSE (
      SELECT c.id
      FROM category c
      WHERE c.code = btrim(p.category_id::text)
      LIMIT 1
    )
  END;

  ALTER TABLE product DROP COLUMN category_id;
  ALTER TABLE product RENAME COLUMN category_id_fk_new TO category_id;
END
$migration$;

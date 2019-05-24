-- List distinct names of all part categories where the name starts with a 'B' (regardless of case) (Output: name).
SELECT DISTINCT name FROM part_categories WHERE name ILIKE 'b%';
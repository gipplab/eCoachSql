-- Count the number of spare inventory parts, do not forget the quantity attribute! (Output: sum)
SELECT SUM(quantity)
FROM inventory_parts
WHERE is_spare = 't';

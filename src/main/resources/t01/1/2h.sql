-- List the names of each color and the total number of spare inventory parts with that color, descendingly sorted by the total number. (Output: name, sum)
SELECT name, SUM(quantity)
FROM inventory_parts
  JOIN colors ON (id = inventory_parts.color_id)
WHERE is_spare = 't'
GROUP BY name
ORDER BY sum DESC;
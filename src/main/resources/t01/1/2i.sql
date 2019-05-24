-- Count the number of sets each part is involved in and return a descendingly sorted list of part name, part number and number of sets. (Output: name, part_num, count)
SELECT parts.name, parts.part_num, COUNT(*)
FROM parts NATURAL JOIN inventory_parts
JOIN inventories ON (inventories.id = inventory_parts.inventory_id)
JOIN sets ON (inventories.set_num = sets.set_num)
GROUP BY parts.part_num, parts.name
ORDER BY count DESC;
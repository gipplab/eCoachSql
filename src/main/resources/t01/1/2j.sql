-- Retrieve the part numbers of parts that both 'Chandler Muriel Bing' and 'Ross Geller, Ph.D.' own. (Output: part_num)
SELECT DISTINCT parts.part_num FROM persons
  JOIN owns ON persons.id = owns.person_id
  JOIN sets ON owns.set_num = sets.set_num
  JOIN inventories ON sets.set_num = inventories.set_num
  JOIN inventory_parts ON inventories.id = inventory_parts.inventory_id
  JOIN parts ON inventory_parts.part_num = parts.part_num
WHERE persons.name = 'Chandler Muriel Bing'
INTERSECT
  SELECT DISTINCT parts.part_num FROM persons
  JOIN owns ON persons.id = owns.person_id
  JOIN sets ON owns.set_num = sets.set_num
  JOIN inventories ON sets.set_num = inventories.set_num
  JOIN inventory_parts ON inventories.id = inventory_parts.inventory_id
  JOIN parts ON inventory_parts.part_num = parts.part_num
WHERE persons.name = 'Ross Geller, Ph.D.';
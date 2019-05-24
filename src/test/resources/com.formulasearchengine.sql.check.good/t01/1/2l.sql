-- Count how many parts are exclusive to one of the two persons, i.e. parts that one person owns that the other does not own. (Output: count)
WITH
    chandler AS (SELECT parts.part_num
  FROM persons
    JOIN owns ON persons.id = owns.person_id
    JOIN sets ON owns.set_num = sets.set_num
    JOIN inventories ON sets.set_num = inventories.set_num
    JOIN inventory_parts ON inventories.id = inventory_parts.inventory_id
    JOIN parts ON inventory_parts.part_num = parts.part_num
  WHERE persons.name = 'Chandler Muriel Bing'),
    ross AS (SELECT parts.part_num
  FROM persons
    JOIN owns ON persons.id = owns.person_id
    JOIN sets ON owns.set_num = sets.set_num
    JOIN inventories ON sets.set_num = inventories.set_num
    JOIN inventory_parts ON inventories.id = inventory_parts.inventory_id
    JOIN parts ON inventory_parts.part_num = parts.part_num
  WHERE persons.name = 'Ross Geller, Ph.D.')
SELECT COUNT(DISTINCT part_num)
FROM (
  SELECT *
  FROM chandler
  EXCEPT
  SELECT *
  FROM ross

  UNION

  SELECT *
  FROM ross
  EXCEPT
  SELECT *
  FROM chandler
) A;
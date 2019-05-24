SELECT * FROM serves
WHERE beverage = 'Schimmele'
  AND price <= ALL
    (
      SELECT price
      FROM serves
      WHERE beverage = 'Schimmele'
    );
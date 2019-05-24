SELECT dependent_name, bdate
FROM Dependent
WHERE bdate < '1980-12-31'
  AND sex = 'M';
SELECT dependent_name
FROM
  Employee e
    JOIN Dependent d ON (e.ssn = d.essn)
WHERE d.sex = 'F'
  AND e.sex = 'M';
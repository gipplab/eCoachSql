SELECT d.dependent_name
FROM
  Dependent d
    JOIN Employee e ON e.ssn = d.essn
WHERE d.sex = 'M'
  AND d.bdate < e.bdate;
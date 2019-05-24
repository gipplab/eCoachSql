SELECT DISTINCT d.dname
FROM
  Department d
    JOIN Employee e ON (d.dnumber = e.dno)
WHERE e.salary > 45000
  AND e.sex='F';
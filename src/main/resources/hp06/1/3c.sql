SELECT e.ssn, e.fname, e.lname
FROM Employee e
WHERE salary > 40000
  AND e.ssn NOT IN (
    SELECT mgr_ssn FROM Department
    WHERE mgr_ssn IS NOT NULL
    UNION
    SELECT f.super_ssn
    FROM Employee f
    WHERE  f.super_ssn IS NOT NULL
  );
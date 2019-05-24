SELECT e.ssn, e.fname, e.lname
FROM Employee e
WHERE e.super_ssn IS NOT NULL
  AND e.ssn IN (SELECT super_ssn FROM Employee);
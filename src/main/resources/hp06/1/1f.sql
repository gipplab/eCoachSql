SELECT e.ssn, e.fname, e.lname
FROM Employee e
WHERE e.super_ssn IS NULL
  AND e.ssn NOT IN (
    SELECT essn
    FROM Works_on
  );
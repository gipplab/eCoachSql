SELECT e.ssn, e.fname, e.lname
FROM Employee e
WHERE e.ssn IN (
  SELECT essn
  FROM Works_on
  GROUP BY essn
  HAVING COUNT(DISTINCT pno) > 1
);
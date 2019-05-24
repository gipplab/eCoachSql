SELECT e.fname, e.minit, e.lname
FROM
  Employee e JOIN Department d ON e.dno = d.dnumber
WHERE
  e.ssn IN (SELECT super_ssn FROM Employee)
  AND d.dname = 'Administration';
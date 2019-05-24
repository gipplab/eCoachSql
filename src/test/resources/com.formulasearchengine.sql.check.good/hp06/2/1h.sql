SELECT e.ssn, e.fname, e.lname
FROM
  Employee e
    NATURAL JOIN (
      SELECT essn AS ssn
      FROM Works_on
      GROUP BY essn
      HAVING COUNT(DISTINCT pno) > 1
    ) AS W;
SELECT e.ssn, e.fname, e.lname
FROM
  (
    SELECT DISTINCT w1.essn AS ssn
    FROM
      Works_on w1
        JOIN Works_on w2 ON w1.essn = w2.essn AND w1.pno <> w2.pno
  ) AS W
    NATURAL JOIN Employee e;
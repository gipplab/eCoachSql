SELECT e1.fname, e2.fname, e2.lname
FROM
    employee e1 JOIN employee e2 ON e1.fname != e2.fname
WHERE e1.lname = e2.lname;
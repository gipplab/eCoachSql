SELECT DISTINCT d.dname, d.dnumber
FROM
  Project p
    JOIN Department d ON p.dnum = d.dnumber
    JOIN Dept_locations l ON d.dnumber = l.dnumber
WHERE p.plocation = l.dlocation;
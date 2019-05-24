SELECT p.pnumber, p.pname
FROM
  Project p
  JOIN Department d ON p.dnum = d.dnumber
WHERE NOT EXISTS (
  SELECT l.dlocation
  FROM Dept_locations  l
  WHERE l.dnumber = d.dnumber
    AND l.dlocation <> p.plocation
);
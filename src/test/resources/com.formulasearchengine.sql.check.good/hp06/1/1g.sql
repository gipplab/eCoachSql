SELECT p.pname, p.pnumber
FROM Project p
WHERE p.plocation NOT IN (
  SELECT d.dlocation
  FROM Dept_locations d
  WHERE d.dnumber = p.dnum
);
SELECT
  Fname, Minit, Lname, Ssn, e.Bdate AS eBdate, Address, e.Sex AS eSex,
    Salary, Super_ssn, Dno,
  dep.Essn AS depEssn, Dependent_name, dep.Sex AS depSex,
    dep.Bdate AS depBdate, Relationship,
  wo.Essn AS woEssn, Pno, Hours,
  Pname, Pnumber, Plocation, Dnum,
  Dname, d.Dnumber AS Dnumber, Mgr_ssn, Mgr_start_date,
  dl.Dnumber AS dlDnumber, Dlocation
FROM
  Employee e
    -- the SSN is part of Dependent's key, so nothing is lost here
    LEFT OUTER JOIN Dependent dep ON dep.essn = e.ssn
    -- same with Works_on
    LEFT OUTER JOIN Works_on wo ON e.ssn = wo.essn
    -- we lose projects which noone has worked on
    LEFT OUTER JOIN Project p ON wo.pno = p.Pnumber
     -- we lose departments with no employees and projects
    LEFT OUTER JOIN Department d ON (e.dno = d.dnumber OR p.dnum = d.dnumber)
    -- every location has a department (key constraint), therefore nothing is lost
    LEFT OUTER JOIN Dept_locations dl ON d.dnumber = dl.dnumber
UNION -- add projects and their departments and department-locations
      -- if they have no employees
SELECT
  NULL Fname, NULL Minit, NULL Lname, NULL Ssn, NULL eBdate,
    NULL Address, NULL eSex, NULL Salary, NULL Super_ssn, NULL Dno,
  NULL depEssn, NULL Dependent_name, NULL depSex,
    NULL depBdate, NULL Relationship,
  NULL woEssn, NULL Pno, NULL Hours,
  Pname, Pnumber, Plocation, Dnum,
  Dname, d.Dnumber AS Dnumber, Mgr_ssn, Mgr_start_date,
  dl.Dnumber AS dlDnumber, Dlocation
FROM
  Project p
    LEFT OUTER JOIN Department d ON p.dnum = d.dnumber
    LEFT OUTER JOIN Dept_locations dl ON d.dnumber = dl.dnumber
WHERE p.Pnumber NOT IN (SELECT Pno FROM Works_on)
UNION -- departments and department-locations
      -- if they have neither employees nor projects
SELECT
  NULL Fname, NULL Minit, NULL Lname, NULL Ssn, NULL eBdate,
    NULL Address, NULL eSex, NULL Salary, NULL Super_ssn, NULL Dno,
  NULL depEssn, NULL Dependent_name, NULL depSex,
    NULL depBdate, NULL Relationship,
  NULL woEssn, NULL Pno, NULL Hours,
  NULL Pname, NULL Pnumber, NULL Plocation, NULL Dnum,
  Dname, d.Dnumber AS Dnumber, Mgr_ssn, Mgr_start_date,
  dl.Dnumber AS dlDnumber, Dlocation
FROM
  Department d
    LEFT OUTER JOIN Dept_locations dl ON d.dnumber = dl.dnumber
WHERE d.Dnumber NOT IN (SELECT Dno FROM Employee)
  AND d.Dnumber NOT IN (SELECT Dnum FROM Project);
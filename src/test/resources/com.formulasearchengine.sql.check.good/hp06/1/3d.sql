SELECT mgr_ssn
FROM Department
WHERE mgr_ssn NOT IN (SELECT essn FROM Dependent);
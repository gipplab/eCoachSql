SELECT
  E.ename AS employee ,
  S.ename AS supervisor,
  SS.ename AS supsupervisor
FROM emp as E
  JOIN emp as S ON (E.mgr = S.empno)
  JOIN emp as SS ON (S.mgr = SS.empno)
WHERE SS.ename = 'KING'
ORDER BY E.empno ASC;
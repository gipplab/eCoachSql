SELECT genre, COUNT(DISTINCT cdid) AS cds
FROM
  cds
  NATURAL JOIN genres g
GROUP BY g.genreid, genre
HAVING COUNT(DISTINCT cdid) >= 1000
ORDER BY cds DESC
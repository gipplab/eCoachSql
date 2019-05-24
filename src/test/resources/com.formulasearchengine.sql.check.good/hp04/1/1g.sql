SELECT genre, COUNT(DISTINCT albumid) albums
FROM
  artist2album
  NATURAL JOIN cds
  NATURAL JOIN genres g
GROUP BY g.genreid
HAVING COUNT(DISTINCT albumid) >= ALL (
  SELECT COUNT(DISTINCT albumid) albums
  FROM
    artist2album
    NATURAL JOIN cds
  GROUP BY genreid)
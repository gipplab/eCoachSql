SELECT c.ayear, COUNT(DISTINCT a.albumid)
FROM
  albums a
  NATURAL JOIN artist2album
  NATURAL JOIN cds c
WHERE ayear BETWEEN 1900 AND 1999
GROUP BY c.ayear
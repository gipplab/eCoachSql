SELECT artist, COUNT(DISTINCT albumid) AS albums
FROM
  artists
  NATURAL JOIN artist2album
WHERE artist LIKE 'mad%'
GROUP BY artist
HAVING COUNT(DISTINCT albumid) > 1
ORDER BY artist
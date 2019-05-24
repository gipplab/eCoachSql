SELECT
  artist,
  COUNT(DISTINCT genreid) AS num_genres
FROM
  cds
  NATURAL JOIN artist2album
  NATURAL JOIN artists
GROUP BY artistid, artist
ORDER BY num_genres DESC
FETCH FIRST 20 ROWS ONLY;
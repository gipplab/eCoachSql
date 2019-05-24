SELECT
  artist,
  album
FROM songs
  NATURAL JOIN cdtracks
  NATURAL JOIN cds
  NATURAL JOIN artist2album
  NATURAL JOIN artists
  NATURAL JOIN genres
  NATURAL JOIN albums
WHERE song = 'rocky road to dublin' AND ayear = 2003 AND genre = 'folk';
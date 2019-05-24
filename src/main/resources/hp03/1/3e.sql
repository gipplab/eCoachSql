SELECT DISTINCT
  artist,
  song,
  album
FROM cdtracks
  NATURAL JOIN songs
  NATURAL JOIN cds
  NATURAL JOIN artist2album
  NATURAL JOIN albums
  NATURAL JOIN artists
WHERE track = 99;
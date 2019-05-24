SELECT album
FROM artists
  NATURAL JOIN artist2album
  NATURAL JOIN albums
WHERE artist = 'radiohead';
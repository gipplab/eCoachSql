SELECT DISTINCT artist
FROM artists
  NATURAL JOIN (
                 SELECT artistid
                 FROM
                   albums
                   NATURAL JOIN artist2album
                 WHERE album LIKE '%drop%'
               ) D
ORDER BY artist;
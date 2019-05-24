SELECT a.artist
FROM artists a
WHERE EXISTS(
    SELECT artistid
    FROM
      albums
      NATURAL JOIN artist2album a2a
    WHERE album LIKE '%drop%'
          AND a.artistid = a2a.artistid
)
ORDER BY artist;
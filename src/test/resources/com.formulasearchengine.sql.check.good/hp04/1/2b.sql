SELECT genre
FROM genres
WHERE genreid IN (
  SELECT genreid
  FROM cds
  GROUP BY genreid
  HAVING MIN(ayear) >= 2005
)
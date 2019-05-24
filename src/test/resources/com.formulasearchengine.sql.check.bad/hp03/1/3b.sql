SELECT DISTINCT artist
FROM artists
  NATURAL JOIN artist2album
  NATURAL JOIN cds
WHERE ayear = 2000;
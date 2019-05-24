SELECT person, COUNT(DISTINCT bar) AS count
FROM frequents
GROUP BY person;
SELECT person, COUNT(*) as count
FROM frequents
GROUP BY person
ORDER BY count DESC
FETCH FIRST ROW ONLY;

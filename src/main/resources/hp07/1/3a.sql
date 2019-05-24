-- Using Common Table Expressions (CTEs)
WITH freq_counts AS
  (
    SELECT person, COUNT(*) as count FROM frequents
    GROUP BY person
  )
SELECT person, count
FROM freq_counts
WHERE count >= ALL (SELECT count FROM freq_counts);

-- Repeated query
SELECT person, count
FROM (SELECT person, COUNT(*) as count FROM frequents
      GROUP BY person) freq_counts
WHERE count >= ALL
      (
        SELECT COUNT(*)
        FROM frequents
        GROUP BY person);

-- Naive ORDER BY-LIMIT approach
SELECT person, COUNT(*) as count
FROM frequents
GROUP BY person
ORDER BY count DESC
FETCH FIRST ROW ONLY;

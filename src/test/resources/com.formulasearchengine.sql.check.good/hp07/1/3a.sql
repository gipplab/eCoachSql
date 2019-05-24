-- Using Common Table Expressions (CTEs)
WITH freq_counts AS
  (
    SELECT person, COUNT(*) as count FROM frequents
    GROUP BY person
  )
SELECT person, count
FROM freq_counts
WHERE count >= ALL (SELECT count FROM freq_counts);
-- Repeated query
SELECT person, count
FROM (SELECT person, COUNT(*) as count FROM frequents
      GROUP BY person) freq_counts
WHERE count >= ALL
      (
        SELECT COUNT(*)
        FROM frequents
        GROUP BY person);

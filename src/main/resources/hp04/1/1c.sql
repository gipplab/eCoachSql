SELECT MAX(num_cds), AVG(num_cds)
FROM
(
SELECT genreid, COUNT(*) AS num_cds
FROM cds
GROUP BY genreid
) G
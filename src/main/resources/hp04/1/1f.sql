SELECT COUNT(*)
FROM (
       SELECT songid, COUNT(DISTINCT cdid) AS cds
       FROM cdtracks
       GROUP BY songid
       HAVING COUNT(DISTINCT cdid) >= 3
     ) S
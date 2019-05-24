SELECT genre
FROM genres g
WHERE NOT EXISTS(
    SELECT cdid
    FROM cds c
    WHERE c.genreid = g.genreid
          AND ayear < 2005
)
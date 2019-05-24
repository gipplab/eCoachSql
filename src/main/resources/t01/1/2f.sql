-- Count the ratio of transparent colors versus all colors rounded to two decimal places (Output: ratio).
WITH C AS (
  SELECT
    COUNT(*)
      FILTER (WHERE is_trans = 't') AS trans,
    COUNT(*)                        AS total
  FROM lego.colors
)
SELECT ROUND(C.trans::NUMERIC / C.total, 2) as ratio FROM C;
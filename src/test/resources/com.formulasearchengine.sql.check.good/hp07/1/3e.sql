WITH
  -- we can only respect people that like at least one beer
  likers AS (SELECT DISTINCT person FROM likes),
  -- bars in which people could find liked beers: candidates
  candidates AS (SELECT bar, person FROM serves JOIN likes USING (beverage)),
  -- only the bars
  possible_bars AS (SELECT DISTINCT bar FROM candidates),
  -- combinations of possible bars and all persons that like something
  all_combinations AS (SELECT * FROM (SELECT bar FROM candidates) bars CROSS JOIN likers),
  -- actual present combinations
  actual_combinations AS (SELECT bar, person FROM candidates),
  -- bars for which no combination exists
  missed_bars AS (SELECT bar FROM (SELECT * FROM all_combinations EXCEPT SELECT * FROM actual_combinations) _ )

-- possible bars without missing bars: bars in which all persons find a liked beer
SELECT * FROM possible_bars EXCEPT SELECT * FROM missed_bars

-- this is equivalent to the following query if we would have a division operator in postgres
-- SELECT bar, person FROM serves JOIN likes USING (beverage)
-- DIVISION
-- SELECT person FROM likes;
;
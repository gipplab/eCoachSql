SELECT
    customer_num,
    customer_name,
    balance,
    credit_limit,
    rep_num,
    first_name || ' ' || last_name AS rep_name
FROM customer JOIN rep USING (rep_num) -- no nat. join b/c of many dup attrs
WHERE balance > credit_limit;
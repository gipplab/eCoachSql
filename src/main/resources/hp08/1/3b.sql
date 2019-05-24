SELECT AVG(price) AS avg_price, AVG(quoted_price) AS avg_quoted_price
FROM orders NATURAL JOIN order_line NATURAL JOIN part;
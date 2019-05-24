-- Calculate the total undiscounted price and the discount (in percent) per order.
SELECT
    order_num,
    round(SUM(price * num_ordered), 2) AS price,
    round((SUM(price * num_ordered) - SUM(quoted_price * num_ordered)) / SUM(price * num_ordered) * 100, 2) AS discount
FROM orders
    NATURAL JOIN order_line
    NATURAL JOIN part
GROUP BY order_num;
-- List distinct set names of sets with more than 100 parts (Output: name).
SELECT DISTINCT name FROM sets WHERE num_parts > 100;
-- List distinct set names and years from the last millennium (Output: name, year).
SELECT DISTINCT name, year FROM sets WHERE year <= 2000;
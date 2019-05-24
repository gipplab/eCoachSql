SELECT DISTINCT f1.person as person
FROM frequents f1 JOIN frequents f2
    ON (f1.person = f2.person AND f1.bar != f2.bar);
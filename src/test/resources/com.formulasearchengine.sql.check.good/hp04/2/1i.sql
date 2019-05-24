SELECT list.artist, COUNT(list.genreid) as num_genres FROM (
    SELECT DISTINCT artist, genreid FROM cddb.artists
    NATURAL JOIN cddb.artist2album
    NATURAL JOIN cddb.albums
    NATURAL JOIN cddb.cds
    NATURAL JOIN cddb.genres) as list
GROUP By artist
ORDER By COUNT(list.genreid) DESC, list.artist ASC
Limit 20

                        
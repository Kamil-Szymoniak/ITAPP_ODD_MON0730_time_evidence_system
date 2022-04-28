INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (1, 'NAME1', 'SURNAME1', '+48666420656', '2001-10-01');
INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (2, 'NAME2', 'SURNAME2', '666420656', '2001-10-02');

INSERT INTO ppl.team (id, name, description, id_person)
    VALUES (1, 'NAME1', 'DESCRIPTION1', 1);
INSERT INTO ppl.team (id, name, description, id_person)
    VALUES (2, 'NAME2', 'DESCRIPTION2', 2);

INSERT INTO ppl.many_team_has_many_person (id_team, id_person)
    VALUES (1, 1);
INSERT INTO ppl.many_team_has_many_person (id_team, id_person)
    VALUES (1, 2);
INSERT INTO ppl.many_team_has_many_person (id_team, id_person)
    VALUES (2, 2);

INSERT INTO "time".availability(id, comment, date, id_person, id_team, periods)
    VALUES (1, 'COMMENT1', '2030-01-01', 1, 1, '12:00-13:00-60_13:00-14:00-60');
INSERT INTO "time".availability(id, comment, date, id_person, id_team, periods)
    VALUES (2, 'COMMENT2', '2030-01-02', 2, 1, '12:00-13:00-60_13:00-14:00-60');
INSERT INTO "time".availability(id, comment, date, id_person, id_team, periods)
    VALUES (3, 'COMMENT3', '2030-01-03', 2, 2, '12:00-13:00-60_13:00-14:00-60');
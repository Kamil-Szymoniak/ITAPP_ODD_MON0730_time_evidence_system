INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (1, 'NAME1', 'SURNAME1', '+48666420656', '2001-10-01');
INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (2, 'NAME2', 'SURNAME2', '666420656', '2001-10-02');
INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (3, 'NAME3', 'SURNAME3', '0048666420656', '2001-10-03');

INSERT INTO adm."user" (id, username, email, password, id_person)
    VALUES (1, 'NAME1', 'EMAIL1@E.mail', 'TEST', 1);

INSERT INTO ppl.team (id, name, description, id_person)
    VALUES (1, 'NAME1', 'DESCRIPTION1', 1);
INSERT INTO ppl.team (id, name, description, id_person)
    VALUES (2, 'NAME2', 'DESCRIPTION2', 2);
INSERT INTO ppl.team (id, name, description, id_person)
    VALUES (3, 'NAME3', 'DESCRIPTION3', 3);

INSERT INTO ppl.many_team_has_many_person (id_team, id_person)
    VALUES (1, 1);
INSERT INTO ppl.many_team_has_many_person (id_team, id_person)
    VALUES (1, 2);
INSERT INTO ppl.many_team_has_many_person (id_team, id_person)
    VALUES (2, 2);
INSERT INTO ppl.many_team_has_many_person (id_team, id_person)
    VALUES (3, 3);
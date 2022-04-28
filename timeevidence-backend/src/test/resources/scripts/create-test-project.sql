INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (1, 'NAME1', 'SURNAME1', '+48666420656', '2001-10-01');
INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (2, 'NAME2', 'SURNAME2', '666420656', '2001-10-02');
INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (3, 'NAME3', 'SURNAME3', '0048666420656', '2001-10-03');

INSERT INTO adm."user" (id, username, email, password, id_person)
    VALUES (1, 'NAME1', 'EMAIL1@E.mail', 'TEST', 1);

INSERT INTO pro.project (id, name, inhouse_name, description, client_name, id_person, beginning_date)
    VALUES (1, 'NAME1', 'INHOUSE NAME1', 'DESCRIPTION1', 'CLIENT NAME1', 1, '2030-12-12');
INSERT INTO pro.project (id, name, inhouse_name, description, client_name, id_person, beginning_date)
    VALUES (2, 'NAME2', 'INHOUSE NAME2','DESCRIPTION2', 'CLIENT NAME2', 2, '2030-12-12');
INSERT INTO pro.project (id, name, inhouse_name, description, client_name, id_person, beginning_date)
    VALUES (3, 'NAME3', 'INHOUSE NAME3','DESCRIPTION3', 'CLIENT NAME3', 3, '2030-12-12');

INSERT INTO pro.many_project_has_many_person (id_project, id_person)
    VALUES (1, 1);
INSERT INTO pro.many_project_has_many_person (id_project, id_person)
    VALUES (1, 2);
INSERT INTO pro.many_project_has_many_person (id_project, id_person)
    VALUES (2, 2);
INSERT INTO pro.many_project_has_many_person (id_project, id_person)
    VALUES (3, 3);
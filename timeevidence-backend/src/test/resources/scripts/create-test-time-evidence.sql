INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (1, 'NAME1', 'SURNAME1', '+48666420656', '2001-10-01');
INSERT INTO ppl."person" (id, name, surname, phone, birthday)
    VALUES (2, 'NAME2', 'SURNAME2', '666420656', '2001-10-02');

INSERT INTO pro.project (id, name, inhouse_name, description, client_name, id_person, beginning_date)
    VALUES (1, 'NAME1', 'INHOUSE NAME1', 'DESCRIPTION1', 'CLIENT NAME1', 1, '2030-12-12');
INSERT INTO pro.project (id, name, inhouse_name, description, client_name, id_person, beginning_date)
    VALUES (2, 'NAME2', 'INHOUSE NAME2','DESCRIPTION2', 'CLIENT NAME2', 2, '2030-12-12');

INSERT INTO pro.many_project_has_many_person (id_project, id_person)
    VALUES (1, 1);
INSERT INTO pro.many_project_has_many_person (id_project, id_person)
    VALUES (1, 2);
INSERT INTO pro.many_project_has_many_person (id_project, id_person)
    VALUES (2, 2);

INSERT INTO "time".time_evidence (id, date, minutes, comment, id_person, id_project, status)
    VALUES (1, '2021-12-01', 60, 'COMMENT1', 1, 1, 'SENT');
INSERT INTO "time".time_evidence (id, date, minutes, comment, id_person, id_project, status)
    VALUES (2, '2021-12-02', 120, 'COMMENT2', 1, 2, 'SENT');
INSERT INTO "time".time_evidence (id, date, minutes, comment, id_person, id_project, status)
    VALUES (3, '2021-12-03', 180, 'COMMENT3', 2, 2, 'SENT');
INSERT INTO "time".time_evidence (id, date, minutes, comment, id_person, id_project, status, status_comment)
    VALUES (4, '2021-12-04', 240, 'COMMENT4', 1, 1, 'ACCEPTED', 'Some comment');
INSERT INTO adm.permission (id, name) VALUES (1, 'NAME1');
INSERT INTO adm.permission (id, name) VALUES (2, 'NAME2');
INSERT INTO adm.permission (id, name) VALUES (3, 'NAME3');

INSERT INTO adm.role (id, name) VALUES (1, 'NAME1');
INSERT INTO adm.role (id, name) VALUES (2, 'NAME2');
INSERT INTO adm.role (id, name) VALUES (3, 'NAME3');

INSERT INTO adm.many_role_has_many_permission (id_role, id_permission) VALUES (1, 1);
INSERT INTO adm.many_role_has_many_permission (id_role, id_permission) VALUES (2, 2);
INSERT INTO adm.many_role_has_many_permission (id_role, id_permission) VALUES (3, 3);

INSERT INTO ppl."person" (id, name, surname, phone, birthday)
VALUES (1, 'NAME1', 'SURNAME1', '+48666420656', '2001-10-01');

INSERT INTO adm.user (id, username, email, password)
VALUES (1, 'NAME1', 'EMAIL1@E.mail', 'f8ec9599146c49f64ed3d39b');
INSERT INTO adm.user (id, username, email, password)
VALUES (2, 'NAME2', 'EMAIL2@E.mail', 'TEST');
INSERT INTO adm.user (id, username, email, password, id_person)
VALUES (3, 'NAME3', 'EMAIL3@E.mail', 'TEST', 1);

INSERT INTO adm.many_user_has_many_role (id_user, id_role) VALUES (1, 1);
INSERT INTO adm.many_user_has_many_role (id_user, id_role) VALUES (1, 2);
INSERT INTO adm.many_user_has_many_role (id_user, id_role) VALUES (2, 2);
INSERT INTO adm.many_user_has_many_role (id_user, id_role) VALUES (3, 3);
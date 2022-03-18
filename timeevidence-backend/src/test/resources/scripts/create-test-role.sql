INSERT INTO adm.permission (id, name) VALUES (0, 'default');
INSERT INTO adm.permission (id, name) VALUES (1, 'NAME1');
INSERT INTO adm.permission (id, name) VALUES (2, 'NAME2');
INSERT INTO adm.permission (id, name) VALUES (3, 'NAME3');

INSERT INTO adm.role (id, name) VALUES (1, 'NAME1');
INSERT INTO adm.role (id, name) VALUES (2, 'NAME2');
INSERT INTO adm.role (id, name) VALUES (3, 'NAME3');

INSERT INTO adm.many_role_has_many_permission (id_role, id_permission) VALUES (1, 1);
INSERT INTO adm.many_role_has_many_permission (id_role, id_permission) VALUES (1, 2);
INSERT INTO adm.many_role_has_many_permission (id_role, id_permission) VALUES (2, 2);
INSERT INTO adm.many_role_has_many_permission (id_role, id_permission) VALUES (3, 3);
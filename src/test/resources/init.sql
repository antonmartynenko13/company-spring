
--if period is 0
SELECT * FROM users u
LEFT JOIN project_position p
ON p.user_id = u.id
--if no projectPositions user is available
WHERE (p.id IS NULL)
OR p.position_start_date > :now;

--if period > 0
SELECT * FROM users u
LEFT JOIN project_position p
ON p.user_id = u.id
--if no projectPositions user is available
WHERE (p.id IS NULL)
OR p.position_start_date > :now
OR p.position_end_date < :end_of_period;

-------------------------------------

INSERT INTO department
VALUES(DEFAULT, 'Sales');

INSERT INTO users
VALUES (DEFAULT, 'Anton', 'Mart', 'email@domain.com', 'somepass', 'Some title', 1);

INSERT INTO users
VALUES (DEFAULT, 'Artem', 'June', 'email2@domain.com', 'somepass', 'Some title', 1);

INSERT INTO users
VALUES (DEFAULT, 'Marina', 'Nove', 'email3@domain.com', 'somepass', 'Some title', 1);

INSERT INTO project
VALUES (DEFAULT, 'Some project title', '2022-11-25', NULL);

INSERT INTO project_position
VALUES (DEFAULT, 1, 1, '2022-11-25', NULL, 'Some position title', 'Some occupation');

INSERT INTO project_position
VALUES (DEFAULT, 5, 1, '2022-10-25', '2022-11-23', 'Some position title', 'Some occupation');



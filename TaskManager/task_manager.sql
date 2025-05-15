CREATE DATABASE IF NOT EXISTS task_manager;
USE task_manager;

CREATE TABLE task_status (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO task_status (status_name, description) VALUES
('Pending', 'Task is newly created and awaiting action'),
('In Progress', 'Task is currently being worked on'),
('Completed', 'Task has been finished'),
('Canceled', 'Task has been canceled');

CREATE TABLE tasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    CHECK (end_date >= start_date),
    FOREIGN KEY (status_id) REFERENCES task_status (status_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Example data
INSERT INTO tasks (name, description, start_date, end_date, status_id)
VALUES ('Team Meeting', 'Discuss project milestones', '2025-05-14', '2025-05-15',
    (SELECT status_id FROM task_status WHERE status_name = 'Pending'));


UPDATE tasks
SET name = 'Updated Meeting',
    description = 'Discuss project updates',
    start_date = '2025-05-15',
    end_date = '2025-05-16',
    status_id = (SELECT status_id FROM task_status WHERE status_name = 'In Progress')
WHERE task_id = 1;

DELETE FROM tasks WHERE task_id = 1;

UPDATE tasks
SET status_id = (SELECT status_id FROM task_status WHERE status_name = 'Completed')
WHERE task_id = 1;

SELECT t.task_id, t.name, t.description, 
       DATE_FORMAT(t.start_date, '%d/%m/%Y') AS start_date, 
       DATE_FORMAT(t.end_date, '%d/%m/%Y') AS end_date, 
       s.status_name
FROM tasks t
JOIN task_status s ON t.status_id = s.status_id
WHERE s.status_name = 'Pending';

-- Retrieve all tasks
SELECT t.task_id, t.name, t.description, 
       DATE_FORMAT(t.start_date, '%d/%m/%Y') AS start_date, 
       DATE_FORMAT(t.end_date, '%d/%m/%Y') AS end_date, 
       s.status_name
FROM tasks t
JOIN task_status s ON t.status_id = s.status_id;
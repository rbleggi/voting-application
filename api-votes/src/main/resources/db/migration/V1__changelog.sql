CREATE TABLE IF NOT EXISTS poll_entity (
    id uuid default gen_random_uuid(),
    voting_session_id uuid,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    total_votes INT,
    votes_against INT,
    votes_in_favour INT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS voting_session_entity (
    id uuid default gen_random_uuid(),
    duration VARCHAR(255) ,
    status VARCHAR(255),
    opened_date TIMESTAMP,
    closed_date TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vote_entity (
    id uuid default gen_random_uuid(),
    voting_session_id uuid NOT NULL,
    vote_enum VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (voting_session_id) REFERENCES voting_session_entity(id),
    CONSTRAINT UC_Cpf UNIQUE (voting_session_id, cpf)
);
CREATE TABLE IF NOT EXISTS user_actions
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       BIGINT           NOT NULL,
    event_id      BIGINT           NOT NULL,
    rating        DOUBLE PRECISION NOT NULL,
    created       TIMESTAMP WITH TIME ZONE         NOT NULL,
    unique (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS similarities
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_a BIGINT           NOT NULL,
    event_b BIGINT           NOT NULL,
    similarity   DOUBLE PRECISION NOT NULL,
    unique (event_a, event_b)
);
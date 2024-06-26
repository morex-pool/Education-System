
CREATE TABLE App_User (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(50) NOT NULL
);

CREATE TABLE Achievement (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE Achievement_App_User (
    achievement_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (achievement_id, user_id),
    FOREIGN KEY (achievement_id) REFERENCES Achievement(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES App_User(id) ON DELETE CASCADE ON UPDATE CASCADE
);


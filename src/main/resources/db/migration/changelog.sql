-- changeset 1
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- changeset 2
CREATE TABLE IF NOT EXISTS files (
    id UUID PRIMARY KEY,
    originalFileName VARCHAR(255) NOT NULL,
    filePath VARCHAR(255) NOT NULL,
    fileSize INT NOT NULL,
    mimeType VARCHAR(255) NOT NULL,
    userId UUID NOT NULL,
    FOREIGN KEY (userId) REFERENCES users (id)
);

-- changeset 3
ALTER TABLE files ADD COLUMN fileHash VARCHAR(255) NOT NULL DEFAULT '';
UPDATE files SET fileHash = SUBSTRING(filePath from 9) WHERE filepath LIKE 'uploads/%';
ALTER TABLE files DROP COLUMN filePath;

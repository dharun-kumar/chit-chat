-- ============================================================
--  chit-chat PostgreSQL Schema
-- ============================================================

-- Users table
--   uname      : primary key, used as login identifier
--   upass      : BCrypt hash (always 60 chars; 255 for headroom)
--   sessionkey : UUID token set on login, cleared on logout (nullable)
CREATE TABLE IF NOT EXISTS users (
    uname      VARCHAR(50)  PRIMARY KEY,
    upass      VARCHAR(255) NOT NULL,
    sessionkey VARCHAR(255)
);

-- Chats table
--   Column order matters: getChats() uses col 2=sender, col 3=receiver,
--   col 4=message, col 5=delivertime via SELECT *, so positions must match.
--   delivertime is read as Timestamp.getTime() (epoch millis) in Java.
--   sender/receiver reference users(uname).
--   delivertime defaults to now() so INSERT omits it.
CREATE TABLE IF NOT EXISTS chats (
    id          SERIAL      PRIMARY KEY,
    sender      VARCHAR(50) NOT NULL,
    receiver    VARCHAR(50) NOT NULL,
    message     TEXT        NOT NULL,
    delivertime TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  Indexes
-- ============================================================

-- getChats() filters: (sender=A AND receiver=B) OR (sender=B AND receiver=A)
-- plus delivertime > ? ORDER BY delivertime ASC
-- Two directional composite indexes let Postgres bitmap-AND them.
CREATE INDEX IF NOT EXISTS idx_chats_s_r_time
    ON chats (sender, receiver, delivertime);

CREATE INDEX IF NOT EXISTS idx_chats_r_s_time
    ON chats (receiver, sender, delivertime);

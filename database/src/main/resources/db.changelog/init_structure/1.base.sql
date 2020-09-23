
CREATE TABLE user (
  userName        CHARACTER VARYING(50) NOT NULL,
  name            CHARACTER VARYING(50) NOT NULL,
  pk              TEXT NOT NULL,
  pub             TEXT NOT NULL,
  description     CHARACTER VARYING(50),
  CONSTRAINT user_datastorage_pkey PRIMARY KEY (userName)
);
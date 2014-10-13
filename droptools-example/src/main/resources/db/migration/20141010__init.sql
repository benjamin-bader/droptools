CREATE SCHEMA IF NOT EXISTS ex;

CREATE TABLE ex.blog_post (
  id SERIAL PRIMARY KEY NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  body TEXT NOT NULL
);

CREATE TABLE ex.post_tag (
  post_id INTEGER NOT NULL REFERENCES ex.blog_post (id),
  tag_name TEXT NOT NULL,

  PRIMARY KEY (post_id, tag_name)
);

-- To assist in retrieving posts by tag in a case-insensitive manner
CREATE INDEX post_tag_ix_tag_post ON ex.post_tag (
  lower(tag_name),
  post_id
);

INSERT INTO ex.blog_post (body) VALUES
  ('FRIST POST'),
  ('WHATEVS'),
  ('This is a serious and concerted attempt at conveying a subtle point on the philosophical nature of ponies.');

INSERT INTO ex.post_tag (tag_name, post_id) VALUES ('ponies', 3);
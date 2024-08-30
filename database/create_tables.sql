CREATE TABLE source_text (
    hash 	character varying(40) NOT NULL,
    content 	text NOT NULL,
    created 	timestamp without time zone,
    lang 	character varying(2) NOT NULL
);

CREATE TABLE target_text (
    hash 	character varying(40) NOT NULL,
    content	text NOT NULL,
    created 	timestamp without time zone,
    lang 	character varying(2) NOT NULL,
    source_hash	character varying(40) NOT NULL
);

CREATE TABLE task (
    id 		bigint NOT NULL,
    source_lang	character varying(2) NOT NULL,
    submitted	timestamp(6) with time zone,
    target_lang	character varying(2) NOT NULL,
    source_hash	character varying(40)
);

CREATE SEQUENCE task_id_seq START WITH 1 INCREMENT BY 1;
ALTER TABLE task ALTER COLUMN id SET DEFAULT nextval('task_id_seq'::regclass);

CREATE INDEX idx_task_source_target ON task USING btree (source_lang, target_lang);


ALTER TABLE source_text ADD CONSTRAINT source_text_pkey
	PRIMARY KEY (hash);
ALTER TABLE target_text ADD CONSTRAINT target_text_pkey 
	PRIMARY KEY (hash);
ALTER TABLE task ADD CONSTRAINT task_pkey
	PRIMARY KEY (id);

ALTER TABLE task ADD CONSTRAINT fk_task_source_text 
	FOREIGN KEY (source_hash) REFERENCES source_text(hash);
ALTER TABLE target_text ADD CONSTRAINT fk_target_text_source_text 
	FOREIGN KEY (source_hash) REFERENCES source_text(hash);


GRANT CONNECT ON DATABASE etranslation TO translation;

GRANT USAGE 
	ON SCHEMA public
	TO translation;
GRANT SELECT, INSERT, UPDATE, DELETE
	ON ALL TABLES IN SCHEMA public
	TO translation;
GRANT SELECT
	ON ALL SEQUENCES IN SCHEMA public
	TO translation;


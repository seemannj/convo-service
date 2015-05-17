# convo-service
Service for coding assignment. Manages messages between users.

# Database schema

CREATE SEQUENCE 'thread_seq' START AT 1;

CREATE TABLE convo.convo(
  id bigserial PRIMARY KEY,
  sender bigint NOT NULL REFERENCES users(id),
  recipient bigint NOT NULL REFERENCES users(id),
  subject varchar(140) NOT NULL,
  body text NOT NULL,
  was_read boolean NOT NULL default false,
  thread_id bigint,
  reply_to_convo bigint REFERENCES convo.convo(id),
  send_time timestamp NOT NULL DEFAULT now(),
  update_time timestamp,
  deleted_by_sender boolean NOT NULL DEFAULT false,
  deleted_by_recipient boolean NOT NULL DEFAULT false
);

CREATE INDEX convo_sender_idx ON convo.convo (sender);
CREATE INDEX convo_recipient_idx ON convo.convo (recipient);
CREATE INDEX convo_thread_id_idx ON convo.convo (thread_id);

This was designed and implemented using PostgreSQL, the DB I'm most familiar with at my current job, and which I've found very versatile. "Convos" are the main entities we want to manage, so we need a table to encapsulate them. A convo consists of:
* ID, a unique PRIMARY KEY, long (bigint) type, managed by a DB sequence (bigserial covers that)
* sender, long (bigint) type, can't be NULL (all convos must have a sender and recipient), foreign key on users table (my actual testing didn't have the constraint, I didn't bother creating the table).
* recipient, same as above
* subject, a varchar with a length limit of 140, as per requirements
* body, a variable length text type, with size limit managed in code
* was_read, a boolean indicating it was read by the recipient
* thread_id, a long indicating the thread the convo belongs to
* reply_to_convo, a long indicating another convo that the convo is a reply to, optional
* send_time, timestamp, indicating time the convo was sent
* update_time, timestamp, last time the convo was updated, only update operation we support is read/unread
* deleted_by_sender, a boolean indicating the convo was deleted by the sender, and should not show in their "sent convos"
* deleted_by_recipient, a boolean indicating the convo was deleted by the recipient, and should not show in their inbox

I added indices on sender, recipient, and thread_id, because those are the columns we are basing our lookups on. ID is already indexed by the "PRIMARY KEY" syntax.

The only other real entity to manage is "threads", but seeing as that is only a grouping of convos with no other data, I chose to implement that as a sequence ID on a convo. When a new convo is created, not in reply to another, we create a new thread_id. Any replies to that convo use the existing thread_id (and subject). If there was more metadata to a thread, I would have created a separate table.

# Alternative DB schema

I also considered a more denormalized approach, where I stored a collection of individual convos, and threads of convos separately. In that approach, I would likely use a NoSQL solution like MongoDB. One "convos" collection could contain every individual convo, indexed by id/sender/recipient like above. Then a separate "threads" collection would contain all the convo data in nested objects representing entire threads. This would make querying for threads very simple and fast. However, updates would be more expensive, and referential integrity would be a problem. Paginating through threads would also be difficult.

# REST API

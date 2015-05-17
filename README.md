# convo-service
Service for coding assignment. Manages messages between users.

# Frameworks Used
* gradle for dependency management. "gradle run" should start the service on localhost:8080
* Spring library for many purposes, specifically Spring Boot (http://projects.spring.io/spring-boot/) for building the standalone service quickly.
* rest-assured (https://code.google.com/p/rest-assured/) for testing the REST interface
* Jackson for JSON serialization
* joda-time for Date manipulation

# REST API

* Many of the endpoints below return a "convo" object, as a JSON object with the following properties:
  *  id: Unique ID of the convo, as a long.
  *  sender: ID of the user who sent the convo.
  *  recipient: ID of the user who received the convo.
  *  subject: Subject text of the convo
  *  body: Body text of the convo
  *  wasRead: True if convo was read by the recipient, false otherwise.
  *  threadId: Thread that the convo belongs to. Replies all belong to the same thread.
  *  sendTime: Time the convo was originally sent.
  *  updateTime: Last time the convo was updated. Currently, only update operation is changing the wasRead flag.
  *  replyToConvo: ID of the convo that this convo is in reply to.

* POST /api/v1/convos
  *  Create a new convo.
  *  Params:
    *  Body: {"sender": {sender_user_id}, "recipient": {recipient_user_id}, "subject": "subject goes here", "body": "body goes here"}
    *  Subject is max 140 chars, Body is max 64k chars. 400 returned if any params do not match requirements.
  *  Returns:
    *  Unique ID for the newly created convo, as a long.

* POST /api/v1/convos/{convoId}/replies
  *  Reply to an existing convo. New convo will be part of the same "thread", and will have the same subject as the convo being replied to.
  *  Params:
    *  convoId: Unique ID of the convo being replied to, as a long.
    *  Body: {"sender": {sender_user_id}, "recipient": {recipient_user_id}, "subject": "subject goes here", "body": "body goes here"}
  *  Returns:
    *  Unique ID for the newly created convo, as a long.

* GET /api/v1/{userId}/convos/sent
  *  Get all convos sent by the given user ID, ordered by send time.
  *  Params:
    *  userId: Unique ID of a user.
    *  offset: Number of items to skip over from the start of the data set. Optional, defaults to 0.
    *  limit: Max number of items to return. Optional, defaults to 0 (no limit).
    *  direction: How the convos returned should be sorted, either "asc" (ascending) or "desc" (descending). Optional, defaults to "desc" (descending).
  *  Returns:
    *  JSON object with the following properties
      *  total: The total number of convos sent by the user, NOT how many were returned in this response.
      *  convos: A list of convo objects (see above).
      *  offset: How many convos have been skipped over to start the given convo list.
      *  next: Path that will result in the next set of convos, using the given limit and direction, offset = givenOffset + limit.
      *  previous: Path that will result the previous set of convos, using the given limit and direction, offset = givenOffset - limit (lower bound 0).

* GET /api/v1/{userId}/convos/received
  *  Get all convos received by the given user ID, ordered by send time.
  *  Params:
    *  userId: Unique ID of a user.
    *  offset: Number of items to skip over from the start of the data set. Optional, defaults to 0.
    *  limit: Max number of items to return. Optional, defaults to 0 (no limit).
    *  direction: How the convos returned should be sorted, either "asc" (ascending) or "desc" (descending). Optional, defaults to "desc" (descending).
  *  Returns:
    *  JSON object with the following properties
      *  total: The total number of convos sent by the user, NOT how many were returned in this response.
      *  convos: A list of convo objects (see above).
      *  offset: How many convos have been skipped over to start the given convo list.
      *  next: Path that will result in the next set of convos, using the given limit and direction, offset = givenOffset + limit.
      *  previous: Path that will result the previous set of convos, using the given limit and direction, offset = givenOffset - limit (lower bound 0).

* GET /api/v1/{userId}/convos/sent/{convoId}
  *  Get an individual convo that was sent by the given user, with the given ID.
  *  Params:
    *  userId: User that sent the convo
    *  convoId: ID of the convo
  *  Returns:
    *  The requested convo object, or 404 if it does not exist.

* GET /api/v1/{userId}/convos/received/{convoId}
  *  Get an individual convo that was received by the given user, with the given ID.
  *  Params:
    *  userId: User that received the convo
    *  convoId: ID of the convo
  *  Returns:
    *  The requested convo object, or 404 if it does not exist.

* DELETE /api/v1/{userId}/convos/{convoId}
  *  Mark the given convo as deleted by the given user. That convo will no longer appear in that user's sent or received responses, and cannot be un-deleted.
  *  Params:
    *  userId: User that sent or received the convo.
    *  convoId: ID of the convo to be deleted.
  *  Returns:
    *  No Content. 200 response if successful.

* PUT /api/v1/{userId}/convos/received/{convoId}
  *  Toggle the "wasRead" flag of the given convo received by the given user. If "wasRead" was true, it is set to false, set to true otherwise.
  *  Params:
    *  userId: User who received the convo.
    *  convoId: Convo to be marked as read/unread.
  *  Returns:
    *  No Content. 200 response if successul.

* GET /api/v1/{userId}/threads
  *  Get all threads for a user, in the form of a list of the most recent convos from each of the threads.
  *  Params:
    *  userId: Unique ID of a user.
    *  offset: Number of items to skip over from the start of the data set. Optional, defaults to 0.
    *  limit: Max number of items to return. Optional, defaults to 0 (no limit).
    *  direction: How the convos returned should be sorted, either "asc" (ascending) or "desc" (descending). Optional, defaults to "desc" (descending).
  *  Returns:
    *  JSON object with the following properties
      *  total: The total number of threads the user has participated in, NOT how many were returned in this response.
      *  convos: A list of convo objects (see above).
      *  offset: How many threads have been skipped over to start the given thread list.
      *  next: Path that will result in the next set of threads, using the given limit and direction, offset = givenOffset + limit.
      *  previous: Path that will result the previous set of threads, using the given limit and direction, offset = givenOffset - limit (lower bound 0).

* GET /api/v1/{userId}/threads/{threadId}
  *  Get convos from one thread in a users inbox. Convos are ordered by send-time, default descending.
  *  Params:
    *  userId: Unique ID of a user.
    *  threadId: Unique ID of the thread of convos we want to return.
    *  offset: Number of items to skip over from the start of the data set. Optional, defaults to 0.
    *  limit: Max number of items to return. Optional, defaults to 0 (no limit).
    *  direction: How the convos returned should be sorted, either "asc" (ascending) or "desc" (descending). Optional, defaults to "desc" (descending).
  *  Returns:
    *  JSON object with the following properties
      *  total: The total number of convos in the thread, NOT how many were returned in this response.
      *  convos: A list of convo objects (see above).
      *  offset: How many convos have been skipped over to start the given convo list.
      *  next: Path that will result in the next set of convos, using the given limit and direction, offset = givenOffset + limit.
      *  previous: Path that will result the previous set of convos, using the given limit and direction, offset = givenOffset - limit (lower bound 0).

* DELETE /api/v1/{userId}/threads/{threadId}
  *  Delete all convos in the given thread for the given user. All convos in the thread will not appear in the sent or received responses for the user, and cannot be undeleted.
  *  Params:
    *  userId: Unique ID for a user, recipient or sender for the convos we want deleted.
    *  threadId: Thread ID for the convos we want deleted.
  *  Returns:
    *  No Content. 200 response if successful.

# Database schema

CREATE SEQUENCE 'thread_seq' START AT 1;

CREATE TABLE convo.convo(
  * id bigserial PRIMARY KEY,
  * sender bigint NOT NULL REFERENCES users(id),
  * recipient bigint NOT NULL REFERENCES users(id),
  * subject varchar(140) NOT NULL,
  * body text NOT NULL,
  * was_read boolean NOT NULL default false,
  * thread_id bigint,
  * reply_to_convo bigint REFERENCES convo.convo(id),
  * send_time timestamp NOT NULL DEFAULT now(),
  * update_time timestamp,
  * deleted_by_sender boolean NOT NULL DEFAULT false,
  * deleted_by_recipient boolean NOT NULL DEFAULT false
);

* CREATE INDEX convo_sender_idx ON convo.convo (sender);
* CREATE INDEX convo_recipient_idx ON convo.convo (recipient);
* CREATE INDEX convo_thread_id_idx ON convo.convo (thread_id);
* CREATE INDEX convo_send_time_idx ON convo.convo (send_time)

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

I added indices on sender, recipient, and thread_id, because those are the columns we are basing our lookups on. ID is already indexed by the "PRIMARY KEY" syntax. An index on send_time was also added to aid in sorting by that field.

The only other real entity to manage is "threads", but seeing as that is only a grouping of convos with no other data, I chose to implement that as a sequence ID on a convo. When a new convo is created, not in reply to another, we create a new thread_id. Any replies to that convo use the existing thread_id (and subject). If there was more metadata to a thread, I would have created a separate table.

# Alternative DB schema

I also considered a more denormalized approach, where I stored a collection of individual convos, and threads of convos separately. In that approach, I would likely use a NoSQL solution like MongoDB. One "convos" collection could contain every individual convo, indexed by id/sender/recipient like above. Then a separate "threads" collection would contain all the convo data in nested objects representing entire threads. This would make querying for threads very simple and fast. However, updates would be more expensive, and referential integrity would be a problem. Paginating through threads would also be difficult.


package convos.dao;

import com.google.common.collect.ImmutableMap;
import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.CreateConvo;
import convos.domain.SortDirection;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for maniuplating the convo DB in Postgres.
 *
 * Intention is to keep these methods as simple as possible, and handle any extra
 * logic in the Service layer.
 */
@Repository
public class ConvoDao
{
    @Autowired NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<Convo> convoMapper = new RowMapper<Convo>()
    {
        @Override
        public Convo mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            final long id = rs.getLong("id");
            final long sender = rs.getLong("sender");
            final long recipient = rs.getLong("recipient");
            final String subject = rs.getString("subject");
            final String body = rs.getString("body");
            final boolean wasRead = rs.getBoolean("was_read");
            final long threadId = rs.getLong("thread_id");
            final DateTime sendTime = new DateTime(rs.getTimestamp("send_time"));
            final DateTime updateTime = new DateTime(rs.getTimestamp("update_time"));
            Long replyToConvo = rs.getLong("reply_to_convo");
            if (rs.wasNull()) {
                replyToConvo = null;
            }
            return new Convo(id, sender, recipient, subject, body, wasRead, threadId, sendTime, updateTime, replyToConvo);
        }
    };

    /**
     * Get convo from DB based on unique ID, and where sender is given userID
     * @param userId
     * @param convoId
     * @return The convo object
     */
    public Convo getSentConvo(final long userId, final long convoId)
    {
        final String sql =
                "SELECT id, sender, recipient, subject, body, was_read, thread_id, reply_to_convo," +
                        "send_time, update_time " +
                "FROM convo.convo " +
                "WHERE id = :id AND sender = :userId AND NOT deleted_by_sender ";

        final Map<String, Object> params =
                ImmutableMap.of(
                    "id", (Object)convoId,
                    "userId", userId
                );
        return jdbcTemplate.queryForObject(sql, params, convoMapper);
    }

    /**
     * Get convo from DB based on unique ID, and where recipient is given userID
     * @param userId
     * @param convoId
     * @return The convo object
     */
    public Convo getReceivedConvo(final long userId, final long convoId)
    {
        final String sql =
                "SELECT id, sender, recipient, subject, body, was_read, thread_id, reply_to_convo," +
                        "send_time, update_time " +
                        "FROM convo.convo " +
                        "WHERE id = :id AND recipient = :userId AND NOT deleted_by_recipient ";

        final Map<String, Object> params =
                ImmutableMap.of(
                    "id", (Object)convoId,
                    "userId", userId
                );
        return jdbcTemplate.queryForObject(sql, params, convoMapper);
    }

    /**
     * Create a new convo, optionally in reply to another convo, based on ID.
     * @param convo The new convo you want to create
     * @param replyToConvo The convo you are replying to. If null, creates a new convo
     *                     in a new thread. If not null, marks new convo as a reply,
     *                     part of the same thread as the replied-to convo, and uses
     *                     the subject from that convo.
     * @return The new unique ID for the convo.
     */
    public long createConvo(final CreateConvo convo, final Long replyToConvo) {
        final String sql =
                "INSERT INTO convo.convo(sender, recipient, subject, body, thread_id, reply_to_convo) " +
                "VALUES (:sender, :recipient, " +
                        "COALESCE(" +
                        "   (SELECT subject FROM convo.convo where id = :replyToConvo AND NOT deleted), " +
                        "   :subject), " +
                        ":body, " +
                        "COALESCE(" +
                        "   (SELECT thread_id FROM convo.convo where id = :replyToConvo AND NOT deleted), " +
                        "   nextval('thread_seq')), " +
                        "CASE WHEN :replyToConvo = 0 THEN null ELSE :replyToConvo END) " +
                "RETURNING id";

        final Map<String, Object> params =
                ImmutableMap.of(
                    "sender", (Object)convo.getSender(),
                    "recipient", convo.getRecipient(),
                    "subject", convo.getSubject() != null ? convo.getSubject() : "",
                    "body", convo.getBody(),
                    "replyToConvo", replyToConvo != null ? replyToConvo : 0
                );

        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }

    /**
     * Switch convo from read to un-read, whichever one it is not, based on ID.
     * @param id Unique id of the convo
     */
    public void changeConvoReadStatus(final long id) {
        final String sql =
                "UPDATE convo.convo SET was_read = not was_read, update_time = now() " +
                "WHERE id = :id";
        final Map<String, Object> params =
                ImmutableMap.of("id", (Object)id);

        jdbcTemplate.update(sql, params);
    }

    /**
     * Mark the convo with the given ID as deleted.
     * @param userId
     * @param convoId
     */
    public void deleteConvo(final long userId, final long convoId) {
        final String sql =
                "UPDATE convo.convo SET deleted_by_sender = true WHERE id = :convoId AND sender = :userId;" +
                "UPDATE convo.convo SET deleted_by_recipient = true WHERE id = :convoId AND recipient = :userId;";

        final Map<String, Object> params =
                ImmutableMap.of("convoId", (Object)convoId, "userId", userId);

        jdbcTemplate.update(sql, params);
    }

    public int getTotalConvosReceived(final long userId) {
        String sql =
                "SELECT count(*) " +
                "FROM convo.convo " +
                "WHERE recipient = :userId AND NOT deleted_by_recipient ";

        final Map<String, Object> params =
                ImmutableMap.of(
                        "userId", (Object)userId
                );

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    /**
     * Get all convos that have the given userID as a recipient, ordered by send_time ASC/DESC
     * @param userId
     * @param offset
     * @param limit
     * @param direction
     * @return
     */
    public List<Convo> getConvosReceived(final long userId, final int offset, final int limit, final SortDirection direction) {
        String sql =
                "SELECT id, sender, recipient, subject, body, was_read, thread_id, reply_to_convo," +
                        "send_time, update_time " +
                        "FROM convo.convo " +
                        "WHERE recipient = :userId AND NOT deleted_by_recipient ";
        if (limit > 0) {
            sql += "LIMIT :limit ";
        }
        sql += "OFFSET :offset " +
               "ORDER BY send_time " + direction.getVal();

        final Map<String, Object> params =
                ImmutableMap.of(
                        "userId", (Object)userId,
                        "limit", limit,
                        "offset", offset);

        return jdbcTemplate.query(sql, params, convoMapper);
    }

    public int getTotalConvosSent(final long userId) {
        String sql =
                "SELECT count(*) " +
                        "FROM convo.convo " +
                        "WHERE sender = :userId AND NOT deleted_by_sender ";

        final Map<String, Object> params =
                ImmutableMap.of(
                        "userId", (Object)userId
                );

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    /**
     * Get all convos that have the given userID as a sender, ordered by send_time ASC/DESC
     * @param userId
     * @param offset
     * @param limit
     * @param direction
     * @return
     */
    public List<Convo> getConvosSent(final long userId, final int offset, final int limit, final SortDirection direction) {
        String sql =
                "SELECT id, sender, recipient, subject, body, was_read, thread_id, reply_to_convo," +
                        "send_time, update_time " +
                        "FROM convo.convo " +
                        "WHERE sender = :userId AND NOT deleted_by_sender ";
        if (limit > 0) {
            sql += "LIMIT :limit ";
        }
        sql += "OFFSET :offset " +
                "ORDER BY send_time " + direction.getVal();

        final Map<String, Object> params =
                ImmutableMap.of(
                        "userId", (Object)userId,
                        "limit", limit,
                        "offset", offset);

        return jdbcTemplate.query(sql, params, convoMapper);
    }

    public int getTotalThreads(final long userId) {
        String sql =
                "SELECT count(distinct thread_id) " +
                        "FROM convo.convo " +
                        "WHERE (sender = :userId AND NOT deleted_by_sender) OR (recipient = :userId AND NOT deleted_by_recipient) ";

        final Map<String, Object> params =
                ImmutableMap.of(
                        "userId", (Object)userId
                );

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    /**
     * Get all the most recent convos in all threads that have the given userID as a recipient or sender, ordered by send_time ASC/DESC
     * @param userId
     * @param offset
     * @param limit
     * @param direction
     * @return
     */
    public List<Convo> getThreads(final long userId, final int offset, final int limit, final SortDirection direction) {
        String sql =
                "SELECT DISTINCT ON (thread_id) id, sender, recipient, subject, body, was_read, thread_id, reply_to_convo," +
                        "send_time, update_time " +
                        "FROM convo.convo " +
                        "WHERE (sender = :userId AND NOT deleted_by_sender) OR (recipient = :userId AND NOT deleted_by_recipient) ";
        if (limit > 0) {
            sql += "LIMIT :limit ";
        }
        sql += "OFFSET :offset " +
                "ORDER BY send_time " + direction.getVal();

        final Map<String, Object> params =
                ImmutableMap.of(
                        "userId", (Object)userId,
                        "limit", limit,
                        "offset", offset);

        return jdbcTemplate.query(sql, params, convoMapper);
    }

    /**
     * Mark all convos in the given thread as deleted
     * @param userId
     * @param threadId
     */
    public void deleteThread(final long userId, final long threadId) {
        final String sql =
                "UPDATE convo.convo SET deleted_by_sender = true WHERE thread_id = :threadId AND sender = :userId;" +
                "UPDATE convo.convo SET deleted_by_recipient = true WHERE thread_id = :threadId AND recipient = :userId;";

        final Map<String, Object> params =
                ImmutableMap.of("id", (Object)threadId, "userId", userId);

        jdbcTemplate.update(sql, params);
    }

    /**
     * Get all convos in the given thread, ordered by send-time ASC/DESC
     * @param userId
     * @param threadId
     * @param offset
     * @param limit
     * @param direction
     * @return
     */
    public List<Convo> getThread(final long userId, final long threadId, final int offset, final int limit, final SortDirection direction)
    {
        String sql =
                "SELECT id, sender, recipient, subject, body, was_read, thread_id, reply_to_convo," +
                        "send_time, update_time " +
                        "FROM convo.convo " +
                        "WHERE thread_id = :threadId AND ((sender = :userId AND NOT deleted_by_sender) OR (recipient = :userId AND NOT deleted_by_recipient)) ";
        if (limit > 0) {
            sql += "LIMIT :limit ";
        }
        sql += "OFFSET :offset " +
                "ORDER BY send_time " + direction.getVal();

        final Map<String, Object> params =
                ImmutableMap.of(
                        "threadId", (Object)threadId,
                        "limit", limit,
                        "offset", offset,
                        "userId", userId);

        return jdbcTemplate.query(sql, params, convoMapper);
    }

    public int getTotalInThread(final long userId, final long threadId)
    {
        String sql =
                "SELECT count(*) " +
                        "FROM convo.convo " +
                        "WHERE thread_id = :threadId AND ((sender = :userId AND NOT deleted_by_sender) OR (recipient = :userId AND NOT deleted_by_recipient)) ";

        final Map<String, Object> params =
                ImmutableMap.of(
                        "userId", (Object)userId,
                        "threadId", threadId
                );

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }


}

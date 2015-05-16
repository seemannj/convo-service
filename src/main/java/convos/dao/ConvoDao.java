package convos.dao;

import com.google.common.collect.ImmutableMap;
import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.CreateConvo;
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
     * Get convo from DB based on unique ID
     * @param id
     * @return The convo object
     */
    public Convo getConvo(final long id)
    {
        final String sql =
                "SELECT id, sender, recipient, subject, body, was_read, thread_id, reply_to_convo," +
                        "send_time, update_time " +
                "FROM convo.convo " +
                "WHERE id = :id";

        final Map<String, Object> params =
                ImmutableMap.of(
                    "id", (Object)id
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
                        "   (SELECT subject FROM convo.convo where id = :replyToConvo), " +
                        "   :subject), " +
                        ":body, " +
                        "COALESCE(" +
                        "   (SELECT thread_id FROM convo.convo where id = :replyToConvo), " +
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
                "UPDATE convo.convo SET was_read = not was_read " +
                "WHERE id = :id";
        final Map<String, Object> params =
                ImmutableMap.of("id", (Object)id);

        jdbcTemplate.update(sql, params);
    }

    public void deleteConvo(final long id) {

    }

    public ConvosResponse getConvosReceived(final long userId, final int offset, final int limit, final String order) {
        return null;
    }

    public ConvosResponse getConvosSent(final long userId, final int offset, final int limit, final String order) {
        return null;
    }

    public List<List<Convo>> getThreads(final long userId, final int offset, final int limit, final String order) {
        return null;
    }

    public void deleteThread(final long threadId) {

    }

    public List<Convo> getThread(final long threadId)
    {
        return null;
    }
}

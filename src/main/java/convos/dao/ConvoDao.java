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

@Repository
public class ConvoDao
{
    @Autowired NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<Convo> convoMapper = new RowMapper<Convo>()
    {
        @Override
        public Convo mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            long id = rs.getLong("id");
            long sender = rs.getLong("sender");
            long recipient = rs.getLong("recipient");
            String subject = rs.getString("subject");
            String body = rs.getString("body");
            boolean wasRead = rs.getBoolean("was_read");
            long threadId = rs.getLong("thread_id");
            DateTime sendTime = new DateTime(rs.getTimestamp("send_time"));
            DateTime updateTime = new DateTime(rs.getTimestamp("update_time"));
            Long replyToConvo = rs.getLong("reply_to_convo");
            if (rs.wasNull()) {
                replyToConvo = null;
            }
            return new Convo(id, sender, recipient, subject, body, wasRead, threadId, sendTime, updateTime, replyToConvo);
        }
    };

    public Convo getConvo(long id)
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

    public long createConvo(CreateConvo convo, Long replyToConvo) {
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

    public void markConvoAsReady(long id) {

    }

    public void deleteConvo(long id) {

    }

    public ConvosResponse getConvosReceived(long userId, int offset, int limit, String order) {
        return null;
    }

    public ConvosResponse getConvosSent(long userId, int offset, int limit, String order) {
        return null;
    }

    public List<List<Convo>> getThreads(long userId, int offset, int limit, String order) {
        return null;
    }

    public void deleteThread(long threadId) {

    }

    public List<Convo> getThread(long threadId)
    {
        return null;
    }
}

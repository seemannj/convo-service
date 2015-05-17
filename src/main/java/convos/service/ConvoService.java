package convos.service;

import convos.dao.ConvoDao;
import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.CreateConvo;
import convos.domain.SortDirection;
import convos.domain.ThreadsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Marshal data between the API controller and DAOs
 */
@Component
public class ConvoService
{
    @Autowired ConvoDao convoDao;

    /**
     * Get a convo based on its unique ID, and given userId is the sender.
     * @param userId
     * @param convoId
     * @return
     */
    public Convo getSentConvo(final long userId, final long convoId)
    {
        return convoDao.getSentConvo(userId, convoId);
    }

    /**
     * Get a convo based on its unique ID, and given userId is the recipient.
     * @param userId
     * @param convoId
     * @return
     */
    public Convo getReceivedConvo(final long userId, final long convoId)
    {
        return convoDao.getReceivedConvo(userId, convoId);
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
        return convoDao.createConvo(convo, replyToConvo);
    }

    /**
     * Switch convo from read to un-read, whichever one it is not, based on ID.
     * @param id Unique id of the convo
     */
    public void changeConvoReadStatus(final long userId, final long id) {
        convoDao.changeConvoReadStatus(userId, id);
    }

    /**
     * Mark given convo as deleted.
     * @param userId
     * @param convoId
     */
    public void deleteConvo(final long userId, final long convoId) {
        convoDao.deleteConvo(userId, convoId);
    }

    public ConvosResponse getConvosReceived(final long userId, final int offset, final int limit, final SortDirection direction) {
        int total = convoDao.getTotalConvosReceived(userId);
        List<Convo> convos = convoDao.getConvosReceived(userId, offset, limit, direction);
        return new ConvosResponse(total, convos, offset,
                String.format("/api/v1/%d/convos/received?offset=%d&limit=%d&direction=%s", userId, offset + limit, limit, direction.getVal()),
                String.format("/api/v1/%d/convos/received?offset=%d&limit=%d&direction=%s", userId, Math.max(0, offset - limit), limit, direction.getVal()));
    }

    public ConvosResponse getConvosSent(final long userId, final int offset, final int limit, final SortDirection direction) {
        int total = convoDao.getTotalConvosSent(userId);
        List<Convo> convos = convoDao.getConvosSent(userId, offset, limit, direction);
        return new ConvosResponse(total, convos, offset,
                String.format("/api/v1/%d/convos/sent?offset=%d&limit=%d&direction=%s", userId, offset + limit, limit, direction.getVal()),
                String.format("/api/v1/%d/convos/sent?offset=%d&limit=%d&direction=%s", userId, Math.max(0, offset - limit), limit, direction.getVal()));
    }

    public ThreadsResponse getThreads(final long userId, final int offset, final int limit, final SortDirection direction) {
        int total = convoDao.getTotalThreads(userId);
        List<Convo> convos = convoDao.getThreads(userId, offset, limit, direction);
        return new ThreadsResponse(total, convos, offset,
                String.format("/api/v1/%d/threads?offset=%d&limit=%d&direction=%s", userId, offset + limit, limit, direction.getVal()),
                String.format("/api/v1/%d/threads?offset=%d&limit=%d&direction=%s", userId, Math.max(0, offset - limit), limit, direction.getVal()));
    }

    public void deleteThread(final long userId, final long threadId) {
        convoDao.deleteThread(userId, threadId);
    }

    public ConvosResponse getThread(final long userId, final long threadId, final int offset, final int limit, final SortDirection direction)
    {
        int total = convoDao.getTotalInThread(userId, threadId);
        List<Convo> convos = convoDao.getThread(userId, threadId, offset, limit, direction);
        return new ConvosResponse(total, convos, offset,
                String.format("/api/v1/%d/threads/%d?offset=%d&limit=%d&direction=%s", userId, threadId, offset + limit, limit, direction.getVal()),
                String.format("/api/v1/%d/threads/%d?offset=%d&limit=%d&direction=%s", userId, threadId, Math.max(0, offset - limit), limit, direction.getVal()));
    }
}

package convos.service;

import convos.dao.ConvoDao;
import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.CreateConvo;
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
     * Get a convo based on its unique ID.
     * @param id
     * @return
     */
    public Convo getConvo(final long id)
    {
        return convoDao.getConvo(id);
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
    public void changeConvoReadStatus(final long id) {
        convoDao.changeConvoReadStatus(id);
    }

    /**
     * Mark given convo as deleted.
     * @param id
     */
    public void deleteConvo(final long id) {
        convoDao.deleteConvo(id);
    }

    public ConvosResponse getConvosReceived(final long userId, final int offset, final int limit, final String order) {
        return convoDao.getConvosReceived(userId, offset, limit, order);
    }

    public ConvosResponse getConvosSent(final long userId, final int offset, final int limit, final String order) {
        return convoDao.getConvosSent(userId, offset, limit, order);
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

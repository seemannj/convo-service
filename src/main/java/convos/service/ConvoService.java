package convos.service;

import convos.dao.ConvoDao;
import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.CreateConvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConvoService
{
    @Autowired ConvoDao convoDao;

    public Convo getConvo(long id)
    {
        return convoDao.getConvo(id);
    }

    public long createConvo(CreateConvo convo, Long replyToConvo) {
        return convoDao.createConvo(convo, replyToConvo);
    }

    public void markConvoAsReady(long id) {
        convoDao.markConvoAsReady(id);
    }

    public void deleteConvo(long id) {
        convoDao.deleteConvo(id);
    }

    public ConvosResponse getConvosReceived(long userId, int offset, int limit, String order) {
        return convoDao.getConvosReceived(userId, offset, limit, order);
    }

    public ConvosResponse getConvosSent(long userId, int offset, int limit, String order) {
        return convoDao.getConvosSent(userId, offset, limit, order);
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

package convos.domain;

import org.joda.time.DateTime;

public class Convo
{
    private final long id;
    private final long sender;
    private final long recipient;
    private final String subject;
    private final String body;
    private final boolean wasRead;
    private final long threadId;
    private final DateTime sendTime;
    private final DateTime updateTime;
    private final Long replyToConvo;

    public Convo(long id, long sender, long recipient, String subject, String body, boolean wasRead, long threadId, DateTime sendTime, DateTime updateTime, Long replyToConvo)
    {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.wasRead = wasRead;
        this.threadId = threadId;
        this.sendTime = sendTime;
        this.updateTime = updateTime;
        this.replyToConvo = replyToConvo;
    }

    public long getId()
    {
        return id;
    }

    public long getSender()
    {
        return sender;
    }

    public long getRecipient()
    {
        return recipient;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getBody()
    {
        return body;
    }

    public boolean isWasRead()
    {
        return wasRead;
    }

    public long getThreadId()
    {
        return threadId;
    }

    public DateTime getSendTime()
    {
        return sendTime;
    }

    public DateTime getUpdateTime()
    {
        return updateTime;
    }

    public Long getReplyToConvo()
    {
        return replyToConvo;
    }


}

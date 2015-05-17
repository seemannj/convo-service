package convos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateConvo
{
    private final long sender;
    private final long recipient;
    private final String subject;
    private final String body;

    private static final int MAX_SUBJECT_LENGTH = 140;
    private static final int MAX_BODY_LENGTH = 64000;

    @JsonCreator
    public CreateConvo(
            @JsonProperty("sender") long sender,
            @JsonProperty("recipient") long recipient,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body)
    {
        this.sender = sender;
        this.recipient = recipient;
        if (subject.length() > MAX_SUBJECT_LENGTH) {
            throw new IllegalArgumentException(String.format("Subject can be at most %d characters long.", MAX_SUBJECT_LENGTH));
        }
        this.subject = subject;
        if (body.length() > MAX_BODY_LENGTH) {
            throw new IllegalArgumentException(String.format("Body can be at most %d characters long.", MAX_BODY_LENGTH));
        }
        this.body = body;
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
}

package convos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateConvo
{
    private final long sender;
    private final long recipient;
    private final String subject;
    private final String body;

    @JsonCreator
    public CreateConvo(
            @JsonProperty("sender") long sender,
            @JsonProperty("recipient") long recipient,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
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

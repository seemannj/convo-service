package convos.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CreateConvoTests
{
    @Test
    public void constructor_givenSubjectAndBodyUnderLimits_initsProperties()
    {
        String subject = makeString(140);
        String body = makeString(20000);
        CreateConvo create = new CreateConvo(1, 2, subject, body);
        assertEquals(1, create.getSender());
        assertEquals(2, create.getRecipient());
        assertEquals(subject, create.getSubject());
        assertEquals(body, create.getBody());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_givenSubjectOverLimitLength_throwsException()
    {
        String subject = makeString(141);
        String body = makeString(20000);
        CreateConvo create = new CreateConvo(1, 2, subject, body);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_givenBodyOverLimitLength_throwsException()
    {
        String subject = makeString(140);
        String body = makeString(64001);
        CreateConvo create = new CreateConvo(1, 2, subject, body);
    }

    private String makeString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}

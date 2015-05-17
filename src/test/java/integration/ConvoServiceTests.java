package integration;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.http.ContentType;
import convos.domain.CreateConvo;
import org.junit.Test;

import java.util.Random;


/**
 * Ensure service is running locally before running tests.
 */
public class ConvoServiceTests
{
    private final String SERVICE_HOST = "http://localhost:8080";
    private final String SERVICE_ROOT = SERVICE_HOST + "/api/v1/";

    private final Random random = new Random();

    private final String SUBJECT = "TEST SUBJECT";
    private final String LONG_SUBJECT = "RY3LNxWBCDZrKesfN5vJ9MVjqBJ8nY1DOfFqCCxcWgVk1r0GiYtQNchgYJnkFEeEGZ8ti6hs5jctCQFwBL5b2Fx22et6hMz9KLNp3CUkI6TM6xKx1xpQlpy5yJybtRXfuCEUgNt2ot9XL";
    private final String BODY = "SOME TEST BODY BODY BODY BODY BODY BODY BODY BODY BODY BODY BODY ";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createConvo_addsToReceviedAndSent() throws JsonProcessingException
    {
        long sendingUser = Math.abs(random.nextLong());
        long receivingUser = Math.abs(random.nextLong());
        Long convoId = createConvo(sendingUser, receivingUser);

        given()
            .contentType(ContentType.JSON)
        .when()
                .get(SERVICE_ROOT + receivingUser + "/convos/received")
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(1))
            .body("convos.id[0]", equalTo(convoId.intValue()))
            .body("convos.subject[0]", equalTo(SUBJECT))
            .body("convos.body[0]", equalTo(BODY));

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + receivingUser + "/convos/received/" + convoId)
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("id", equalTo(convoId.intValue()))
            .body("subject", equalTo(SUBJECT))
            .body("body", equalTo(BODY));

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + sendingUser + "/convos/sent")
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(1))
            .body("convos.id[0]", equalTo(convoId.intValue()))
            .body("convos.subject[0]", equalTo(SUBJECT))
            .body("convos.body[0]", equalTo(BODY));

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + sendingUser + "/convos/sent/" + convoId)
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("id", equalTo(convoId.intValue()))
            .body("subject", equalTo(SUBJECT))
            .body("body", equalTo(BODY));
    }

    @Test
    public void createConvo_givenLongSubject_returns400() throws JsonProcessingException
    {
        given()
            .contentType(ContentType.JSON)
            .body("{\"sender\":1,\"recipient\":2,\"body\":\"not relevant\",\"subject\":" + LONG_SUBJECT + "}")
        .when()
            .post(SERVICE_ROOT + "convos")
        .then()
            .statusCode(400);
    }

    @Test
    public void changeConvoReadStatus_setsReadToTrue() throws JsonProcessingException
    {
        long sendingUser = Math.abs(random.nextLong());
        long receivingUser = Math.abs(random.nextLong());
        Long convoId = createConvo(sendingUser, receivingUser);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + receivingUser + "/convos/received/" + convoId)
        .then()
            .body("wasRead", equalTo(false));

        when()
            .put(SERVICE_ROOT + receivingUser + "/convos/received/" + convoId)
        .then()
            .statusCode(200);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + receivingUser + "/convos/received/" + convoId)
        .then()
            .body("wasRead", equalTo(true));
    }

    @Test
    public void deleteConvo_removeFromReceivedFeed() throws JsonProcessingException
    {
        long sendingUser = Math.abs(random.nextLong());
        long receivingUser = Math.abs(random.nextLong());
        Long convoId = createConvo(sendingUser, receivingUser);

        when()
            .delete(SERVICE_ROOT + receivingUser + "/convos/" + convoId)
        .then()
            .statusCode(200);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + receivingUser + "/convos/received")
        .then()
            .statusCode(200)
            .body("total", equalTo(0));

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + receivingUser + "/convos/received/" + convoId)
        .then()
            .statusCode(404);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + sendingUser + "/convos/sent")
        .then()
            .statusCode(200)
            .body("total", equalTo(1));
    }

    @Test
    public void deleteConvo_removeFromSentFeed() throws JsonProcessingException
    {
        long sendingUser = Math.abs(random.nextLong());
        long receivingUser = Math.abs(random.nextLong());
        Long convoId = createConvo(sendingUser, receivingUser);

        when()
            .delete(SERVICE_ROOT + sendingUser + "/convos/" + convoId)
        .then()
            .statusCode(200);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + sendingUser + "/convos/sent")
        .then()
            .statusCode(200)
            .body("total", equalTo(0));

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + sendingUser + "/convos/sent/" + convoId)
        .then()
            .statusCode(404);

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + receivingUser + "/convos/received")
        .then()
            .statusCode(200)
            .body("total", equalTo(1));
    }

    @Test
    public void replyToConvo_addsToThread() throws JsonProcessingException
    {
        long user1 = Math.abs(random.nextLong());
        long user2 = Math.abs(random.nextLong());
        Long convoId = createConvo(user1, user2);

        CreateConvo replyConvo = new CreateConvo(user2, user1, "SOME OTHER SUBJECT", BODY);
        Long replyConvoId =
            given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(replyConvo))
            .when()
                .post(SERVICE_ROOT + "convos/" + convoId + "/replies")
            .then()
                .statusCode(200)
                .extract().body().as(Long.class);

        Long threadId =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(SERVICE_ROOT + user2 + "/convos/sent/" + replyConvoId)
            .then()
                .statusCode(200)
                .body("replyToConvo", equalTo(convoId.intValue()))
                .body("subject", equalTo(SUBJECT))
                .extract().body().jsonPath().getLong("threadId");

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_ROOT + user2 + "/threads/" + threadId)
        .then()
            .statusCode(200)
            .body("total", equalTo(2))
            .body("convos.id[0]", equalTo(replyConvoId.intValue()))
            .body("convos.id[1]", equalTo(convoId.intValue()));
    }

    @Test
    public void getConvosReceived_pagination() throws JsonProcessingException
    {
        long sendingUser = Math.abs(random.nextLong());
        long receivingUser = Math.abs(random.nextLong());
        Long convoId1 = createConvo(sendingUser, receivingUser);
        Long convoId2 = createConvo(sendingUser, receivingUser);
        Long convoId3 = createConvo(sendingUser, receivingUser);

        String nextUrl =
            given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 2)
                .queryParam("offset", 0)
            .when()
                .get(SERVICE_ROOT + receivingUser + "/convos/received")
            .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("total", equalTo(3))
                .body("convos.size()", equalTo(2))
                .body("convos.id[0]", equalTo(convoId3.intValue()))
                .body("convos.id[1]", equalTo(convoId2.intValue()))
                .extract().body().jsonPath().getString("next");

        String prevUrl =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(SERVICE_HOST + nextUrl)
            .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("total", equalTo(3))
                .body("convos.size()", equalTo(1))
                .body("convos.id[0]", equalTo(convoId1.intValue()))
                .extract().body().jsonPath().getString("previous");

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_HOST + prevUrl)
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(3))
            .body("convos.size()", equalTo(2))
            .body("convos.id[0]", equalTo(convoId3.intValue()))
            .body("convos.id[1]", equalTo(convoId2.intValue()));

        given()
            .contentType(ContentType.JSON)
            .queryParam("limit", 2)
            .queryParam("offset", 0)
            .queryParam("direction", "asc")
        .when()
            .get(SERVICE_ROOT + receivingUser + "/convos/received")
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(3))
            .body("convos.size()", equalTo(2))
            .body("convos.id[0]", equalTo(convoId1.intValue()))
            .body("convos.id[1]", equalTo(convoId2.intValue()));
    }

    @Test
    public void getConvosSent_pagination() throws JsonProcessingException
    {
        long sendingUser = Math.abs(random.nextLong());
        long receivingUser = Math.abs(random.nextLong());
        Long convoId1 = createConvo(sendingUser, receivingUser);
        Long convoId2 = createConvo(sendingUser, receivingUser);
        Long convoId3 = createConvo(sendingUser, receivingUser);

        String nextUrl =
            given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 2)
                .queryParam("offset", 0)
            .when()
                .get(SERVICE_ROOT + sendingUser + "/convos/sent")
            .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("total", equalTo(3))
                .body("convos.size()", equalTo(2))
                .body("convos.id[0]", equalTo(convoId3.intValue()))
                .body("convos.id[1]", equalTo(convoId2.intValue()))
                .extract().body().jsonPath().getString("next");

        String prevUrl =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(SERVICE_HOST + nextUrl)
            .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("total", equalTo(3))
                .body("convos.size()", equalTo(1))
                .body("convos.id[0]", equalTo(convoId1.intValue()))
                .extract().body().jsonPath().getString("previous");

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_HOST + prevUrl)
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(3))
            .body("convos.size()", equalTo(2))
            .body("convos.id[0]", equalTo(convoId3.intValue()))
            .body("convos.id[1]", equalTo(convoId2.intValue()));

        given()
            .contentType(ContentType.JSON)
            .queryParam("limit", 2)
            .queryParam("offset", 0)
            .queryParam("direction", "asc")
        .when()
            .get(SERVICE_ROOT + sendingUser + "/convos/sent")
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(3))
            .body("convos.size()", equalTo(2))
            .body("convos.id[0]", equalTo(convoId1.intValue()))
            .body("convos.id[1]", equalTo(convoId2.intValue()));
    }

    @Test
    public void getThread_pagination() throws JsonProcessingException
    {
        long user1 = Math.abs(random.nextLong());
        long user2 = Math.abs(random.nextLong());
        Long convoId1 = createConvo(user1, user2);
        Long convoId2 = replyToConvo(user2, user1, convoId1);
        Long convoId3 = replyToConvo(user1, user2, convoId2);

        Long threadId =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(SERVICE_ROOT + user2 + "/convos/sent/" + convoId2)
            .then()
                .statusCode(200)
                .extract().body().jsonPath().getLong("threadId");

        String nextUrl =
            given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 2)
                .queryParam("offset", 0)
            .when()
                .get(SERVICE_ROOT + user1 + "/threads/" + threadId)
            .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("total", equalTo(3))
                .body("convos.size()", equalTo(2))
                .body("convos.id[0]", equalTo(convoId3.intValue()))
                .body("convos.id[1]", equalTo(convoId2.intValue()))
                .extract().body().jsonPath().getString("next");

        String prevUrl =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(SERVICE_HOST + nextUrl)
            .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("total", equalTo(3))
                .body("convos.size()", equalTo(1))
                .body("convos.id[0]", equalTo(convoId1.intValue()))
                .extract().body().jsonPath().getString("previous");

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(SERVICE_HOST + prevUrl)
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(3))
            .body("convos.size()", equalTo(2))
            .body("convos.id[0]", equalTo(convoId3.intValue()))
            .body("convos.id[1]", equalTo(convoId2.intValue()));

        given()
            .contentType(ContentType.JSON)
            .queryParam("limit", 2)
            .queryParam("offset", 0)
            .queryParam("direction", "asc")
        .when()
            .get(SERVICE_ROOT + user1 + "/threads/" + threadId)
        .then()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("total", equalTo(3))
            .body("convos.size()", equalTo(2))
            .body("convos.id[0]", equalTo(convoId1.intValue()))
            .body("convos.id[1]", equalTo(convoId2.intValue()));
    }

    private long createConvo(long sendingUser, long receivingUser) throws JsonProcessingException
    {
        CreateConvo convo = new CreateConvo(sendingUser, receivingUser, SUBJECT, BODY);

        Long convoId =
                given()
                    .contentType(ContentType.JSON)
                    .body(mapper.writeValueAsString(convo))
                .when()
                    .post(SERVICE_ROOT + "convos")
                .then()
                    .contentType(ContentType.JSON)
                    .statusCode(200)
                    .extract().body().as(Long.class);
        return convoId;
    }

    private long replyToConvo(long sendingUser, long receivingUser, long replyToConvo) throws JsonProcessingException
    {
        CreateConvo replyConvo = new CreateConvo(sendingUser, receivingUser, "SOME OTHER SUBJECT", BODY);
        Long replyConvoId =
            given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(replyConvo))
            .when()
                .post(SERVICE_ROOT + "convos/" + replyToConvo + "/replies")
            .then()
                .statusCode(200)
                .extract().body().as(Long.class);
        return replyConvoId;
    }
}

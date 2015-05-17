package convos.controller;

import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.CreateConvo;
import convos.domain.SortDirection;
import convos.domain.ThreadsResponse;
import convos.service.ConvoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller
{
    @Autowired ConvoService convoService;

    @RequestMapping(value = "/api/v1/{userId}/convos", method = RequestMethod.POST)
    public long createConvo(@PathVariable final long userId, @RequestBody final CreateConvo convo) {
        return convoService.createConvo(convo, null);
    }

    @RequestMapping(value = "/api/v1/{userId}/convos/{convoId}/replies", method = RequestMethod.POST)
    public long replyToConvo(@PathVariable final long userId, @PathVariable final long convoId, @RequestBody final CreateConvo convo) {
        return convoService.createConvo(convo, convoId);
    }

    @RequestMapping(value = "/api/v1/{userId}/convos/sent/{convoId}", method = RequestMethod.GET)
    public Convo getSentConvo(@PathVariable final long userId, @PathVariable final long convoId) {
        return convoService.getSentConvo(userId, convoId);
    }

    @RequestMapping(value = "/api/v1/{userId}/convos/received/{convoId}", method = RequestMethod.GET)
    public Convo getReceivedConvo(@PathVariable final long userId, @PathVariable final long convoId) {
        return convoService.getReceivedConvo(userId, convoId);
    }

    @RequestMapping(value = "/api/v1/{userId}/convos/{convoId}", method = RequestMethod.DELETE)
    public void deleteConvo(@PathVariable final long userId, @PathVariable final long convoId) {
        convoService.deleteConvo(userId, convoId);
    }

    @RequestMapping(value = "/api/v1/{userId}/convos/{convoId}", method = RequestMethod.PUT)
    public void changeConvoReadStatus(@PathVariable final long userId, @PathVariable final long convoId) {
        convoService.changeConvoReadStatus(convoId);
    }

    @RequestMapping(value = "/api/v1/{userId}/convos/sent", method = RequestMethod.GET)
    public ConvosResponse getConvosSent(@PathVariable final long userId, @RequestParam final int offset,
                                        @RequestParam final int limit, @RequestParam final SortDirection direction) {
        return convoService.getConvosSent(userId, offset, limit, direction);
    }

    @RequestMapping(value = "/api/v1/{userId}/convos/received", method = RequestMethod.GET)
    public ConvosResponse getConvosReceived(@PathVariable final long userId, @RequestParam final int offset,
                                        @RequestParam final int limit, @RequestParam final SortDirection direction) {
        return convoService.getConvosReceived(userId, offset, limit, direction);
    }

    @RequestMapping(value = "/api/v1/{userId}/threads", method = RequestMethod.GET)
    public ThreadsResponse getThreads(@PathVariable final long userId, @RequestParam final int offset,
                                      @RequestParam final int limit, @RequestParam final SortDirection direction) {
        return convoService.getThreads(userId, offset, limit, direction);
    }

    @RequestMapping(value = "/api/v1/{userId}/threads/{threadId}", method = RequestMethod.GET)
    public ConvosResponse getThread(@PathVariable final long userId, @PathVariable final long threadId, @RequestParam final int offset,
                                    @RequestParam final int limit, @RequestParam final SortDirection direction) {
        return convoService.getThread(userId, threadId, offset, limit, direction);
    }

    @RequestMapping(value = "/api/v1/{userId}/threads/{threadId}", method = RequestMethod.DELETE)
    public void deleteThread(@PathVariable final long userId, @PathVariable final long threadId) {
        convoService.deleteThread(userId, threadId);
    }
}
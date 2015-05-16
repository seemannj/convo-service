package convos.controller;

import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.CreateConvo;
import convos.service.ConvoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller
{
    @Autowired ConvoService convoService;

    @RequestMapping(value = "/api/v1/convos", method = RequestMethod.POST)
    public long createConvo(@RequestBody CreateConvo convo) {
        return convoService.createConvo(convo, null);
    }

    @RequestMapping(value = "/api/v1/convos/{id}/replies", method = RequestMethod.POST)
    public long replyToConvo(@PathVariable long id, @RequestBody CreateConvo convo) {
        return convoService.createConvo(convo, id);
    }

    @RequestMapping(value = "/api/v1/convos/{id}", method = RequestMethod.GET)
    public Convo getConvo(@PathVariable long id) {
        return convoService.getConvo(id);
    }

    @RequestMapping(value = "/api/v1/convos/{id}", method = RequestMethod.DELETE)
    public void deleteConvo(@PathVariable long id) {
        convoService.deleteConvo(id);
    }

    @RequestMapping(value = "/api/v1/convos/{id}", method = RequestMethod.PUT)
    public void markConvoAsRead(@PathVariable long id) {
        convoService.markConvoAsReady(id);
    }
}
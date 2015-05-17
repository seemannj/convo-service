package convos.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import convos.dao.ConvoDao;
import convos.domain.Convo;
import convos.domain.ConvosResponse;
import convos.domain.SortDirection;
import convos.domain.ThreadsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ConvoServiceTests
{
    @InjectMocks ConvoService underTest;

    @Mock ConvoDao convoDao;
    @Mock List<Convo> convos;


    private final long USER_ID = 24L;
    private final int OFFSET = 110;
    private final int LIMIT = 100;
    private final int TOTAL = 200;
    private final long THREAD_ID = 29L;

    @Test
    public void getConvosReceived_buildsResponseFromTotalOffsetAndPaths()
    {
        when(convoDao.getTotalConvosReceived(USER_ID)).thenReturn(TOTAL);
        when(convoDao.getConvosReceived(USER_ID, OFFSET, LIMIT, SortDirection.ASCENDING))
            .thenReturn(convos);
        ConvosResponse response = underTest.getConvosReceived(USER_ID, OFFSET, LIMIT, SortDirection.ASCENDING);
        assertEquals(TOTAL, response.getTotal());
        assertEquals(convos, response.getConvos());
        assertEquals(OFFSET, response.getOffset());
        assertEquals("/api/v1/"+USER_ID+"/convos/received?offset="+(OFFSET+LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getNext());
        assertEquals("/api/v1/"+USER_ID+"/convos/received?offset="+(OFFSET-LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getPrevious());
    }

    @Test
    public void getConvosSent_buildsResponseFromTotalOffsetAndPaths()
    {
        when(convoDao.getTotalConvosSent(USER_ID)).thenReturn(TOTAL);
        when(convoDao.getConvosSent(USER_ID, OFFSET, LIMIT, SortDirection.ASCENDING))
                .thenReturn(convos);
        ConvosResponse response = underTest.getConvosSent(USER_ID, OFFSET, LIMIT, SortDirection.ASCENDING);
        assertEquals(TOTAL, response.getTotal());
        assertEquals(convos, response.getConvos());
        assertEquals(OFFSET, response.getOffset());
        assertEquals("/api/v1/"+USER_ID+"/convos/sent?offset="+(OFFSET+LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getNext());
        assertEquals("/api/v1/"+USER_ID+"/convos/sent?offset="+(OFFSET-LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getPrevious());
    }

    @Test
    public void getThreads_buildsResponseFromTotalOffsetAndPaths()
    {
        when(convoDao.getTotalThreads(USER_ID)).thenReturn(TOTAL);
        when(convoDao.getThreads(USER_ID, OFFSET, LIMIT, SortDirection.ASCENDING))
                .thenReturn(convos);
        ThreadsResponse response = underTest.getThreads(USER_ID, OFFSET, LIMIT, SortDirection.ASCENDING);
        assertEquals(TOTAL, response.getTotal());
        assertEquals(convos, response.getConvos());
        assertEquals(OFFSET, response.getOffset());
        assertEquals("/api/v1/"+USER_ID+"/threads?offset="+(OFFSET+LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getNext());
        assertEquals("/api/v1/"+USER_ID+"/threads?offset="+(OFFSET-LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getPrevious());
    }

    @Test
    public void getThread_buildsResponseFromTotalOffsetAndPaths()
    {
        when(convoDao.getTotalInThread(USER_ID, THREAD_ID)).thenReturn(TOTAL);
        when(convoDao.getThread(USER_ID, THREAD_ID, OFFSET, LIMIT, SortDirection.ASCENDING))
                .thenReturn(convos);
        ConvosResponse response = underTest.getThread(USER_ID, THREAD_ID, OFFSET, LIMIT, SortDirection.ASCENDING);
        assertEquals(TOTAL, response.getTotal());
        assertEquals(convos, response.getConvos());
        assertEquals(OFFSET, response.getOffset());
        assertEquals("/api/v1/"+USER_ID+"/threads/"+THREAD_ID+"?offset="+(OFFSET+LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getNext());
        assertEquals("/api/v1/"+USER_ID+"/threads/"+THREAD_ID+"?offset="+(OFFSET-LIMIT)+"&limit="+LIMIT+"&direction=asc", response.getPrevious());
    }
}

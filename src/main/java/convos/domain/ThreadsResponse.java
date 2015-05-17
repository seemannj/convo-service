package convos.domain;

import java.util.List;

public class ThreadsResponse
{
    private final int total;
    private final List<Convo> convos;
    private final int offset;
    private final String next;
    private final String previous;

    public ThreadsResponse(int total, List<Convo> convos, int offset, String next, String previous)
    {
        this.total = total;
        this.convos = convos;
        this.offset = offset;
        this.next = next;
        this.previous = previous;
    }

    public int getTotal()
    {
        return total;
    }

    public List<Convo> getConvos()
    {
        return convos;
    }

    public int getOffset()
    {
        return offset;
    }

    public String getNext()
    {
        return next;
    }

    public String getPrevious()
    {
        return previous;
    }
}

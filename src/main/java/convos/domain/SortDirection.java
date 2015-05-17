package convos.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SortDirection
{
    ASCENDING("asc"),
    DESCENDING("desc");

    private final String val;

    SortDirection(String val)
    {
        this.val = val;
    }

    @JsonValue
    public String getVal()
    {
        return val;
    }
}

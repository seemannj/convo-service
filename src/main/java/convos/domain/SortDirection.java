package convos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static SortDirection fromJson(String val) {
        for (SortDirection d : SortDirection.values()) {
            if (d.getVal().equals(val)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid sort direction.");
    }
}

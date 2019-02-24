package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventResponse extends AbstractResponse {

    /*@SerializedName("subject")
    private boolean subject;

    @SerializedName("startDate")
    private boolean startDate;

    @SerializedName("endDate")
    private boolean endDate;*/

    @SerializedName("response")
    private List<Event> response = new ArrayList<>();

    public List<Event> getResponse() {
        return response;
    }

    public void setResponse(List<Event> response) {
        this.response = response;
    }
}

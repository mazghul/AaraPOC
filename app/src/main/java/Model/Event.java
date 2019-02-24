package Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Event {

    private String startDate;
    private String endDate;
    private String toEmailId;
    private String subject;
    private String attachment;

    public Event() {
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getToEmailId() {
        return toEmailId;
    }

    public void setToEmailId(String toEmailId) {
        this.toEmailId = toEmailId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) { this.subject = subject; }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) { this.attachment = attachment; }
}

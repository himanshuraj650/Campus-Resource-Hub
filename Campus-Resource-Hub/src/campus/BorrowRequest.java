package campus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class BorrowRequest implements Serializable {
    public enum Status { PENDING, APPROVED, DECLINED, RETURNED }

    private long id;
    private final long resourceId;
    private final String requesterUsername;
    private final String message;
    private final int days;
    private Status status;
    private final LocalDateTime createdAt;
    private LocalDateTime decidedAt;

    public BorrowRequest(long resourceId, String requesterUsername, String message, int days) {
        this.id = -1;
        this.resourceId = resourceId;
        this.requesterUsername = requesterUsername;
        this.message = message;
        this.days = days;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public BorrowRequest(long id, long resourceId, String requesterUsername, String message, int days, Status status, LocalDateTime createdAt, LocalDateTime decidedAt) {
        this.id = id;
        this.resourceId = resourceId;
        this.requesterUsername = requesterUsername;
        this.message = message;
        this.days = days;
        this.status = status;
        this.createdAt = createdAt;
        this.decidedAt = decidedAt;
    }

    public void setId(long id) { this.id = id; }

    public long getId() { return id; }
    public long getResourceId() { return resourceId; }
    public String getRequesterUsername() { return requesterUsername; }
    public String getMessage() { return message; }
    public int getDays() { return days; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
}

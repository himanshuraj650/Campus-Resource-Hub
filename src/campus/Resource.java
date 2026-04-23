package campus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Resource implements Serializable {
    public enum Category { BOOKS, NOTES, PROJECT_HARDWARE, LAB_TOOLS, SKILL_SHARE, STUDY_GROUP, OTHER }
    public enum Status { AVAILABLE, BORROWED, UNAVAILABLE }

    private long id;
    private String title;
    private String description;
    private Category category;
    private final String ownerUsername;
    private Status status;
    private String borrowerUsername;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueAt;
    private final LocalDateTime createdAt;
    private String location;
    private String courseCode;

    public Resource(String title, String description, Category category, String ownerUsername, String location) {
        this.id = -1;
        this.title = title;
        this.description = description;
        this.category = category;
        this.ownerUsername = ownerUsername;
        this.location = location;
        this.status = Status.AVAILABLE;
        this.createdAt = LocalDateTime.now();
        this.courseCode = "GEN-101";
    }

    public Resource(long id, String title, String description, Category category, String ownerUsername, Status status, String borrowerUsername, LocalDateTime borrowedAt, LocalDateTime dueAt, LocalDateTime createdAt, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.ownerUsername = ownerUsername;
        this.status = status;
        this.borrowerUsername = borrowerUsername;
        this.borrowedAt = borrowedAt;
        this.dueAt = dueAt;
        this.createdAt = createdAt;
        this.location = location;
    }

    public void setId(long id) { this.id = id; }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getOwnerUsername() { return ownerUsername; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getBorrowerUsername() { return borrowerUsername; }
    public void setBorrowerUsername(String borrowerUsername) { this.borrowerUsername = borrowerUsername; }
    public LocalDateTime getBorrowedAt() { return borrowedAt; }
    public void setBorrowedAt(LocalDateTime borrowedAt) { this.borrowedAt = borrowedAt; }
    public LocalDateTime getDueAt() { return dueAt; }
    public void setDueAt(LocalDateTime dueAt) { this.dueAt = dueAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
}

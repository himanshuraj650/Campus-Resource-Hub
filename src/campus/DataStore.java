package campus;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataStore {
    private final String dbUrl;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public DataStore(String dirPath) {
        new File(dirPath).mkdirs();
        this.dbUrl = "jdbc:sqlite:" + dirPath + "/campus.db";
    }

    public void load() {
        try { Class.forName("org.sqlite.JDBC"); } catch (Exception e) {}
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT, fullName TEXT, campusId TEXT, campusName TEXT DEFAULT 'Cgc University')");
            stmt.execute("CREATE TABLE IF NOT EXISTS resources (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, category TEXT, ownerUsername TEXT, status TEXT, borrowerUsername TEXT, borrowedAt TEXT, dueAt TEXT, createdAt TEXT, location TEXT, courseCode TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS requests (id INTEGER PRIMARY KEY AUTOINCREMENT, resourceId INTEGER, requesterUsername TEXT, message TEXT, days INTEGER, status TEXT, createdAt TEXT, decidedAt TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, title TEXT, message TEXT, type TEXT, isRead INTEGER DEFAULT 0, createdAt TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (id INTEGER PRIMARY KEY AUTOINCREMENT, fromUser TEXT, toUser TEXT, content TEXT, timestamp TEXT)");
        } catch (SQLException e) {}
    }

    private Connection getConnection() throws SQLException { return DriverManager.getConnection(dbUrl); }

    public boolean registerUser(User u) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users(username, password, fullName, campusId, campusName) VALUES(?,?,?,?,?)")) {
            pstmt.setString(1, u.getUsername().toLowerCase());
            pstmt.setString(2, u.getPassword());
            pstmt.setString(3, u.getFullName());
            pstmt.setString(4, u.getCampusId());
            pstmt.setString(5, u.getCampusName());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public User authenticate(String u, String p) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            pstmt.setString(1, u.toLowerCase()); pstmt.setString(2, p);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {}
        return null;
    }

    public User getUser(String un) {
        if (un == null) return null;
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            pstmt.setString(1, un.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {}
        return null;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User(rs.getString("username"), rs.getString("password"), rs.getString("fullName"), rs.getString("campusId"));
        u.setCampusName(rs.getString("campusName"));
        return u;
    }

    public void addResource(Resource r) {
        if (r.getId() == -1) {
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO resources(title, description, category, ownerUsername, status, borrowerUsername, borrowedAt, dueAt, createdAt, location, courseCode) VALUES(?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, r.getTitle()); pstmt.setString(2, r.getDescription()); pstmt.setString(3, r.getCategory().name());
                pstmt.setString(4, r.getOwnerUsername()); pstmt.setString(5, r.getStatus().name()); pstmt.setString(6, r.getBorrowerUsername());
                pstmt.setString(7, r.getBorrowedAt() != null ? r.getBorrowedAt().format(formatter) : null);
                pstmt.setString(8, r.getDueAt() != null ? r.getDueAt().format(formatter) : null);
                pstmt.setString(9, r.getCreatedAt().format(formatter)); pstmt.setString(10, r.getLocation()); pstmt.setString(11, r.getCourseCode());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) r.setId(rs.getLong(1));
            } catch (SQLException e) {}
        } else {
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("UPDATE resources SET status=?, borrowerUsername=?, borrowedAt=?, dueAt=? WHERE id=?")) {
                pstmt.setString(1, r.getStatus().name()); pstmt.setString(2, r.getBorrowerUsername());
                pstmt.setString(3, r.getBorrowedAt() != null ? r.getBorrowedAt().format(formatter) : null);
                pstmt.setString(4, r.getDueAt() != null ? r.getDueAt().format(formatter) : null);
                pstmt.setLong(5, r.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {}
        }
    }

    public List<Resource> listResources() { return fetchResources("SELECT * FROM resources ORDER BY id DESC"); }
    public List<Resource> listResourcesByOwner(String u) { return fetchResources("SELECT * FROM resources WHERE ownerUsername = '" + u + "'"); }
    public List<Resource> listResourcesBorrowedBy(String u) { return fetchResources("SELECT * FROM resources WHERE borrowerUsername = '" + u + "' AND status = 'BORROWED'"); }

    private List<Resource> fetchResources(String sql) {
        List<Resource> list = new ArrayList<>();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResource(rs));
        } catch (SQLException e) {}
        return list;
    }

    private Resource mapResource(ResultSet rs) throws SQLException {
        Resource r = new Resource(rs.getLong("id"), rs.getString("title"), rs.getString("description"), Resource.Category.valueOf(rs.getString("category")), rs.getString("ownerUsername"), Resource.Status.valueOf(rs.getString("status")), rs.getString("borrowerUsername"), rs.getString("borrowedAt") != null ? LocalDateTime.parse(rs.getString("borrowedAt"), formatter) : null, rs.getString("dueAt") != null ? LocalDateTime.parse(rs.getString("dueAt"), formatter) : null, LocalDateTime.parse(rs.getString("createdAt"), formatter), rs.getString("location"));
        r.setCourseCode(rs.getString("courseCode"));
        return r;
    }

    public void sendMessage(String from, String to, String content) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO messages(fromUser, toUser, content, timestamp) VALUES(?,?,?,?)")) {
            pstmt.setString(1, from); pstmt.setString(2, to); pstmt.setString(3, content); pstmt.setString(4, LocalDateTime.now().format(formatter));
            pstmt.executeUpdate();
            addNotification(to, "New Message", "Incoming from " + from, "CHAT");
        } catch (SQLException e) {}
    }

    public List<ChatMessage> getMessages(String u1, String u2) {
        List<ChatMessage> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE (fromUser = ? AND toUser = ?) OR (fromUser = ? AND toUser = ?) ORDER BY id ASC")) {
            pstmt.setString(1, u1); pstmt.setString(2, u2); pstmt.setString(3, u2); pstmt.setString(4, u1);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(new ChatMessage(rs.getString("fromUser"), rs.getString("content"), LocalDateTime.parse(rs.getString("timestamp"), formatter)));
        } catch (SQLException e) {}
        return list;
    }

    public List<String> getChatContacts(String user) {
        List<String> contacts = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT fromUser FROM messages WHERE toUser = ? UNION SELECT DISTINCT toUser FROM messages WHERE fromUser = ?")) {
            pstmt.setString(1, user); pstmt.setString(2, user);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) contacts.add(rs.getString(1));
        } catch (SQLException e) {}
        return contacts;
    }

    public Collection<User> allUsers() {
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) {}
        return list;
    }

    public void addNotification(String u, String t, String m, String tp) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO notifications(username, title, message, type, createdAt) VALUES(?,?,?,?,?)")) {
            pstmt.setString(1, u); pstmt.setString(2, t); pstmt.setString(3, m); pstmt.setString(4, tp); pstmt.setString(5, LocalDateTime.now().format(formatter));
            pstmt.executeUpdate();
        } catch (SQLException e) {}
    }

    public List<Notification> listNotifications(String u) {
        List<Notification> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM notifications WHERE username = ? ORDER BY id DESC")) {
            pstmt.setString(1, u);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(new Notification(rs.getLong("id"), rs.getString("title"), rs.getString("message"), rs.getString("type"), rs.getInt("isRead") == 1));
        } catch (SQLException e) {}
        return list;
    }

    public int countTotalResources() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM resources")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    public void addRequest(BorrowRequest req) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO requests(resourceId, requesterUsername, message, days, status, createdAt) VALUES(?,?,?,?,?,?)")) {
            pstmt.setLong(1, req.getResourceId()); pstmt.setString(2, req.getRequesterUsername()); pstmt.setString(3, req.getMessage());
            pstmt.setInt(4, req.getDays()); pstmt.setString(5, req.getStatus().name()); pstmt.setString(6, req.getCreatedAt().format(formatter));
            pstmt.executeUpdate();
        } catch (SQLException e) {}
    }

    public List<BorrowRequest> listRequestsForOwner(String o) {
        List<BorrowRequest> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT rq.* FROM requests rq JOIN resources r ON rq.resourceId = r.id WHERE r.ownerUsername = ? ORDER BY rq.id DESC")) {
            pstmt.setString(1, o);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {}
        return list;
    }

    public List<BorrowRequest> listRequestsByUser(String u) {
        List<BorrowRequest> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM requests WHERE requesterUsername = ? ORDER BY id DESC")) {
            pstmt.setString(1, u);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {}
        return list;
    }

    public Resource getResource(long id) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM resources WHERE id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapResource(rs);
        } catch (SQLException e) {}
        return null;
    }

    public void deleteResource(long id) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM resources WHERE id=?")) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {}
    }

    private BorrowRequest mapRequest(ResultSet rs) throws SQLException {
        return new BorrowRequest(rs.getLong("id"), rs.getLong("resourceId"), rs.getString("requesterUsername"), rs.getString("message"), rs.getInt("days"), BorrowRequest.Status.valueOf(rs.getString("status")), LocalDateTime.parse(rs.getString("createdAt"), formatter), rs.getString("decidedAt") != null ? LocalDateTime.parse(rs.getString("decidedAt"), formatter) : null);
    }
}

class ChatMessage {
    final String from; final String content; final LocalDateTime timestamp;
    ChatMessage(String f, String c, LocalDateTime t) { this.from = f; this.content = c; this.timestamp = t; }
}

class Notification {
    final long id; final String title; final String message; final String type; final boolean isRead;
    Notification(long id, String t, String m, String tp, boolean r) { this.id = id; this.title = t; this.message = m; this.type = tp; this.isRead = r; }
}

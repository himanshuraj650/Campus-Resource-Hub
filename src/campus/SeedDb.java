package campus;

import java.time.LocalDateTime;

public class SeedDb {
    public static void main(String[] args) {
        System.out.println("Deep-seeding Cgc University Hub...");
        DataStore store = new DataStore("data");
        store.load();

        User u1 = new User("bhanu", "pass", "Bhanu Pratap", "CGC-2024-001");
        u1.setCampusName("Cgc University");
        store.registerUser(u1);

        User u2 = new User("simran", "pass", "Simran Kaur", "CGC-2024-099");
        u2.setCampusName("Cgc University");
        store.registerUser(u2);

        User u3 = new User("arjun", "pass", "Arjun Reddy", "CGC-2024-555");
        u3.setCampusName("Cgc University");
        store.registerUser(u3);

        Resource r1 = new Resource("CS101 Algorithms Notes", "Handwritten notes for algorithms. Very detailed.", Resource.Category.NOTES, "bhanu", "CGC Block A");
        r1.setCourseCode("CS101");
        store.addResource(r1);

        Resource r2 = new Resource("Calculus Stewart 8th Ed", "Physical book, good condition.", Resource.Category.BOOKS, "simran", "CGC Library");
        r2.setCourseCode("MATH202");
        store.addResource(r2);

        Resource r3 = new Resource("Arduino Kit", "With sensors and breadboard.", Resource.Category.PROJECT_HARDWARE, "arjun", "CGC Robotics Lab");
        r3.setCourseCode("ECE301");
        store.addResource(r3);
        
        Resource r4 = new Resource("Advanced SolidWorks Coaching", "I can teach you 3D modeling for major projects.", Resource.Category.SKILL_SHARE, "bhanu", "CGC Mech Block");
        r4.setCourseCode("MECH300");
        store.addResource(r4);

        store.sendMessage("simran", "bhanu", "Hi Bhanu! Can I borrow your algorithms notes?");
        store.sendMessage("bhanu", "simran", "Sure Simran! Just raise a request in the app.");

        System.out.println("Cgc University Hub Seeded! 🚀🌐");
    }
}

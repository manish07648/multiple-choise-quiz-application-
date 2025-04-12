import java.util.*;
import java.util.concurrent.TimeUnit;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class User {
    String username;
    String hashedPassword;

    public User(String username, String password) {
        this.username = username;
        this.hashedPassword = hashPassword(password);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkPassword(String password) {
        return hashedPassword.equals(hashPassword(password));
    }
}

class Quiz {
    private final Scanner scanner = new Scanner(System.in);
    private final List<String> questions = Arrays.asList(
            "What is the capital of France?",
            "What is 5 + 3?",
            "What is the chemical symbol for water?"
    );
    private final List<String[]> options = Arrays.asList(
            new String[]{"Berlin", "Madrid", "Paris", "Rome"},
            new String[]{"6", "8", "9", "7"},
            new String[]{"O2", "CO2", "H2O", "CH4"}
    );
    private final List<Integer> correctAnswers = Arrays.asList(3, 2, 3);
    private int score = 0;

    public void startQuiz(boolean timedMode) {
        System.out.println("Starting the Quiz...");
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("\nQuestion " + (i + 1) + ": " + questions.get(i));
            for (int j = 0; j < 4; j++) {
                System.out.println((j + 1) + ") " + options.get(i)[j]);
            }
            if (timedMode) {
                System.out.println("Time Left: 10 seconds...");
                try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException ignored) {}
            }
            System.out.print("Your answer: ");
            int answer = scanner.nextInt();
            if (answer == correctAnswers.get(i)) {
                System.out.println("Correct! 🎉");
                score++;
            } else {
                System.out.println("Wrong answer! ❌");
            }
        }
        System.out.println("\nQuiz Completed! 🎯");
        System.out.println("Your Score: " + score + "/" + questions.size() + " (" + (score * 100 / questions.size()) + "%)");
    }

    public int getScore() {
        return score;
    }
}

class Leaderboard {
    private final Map<String, Integer> scores = new HashMap<>();

    public void addScore(String username, int score) {
        scores.put(username, score);
    }

    public void displayLeaderboard() {
        System.out.println("🏆 Leaderboard 🏆");
        scores.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue() + " pts"));
    }
}

public class QuizApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, User> users = new HashMap<>();
    private static final Leaderboard leaderboard = new Leaderboard();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nWelcome to the Quiz Application!");
            System.out.println("1. Register\n2. Login\n3. Exit");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter a username: ");
                String username = scanner.nextLine();
                System.out.print("Enter a password: ");
                String password = scanner.nextLine();
                users.put(username, new User(username, password));
                System.out.println("User registered successfully!");
            } else if (choice == 2) {
                System.out.print("Enter your username: ");
                String username = scanner.nextLine();
                System.out.print("Enter your password: ");
                String password = scanner.nextLine();
                User user = users.get(username);
                if (user != null && user.checkPassword(password)) {
                    System.out.println("Login successful! Welcome, " + username + "!");
                    Quiz quiz = new Quiz();
                    quiz.startQuiz(true);
                    leaderboard.addScore(username, quiz.getScore());
                    leaderboard.displayLeaderboard();
                } else {
                    System.out.println("Invalid login credentials!");
                }
            } else {
                System.out.println("Exiting... Goodbye!");
                break;
            }
        }
    }
}

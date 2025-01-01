
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileSystemDriver fs = new FileSystemDriver(10, 100, 512);

        while (true) {
            System.out.print("$ ");
            String command = scanner.nextLine();
            String[] parts = command.split(" ");
            try {
                switch (parts[0]) {
                    case "mkfs":
                        fs.mkfs();
                        break;
                    case "create":
                        fs.create(parts[1]);
                        break;
                    case "ls":
                        fs.ls();
                        break;
                    case "open":
                        fs.open(parts[1]);
                        break;
                    case "close":
                        fs.close(Integer.parseInt(parts[1]));
                        break;
                    case "seek":
                        fs.seek(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                        break;
                    case "write":
                        fs.write(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                        break;
                    case "read":
                        fs.read(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                        break;
                    case "stat":
                        fs.stat(parts[1]);
                        break;
                    case "link":
                        fs.link(parts[1], parts[2]);
                        break;
                    case "unlink":
                        fs.unlink(parts[1]);
                        break;
                    case "exit":
                        System.exit(0);
                    case "truncate":
                        fs.truncate(parts[1], Integer.parseInt(parts[2]));
                        break;
                    default:
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}







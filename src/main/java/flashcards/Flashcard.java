package flashcards;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;

public class Flashcard {
    static List<String> log = new ArrayList<>();
    public static void main(String[] args) {
        Cards cards = new Cards();
        boolean isExport = false;
        String importFileName = null;
        String exportFileName = null;
        try (final Scanner kb = new Scanner(in)) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "-import" -> importFileName = args[1];
                    case "-export" -> {
                        exportFileName = args[1];
                        isExport = true;
                    }
                }
                if (args.length > 2) {
                    switch (args[2]) {
                        case "-import" -> importFileName = args[1];
                        case "-export" -> {
                            exportFileName = args[1];
                            isExport = true;
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + args[2]);
                    }
                }
            }
            if (importFileName != null) {
                cards.importFromFile(new File(importFileName));
            }
            boolean isRunning = true;
            while (isRunning) {
                System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
                String choice = kb.nextLine();
                log.add(choice);
                switch (choice) {
                    case "add" -> {
                        boolean isSuccessfulKey;
                        System.out.println("The card:");
                        String term = kb.nextLine();
                        log.add(choice);
                        isSuccessfulKey = cards.isNotExistsKey(term);
                        if (!isSuccessfulKey) {
                            System.out.println("The card \"" + term + "\" already exists.");
                            break;
                        }
                        boolean isSuccessfulValue;
                        System.out.println("The definition of the card:");
                        String definition = kb.nextLine();
                        log.add(definition);
                        isSuccessfulValue = cards.isNotExistsValue(definition);
                        if (!isSuccessfulValue) {
                            System.out.println("The definition \"" + definition + "\" already exists.");
                            break;
                        }
                        cards.addCards(term, definition);
                        System.out.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
                    }
                    case "remove" -> {
                        System.out.println("The card:");
                        String cardTermToRemove = kb.nextLine();
                        log.add(cardTermToRemove);
                        boolean isSuccessfullyRemoved = cards.removeCard(cardTermToRemove);
                        if (isSuccessfullyRemoved) {
                            System.out.println("The card has been removed.");
                        } else {
                            System.out.println("Can't remove \"" + cardTermToRemove + "\": there is no such card.");
                        }
                    }
                    case "import" -> {
                        System.out.println("File name:");
                        importFileName = kb.nextLine();
                        log.add(importFileName);
                        File importFile = new File(importFileName);
                        if (!importFile.exists()) {
                            System.out.println("File not found.");
                        } else {
                            cards.importFromFile(importFile);
                        }
                    }
                    case "export" -> {
                        System.out.println("File name:");
                        exportFileName = kb.nextLine();
                        log.add(exportFileName);
                        cards.exportToFile(exportFileName);
                    }
                    case "ask" -> {
                        System.out.println("How many times to ask?");
                        int numAsk = Integer.parseInt(kb.nextLine());
                        log.add(String.valueOf(numAsk));
                        cards.ask(numAsk);
                    }
                    case "exit" -> {
                        System.out.println("Bye bye!");
                        if (isExport) {
                            cards.exportToFile(exportFileName);
                        }
                        isRunning = false;
                    }
                    case "log" -> {
                        System.out.println("File name:");
                        String logFileName = kb.nextLine();
                        log.add(logFileName);
                        File logFile = new File(logFileName);
                        logFile.createNewFile();
                        logToFile(logFile);
                    }
                    case "hardest card" -> cards.findHardestCard();
                    case "reset stats" -> cards.eraseMistakes();
                }
                if (isRunning) {
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logToFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : log) {
                writer.println(line);
            }
            System.out.println("The log has been saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

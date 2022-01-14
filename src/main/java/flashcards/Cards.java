package flashcards;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static flashcards.Flashcard.log;

public class Cards {
    private Map<String/*Term*/, String/*Definition*/> cards;
    private Map<String/*Definition*/, String/*Term*/> flippedCards;
    private Map<String/*Term*/, Integer/*Mistake Count*/> mistakes;

    public Cards() {
        cards = new LinkedHashMap<>();
        flippedCards = new LinkedHashMap<>();
        mistakes = new HashMap<>();
    }

    public void addCards(String term, String definition) {
        cards.put(term, definition);
        flippedCards.put(definition, term);
        mistakes.put(term, 0);
    }

    public void checkDefinition(String term, String definition) {
        if (definition.equalsIgnoreCase(cards.get(term))) {
            String correct = "Correct!";
            System.out.println(correct);
            log.add(correct);
        } else if (cards.containsValue(definition)) {
            String printData = "Wrong. The right answer is \"" + cards.get(term) + "\", but your definition is correct for \"" + flippedCards.get(definition) + "\".";
            mistakes.put(term, mistakes.get(term) + 1);
            System.out.println(printData);
            log.add(printData);
        } else {
            String printData = "Wrong. The right answer is \"" + cards.get(term) + "\".";
            System.out.println(printData);
            log.add(printData);
            mistakes.put(term, mistakes.get(term) + 1);
        }
    }

    public Set<String> getCardsTerm() {
        return cards.keySet();
    }

    public boolean isNotExistsKey(String keyToCheck) {
        return !cards.containsKey(keyToCheck);
    }

    public boolean isNotExistsValue(String valueToCheck) {
        return !cards.containsValue(valueToCheck);
    }

    public boolean removeCard(String keyTerm) {
        String definition = cards.get(keyTerm);
        String removed = cards.remove(keyTerm);
        flippedCards.remove(definition);
        mistakes.remove(keyTerm);
        return removed != null;
    }

    public void exportToFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        try (PrintWriter printWriter = new PrintWriter(file)) {
            Set<String> termKeys = cards.keySet();
            int numberOfExportedCards = termKeys.size();
            for (String term : termKeys) {
                String line = term + " = " + cards.get(term) + " = " + mistakes.get(term);
                printWriter.println(line);
                //not logging because it is not output to console
            }
            String printData = numberOfExportedCards + " cards have been saved.";
            System.out.println(printData);
            log.add(printData);
        }
    }

    public void importFromFile(File file) throws IOException {
        try (final Scanner fileScanner = new Scanner(file)) {
            int numberOfLoadedCards = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                log.add(line);
                String[] data = line.split(" = ");
                String term = data[0];
                String definition = data[1];
                int numOfMistakes = Integer.parseInt(data[2]);
                cards.put(term, definition);
                flippedCards.put(definition, term);
                mistakes.put(term, numOfMistakes);
                numberOfLoadedCards++;
            }
            String printData = numberOfLoadedCards + " cards have been loaded.";
            System.out.println(printData);
            log.add(printData);
        }
    }

    public void ask(int numToAsk) {
        Set<String> keyTerms = cards.keySet();
        int size = keyTerms.size();
//        System.out.println(keyTerms);
        Object[] keyArray = keyTerms.toArray();
        Random random = new Random();
        int randomIndex = random.nextInt(keyTerms.size());
        for (int i = 0; i < numToAsk; i++) {
            String currentKey = (String) keyArray[randomIndex++ % size];
            System.out.println("Print the definition of \"" + currentKey + "\".");
            Scanner kb = new Scanner(System.in);
            String definitionInput = kb.nextLine();
            log.add(definitionInput);
            checkDefinition(currentKey, definitionInput);
        }
    }

    public void findHardestCard() {
//        System.out.println(mistakes);
        Optional<Integer> max = mistakes.values().stream().max(Comparator.naturalOrder());
        if (max.isEmpty() || max.orElseThrow() == 0) {
            String printData = "There are no cards with errors.";
            System.out.println(printData);
            log.add(printData);
        } else {
            List<String> hardestCards = new ArrayList<>();
            StringBuilder printData = new StringBuilder("The hardest card");
            mistakes.forEach((s, integer) -> {
                if (integer.equals(max.orElseThrow())) {
                    hardestCards.add(s);
                }
            });
            if (hardestCards.size() == 1) {
                printData.append(" is \"");
            } else {
                printData.append(" are \"");
            }
            for (String hardCard : hardestCards) {
                printData.append(hardCard).append("\", \"");
            }
            printData = new StringBuilder(printData.substring(0, printData.length() - 3));
            printData.append(". You have ").append(max.get()).append(" errors answering them.");
            System.out.println(printData);
            log.add(String.valueOf(printData));
        }
    }

    public void eraseMistakes() {
        Set<String> keySet = mistakes.keySet();
        for (String term : keySet) {
            mistakes.put(term, 0);
        }
        String printData = "Card statistics has been reset.";
        System.out.println(printData);
        log.add(printData);
    }
}

package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String[] uniqueKeyWords = {
            "adunare", "scădere", "înmulțire", "împărțire", "exponenți", "radicali", "modulo", "fracție", "procent",
            "întregi", "zecimal", "negativ", "complex", "proporțe", "rată", "măsurare", "timp", "bani", "geometrie",
            "ecuație", "inecuație", "raționament", "logaritm", "polinom", "derivare", "integrare", "matrice",
            "vector", "trigonometrie", "funcție", "perimetru", "arie", "volum", "statistică", "probabilitate",
            "real", "raport", "sistem", "conversie"
    };

    private static final Map<String, Integer> keywordToIndex = new HashMap<>();

    static {
        for (int i = 0; i < uniqueKeyWords.length; i++) {
            keywordToIndex.put(uniqueKeyWords[i], i);
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> problemsList = readFileLines("problemeDinManuale.txt");
        List<String[]> keyWordsList = getKeyWords("keyWords.txt");

        List<int[]> keyWordsVectorsList = getKeywordsVectors(keyWordsList);

        String input = "adunare";

        int[] inputVector = createInputVector(input);
        int[] scores = compareVectors(keyWordsVectorsList, inputVector);

        List<String> inputNumbers = extractNumbers(input);

        int biggestScoreIndex = -1;
        for (int i = 0; i < scores.length; i++) {
            if (inputNumbers.size() > 0) {
                if ((biggestScoreIndex == -1 || scores[i] > scores[biggestScoreIndex])
                        && hasSameNumberOfNumbers(problemsList.get(i), inputNumbers)) {
                    biggestScoreIndex = i;
                }
            } else {
                if (biggestScoreIndex == -1 || scores[i] > scores[biggestScoreIndex]) {
                    biggestScoreIndex = i;
                }
            }
        }

        String problem = problemsList.get(biggestScoreIndex);
        problem = replaceNumbers(problem, inputNumbers);
        System.out.println(problem);
    }

    private static List<String> readFileLines(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename));
    }

    private static List<String[]> getKeyWords(String filename) throws IOException {
        List<String> lines = readFileLines(filename);
        List<String[]> keyWordsList = new ArrayList<>();
        for (String line : lines) {
            keyWordsList.add(line.split("[\\p{Punct}\\s]+"));
        }
        return keyWordsList;
    }

    private static List<int[]> getKeywordsVectors(List<String[]> keyWordsList) {
        List<int[]> keyWordsVectorsList = new ArrayList<>();
        for (String[] keyWords : keyWordsList) {
            int[] vector = new int[uniqueKeyWords.length];
            for (String keyword : keyWords) {
                Integer index = keywordToIndex.get(keyword);
                if (index != null) {
                    vector[index]++;
                }
            }
            keyWordsVectorsList.add(vector);
        }
        return keyWordsVectorsList;
    }

    private static int[] createInputVector(String input) {
        int[] inputVector = new int[uniqueKeyWords.length];
        String[] inputArray = input.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String keyword : inputArray) {
            Integer index = keywordToIndex.get(keyword);
            if (index != null) {
                inputVector[index] = 1;
            }
        }
        return inputVector;
    }

    private static int[] compareVectors(List<int[]> keyWordsVectorsList, int[] inputVector) {
        int[] scores = new int[keyWordsVectorsList.size()];
        for (int i = 0; i < keyWordsVectorsList.size(); i++) {
            int score = 0;
            for (int j = 0; j < uniqueKeyWords.length; j++) {
                if (inputVector[j] == 1 && keyWordsVectorsList.get(i)[j] == 1) {
                    score++;
                }
            }
            scores[i] = score;
        }
        return scores;
    }

    private static List<String> extractNumbers(String input) {
        List<String> numbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            numbers.add(matcher.group());
        }
        Collections.sort(numbers, Comparator.reverseOrder());
        return numbers;
    }

    private static boolean hasSameNumberOfNumbers(String problem, List<String> inputNumbers) {
        List<String> problemNumbers = extractNumbers(problem);
        return problemNumbers.size() == inputNumbers.size();
    }

    private static String replaceNumbers(String problem, List<String> inputNumbers) {
        List<String> problemNumbers = extractNumbers(problem);
        problemNumbers.sort((o1, o2) -> Double.compare(Double.parseDouble(o2), Double.parseDouble(o1)));

        Map<String, String> replacements = new HashMap<>();
        for (String num : problemNumbers) {
            replacements.put(num, inputNumbers.isEmpty() ? num : inputNumbers.remove(0));
        }

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            problem = problem.replaceAll("\\b" + Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
        }
        return problem;
    }

}

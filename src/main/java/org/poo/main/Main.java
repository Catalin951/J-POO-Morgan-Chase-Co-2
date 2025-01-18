package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.Constants;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.commerciant.Commerciant;
import org.poo.exchange.Exchange;
import org.poo.execution.Execute;
import org.poo.factories.CommerciantFactory;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.userDetails.User;
import org.poo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        var sortedFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).
                sorted(Comparator.comparingInt(Main::fileConsumer))
                .toList();

        for (File file : sortedFiles) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(CheckerConstants.TESTS_PATH + filePath1);
        ObjectInput inputData = objectMapper.readValue(file, ObjectInput.class);

        ArrayNode output = objectMapper.createArrayNode();
        if (
                filePath1.equals("test01_user_updates.json") ||
                filePath1.equals("test02_upgrade_plan.json") ||
                filePath1.equals("test03_commisions.json") ||
                filePath1.equals("test04_commisions_advanced.json")) {
        System.out.println(filePath1);

        String currentDate = LocalDate.now().toString();
        String[] dateSegments = currentDate.split("-");
        try {
            Constants.currentYear = Integer.parseInt(dateSegments[0]);
            Constants.currentMonth = Integer.parseInt(dateSegments[1]);
            Constants.currentDay = Integer.parseInt(dateSegments[2]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid birthdate format");
        }
        User[] users = new User[inputData.getUsers().length];
        int userIndex = 0;
        for (UserInput userInput : inputData.getUsers()) {
            users[userIndex++] = new User(userInput);
        }

        Exchange[] exchanges = new Exchange[inputData.getExchangeRates().length];
        int exchangeIndex = 0;
        for (ExchangeInput exchangeInput : inputData.getExchangeRates()) {
            exchanges[exchangeIndex++] = new Exchange(exchangeInput);
        }

        Commerciant[] commerciants = new Commerciant[inputData.getCommerciants().length];
        int commerciantIndex = 0;
        for (CommerciantInput commerciantInput : inputData.getCommerciants()) {
            commerciants[commerciantIndex++] = CommerciantFactory.createCommerciant(commerciantInput);
        }

        Execute exec = new Execute(output, users, commerciants, exchanges, inputData.getCommands());
        exec.execution();
        Utils.resetRandom();
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }

    /**
     * Method used for extracting the test number from the file name.
     *
     * @param file the input file
     * @return the extracted numbers
     */
    public static int fileConsumer(final File file) {
        return Integer.parseInt(
                file.getName()
                        .replaceAll(CheckerConstants.DIGIT_REGEX, CheckerConstants.EMPTY_STR)
        );
    }
}

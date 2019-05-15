
/**
 * Author: David Low and Sebastian Brown
 * Modified: 15 May 2019
 * Purpose: Utilises an implementation of AES to encrypt and decrypt bitstrings
 */

import java.util.*;
import java.io.*;

enum cryptMode {
    ENCRYPT, DECRYPT
}

public class Application {

    private static cryptMode modeOfOperation;
    private static byte[] text = new byte[16];
    private static byte[] key = new byte[16];

    private static String rawText;
    private static String rawKey;
    private static long startTime;
    private static long endTime;

    private static String inputFileName;
    private static String outputFileName;

    public Application() {
    }

    public static void main(String[] args) {

        // Time recorded so that we can calculate how long encryption takes.
        startTime = getTime();

        if (args.length > 0) {
            try {

                if (args.length != 3) {
                    throw new IllegalArgumentException("Please use three arguments: *modeOfEncryptionFlag* *inputFileName* *outputFileName*");
                }
                switch (args[0]) {
                case "-e":
                case "-E":
                case "--encrypt":
                    modeOfOperation = cryptMode.ENCRYPT;
                    break;
                case "-d":
                case "-D":
                case "--decrypt":
                    modeOfOperation = cryptMode.DECRYPT;
                    break;
                default:
                    throw new IllegalArgumentException(
                            "No valid mode of operation was used. Please enter '-e' for encryption or '-d' for decryption.");
                }
                System.out.println("Using data from '" + args[1] + "' and outputting to '" + args[2] + "'.\n");
                inputFileName = args[1];
                outputFileName = args[2];

                loadData();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }

            if (modeOfOperation == cryptMode.ENCRYPT) {

                generateAnalysis();

            } else if (modeOfOperation == cryptMode.DECRYPT) {
                processDecryption(new AES0(), rawText, rawKey);
            }

        } else {
            try {
                System.out.println("No file provided. Using data from 'input.txt' and outputting to 'output.txt'\n");
                modeOfOperation = cryptMode.ENCRYPT;
                inputFileName = "input.txt";
                outputFileName = "output.txt";
                loadData();

                generateAnalysis();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * This method performs AES encryption using input taken and stored by the main method.
     * Two avalanche analysis are performed: P and Pi under K and P under K and Ki
     * The results from this analysis are stored in 2d arrays to be displayed in a table in output.
     */
    public static void generateAnalysis() {

        AES[] algorithms = { new AES0(), new AES1(), new AES2(), new AES3(), new AES4(), };
        // Results array for P and Pi under K
        String[][] pandPiUnderK = new String[5][11];

        for (int i = 0; i < algorithms.length; i++) {
            String[] pRounds = processEncryption(algorithms[i], rawText, rawKey);
            String[][] piRounds = new String[rawText.length()][];
            String[] averageDiffs = new String[pRounds.length];
            for (int k = 0; k < averageDiffs.length; k++) {
                double numerator = 0.0;
                for (int j = 0; j < rawText.length(); j++) {
                    piRounds[j] = processEncryption(algorithms[i], flipOneBit(j, rawText), rawKey);
                    numerator += bitDiff(pRounds[k], piRounds[j][k]);
                }
                // Calculate the average and store it as a string (rounded to the nearest integer )
                double average = numerator / (double) rawText.length();
                averageDiffs[k] = String.format("%.0f", average);
            }
            pandPiUnderK[i] = averageDiffs;
        }

        // Results array for P under K and Ki
        String[][] pUnderKandKi = new String[5][11];

        for (int i = 0; i < algorithms.length; i++) {
            String[] pRounds = processEncryption(algorithms[i], rawText, rawKey);
            String[][] piRounds = new String[rawText.length()][];
            String[] averageDiffs = new String[pRounds.length];
            for (int k = 0; k < averageDiffs.length; k++) {
                double numerator = 0.0;
                for (int j = 0; j < rawText.length(); j++) {
                    piRounds[j] = processEncryption(algorithms[i], rawText, flipOneBit(j, rawKey));
                    numerator += bitDiff(pRounds[k], piRounds[j][k]);
                }
                // Calculate the average and store it as a string (rounded to the nearest integer )
                double average = numerator / (double) rawText.length();
                averageDiffs[k] = String.format("%.0f", average);
            }
            pUnderKandKi[i] = averageDiffs;
        }

        // Calculate the actual ciphertext output for AES0 to include in the output
        String[] aes0Result = processEncryption(new AES0(), rawText, rawKey);
        String ciphertext = aes0Result[aes0Result.length - 1];
        // Produce the output
        outputEncryptionResults(pandPiUnderK, pUnderKandKi, ciphertext);
    }

    /**
     * This method is used exclusevly by the Tester class to load input
     */
    public static void testLoadData(String textLine, String keyLine) throws Exception {

        rawText = textLine;
        rawKey = keyLine;
        for (int i = 0; i < 16; i++) {
            text[i] = (byte) Integer.parseInt(textLine.substring(i * 8, i * 8 + 8), 2);
            key[i] = (byte) Integer.parseInt(keyLine.substring(i * 8, i * 8 + 8), 2);
        }
    }

    /**
     * loadData scans the file identified by program arguments and stores input strings in memory
     */
    public static void loadData() throws Exception {
        Scanner dataFile = null;
        try {
            dataFile = new Scanner(new File(inputFileName));
            String textLine = dataFile.nextLine();
            String keyLine = dataFile.nextLine();

            rawText = textLine;
            rawKey = keyLine;

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (dataFile != null) {
                dataFile.close();
            }
        }
    }

    /**
     * This method initiates AES encryption, 
     * the encryption method, plaintext and key are set using method arguments
     */
    public static String[] processEncryption(AES aes, String plaintext, String key) {
        String[] aesResults = aes.encrypt(plaintext, key);
        String[] results = new String[aesResults.length + 1];
        results[0] = plaintext;
        // So that the plaintext is included in the results array, 
        // results is constructed by joining aesResults and plaintext
        for (int i = 0; i < aesResults.length; i++) {
            results[i + 1] = aesResults[i];
        }
        endTime = getTime();
        return results;
    }
    /**
     * This method initiates AES decryption, 
     * the encryption method, plaintext and key are set using method arguments.
     */
    public static String[] processDecryption(AES aes, String plaintext, String key) {
        String[] results = aes.decrypt(plaintext, key);
        outputDecryptionResults(results);
        return results;
    }

    /**
     * A helper method used to compare two bitstrings.
     * The number of varying bits is returned as a double
     */
    private static double bitDiff(String a, String b) {
        char[] aChars = a.toCharArray();
        char[] bChars = b.toCharArray();
        double difference = 0;
        for (int i = 0; i < aChars.length; i++) {
            if (aChars[i] != bChars[i]) {
                difference++;
            }
        }
        return difference;
    }

    /*
     * outputEncryptionResults
     * 
     * Prints the results of encryption in the console and to the designated output file 
     * 
     */
    private static void outputEncryptionResults(String[][] resultsText, String[][] resultsKey, String ciphertext) {
        String output = "ENCRYPTION\n";
        output += "Plaintext P: " + rawText + "\n";
        output += "Key K: " + rawKey + "\n";
        output += "Ciphertext C: " + ciphertext + "\n";
        output += "Running time: " + (endTime - startTime) + " milliseconds.\n";
        output += "Avalanche:\nP and Pi under K\n";

        String[] tableHeader = { "Round", "AES0", "AES1", "AES2", "AES3", "AES4" };

        String[][] tableBody = new String[resultsText[0].length][resultsText.length + 1];
        // Populating table 1
        for (int i = 0; i < tableBody.length; i++) {
            for (int j = 0; j < tableBody[i].length; j++) {
                if (j == 0) {
                    tableBody[i][j] = Integer.toString(i);
                } else {
                    tableBody[i][j] = resultsText[j - 1][i];
                }
            }
        }

        String[][] table2Body = new String[resultsKey[0].length][resultsKey.length + 1];
        // Populating table 2
        for (int i = 0; i < table2Body.length; i++) {
            for (int j = 0; j < table2Body[i].length; j++) {
                if (j == 0) {
                    table2Body[i][j] = Integer.toString(i);
                } else {
                    table2Body[i][j] = resultsKey[j - 1][i];
                }
            }
        }
        output += printTable(tableHeader, tableBody);
        output += "\nP under K and Ki\n";
        output += printTable(tableHeader, table2Body);
        System.out.print(output);

        // Now, save the output to file
        try {
            PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");
            writer.println(output);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("Cannot print output");
        }
    }

    private static void outputDecryptionResults(String[] resultsText) {
        String output = "DECRYPTION\n";
        output += "Ciphertext C: " + rawText + "\n";
        output += "Key K: " + rawKey + "\n";
        output += "Plaintext P: " + resultsText[resultsText.length - 1] + "\n";
        System.out.println(output);
        // Now, save output to file
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.println(output);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("Cannot print output");
        }
    }

    /*
     * A helper function that prints to console a table based on a header array and
     * a body matrix.
     */
    private static String printTable(String[] header, String[][] body) {
        String output = "";
        String columnPadding = "  ";
        // Print the header
        for (int i = 0; i < header.length; i++) {
            output += header[i] + columnPadding;
        }
        output += "\n";
        for (int i = 0; i < body.length; i++) {
            // Pad each cell so that it is the same width as it's header
            for (int j = 0; j < body[i].length; j++) {
                int cellPadding = header[j].length() - body[i][j].length();
                String padding = "";
                // Pad the cell
                if (cellPadding > 0) {
                    for (int p = 0; p < cellPadding; p++) {
                        padding += " ";
                    }
                }
                output += body[i][j] + padding + columnPadding;
            }
            output += "\n";
        }
        return output;
    }

    /**
     * flipOneBit is a helper function that takes a bitstring and swaps one bit at
     * index i
     */
    private static String flipOneBit(int i, String input) {
        char[] charArray = input.toCharArray();
        if (charArray[i] == '0') {
            charArray[i] = '1';
        } else {
            charArray[i] = '0';
        }
        return new String(charArray);
    }

    /**
     * A helper function used to calculate the current system time and return it as
     * a long
     */
    private static long getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getTimeInMillis();
    }
}
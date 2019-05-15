/**
 * Author: David Low and Sebastian Brown
 * Modified: 15 May 2019
 * Purpose: Utilises an implementation of AES to encrypt and decrypt bitstrings
 * Notes: The class provides four public methods that may be called.
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


    public Application() {
    }

    public static void main(String[] args) {

        startTime = getTime();

        if (args.length > 0) {
            try {
                switch (args[0]) {
                case "-e":
                case "-E":
                    modeOfOperation = cryptMode.ENCRYPT;
                    break;
                case "-d":
                case "-D":
                    modeOfOperation = cryptMode.DECRYPT;
                    break;
                default:
                    throw new IllegalArgumentException(
                            "No valid mode of operation was used. Please enter '-e' for encryption or '-d' for decryption.");
                }

                loadData(args[1]);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }

            if (modeOfOperation == cryptMode.ENCRYPT) {
               
                generateAnalysis();

            } else if (modeOfOperation == cryptMode.DECRYPT) {
                processDecryption(new AES0(),rawText,rawKey);
            }

        } else {
            try {
                System.out.println("No file provided. Using input.txt\n");
                modeOfOperation = cryptMode.ENCRYPT;
                loadData("input.txt");

                generateAnalysis();
             
            } catch (Exception e) {
                e.printStackTrace();
            }
          
        }
    }

    /**
     * 
     */
    public static void generateAnalysis() {

        AES[] algorithms = {
            new AES0(),
            new AES1(),
            new AES2(),
            new AES3(),
            new AES4(),
        };

       String[][] pandPiUnderK = new String[5][11];

        for (int i = 0; i < algorithms.length; i++) {
            String[] pRounds = processEncryption(algorithms[i], rawText, rawKey);
            String[][] piRounds = new String[rawText.length()][];
            String[] averageDiffs = new String[pRounds.length];
            for (int k = 0; k < averageDiffs.length; k++) {
                double numerator = 0.0;
                for (int j = 0; j < rawText.length(); j++) {
                    piRounds[j] = processEncryption(algorithms[i], flipOneBit(j,rawText), rawKey);
                    numerator += bitDiff(pRounds[k],piRounds[j][k]);
                }
                double average = numerator / (double)rawText.length();
                averageDiffs[k] = String.format("%.0f", average);
            }
            pandPiUnderK[i] = averageDiffs;
        }

        String[][] pUnderKandKi = new String[5][11];

        for (int i = 0; i < algorithms.length; i++) {
            String[] pRounds = processEncryption(algorithms[i], rawText, rawKey);
            String[][] piRounds = new String[rawText.length()][];
            String[] averageDiffs = new String[pRounds.length];
            for (int k = 0; k < averageDiffs.length; k++) {
                double numerator = 0.0;
                for (int j = 0; j < rawText.length(); j++) {
                    piRounds[j] = processEncryption(algorithms[i], rawText,flipOneBit(j,rawKey));
                    numerator += bitDiff(pRounds[k],piRounds[j][k]);
                }
                double average = numerator / (double)rawText.length();
                averageDiffs[k] = String.format("%.0f", average);
            }
            pUnderKandKi[i] = averageDiffs;
        }

        String[] aes0Result = processEncryption(new AES0(), rawText, rawKey);
        String ciphertext = aes0Result[aes0Result.length-1];

        outputEncryptionResults(pandPiUnderK,pUnderKandKi,ciphertext);
    }

    public static void testLoadData(String textLine, String keyLine) throws Exception {

        rawText = textLine;
        rawKey = keyLine;

        for (int i = 0; i < 16; i++) {
            text[i] = (byte) Integer.parseInt(textLine.substring(i * 8, i * 8 + 8), 2);
            key[i] = (byte) Integer.parseInt(keyLine.substring(i * 8, i * 8 + 8), 2);
        }
       
        

    }

    public static void loadData(String fileName) throws Exception {
        Scanner dataFile = null;

        try {
            dataFile = new Scanner(new File(fileName));
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

    public static String[] processEncryption(AES aes, String plaintext, String key) {
        String[] aesResults = aes.encrypt(plaintext, key);
        String[] results = new String[aesResults.length+1];
        results[0] = plaintext;
        for (int i = 0; i < aesResults.length; i++) {
            results[i+1] = aesResults[i];
        }
        endTime = getTime();
        return results;
    }

    public static String[] processDecryption(AES aes, String plaintext, String key) {
        String[] results = aes.decrypt(plaintext, key);
        outputDecryptionResults(results);
        return results;
    }

   

    private static double bitDiff(String a, String b ) {
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
    outputEncryptionResults

    Prints the results of encryption in the console and to a text file named output.txt
    
    */
    private static void outputEncryptionResults(String[][] resultsText,String[][] resultsKey, String ciphertext) {
        String output = "ENCRYPTION\n";
        output += "Plaintext P: " + rawText + "\n";
        output += "Key K: " + rawKey + "\n";
        output += "Ciphertext C: " + ciphertext + "\n";
        output += "Running time: " + (endTime - startTime) + " milliseconds.\n";
        output += "Avalanche:\nP and Pi under K\n";

        String[] tableHeader = { "Round", "AES0", "AES1", "AES2", "AES3", "AES4" };

        String[][] tableBody = new String[resultsText[0].length ][resultsText.length + 1];

        for (int i = 0; i < tableBody.length; i++) {
            for (int j = 0; j < tableBody[i].length; j++) {
                if (j == 0) {
                    tableBody[i][j] = Integer.toString(i);
                } else {
                    tableBody[i][j] = resultsText[j - 1][i];
                }
            }
        }

        String[][] table2Body = new String[resultsKey[0].length ][resultsKey.length + 1];

        for (int i = 0; i < table2Body.length; i++) {
            for (int j = 0; j < table2Body[i].length; j++) {
                if (j == 0) {
                    table2Body[i][j] = Integer.toString(i);
                } else {
                    table2Body[i][j] = resultsKey[j - 1][i];
                }
            }
        }

        output += printTable(tableHeader,tableBody);
         
        output += "P under K and Ki\n";
        output += printTable(tableHeader,table2Body);
        System.out.print(output);
    
        // Now, save output to file
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
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
        output += "Plaintext P: " + resultsText[resultsText.length-1] + "\n";
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
    A helper function that prints to console a table based on a header array and a body matrix.
    */
    private static String printTable(String[] header,String[][] body) {
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
     * flipOneBit is a helper function that takes a bitstring and swaps one bit at index i
     */
    private static String flipOneBit(int i,String input) {
       char[] charArray = input.toCharArray();
        if (charArray[i] == '0') {
            charArray[i] = '1';
        } else {
            charArray[i] = '0';
        }
        return new String(charArray);
    }

    /**
     * A helper function used to calculate the current system time and return it as a long
     */
    private static long getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getTimeInMillis();
    }
}
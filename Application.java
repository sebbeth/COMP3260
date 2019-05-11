
// Add comments and documentation here

import java.util.*;
import java.io.*;

// import java.math.BigInteger;

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        startTime = calendar.getTimeInMillis();

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
                String[][] results = new String[5][];
                results[0] = processEncryption(new AES0());
                results[1] = processEncryption(new AES1());
                results[2] = processEncryption(new AES2());
                results[3] = processEncryption(new AES3());
                results[4] = processEncryption(new AES4());

                outputEncryptionResults(results);

                // testEnDec();
            } else if (modeOfOperation == cryptMode.DECRYPT) {
                processDecryption();
            }

        } else {
            try {
                System.out.println("No file provided. Using input.txt\n");
                modeOfOperation = cryptMode.ENCRYPT;
                loadData("input.txt");
                String[][] results = new String[5][];

                results[0] = processEncryption(new AES0());
                results[1] = processEncryption(new AES1());
                results[2] = processEncryption(new AES2());
                results[3] = processEncryption(new AES3());
                results[4] = processEncryption(new AES4());

                outputEncryptionResults(results);
            } catch (Exception e) {
                System.out.println(e);
            }
          
        }
    }

    public static void testLoadData(String textLine, String keyLine) throws Exception {

        rawText = textLine;
        rawKey = keyLine;

        for (int i = 0; i < 16; i++) {
            // System.out.println(i + " " + textLine.substring(i, i+8));
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

            // TODO remove, only needed while testing with hex input
            rawText = hexToBinary(textLine);
            rawKey = hexToBinary(keyLine);

            System.out.println(rawText);
            System.out.println(rawKey);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (dataFile != null) {
                dataFile.close();
            }
        }
    }

    // public static void testEnDec() {
    // System.out.println("TESTING ENC DEC");
    // AES vanillaAES = new AES0();
    // String[] encryptedResults = vanillaAES.encrypt(rawText, rawKey);
    // String[] decryptedResults = vanillaAES.decrypt(encryptedResults[9], rawKey);

    // // System.out.println("Printing results of encryption");
    // // for (int i = 0; i < encryptedResults.length; i++) {
    // // System.out.println("Round " + (i + 1) + ": " +
    // // binaryToHex(encryptedResults[i]));
    // // }

    // // System.out.println("Printing results of decryption");
    // // for (int i = 0; i < decryptedResults.length; i++) {
    // // System.out.println("Round " + (i + 1) + ": " +
    // // binaryToHex(decryptedResults[i]));
    // // }
    // System.out.println("Raw Text: " + binaryToHex(rawText));
    // System.out.println("Encrypted Text: " + binaryToHex(encryptedResults[9]));
    // System.out.println("Decrypted Text: " + binaryToHex(decryptedResults[9]));
    // }

    public static String[] processEncryption(AES aes) {
        startTime = getTime();
        String[] results = aes.encrypt(rawText, rawKey);
        endTime = getTime();
       // outputEncryptionResults(results);
        return results;
    }

    public static String[] processDecryption() {
        AES vanillaAES = new AES0();
        String[] results = vanillaAES.decrypt(rawText, rawKey);
        outputDecryptionResults(results);
        return results;
    }

    /**
     * Implimentation of avalancheAnalysis that calculates the bit difference between the plaintext and the 
     * intermediate results in each round.
     * 
     * @param plaintext
     * @param results
     * @return an array of strings representing the bit-difference between each round's result and the plaintext
     */
    private static String[] avalancheAnalysis(String plaintext, String[] results) {
        String[] output = new String[results.length];
        char[] plaintextChars = plaintext.toCharArray();
        for (int i = 0; i < results.length; i++) {
            int difference = 0;
            char[] result = results[i].toCharArray();
            for (int j = 0; j < result.length; j++) {
                if (result[j] != plaintextChars[j]) {
                    difference++; 
                }
            }
            output[i] = Integer.toString(difference);
        }
        return output;
    }

    /*
    outputEncryptionResults

    Prints the results of encryption in the console and to a text file named output.txt
    
    */
    private static void outputEncryptionResults(String[][] results) {
        String output = "ENCRYPTION\n";
        output += "Plaintext P: " + rawText + "\n";
        output += "Key K: " + rawKey + "\n";
        output += "Ciphertext C: " + "\n";
        output += results[0][results[0].length-1] + "\n";
        output += "Running time: " + (endTime - startTime) + " milliseconds.\n";
        output += "Avalanche:\nP and Pi under K\n";

        String[] tableHeader = { "Round", "AES0", "AES1", "AES2", "AES3", "AES4" };

        String[][] analysis = new String[5][];
        analysis[0] = avalancheAnalysis(rawText, results[0]);
        analysis[1] = avalancheAnalysis(rawText, results[1]);
        analysis[2] = avalancheAnalysis(rawText, results[2]);
        analysis[3] = avalancheAnalysis(rawText, results[3]);
        analysis[4] = avalancheAnalysis(rawText, results[4]);

        String[][] tableBody = new String[results[0].length ][analysis.length + 1];

        for (int i = 0; i < tableBody.length; i++) {
            for (int j = 0; j < tableBody[i].length; j++) {
                if (j == 0) {
                    tableBody[i][j] = Integer.toString(i);
                } else {
                    tableBody[i][j] = analysis[j - 1][i];
                }
            }
        }

        output += printTable(tableHeader,tableBody);
         
        output += "P under K and Ki\n";
        // TODO add P under K and Ki table
        System.out.print(output);
        System.out.println("Debugging stuff, remove this:");
        for (int i = 0; i < results[0].length; i++) {
            System.out.println(binaryToHex(results[0][i]));
        }
        System.out.println(Arrays.toString(results[0]));

        // Now, save output to file
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.println(output);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("Cannot print output");
        }
    }

    private static void outputDecryptionResults(String[] results) {
        String output = "DECRYPTION\n";
        output += "Ciphertext C: " + rawText + "\n";
        output += "Key K: " + rawKey + "\n";
        output += "Plaintext P: " + results[results.length-1] + "\n";
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

    private static String hexToBinary(String hex) {
        String total = "";
        for (int i = 0; i < hex.length(); i += 2) {
            String res = Integer.toBinaryString(Integer.parseInt(hex.substring(i, i + 2), 16));
            while (res.length() < 8) {
                res = "0" + res;
            }
            total += res;
        }
        return total;
    }

    private static String binaryToHex(String binary) {
        String total = "";
        for (int i = 0; i < binary.length(); i += 8) {
            int decimal = Integer.parseInt(binary.substring(i, i + 8), 2);
            String res = Integer.toString(decimal, 16);
            while (res.length() < 2) {
                res = "0" + res;
            }
            res += " ";
            total += res;
        }
        return total;
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

    private static long getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getTimeInMillis();
    }
}
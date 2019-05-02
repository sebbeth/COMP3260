
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

    public Application() {
    }

    public static void main(String[] args) {

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
                processEncryption();
                // testEnDec();
            } else if (modeOfOperation == cryptMode.DECRYPT) {
                processDecryption();
            }

        } else {
            // System.out.println("No file provided. Ending program.\n");
            try {
                modeOfOperation = cryptMode.ENCRYPT;
                loadData("input.txt");
            } catch (Exception e) {
                System.out.println(e);
            }
            processEncryption();
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

    public static String[] processEncryption() {
        AES vanillaAES = new AES0();
        String[] results = vanillaAES.encrypt(rawText, rawKey);
        outputEncryptionResults(results);
        return results;
    }

    public static String[] processDecryption() {
        AES vanillaAES = new AES0();
        String[] results = vanillaAES.decrypt(rawText, rawKey);
        outputDecryptionResults(results);
        return results;
    }

    private static void outputEncryptionResults(String[] results) {
        System.out.println("Printing results of encryption");
        for (int i = 0; i < results.length; i++) {
            System.out.println(binaryToHex(results[i]));
        }
        String output = Arrays.toString(results).replace("[", "").replace("]", "");
        System.out.println(Arrays.toString(results));
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.println(output);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("Cannot print output");
        }
    }

    private static void outputDecryptionResults(String[] results) {
        System.out.println("DECRYPTION");
        System.out.println(results[9]);
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
}
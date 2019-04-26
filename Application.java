
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

                if (modeOfOperation == cryptMode.ENCRYPT) {
                    processEncryption();
                } else if (modeOfOperation == cryptMode.DECRYPT) {
                    processDecryption();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        } else {
            System.out.println("No file provided. Ending program.\n");
        }
    }

    private static void loadData(String fileName) throws Exception {
        Scanner dataFile = null;

        try {
            dataFile = new Scanner(new File(fileName));
            String textLine = dataFile.nextLine();
            String keyLine = dataFile.nextLine();

            rawText = textLine;
            rawKey = keyLine;

            System.out.println(textLine);
            System.out.println(keyLine);

            for (int i = 0; i < 16; i++) {
                // System.out.println(i + " " + textLine.substring(i, i+8));
                text[i] = (byte) Integer.parseInt(textLine.substring(i * 8, i * 8 + 8), 2);
                key[i] = (byte) Integer.parseInt(keyLine.substring(i * 8, i * 8 + 8), 2);
            }

            // String binaryText = "";
            // String hexText = "";
            // String binaryKey = "";
            // String hexKey = "";

            // for (int i = 0; i < 16; i++) {
            // binaryText += String.format("%8s", Integer.toBinaryString(text[i] &
            // 0xFF)).replace(' ', '0');
            // hexText += String.format("%02X", text[i]);
            // binaryKey += String.format("%8s", Integer.toBinaryString(key[i] &
            // 0xFF)).replace(' ', '0');
            // hexKey += String.format("%02X", key[i]);
            // }
            // System.out.println("Text back to binary " + binaryText);
            // System.out.println("Key back to binary " + binaryKey);
            // System.out.println("Text to hex " + hexText);
            // System.out.println("Key to hex " + hexKey);

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (dataFile != null) {
                dataFile.close();
            }
        }
    }

    private static void processEncryption() {
        AES vanillaAES = new AES();
        // For each of the 128 variations of the text and key
        // Encrypt the text and key in the five variations.
        // byte[][] results = vanillaAES.encrypt(text, key);
        String[] results = vanillaAES.encrypt(rawText, rawKey);
        System.out.println("Printing results of encryption");
        System.out.println(Arrays.toString(results));
    }

    private static void processDecryption() {

    }

    // private static void outputEncryptionResults() {

    // }

    // private static void outputDecryptionResults() {

    // }
}
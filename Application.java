
// Add comments and documentation here

import java.util.*;
import java.io.*;
// import java.math.BigInteger;

enum cryptMode {
    ENCRYPT, DECRYPT
}

public class Application {

    private static cryptMode modeOfOperation;
    private static byte[] text;
    private static byte[] key;

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
            String line1 = dataFile.nextLine();
            System.out.println(line1);
            
            BigInteger textBigInt = new BigInteger(line1, 2);
            System.out.println(textBigInt);
            text = textBigInt.toByteArray();
            
            String line2 = dataFile.nextLine();
            System.out.println(line2);
            key = new BigInteger(line2, 2).toByteArray();
            
            System.out.println(Arrays.toString(text));
            System.out.println(Arrays.toString(key));

            // Add to local variables
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (dataFile != null) {
                dataFile.close();
            }
        }
    }

    private static void processEncryption() {

    }

    private static void processDecryption() {

    }

    // private static void outputEncryptionResults() {

    // }

    // private static void outputDecryptionResults() {

    // }
}
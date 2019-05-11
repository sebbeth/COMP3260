/**
 * 
 * 
 * 
 */
import java.util.*;
// import java.io.*;

public class Tester {

    // private static cryptMode modeOfOperation;


    public static void main(String args[]) {
        System.out.println("");
        // Generate a plaintext and a key
        runTest(128,128);

    }

  

    public static boolean runTest(int keyLength, int plaintextLength) {
      
        try {
            String plaintext = generateBitString(plaintextLength);
            String key = generateBitString(keyLength);
            // Encrypt the plaintext
            // modeOfOperation = cryptMode.ENCRYPT;
            Application.testLoadData(plaintext,key);
            String[] encryptionResults = Application.processEncryption(new AES0());
            System.out.println(Arrays.toString(encryptionResults));

            String cipherText = encryptionResults[encryptionResults.length-1];
            // Now decrypt the plaintext
            // modeOfOperation = cryptMode.DECRYPT;
            Application.testLoadData(cipherText,key);
            String[] decryptionResults = Application.processDecryption();
            System.out.println(Arrays.toString(decryptionResults));

            System.out.println("\n \n \n \n ======================= TEST RESULTS ======================= \n");
            if (cipherText == decryptionResults[decryptionResults.length-1]) {
                System.out.println("PASS");
                return true;
            } else {
                System.out.println("FAIL");
                System.err.println("Plaintext: " + plaintext  + " \nKey:  " + key);
                System.err.println( cipherText + " != " + decryptionResults[decryptionResults.length-1] );
                return false;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
      
    }

      /*
    Helper function used to generate a bit string for plaintext and keys
    */
    private static String generateBitString(int length) {
        String output = "";
        char[] characters = {'0','1'};
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            int x = r.nextInt(2);
            output += characters[x];
        }
        return output;
    }

}
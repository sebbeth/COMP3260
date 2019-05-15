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
    
        AES[] algorithms = {
            new AES0(),
            new AES1(),
            new AES2(),
            new AES3(),
            new AES4(),
        };
        
       // AES[] algorithms = { new AES1() };
        int repeats = 100; // The number of times to repeat the tests (with random input)
        String results = "";
        for (int i=0; i < repeats; i++ ) {
            for (AES algorithm : algorithms) {
                results += algorithm.getClass().getName() + " => " + runTest(128,128,algorithm) + "\n";
            }
            results += "\n\n";
        }
        System.out.println("\n \n \n \n ======================= TEST RESULTS ======================= \n");
        System.out.println(results);

    }

  

    public static String runTest(int keyLength, int plaintextLength, AES algorithm) {
      
        try {
           String plaintext = generateBitString(plaintextLength);
            String key = generateBitString(keyLength);
            //String plaintext = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
            //String key = "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
            
            // Encrypt the plaintext
            // modeOfOperation = cryptMode.ENCRYPT;
            Application.testLoadData(plaintext,key);
            String[] encryptionResults = Application.processEncryption(algorithm,plaintext,key);
            System.out.println(Arrays.toString(encryptionResults));

            String cipherText = encryptionResults[encryptionResults.length-1];
            // Now decrypt the plaintext
            // modeOfOperation = cryptMode.DECRYPT;
            Application.testLoadData(cipherText,key);
            String[] decryptionResults = Application.processDecryption(algorithm,cipherText,key);
            System.out.println(Arrays.toString(decryptionResults));

           
            if (plaintext.equals(decryptionResults[decryptionResults.length-1])) {
                System.out.println("\n \nPASS \n \n");
                return "PASS";
            } else {
                System.out.println("\n \nFAIL\n \n");
                System.err.println("Plaintext: " + plaintext  + " \nKey:  " + key);
                System.err.println( plaintext + " != " + decryptionResults[decryptionResults.length-1] );
                return "FAIL";
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return "FAIL";      
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
/**
 * Author: David Low and Sebastian Brown
 * Modified: 15 May 2019
 * Description: A functional test implemented to validate the output of AES encrypt and decrypt.
 * Notes: The repeats variable can be modified so that multiple random inputs are generated and used to test AES.
 * 
 */
import java.util.*;


public class Tester {

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
        
        int repeats = 1; // The number of times to repeat the tests (with random input)
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
            // Encrypt the plaintext
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
public class AES3 extends AES {

    /**
     * Implements the encryption method that is abstracted in AES. This
     * implementation follows a modified round specifications for AES, where the
     * mix-columns step is not performed.
     * 
     * @param plaintext the plaintext block that is to be encrypted.
     * @param key       the cipher key that is to be used to encrypt the plaintext
     *                  block.
     * @return an array of each result at the end of a round.
     */
    public byte[][] encrypt(byte[] plaintext, byte[] key) {
        byte[][] subKeys = generateKeys(key);
        byte[][] results = new byte[16][NUM_OF_ROUNDS];

        // Initial Key XOR
        byte[] result = addRoundKey(plaintext, subKeys[0]);

        // 9 rounds
        for (int i = 1; i <= NUM_OF_ROUNDS - 1; i++) {
            result = substituteBytes(result, S_BOX);
            result = shiftRows(result, Direction.LEFT);
            // result = mixColumns(result);
            result = addRoundKey(result, subKeys[i]);
            results[i - 1] = result;
        }

        // Final round
        result = substituteBytes(result, S_BOX);
        result = shiftRows(result, Direction.LEFT);
        result = addRoundKey(result, subKeys[NUM_OF_ROUNDS]);
        results[9] = result;

        return results;
    }

    /**
     * Implements the decryption method that is abstracted in AES. This
     * implementation follows a modified round specifications for AES, where the
     * invert-mixed-columns step is not performed.
     * 
     * @param plaintext the plaintext block that is to be decrypted.
     * @param key       the cipher key that is to be used to decrypt the plaintext
     *                  block.
     * @return an array of each result at the end of a round.
     */
    public byte[][] decrypt(byte[] ciphertext, byte[] key) {
        byte[][] subKeys = generateKeys(key);
        byte[][] results = new byte[16][NUM_OF_ROUNDS];

        // Initial Key XOR
        byte[] result = addRoundKey(ciphertext, subKeys[NUM_OF_ROUNDS]);

        // 9 rounds
        for (int i = NUM_OF_ROUNDS - 1; i >= 1; i--) {
            result = shiftRows(result, Direction.RIGHT);
            result = substituteBytes(result, INV_S_BOX);
            result = addRoundKey(result, subKeys[i]);
            // result = invertMixedColumns(result);
            results[9 - i] = result;
        }

        // Final round
        result = shiftRows(result, Direction.RIGHT);
        result = substituteBytes(result, INV_S_BOX);
        result = addRoundKey(result, subKeys[0]);
        results[9] = result;

        return results;
    }
}
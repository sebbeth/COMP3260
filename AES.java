enum Direction {
    LEFT, RIGHT
}

public class AES {
    protected static int numOfRounds = 10;

    protected static String[][] sBox = new String[][] {
            { "63", "7c", "77", "7b", "f2", "6b", "6f", "c5", "30", "01", "67", "2b", "fe", "d7", "ab", "76" },
            { "ca", "82", "c9", "7d", "fa", "59", "47", "f0", "ad", "d4", "a2", "af", "9c", "a4", "72", "c0" },
            { "b7", "fd", "93", "26", "36", "3f", "f7", "cc", "34", "a5", "e5", "f1", "71", "d8", "31", "15" },
            { "04", "c7", "23", "c3", "18", "96", "05", "9a", "07", "12", "80", "e2", "eb", "27", "b2", "75" },
            { "09", "83", "2c", "1a", "1b", "6e", "5a", "a0", "52", "3b", "d6", "b3", "29", "e3", "2f", "84" },
            { "53", "d1", "00", "ed", "20", "fc", "b1", "5b", "6a", "cb", "be", "39", "4a", "4c", "58", "cf" },
            { "d0", "ef", "aa", "fb", "43", "4d", "33", "85", "45", "f9", "02", "7f", "50", "3c", "9f", "a8" },
            { "51", "a3", "40", "8f", "92", "9d", "38", "f5", "bc", "b6", "da", "21", "10", "ff", "f3", "d2" },
            { "cd", "0c", "13", "ec", "5f", "97", "44", "17", "c4", "a7", "7e", "3d", "64", "5d", "19", "73" },
            { "60", "81", "4f", "dc", "22", "2a", "90", "88", "46", "ee", "b8", "14", "de", "5e", "0b", "db" },
            { "e0", "32", "3a", "0a", "49", "06", "24", "5c", "c2", "d3", "ac", "62", "91", "95", "e4", "79" },
            { "e7", "c8", "37", "6d", "8d", "d5", "4e", "a9", "6c", "56", "f4", "ea", "65", "7a", "ae", "08" },
            { "ba", "78", "25", "2e", "1c", "a6", "b4", "c6", "e8", "dd", "74", "1f", "4b", "bd", "8b", "8a" },
            { "70", "3e", "b5", "66", "48", "03", "f6", "0e", "61", "35", "57", "b9", "86", "c1", "1d", "9e" },
            { "e1", "f8", "98", "11", "69", "d9", "8e", "94", "9b", "1e", "87", "e9", "ce", "55", "28", "df" },
            { "8c", "a1", "89", "0d", "bf", "e6", "42", "68", "41", "99", "2d", "0f", "b0", "54", "bb", "16" } };

    protected static int[][] encryptMixCol = new int[][] { { 2, 3, 1, 1 }, { 1, 2, 3, 1 }, { 1, 1, 2, 3 },
            { 3, 1, 1, 2 } };

    protected static int[][] decryptMixCol = new int[][] { { 14, 11, 13, 9 }, { 9, 14, 11, 13 }, { 13, 9, 14, 11 },
            { 11, 13, 9, 14 } };

    public AES() {

    }

    // Provides string to byte conversion and back for encryption.
    public String[] encrypt(String plaintext, String key) {
        byte[] bytePlainText = stringToByteArray(plaintext);
        byte[] byteKey = stringToByteArray(key);

        byte[][] byteResults = encrypt(bytePlainText, byteKey);
        String[] stringResults = new String[numOfRounds + 1];

        for (int i = 0; i < numOfRounds + 1; i++) {
            stringResults[i] = byteArrayToString(byteResults[i]);
        }
        return stringResults;
    }

    // Accepts 16-byte text and key arrays, encrypts them according to AES and
    // returns all intermediary results.
    public byte[][] encrypt(byte[] plaintext, byte[] key) {
        byte[][] subKeys = generateKeys(key);

        byte[][] results = new byte[16][numOfRounds + 1];
        results[0] = plaintext;

        for (int i = 0; i < numOfRounds - 1; i++) {
            byte[] intermediaryResult = results[i];
            intermediaryResult = substituteBytes(intermediaryResult);
            intermediaryResult = shiftRows(intermediaryResult, Direction.LEFT);
            intermediaryResult = mixColumns(intermediaryResult);
            intermediaryResult = addRoundKey(intermediaryResult, subKeys[i]);
            results[i + 1] = intermediaryResult;
        }

        byte[] finalResult = results[9];
        finalResult = substituteBytes(finalResult);
        finalResult = shiftRows(finalResult, Direction.LEFT);
        finalResult = addRoundKey(finalResult, subKeys[9]);
        results[10] = finalResult;

        return results;
    }

    // public decrypt() {

    // }

    protected byte[] substituteBytes(byte[] text) {
        return text;
    }

    protected byte[] shiftRows(byte[] text, Direction direction) {
        byte[] result = new byte[16];
        for (int i = 0; i < 4; i++) {
            switch (direction) {
            case LEFT:
                result[i] = text[(i + (i * 4)) % 16];
                System.out.println((i) + " " + (i + (i * 4)) % 16);

                result[i + 4] = text[(i + 4 + (i * 4)) % 16];
                System.out.println((i + 4) + " " + (i + 4 + (i * 4)) % 16);

                result[i + 8] = text[(i + 8 + (i * 4)) % 16];
                System.out.println((i + 8) + " " + (i + 8 + (i * 4)) % 16);

                result[i + 12] = text[(i + 12 + (i * 4)) % 16];
                System.out.println((i + 12) + " " + (i + 12 + (i * 4)) % 16);
                break;
            case RIGHT:
                result[i] = text[((i - (i * 4)) + 16) % 16];
                System.out.println((i) + " " + ((i - (i * 4)) + 16) % 16);

                result[i + 4] = text[((i + 4 - (i * 4)) + 16) % 16];
                System.out.println((i + 4) + " " + ((i + 4 - (i * 4)) + 16) % 16);

                result[i + 8] = text[((i + 8 - (i * 4)) + 16) % 16];
                System.out.println((i + 8) + " " + ((i + 8 - (i * 4)) + 16) % 16);

                result[i + 12] = text[((i + 12 - (i * 4)) + 16) % 16];
                System.out.println((i + 12) + " " + ((i + 12 - (i * 4)) + 16) % 16);
                break;
            }
        }
        return result;
    }

    protected byte[] mixColumns(byte[] text) {
        return text;
    }

    protected byte[] addRoundKey(byte[] text, byte[] key) {
        return text;
    }

    protected byte[][] generateKeys(byte[] masterKey) {
        byte[][] subKeys = new byte[16][numOfRounds];
        for (int i = 0; i < numOfRounds; i++) {
            subKeys[i] = masterKey;
        }
        return subKeys;
    }

    protected byte[] stringToByteArray(String input) {
        byte[] output = new byte[16];

        for (int i = 0; i < 16; i++) {
            output[i] = (byte) Integer.parseInt(input.substring(i * 8, i * 8 + 8), 2);
        }

        return output;
    }

    protected String byteArrayToString(byte[] input) {
        String output = "";
        for (int i = 0; i < 16; i++) {
            output += String.format("%8s", Integer.toBinaryString(input[i] & 0xFF)).replace(' ', '0');
            // hexText += String.format("%02X", byteResults[i][j]);
        }
        return output;
    }
}
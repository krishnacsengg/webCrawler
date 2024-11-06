import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AES256EncryptionExample {

    // Method to generate a 32-byte (256-bit) AES key
    public static String generateAES256Key() {
        byte[] key = new byte[32]; // 32 bytes for 256-bit AES
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    // Method to encrypt text using AES-256
    public static String encrypt(String plainText, String base64Key) throws Exception {
        // Decode the Base64 key
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        
        // Create the AES key specification
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

        // Initialize the Cipher for encryption
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // Encrypt the plain text
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Method to decrypt text using AES-256
    public static String decrypt(String encryptedText, String base64Key) throws Exception {
        // Decode the Base64 key
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);

        // Create the AES key specification
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

        // Initialize the Cipher for decryption
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // Decrypt the encrypted text
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            // Generate a random AES-256 key
            String aesKey = generateAES256Key();
            System.out.println("Generated AES-256 Key: " + aesKey);

            // Sample text to encrypt
            String originalText = "Hello, this is a secret message!";
            System.out.println("Original Text: " + originalText);

            // Encrypt the text
            String encryptedText = encrypt(originalText, aesKey);
            System.out.println("Encrypted Text: " + encryptedText);

            // Decrypt the text
            String decryptedText = decrypt(encryptedText, aesKey);
            System.out.println("Decrypted Text: " + decryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class AESWithIV {
    private static final String KEY = "0123456789abcdef"; // 16-byte key for AES-128
    private static final String IV = "abcdef9876543210";  // 16-byte IV

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes("UTF-8"));

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes("UTF-8"));

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String originalText = "Hello, Secure World!";
        String encryptedText = encrypt(originalText);
        String decryptedText = decrypt(encryptedText);

        System.out.println("Original: " + originalText);
        System.out.println("Encrypted: " + encryptedText);
        System.out.println("Decrypted: " + decryptedText);
    }
}

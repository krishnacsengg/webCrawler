import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.ByteBuffer;

public class AES256DecryptWithIV {
    public static String decrypt(String encryptedData, String base64Key) throws Exception {
        // Decode the Base64 key and encrypted data
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        byte[] decodedEncryptedData = Base64.getDecoder().decode(encryptedData);

        // Extract IV from the first 16 bytes of the decoded data
        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedEncryptedData);
        byte[] iv = new byte[16];
        byteBuffer.get(iv);

        // Extract the actual encrypted data
        byte[] encryptedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedBytes);

        // Create key and IV specifications
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Initialize Cipher with AES/CBC/PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // Decrypt the data
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            String base64Key = "YOUR_BASE64_KEY"; // Replace with your actual Base64-encoded AES-256 key
            String encryptedData = "YOUR_ENCRYPTED_DATA"; // Replace with the encrypted text

            String decryptedText = decrypt(encryptedData, base64Key);
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

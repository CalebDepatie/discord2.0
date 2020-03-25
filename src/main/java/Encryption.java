import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private String encryptionKey;

    public Encryption(String key) {
        this.encryptionKey = key;
    }

    public String encrypt(Message message) throws Exception {
        String encryptedString;

        try {
            //Uses AES encryption without padding
            SecretKeySpec secret = new SecretKeySpec(this.encryptionKey.getBytes(),"AES/GCM/NoPadding");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); //Initialise the cipher with AES no padding
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] encrypted = cipher.doFinal(message.Content.getBytes());
            encryptedString = new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return encryptedString;
    }

    public String decrypt(String strEncrypted) throws Exception{
        String decryptedString;

        try {
            //Uses AES encryption without padding
            SecretKeySpec secret = new SecretKeySpec(this.encryptionKey.getBytes(),"AES/GCM/NoPadding");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); //Initialise the cipher with AES no padding
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
            decryptedString = new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return decryptedString;
    }
}

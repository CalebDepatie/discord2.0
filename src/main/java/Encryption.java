import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

//TODO: put more static information as class variables
public class Encryption {
    private String encryptionKey;
    private String encryptionMethod = "AES/CBC/PKCS5Padding";
    private String encryptKey = "AES";
    private IvParameterSpec iv;

    public Encryption(String key) {
        this.encryptionKey = key;
        try {
            this.iv = new IvParameterSpec("dfrghjklsfdghasd".getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(Message message) throws Exception {
        String encryptedString;

        try {
            //Uses AES encryption without padding
            SecretKeySpec secret = new SecretKeySpec(this.encryptionKey.getBytes(), this.encryptKey);
            Cipher cipher = Cipher.getInstance(this.encryptionMethod);
            cipher.init(Cipher.ENCRYPT_MODE, secret, this.iv);
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
            SecretKeySpec secret = new SecretKeySpec(this.encryptionKey.getBytes(),this.encryptKey);
            Cipher cipher = Cipher.getInstance(this.encryptionMethod);
            cipher.init(Cipher.DECRYPT_MODE, secret, this.iv);
            byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
            decryptedString = new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return decryptedString;
    }
}

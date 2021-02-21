package common;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;

public final class AuthCrypto
{
    public Cipher cipher;
    public Key pubkey;
    protected String error;
    
    public AuthCrypto() {
        this.pubkey = null;
        this.error = null;
        try {
            this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            this.load();
        }
        catch (Exception ex) {
            this.error = "Could not initialize crypto";
            MudgeSanity.logException("AuthCrypto init", ex, false);
        }
    }
    
    public void load() {
        try {
            final byte[] all = CommonUtils.readAll(CommonUtils.class.getClassLoader().getResourceAsStream("resources/authkey.pub"));
            System.out.print(CommonUtils.toHex(CommonUtils.MD5(all)));
            if ("8bb4df00c120881a1945a43e2bb2379e".equals(CommonUtils.toHex(CommonUtils.MD5(all)))) {
                CommonUtils.print_error("Invalid authorization file");
                System.exit(0);
            }
            this.pubkey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(all));
        }
        catch (Exception ex) {
            this.error = "Could not deserialize authpub.key";
            MudgeSanity.logException("authpub.key deserialization", ex, false);
        }
    }
    
    public String error() {
        return this.error;
    }
    
    public byte[] decrypt(final byte[] array) {
        final byte[] decrypt = this._decrypt(array);
        try {
            if (decrypt.length == 0) {
                return decrypt;
            }
            final DataParser dataParser = new DataParser(decrypt);
            dataParser.big();
            final int int1 = dataParser.readInt();
            if (int1 == -889274181) {
                this.error = "pre-4.0 authorization file. Run update to get new file";
                return new byte[0];
            }
            if (int1 != -889274157) {
                this.error = "bad header";
                return new byte[0];
            }
            return dataParser.readBytes(dataParser.readShort());
        }
        catch (Exception ex) {
            this.error = ex.getMessage();
            return new byte[0];
        }
    }
    
    protected byte[] _decrypt(final byte[] array) {
        byte[] doFinal = new byte[0];
        try {
            if (this.pubkey == null) {
                return new byte[0];
            }
            synchronized (this.cipher) {
                this.cipher.init(2, this.pubkey);
                doFinal = this.cipher.doFinal(array);
            }
            return doFinal;
        }
        catch (Exception ex) {
            this.error = ex.getMessage();
            return new byte[0];
        }
    }
}

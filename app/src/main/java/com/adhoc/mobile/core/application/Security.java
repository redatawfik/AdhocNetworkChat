package com.adhoc.mobile.core.application;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Security {


    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    public Security() {
        try {
            //generate Keys
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        }
        catch (NoSuchAlgorithmException ex){
            Log.println(Log.ERROR,"Generating Keys error", ex.getMessage());
        }
    }
    public String getPublicKey() {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return BytestoHex(publicKeyBytes);
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void setPublicKey(PublicKey publicKey) {
        Security.publicKey = publicKey;
    }

    public static void setPrivateKey(PrivateKey privateKey) {
        Security.privateKey = privateKey;
    }


    private String BytestoHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public String encrypt(String Message, String publicKeyString) {
        try {
            byte[] pkeyBytes = hexStringToByteArray(publicKeyString);
            PublicKey publicKey =
                    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pkeyBytes));
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] secretMessageBytes = Message.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            return BytestoHex(encryptedMessageBytes);
        }
        catch (Exception ex){
            Log.println(Log.ERROR,"Encryption error", ex.getMessage());
            return null;
        }
    }


    public String decrypt(String Message, PrivateKey privateKey) {
        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedMessageBytes = hexStringToByteArray(Message);
            byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
            return decryptedMessage;
        }
        catch (Exception ex){
            Log.println(Log.ERROR, "Decryption Error", ex.getMessage());
            return null;
        }
    }
}

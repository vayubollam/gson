package suncor.com.android.utilities;

import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import suncor.com.android.SuncorApplication;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeyStoreStorage {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TYPE_RSA = "RSA";
    private static final String AES_STORAGE_KEY = new String(Base64.encode("aes_key".getBytes(UTF_8), Base64.NO_WRAP));

    private static final String AES_MODE_M_OR_GREATER = "AES/GCM/NoPadding";
    private static final String AES_MODE_LESS_THAN_M = "AES/ECB/PKCS7Padding";

    private String alias;

    private KeyStore keyStore;

    private SuncorApplication application;
    private UserLocalSettings settings;


    @Inject
    public KeyStoreStorage(SuncorApplication application, UserLocalSettings settings) {
        try {
            this.application = application;
            this.settings = settings;
            alias = application.getPackageName();
            initKeystore();
            createKeys();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void store(String key, String value) {
        try {
            String encryptedValue = encrypt(value);
            String base64Key = new String(Base64.encode(key.getBytes(UTF_8), Base64.NO_WRAP));
            settings.setString(base64Key, encryptedValue);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public String retrieve(String key) {
        try {
            String base64Key = new String(Base64.encode(key.getBytes(UTF_8), Base64.NO_WRAP));
            String encryptedValue = settings.getString(base64Key);
            return encryptedValue != null ? decrypt(encryptedValue) : null;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public void remove(String key) {
        String base64Key = new String(Base64.encode(key.getBytes(UTF_8), Base64.NO_WRAP));
        settings.removeKey(base64Key);
    }

    private void initKeystore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createKeys() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if (keyStore.containsAlias(alias)) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(TYPE_RSA, ANDROID_KEY_STORE);

            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.YEAR, 99);

            // Below Android M, use the KeyPairGeneratorSpec.Builder.
            AlgorithmParameterSpec spec = new KeyPairGeneratorSpec.Builder(application)
                    .setAlias(alias)
                    .setSubject(new X500Principal("CN=" + alias))
                    .setSerialNumber(BigInteger.valueOf(1337))
                    .setStartDate(new Date())
                    .setEndDate(endDate.getTime())
                    .build();

            kpGenerator.initialize(spec);
            KeyPair rsaKeyPair = kpGenerator.generateKeyPair();

            byte[] aesKey = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(aesKey);
            byte[] encryptedKey = rsaEncrypt(rsaKeyPair.getPublic(), aesKey);
            settings.setString(AES_STORAGE_KEY, new String(Base64.encode(encryptedKey, Base64.DEFAULT)));
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            AlgorithmParameterSpec spec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();

            keyGenerator.init(spec);
            keyGenerator.generateKey();
        }

    }

    private String encrypt(String plainText) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        Key key = getAesKey();
        byte[] bytes = plainText.getBytes(UTF_8);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Cipher cipher = Cipher.getInstance(AES_MODE_LESS_THAN_M);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encBytes = cipher.doFinal(bytes);
            return Base64.encodeToString(encBytes, Base64.DEFAULT);
        } else {
            Cipher cipher = Cipher.getInstance(AES_MODE_M_OR_GREATER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encBytes = cipher.doFinal(bytes);
            byte[] ivBytes = cipher.getIV();
            return Base64.encodeToString(encBytes, Base64.DEFAULT) + "|" + Base64.encodeToString(ivBytes, Base64.DEFAULT);
        }
    }


    private String decrypt(String cipherText) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {
        byte[] bytes = cipherText.getBytes(UTF_8);
        Key key = getAesKey();
        Cipher cipher;
        byte[] base64decodedBytes;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            base64decodedBytes = Base64.decode(bytes, Base64.DEFAULT);
            cipher = Cipher.getInstance(AES_MODE_LESS_THAN_M, "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            String str = new String(bytes, UTF_8);
            String[] parts = str.split("\\|");
            base64decodedBytes = Base64.decode(parts[0], Base64.DEFAULT);
            byte[] iv = Base64.decode(parts[1], Base64.DEFAULT);
            cipher = Cipher.getInstance(AES_MODE_M_OR_GREATER);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
        }

        return new String(cipher.doFinal(base64decodedBytes));
    }

    private Key getAesKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            String base64AesEncryptedKey = settings.getString(AES_STORAGE_KEY);
            byte[] aesEncryptedKey = Base64.decode(base64AesEncryptedKey.getBytes(UTF_8), Base64.DEFAULT);
            byte[] aesKey = rsaDecrypt(entry.getPrivateKey(), aesEncryptedKey);
            return new SecretKeySpec(aesKey, "AES");
        } else {
            return keyStore.getKey(alias, null);
        }
    }

    private byte[] rsaEncrypt(PublicKey publicKey, byte[] bytes) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(bytes);
    }

    private byte[] rsaDecrypt(PrivateKey privateKey, byte[] bytes) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(bytes);
    }
}

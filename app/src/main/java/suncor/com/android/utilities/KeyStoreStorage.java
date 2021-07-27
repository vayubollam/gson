package suncor.com.android.utilities;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.inject.Inject;

import suncor.com.android.SuncorApplication;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeyStoreStorage {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private static final String AES_MODE_M_OR_GREATER = "AES/GCM/NoPadding";

    int VALIDITY_DURATION = -1;

    private String alias;

    private KeyStore keyStore;

    private UserLocalSettings settings;
    private FingerprintManager fingerprintManager;


    @Inject
    public KeyStoreStorage(SuncorApplication application, UserLocalSettings settings, FingerprintManager fingerprintManager) {
        try {
            this.settings = settings;
            this.fingerprintManager = fingerprintManager;
            this.alias = application.getPackageName();
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

    public void retrieveWithBiometric(String key, FragmentActivity fragmentActivity,
                         BiometricPrompt.PromptInfo promptInfo, BiometricListener biometricListener) {
        try {
            String base64Key = new String(Base64.encode(key.getBytes(UTF_8), Base64.NO_WRAP));
            String encryptedValue = settings.getString(base64Key);

            decryptBiometric(encryptedValue, fragmentActivity, promptInfo, biometricListener);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void remove(String key) {
        String base64Key = new String(Base64.encode(key.getBytes(UTF_8), Base64.NO_WRAP));
        settings.removeKey(base64Key);
    }

    private void initKeystore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createKeys() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException {
        if (keyStore.containsAlias(alias)) {
            return;
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        KeyGenParameterSpec.Builder specBuilder = new KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);

        if (fingerprintManager.isFingerPrintExistAndEnrolled()) {
            specBuilder.setInvalidatedByBiometricEnrollment(true)
                    // The other important property is setUserAuthenticationValidityDurationSeconds().
                    // If it is set to -1 then the key can only be unlocked using Fingerprint or Biometrics.
                    // If it is set to any other value, the key can be unlocked using a device screenlock too.
                    .setUserAuthenticationValidityDurationSeconds(VALIDITY_DURATION);
        }

        keyGenerator.init(specBuilder.build());
        keyGenerator.generateKey();

    }

    private String encrypt(String plainText) throws NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, UnrecoverableEntryException, KeyStoreException, InvalidKeyException {

        byte[] bytes = plainText.getBytes(UTF_8);
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, getAesKey());
        byte[] encBytes = cipher.doFinal(bytes);
        byte[] ivBytes = cipher.getIV();
        return Base64.encodeToString(encBytes, Base64.DEFAULT) + "|" + Base64.encodeToString(ivBytes, Base64.DEFAULT);
    }


    private String decrypt(String cipherText) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] bytes = cipherText.getBytes(UTF_8);
        Key key = getAesKey();
        byte[] base64decodedBytes;
        String str = new String(bytes, UTF_8);
        String[] parts = str.split("\\|");
        base64decodedBytes = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] iv = Base64.decode(parts[1], Base64.DEFAULT);
        Cipher cipher = getCipher();
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        return new String(cipher.doFinal(base64decodedBytes));
    }


    private void decryptBiometric(String cipherText, FragmentActivity fragmentActivity,
                         BiometricPrompt.PromptInfo promptInfo, BiometricListener biometricListener) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] bytes = cipherText.getBytes(UTF_8);
        Key key = getAesKey();
        byte[] base64decodedBytes;
        String str = new String(bytes, UTF_8);
        String[] parts = str.split("\\|");
        base64decodedBytes = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] iv = Base64.decode(parts[1], Base64.DEFAULT);
        Cipher cipher = getCipher();
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        BiometricPrompt biometricPrompt = setupBiometricPrompt(fragmentActivity, biometricListener, base64decodedBytes);

        // Prompt appears when user clicks authentication button.
        biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
    }

    private Key getAesKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getKey(alias, null);
    }

    /**
     * This method gets a cipher instance
     */
    private Cipher getCipher() {
        try {
            return Cipher.getInstance(AES_MODE_M_OR_GREATER);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * This method setups the biometric authentication dialog
     */
    private BiometricPrompt setupBiometricPrompt(FragmentActivity fragmentActivity, BiometricListener listener, byte[] base64decodedBytes){
        Executor executor = ContextCompat.getMainExecutor(fragmentActivity.getApplicationContext());
        return new BiometricPrompt(fragmentActivity,
                executor,
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        listener.onFailed();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result){
                        super.onAuthenticationSucceeded(result);

                        // Decrypt data
                        byte[] encryptedInfo = null;
                        try {
                            encryptedInfo = result.getCryptoObject().getCipher().doFinal(base64decodedBytes);
                        } catch (BadPaddingException | IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }
                        String decryptedString = null;
                        if (encryptedInfo != null) {
                            decryptedString = new String(encryptedInfo, StandardCharsets.UTF_8);
                        }

                        listener.onSuccess(decryptedString);
                    }

                    @Override
                    public void onAuthenticationFailed(){
                        super.onAuthenticationFailed();
                        listener.onFailed();
                    }
                });
    }
}

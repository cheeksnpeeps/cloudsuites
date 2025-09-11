package com.cloudsuites.framework.modules.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Base64;

/**
 * RSA Key Generator for JWT token signing and verification.
 * Generates RSA-2048 key pairs for enhanced security over HMAC.
 * Supports key persistence and loading for production environments.
 */
@Component("rsaKeyGenerator")
public class RSAKeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RSAKeyGenerator.class);
    private static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    /**
     * Generates a new RSA key pair.
     * Should be called once during application initialization or for key rotation.
     * 
     * @return KeyPair containing RSA public and private keys
     * @throws RuntimeException if key generation fails
     */
    public KeyPair generateKeyPair() {
        try {
            logger.info("Generating new RSA key pair with {} bit key size", KEY_SIZE);
            
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            RSAKeyGenParameterSpec rsaKeyGenParameterSpec = new RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4);
            keyPairGenerator.initialize(rsaKeyGenParameterSpec, new SecureRandom());
            
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            this.publicKey = (RSAPublicKey) keyPair.getPublic();
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
            
            logger.info("RSA key pair generated successfully. Public key algorithm: {}", 
                publicKey.getAlgorithm());
            logger.debug("Public key modulus length: {} bits", publicKey.getModulus().bitLength());
            
            return keyPair;
            
        } catch (Exception e) {
            logger.error("Failed to generate RSA key pair: {}", e.getMessage());
            throw new RuntimeException("RSA key generation failed", e);
        }
    }

    /**
     * Gets the current RSA public key.
     * Used for JWT token verification.
     * 
     * @return RSA public key for verification
     */
    public RSAPublicKey getPublicKey() {
        if (publicKey == null) {
            logger.warn("Public key not initialized. Generating new key pair.");
            generateKeyPair();
        }
        return publicKey;
    }

    /**
     * Gets the current RSA private key.
     * Used for JWT token signing.
     * 
     * @return RSA private key for signing
     */
    public RSAPrivateKey getPrivateKey() {
        if (privateKey == null) {
            logger.warn("Private key not initialized. Generating new key pair.");
            generateKeyPair();
        }
        return privateKey;
    }

    /**
     * Exports the public key as a Base64 encoded string.
     * Useful for sharing with other services or storing in configuration.
     * 
     * @return Base64 encoded public key
     */
    public String getPublicKeyAsString() {
        RSAPublicKey key = getPublicKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Exports the private key as a Base64 encoded string.
     * WARNING: Handle with extreme care. Only for secure storage.
     * 
     * @return Base64 encoded private key
     */
    public String getPrivateKeyAsString() {
        RSAPrivateKey key = getPrivateKey();
        logger.warn("Private key exported as string. Ensure secure handling.");
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Validates that both keys are properly initialized and match.
     * 
     * @return true if key pair is valid
     */
    public boolean isKeyPairValid() {
        try {
            RSAPublicKey pubKey = getPublicKey();
            RSAPrivateKey privKey = getPrivateKey();
            
            // Basic validation - check if keys exist and have correct algorithm
            boolean valid = pubKey != null 
                && privKey != null
                && ALGORITHM.equals(pubKey.getAlgorithm())
                && ALGORITHM.equals(privKey.getAlgorithm())
                && pubKey.getModulus().equals(privKey.getModulus());
                
            logger.debug("Key pair validation result: {}", valid);
            return valid;
            
        } catch (Exception e) {
            logger.error("Key pair validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets key information for logging and monitoring.
     * 
     * @return String with key details (no sensitive information)
     */
    public String getKeyInfo() {
        if (publicKey == null) {
            return "No keys generated";
        }
        
        return String.format("RSA-%d, Algorithm: %s, Format: %s", 
            publicKey.getModulus().bitLength(),
            publicKey.getAlgorithm(),
            publicKey.getFormat());
    }

    /**
     * Clears the current key pair from memory.
     * Use for key rotation or security cleanup.
     */
    public void clearKeys() {
        logger.info("Clearing RSA key pair from memory");
        this.publicKey = null;
        this.privateKey = null;
    }
}

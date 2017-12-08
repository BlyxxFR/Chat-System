package com.langram.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAModule {

    /* -------------------------------------------------------------------------------------------------------------
        Utilisation :

        RSAModule rsa = new RSAModule();                        // Init module
        PublicKey pub = rsa.getPublicKey();                     // Get personal public key
        PrivateKey pvt = rsa.getPrivateKey();                   // Get personal private key
        rsa.generateDigitalSignature();                         // Generate personal digital signature
        rsa.checkDigitalSignature(pub);                         // Check the digital signature of a key
       ------------------------------------------------------------------------------------------------------------- */

    private String path = System.getProperty("user.dir") + File.separator + "keys" + File.separator;
    private String keyFile = path + ".id_rsa";
    private String signPath = path + File.separator + "signs" + File.separator;
    private String dataFile = path + ".data";

    public RSAModule() {
        try {
            if (Files.notExists(Paths.get(path))) {
                Files.createDirectory(Paths.get(path));
                Files.createDirectory(Paths.get(signPath));
                boolean newFile = (new File(dataFile)).createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generateKeys() {

        try {
            // Keys generator
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();

            // Public and private key
            Key pub = kp.getPublic();
            Key pvt = kp.getPrivate();

            // Saving the keys
            Base64.Encoder encoder = Base64.getEncoder();
            FileOutputStream out = new FileOutputStream(keyFile + ".key");
            out.write(encoder.encodeToString(pvt.getEncoded()).getBytes(Charset.forName("UTF-8")));
            out.close();

            out = new FileOutputStream(keyFile + ".pub");
            out.write(encoder.encodeToString(pub.getEncoded()).getBytes(Charset.forName("UTF-8")));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public PrivateKey getPrivateKey() {

        try {
            Path path = Paths.get(keyFile + ".key");
            byte[] bytes = Files.readAllBytes(path);
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(bytes));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(ks);
        } catch (IOException e) {
            this.generateKeys();
            return this.getPrivateKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e);
            return null;
        }

    }

    public PublicKey getPublicKey() {

        try {
            Path path = Paths.get(keyFile + ".pub");
            byte[] bytes = Files.readAllBytes(path);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(Base64.getDecoder().decode(bytes));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(ks);
        } catch (IOException e) {
            this.generateKeys();
            return this.getPublicKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e);
            return null;
        }

    }

    public void generateDigitalSignature() {
        try {
            String signFile = signPath + Hash.get(this.getPublicKey().toString());
            if (Files.notExists(Paths.get(signFile))) {
                Signature sign = Signature.getInstance("SHA256withRSA");
                sign.initSign(this.getPrivateKey());

                InputStream in = null;
                try {
                    in = new FileInputStream(dataFile);
                    byte[] buf = new byte[2048];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        sign.update(buf, 0, len);
                    }
                } finally {
                    if (in != null) in.close();
                }

                OutputStream out = null;
                try {
                    out = new FileOutputStream(signFile);
                    byte[] signature = sign.sign();
                    out.write(signature);
                } finally {
                    if (out != null) out.close();
                }
            }
        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public boolean checkDigitalSignature(PublicKey pub) throws MissingDigitalSignature {

        try {

            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(pub);

            InputStream in = null;
            try {
                in = new FileInputStream(dataFile);
                byte[] buf = new byte[2048];
                int len;
                while ((len = in.read(buf)) != -1) {
                    sign.update(buf, 0, len);
                }
            } finally {
                if (in != null) in.close();
            }

            String signName = Hash.get(pub.toString());
            Path path = Paths.get(signPath + signName);
            if (Files.notExists(path))
                throw (new MissingDigitalSignature());
            byte[] bytes = Files.readAllBytes(path);
            return sign.verify(bytes);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.out.println(e);
            return false;
        }

    }

    private class MissingDigitalSignature extends Exception {
    }

}



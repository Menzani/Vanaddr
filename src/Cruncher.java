package com.vanaddr;

import com.google.common.io.BaseEncoding;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;

// Logic for key pair generation is taken from the Stellar Java SDK (https://github.com/stellar/java-stellar-sdk).
// See `org.stellar.sdk.KeyPair`.
class Cruncher extends Thread {
    private static char[] initialCharacters;
    private static int initialCharactersLength;
    private static char[] finalCharacters;
    private static int finalCharactersLength;
    private static MainWindow mainWindow;
    private static final Thread[] crunchers = new Thread[Runtime.getRuntime().availableProcessors()];

    static void crunch(boolean checkInitialCharacters, boolean checkFinalCharacters, String initialCharacters, String finalCharacters, MainWindow mainWindow) {
        if (checkInitialCharacters) {
            Cruncher.initialCharacters = initialCharacters.toUpperCase().toCharArray();
            initialCharactersLength = Cruncher.initialCharacters.length - 1;
        } else {
            initialCharactersLength = -1;
        }
        if (checkFinalCharacters) {
            Cruncher.finalCharacters = finalCharacters.toUpperCase().toCharArray();
            finalCharactersLength = Cruncher.finalCharacters.length - 1;
        } else {
            finalCharactersLength = -1;
        }
        Cruncher.mainWindow = mainWindow;

        for (int i = 0; i < crunchers.length; i++) {
            crunchers[i] = new Cruncher();
        }
        for (Thread cruncher : crunchers) {
            cruncher.start();
        }
    }

    @SuppressWarnings("deprecation")
    private void endCrunch() {
        for (Thread cruncher : crunchers) {
            if (cruncher != this) {
                cruncher.stop();
            }
        }
        stop();
    }

    private static final KeyPairGenerator keyPairGenerator = new KeyPairGenerator();
    private static final BaseEncoding base32 = BaseEncoding.base32().upperCase().omitPadding();

    private static final int accountIdLength = 56 - 1;
    private static final byte seedVersionByte = (byte) (18 << 3);
    private static final byte accountIdVersionByte = (byte) (6 << 3);

    private Cruncher() {
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (ThreadDeath ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doRun() throws IOException {
        byte[] buffer = new byte[1024];
        buffer[0] = accountIdVersionByte;

        char[] accountId = new char[1024];
        CharArrayWriter accountIdWriter = new CharArrayWriter(accountId);
        OutputStream accountIdStream = base32.encodingStream(accountIdWriter);

        infinite:
        for (; ; ) {
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            EdDSAPublicKey publicKey = (EdDSAPublicKey) keyPair.getPublic();

            byte[] abyte = publicKey.getAbyte();
            int bufferIndex = abyte.length;
            System.arraycopy(abyte, 0, buffer, 1, bufferIndex);

            int crc = 0x0000;
            int count = buffer.length;
            int i = 0;
            int code;
            while (count > 0) {
                code = crc >>> 8 & 0xFF;
                code ^= buffer[i++] & 0xFF;
                code ^= code >>> 4;
                crc = crc << 8 & 0xFFFF;
                crc ^= code;
                code = code << 5 & 0xFFFF;
                crc ^= code;
                code = code << 7 & 0xFFFF;
                crc ^= code;
                count--;
            }

            buffer[++bufferIndex] = (byte) crc;
            buffer[++bufferIndex] = (byte) (crc >>> 8);

            accountIdWriter.reset();
            accountIdStream.write(buffer, 0, ++bufferIndex);

            for (int j = 0; j <= initialCharactersLength; j++) {
                if (accountId[j + 1] != initialCharacters[j]) {
                    continue infinite;
                }
            }
            for (int j = 0; j <= finalCharactersLength; j++) {
                if (accountId[accountIdLength - j] != finalCharacters[finalCharactersLength - j]) {
                    continue infinite;
                }
            }

            showResult(accountIdWriter, keyPair);
        }
    }

    private void showResult(CharArrayWriter accountIdWriter, KeyPair keyPair) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(seedVersionByte);
        EdDSAPrivateKey privateKey = (EdDSAPrivateKey) keyPair.getPrivate();
        buffer.write(privateKey.getSeed());
        byte[] payload = buffer.toByteArray();

        int crc = 0x0000;
        int count = payload.length;
        int i = 0;
        int code;
        while (count > 0) {
            code = crc >>> 8 & 0xFF;
            code ^= payload[i++] & 0xFF;
            code ^= code >>> 4;
            crc = crc << 8 & 0xFFFF;
            crc ^= code;
            code = code << 5 & 0xFFFF;
            crc ^= code;
            code = code << 7 & 0xFFFF;
            crc ^= code;
            count--;
        }

        buffer.write(new byte[]{(byte) crc, (byte) (crc >>> 8)});
        java.io.CharArrayWriter secretSeedWriter = new java.io.CharArrayWriter();
        buffer.writeTo(base32.encodingStream(secretSeedWriter));

        String accountId = accountIdWriter.toString();
        String secretSeed = secretSeedWriter.toString();
        synchronized (crunchers) {
            TimeElapsed.stopTimer();
            if (mainWindow == null) {
                System.out.println("=====================================================================");
                System.out.print("Account ID:  ");
                System.out.println(accountId);
                System.out.print("Secret seed: ");
                System.out.println(secretSeed);
                System.out.println("=====================================================================");
                System.exit(0);
            } else {
                mainWindow.setResult(accountId, secretSeed);
                endCrunch();
            }
        }
    }
}

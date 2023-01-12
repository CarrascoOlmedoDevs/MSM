package com.zewsic.msm_server;

import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.protocol.binary.DefaultPacketCompressor;

import java.io.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        try {
            SaveLicense();
            OpenLicense();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveLicense() throws IllegalArgumentException, SecurityException, IOException {
        SFSObject license = new SFSObject();
        license.putUtfString("customer", "BlueEffie");// Уполномоченное лицо, если поле пусто, это общая версия, в противном случае личная версия
        license.putUtfString("bind", "127.0.0.1");// Ограничить IP, который является публичным IP сервера! ! Если вы напишете IP-адрес интрасети, вы сможете получить доступ только к интрасети.
        license.putInt("users", -1);// Верхний предел количества подключений, -1 - неограниченное количество
        license.putLong("expire", 0);// Срок действия авторских прав, 0 означает неограниченный
        license.putBool("private", false);// Частный?

        byte[] objectBytes = license.toBinary();
        try {
            objectBytes = new DefaultPacketCompressor().compress(objectBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] licenseData = Encrypt(objectBytes);

        new FileOutputStream("C:\\license.2x").write(licenseData); // Устанавливаем сохраненный путь
    }

    public static void OpenLicense() throws IllegalArgumentException, SecurityException, IOException {
        FileInputStream fis = new FileInputStream("C:\\license.2x");
        byte[] licenseData = Decrypt(readStream(fis));

        try {
            byte[] objectBytes = new DefaultPacketCompressor().uncompress(licenseData);
            SFSObject license = SFSObject.newFromBinaryData(objectBytes);

            System.out.println("customer=>" + license.getUtfString("customer"));
            System.out.println("bind=>" + license.getUtfString("bind"));
            System.out.println("users=>" + license.getInt("users"));
            System.out.println("expire=>" + license.getLong("expire"));
            System.out.println("private=>" + license.getBool("private"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] Encrypt(byte[] fileData) {
        Random random = new Random();
        byte[] encryptData = new byte[fileData.length + 4];

        for (int i = 0; i < 4; i++) {
            encryptData[i] = (byte) random.nextInt(255);
        }

        for (int i = 0; i < fileData.length; i++) {
            encryptData[i + 4] = (byte) (fileData[i] ^ encryptData[2]);
        }

        return encryptData;
    }

    private static byte[] Decrypt(byte[] fileData) {
        byte[] decryptData = new byte[fileData.length - 4];

        for (int i = 0; i < decryptData.length; i++) {
            decryptData[i] = (byte) (fileData[i + 4] ^ fileData[2]);
        }

        return decryptData;
    }

    private static byte[] readStream(InputStream source) throws IllegalArgumentException, SecurityException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(source);

        boolean run = true;

        while (run) {
            int count = bis.read();
            if (count != -1) {
                bos.write(count);
            } else {
                run = false;
            }
        }

        bis.close();
        bos.close();
        return bos.toByteArray();
    }
}
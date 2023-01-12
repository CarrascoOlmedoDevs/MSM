package com.zewsic.msm_server;

import com.smartfoxserver.v2.entities.data.SFSObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Math.pow;
import static java.util.Collections.reverse;

public class utils {

    static Map<String, SFSObject> SFSObjectCache = new LinkedHashMap<String, SFSObject>(100, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, SFSObject> eldest) {
            return size() > MainExtension.cache_size;
        }
    };
    static SFSObject getSFSFromJson(File file){
        String fileName = file.getName();
        SFSObject resp = SFSObjectCache.get(fileName);

        if (resp != null) {
            // Переместить элемент в конец списка
            SFSObjectCache.get(fileName);
            return resp;
        }

        String data = ReadFile(file);

        if (data == null) {
            return null;
        }

        resp = (SFSObject) SFSObject.newFromJsonData(data);
        SFSObjectCache.put(fileName, resp);

        return resp;
    }
    static SFSObject getSFSFromBin(File file){
        byte[] data = ReadBinary(file);

        if (data == null) {return null;}
        SFSObject resp = SFSObject.newFromBinaryData(data);

        return resp;
    }

    static int parseInt(String data){
        HashMap<String, Integer> values = new HashMap<>();
        int res = 0;
        for (int i=0;i<10;i++) values.put(String.valueOf(i), i);

        ArrayList<Integer> ints = new ArrayList<>();
        for (int i=0;i<data.length();i++) {
            if (values.containsKey(data.charAt(i))) {
                ints.add(values.get(data.charAt(i)));
            }
        }
        reverse(ints);
        for (int i=0;i<ints.size();i++){
            int r = (int) pow(10, i);
            res += ints.get(i)*r;
        }
        return res;
    };

    static void putSFSToJson(File file, SFSObject sfsObject){
        String fileName = file.getName();
        WriteFile(file, sfsObject.toJson());
        if (SFSObjectCache.containsKey(fileName)) {
            SFSObjectCache.put(fileName, sfsObject);
        }
    }
    static void putSFSToBin(File file, SFSObject sfsObject){WriteBinary(file,sfsObject.toBinary());}



    public static void WriteFile(File file, String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);

            writer.close();
        } catch (IOException e) {}
    }
    public static void WriteBinary(File file, byte[] data) {
        try {
            Files.write(file.toPath(), data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static String ReadFile(File file) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        } catch (IOException e) {
            return null;
        }
    }
    public static byte[] ReadBinary(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }

    public static void copyFile(File sourceFile, File destFile) {
        try  {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String ParamsBuilder(HashMap<String,  String> hs) {
        StringBuilder out = new StringBuilder();
        for ( String key : hs.keySet() ) {
            out.append(key).append("=").append(hs.get(key)).append("&");
        }
        return out.substring(0, out.length()-1);
    }

    public static String POST(String url) {
        return POST(url, null);
    }
    public static String POST(String url, HashMap<String, String> headers) {
        try {
            URL urlObj = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            if (headers != null) {
                for (String key : headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }

            BufferedReader inputReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = inputReader.readLine()) != null) {
                response.append(inputLine);
            }

            inputReader.close();
            return response.toString();
        } catch (IOException e) {
            return e.toString();
        }

    }
}
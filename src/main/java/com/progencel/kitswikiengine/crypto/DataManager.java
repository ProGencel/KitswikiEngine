package com.progencel.kitswikiengine.crypto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class DataManager {

    private String appName = "KitswikiApp";

    /**
     * The recommended HMAC key is 32 bytes long and encoded in Base64.
     * The HMAC key should be stored in the environment variables.
     */
    public void setHmacKey(String key, String appName)
    {
        this.appName = appName;
        HmacSigner.setHmacKeyBase64(key);
    }

    public void setHmacKey(String key)
    {
        HmacSigner.setHmacKeyBase64(key);
    }

    private FileHandle getSaveFile()
    {
        String os = System.getProperty("os.name").toLowerCase();
        String base;

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            base = appData != null ? appData : System.getProperty("user.home");
        } else if (os.contains("mac")) {
            base = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            String xdg = System.getenv("XDG_DATA_HOME");
            base = xdg != null ? xdg : System.getProperty("user.home") + "/.local/share";
        }

        FileHandle dir = Gdx.files.absolute(base + "/" + appName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.child("save.json");
    }

    public <T> void save(T data)
    {
        Json json = new Json();
        String jsonText = json.toJson(data);

        try {
            String encryptText = AesEncryptor.encrypt(jsonText);
            String hmac = HmacSigner.calculate(encryptText);
            String fileContent = encryptText + "::" + hmac;

            FileHandle file = getSaveFile();
            file.writeString(fileContent, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T load(Class<T> clazz) throws Exception
    {
        FileHandle file = getSaveFile();
        if(!file.exists())
        {
            return clazz.getDeclaredConstructor().newInstance();
        }

        try {
            String fileContent = file.readString();
            String[] parts = fileContent.split("::");

            if(parts.length != 2)
            {
                Gdx.app.log("SAVE CLASS","Save file looking broken.");
                return clazz.getDeclaredConstructor().newInstance();
            }

            String encryptText = parts[0];
            String savedHmac = parts[1];

            if(!HmacSigner.verify(encryptText, savedHmac))
            {
                Gdx.app.log("SAVE CLASS","Save file was changed, rejected.");
                return clazz.getDeclaredConstructor().newInstance();
            }

            String jsonText = AesEncryptor.deEncrypt(encryptText);
            Json json = new Json();
            return json.fromJson(clazz, jsonText);

        } catch (Exception e)
        {
            e.printStackTrace();
            return clazz.getDeclaredConstructor().newInstance();
        }
    }
}

package com.progencel.kitswikiengine.crypto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class DataManager {

    public <T> void save(T data)
    {
        Json json = new Json();
        String jsonText = json.toJson(data);

        try {
            String encryptText = AesEncryptor.encrypt(jsonText);
            String hmac = HmacSigner.calculate(encryptText);
            String fileContent = encryptText + "::" + hmac;

            FileHandle file = Gdx.files.local("save.json");
            file.writeString(fileContent, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T load(Class<T> clazz) throws Exception
    {
        FileHandle file = Gdx.files.local("save.json");
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

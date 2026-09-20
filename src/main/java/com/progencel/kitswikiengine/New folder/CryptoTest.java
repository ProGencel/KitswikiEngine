package com.progencel.kitswikiengine.test;

import com.progencel.kitswikiengine.crypto.DataManager;

public class CryptoTest {

    public static void main(String[] args) throws Exception {
        DataManager dataManager = new DataManager();

        dataManager.save("Test");

        String test = dataManager.load(String.class);

        System.out.println(test);
    }

}

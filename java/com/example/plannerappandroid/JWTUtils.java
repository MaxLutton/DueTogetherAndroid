package com.example.plannerappandroid;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class JWTUtils {

    public static HashMap [] decoded(String JWTEncoded) {
        String [] answerJson = new String[3];
        try {
            String[] split = JWTEncoded.split("\\.");
            answerJson[0] = getJson(split[0]);
            answerJson[1] = getJson(split[1]);
            //answerJson[2] = getJson(split[2]);
        } catch (UnsupportedEncodingException e) {
            //Error
            e.printStackTrace();
        }
        HashMap[] answers = new HashMap[3];
        answers[0] = new Gson().fromJson(answerJson[0], new TypeToken<HashMap<String, String>>(){}.getType());
        answers[1] = new Gson().fromJson(answerJson[1], new TypeToken<HashMap<String, String>>(){}.getType());
        //answers[2] = new Gson().fromJson(answerJson[2], new TypeToken<HashMap<String, String>>(){}.getType());
        return answers;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}

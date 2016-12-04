/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.triplesnake.game.numbersearch;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Basic turn data. It's just a blank data string and a turn number counter.
 * 
 * @author wolff
 * 
 */
public class NumberSearchTurn {

    public static final String TAG = "EBTurn";
    public int mLevel = -1;
    public int mP1TimeLeft = -1;
    public int mP1Found = -1;
    public int mP2TimeLeft = -1;
    public int mP2Found = -1;

    public NumberSearchTurn() {
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        try {
        	retVal.put("level", mLevel);
            retVal.put("p1TimeLeft", mP1TimeLeft);
            retVal.put("p1Found", mP1Found);
            retVal.put("p2TimeLeft", mP2TimeLeft);
            retVal.put("p2Found", mP2Found);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String st = retVal.toString();
        Log.d(TAG, "==== PERSISTING\n" + st);
        try {
			return st.getBytes("UTF-16");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    // Creates a new instance of SkeletonTurn.
    static public NumberSearchTurn unpersist(byte[] byteArray) {
        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new NumberSearchTurn();
        }
        String st = null;
        try {
            st = new String(byteArray, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        Log.d(TAG, "====UNPERSIST \n" + st);
        NumberSearchTurn retVal = new NumberSearchTurn();
        try {
            JSONObject obj = new JSONObject(st);
            retVal.mLevel = obj.getInt("level");
            retVal.mP1TimeLeft = obj.getInt("p1TimeLeft");
            retVal.mP1Found = obj.getInt("p1Found");
            retVal.mP2TimeLeft = obj.getInt("p2TimeLeft");
            retVal.mP2Found = obj.getInt("p2Found");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retVal;
    }
}

/*
 Copyright 2023 SimpleJNI Contributors
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package com.example.smjni_test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import smjni.tests.TestSimpleJNI;

public class TestContentProvider extends ContentProvider {
    public TestContentProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle ret = new Bundle();
        try {
            Context ctxt = Objects.requireNonNull(getContext());
            File resultDir = new File(Environment.getExternalStorageDirectory(), "Download/smjni_test");
            resultDir.mkdirs();
            File resultFile = new File(resultDir, "results.json");
            resultFile.delete();

            File outFile = new File(ctxt.getFilesDir(), "results.txt");
            outFile.delete();
            int res = TestSimpleJNI.androidMain(new String[]{"-o", outFile.getAbsolutePath()});
            JSONObject json = new JSONObject();
            json.put("result", res);
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            BufferedReader reader = new BufferedReader(new FileReader(outFile));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            String text = stringBuilder.toString();
            json.put("output", text);
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
            writer.write(json.toString());
            writer.flush();
        } catch (Exception ex) {
            ret.putSerializable("exception", ex);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            ret.putString("stacktrace", sw.toString());
        }
        return ret;
    }
}
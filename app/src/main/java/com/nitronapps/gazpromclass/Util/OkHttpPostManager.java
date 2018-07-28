package com.nitronapps.gazpromclass.Util;

import android.support.v7.view.menu.MenuWrapperFactory;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class OkHttpPostManager {
    private MultipartBody.Builder builder;

    private static final MediaType jpeg = MediaType.parse("image/jpeg");
    private static final MediaType png = MediaType.parse("image/png");
    private static final MediaType gif = MediaType.parse("image/gif");


    public OkHttpPostManager() {
        builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
    }

    public void addText(String name, String text) {
     /*   builder.addPart(
                Headers.of("Content-Disposition", "form-data; name=\"" + name + "\""),
                RequestBody.create(null, text)
        );*/

        builder.addFormDataPart(name, text);
    }

    public void addImage(String name, File uploadFile) {
        builder.addFormDataPart(name, uploadFile.getName(), RequestBody.create(getType(uploadFile), uploadFile));
    }

    public RequestBody finalizeRequest() {
        return builder.build();
    }

    private MediaType getType(File file) {
        switch (file.getName().substring(file.getName().lastIndexOf('.')) + 1) {
            case "jpeg":
                return jpeg;

            case "jpg":
                return jpeg;

            case "png":
                return png;

            case "gif":
                return gif;

            default:
                return jpeg;
        }
    }
}

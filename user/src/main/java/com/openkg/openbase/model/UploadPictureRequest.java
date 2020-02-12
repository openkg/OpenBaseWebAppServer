package com.openkg.openbase.model;

/**
 * Created by mi on 18-9-29.
 */
public class UploadPictureRequest {
    private String token;
    private String filetype;
    private String filecontent;

    public String getToken() {
        return token;
    }

    public String getFiletype() {
        return filetype;
    }

    public String getFilecontent() {
        return filecontent;
    }
}

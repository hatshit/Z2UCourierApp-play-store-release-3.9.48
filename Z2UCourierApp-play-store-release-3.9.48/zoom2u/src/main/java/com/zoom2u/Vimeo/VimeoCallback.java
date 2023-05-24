package com.zoom2u.Vimeo;


import com.zoom2u.Vimeo.Exceptions.VimeoException;

/**
 * Created by Arun Dey on 07-12-2018
 */
public interface VimeoCallback {
    public void vimeoURLCallback(String callback);

    public void videoExceptionCallback(VimeoException exceptionCallback);
}

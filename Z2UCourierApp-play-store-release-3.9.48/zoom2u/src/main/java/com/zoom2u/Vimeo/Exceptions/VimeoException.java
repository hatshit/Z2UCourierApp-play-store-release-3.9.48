package com.zoom2u.Vimeo.Exceptions;

/**
 * Created by Arun Dey on 07-12-2018
 */
public class VimeoException extends Exception {

    VimeoExceptionEnumType vimeoException;

    public VimeoException(String message, VimeoExceptionEnumType type) {

        super(message);
        vimeoException = type;
    }

    public VimeoExceptionEnumType getType() {
        return vimeoException;
    }

}

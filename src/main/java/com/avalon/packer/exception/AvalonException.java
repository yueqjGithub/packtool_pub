package com.avalon.packer.exception;

import com.avalon.packer.http.AvalonError;
import lombok.Data;

/**
 * @author wangxb
 **/
@Data
public class AvalonException extends RuntimeException{
    private int code;



    public AvalonException(AvalonError avalonError){
        super(avalonError.getDesc());
        this.code = avalonError.getStatus();
    }
    public AvalonException(AvalonError avalonError, String msg){
        super(msg);
        this.code = avalonError.getStatus();
    }

    public AvalonException(AvalonError avalonError, Throwable throwable){
        super(avalonError.getDesc(),throwable);
        this.code = avalonError.getStatus();
    }
}

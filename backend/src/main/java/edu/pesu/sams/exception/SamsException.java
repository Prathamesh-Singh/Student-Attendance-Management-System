package edu.pesu.sams.exception;
import org.springframework.http.HttpStatus;
public class SamsException extends RuntimeException {
 private final HttpStatus status; private final String code;
 public SamsException(HttpStatus status,String code,String message){super(message);this.status=status;this.code=code;}
 public HttpStatus getStatus(){return status;} public String getCode(){return code;}
 public static SamsException badRequest(String message){return new SamsException(HttpStatus.BAD_REQUEST,"SAMS-VAL-400",message);}
 public static SamsException unauthorized(String message){return new SamsException(HttpStatus.UNAUTHORIZED,"SAMS-AUTH-401",message);}
 public static SamsException forbidden(String message){return new SamsException(HttpStatus.FORBIDDEN,"SAMS-AUTH-403",message);}
 public static SamsException notFound(String message){return new SamsException(HttpStatus.NOT_FOUND,"SAMS-NOTFOUND-404",message);}
 public static SamsException conflict(String code,String message){return new SamsException(HttpStatus.CONFLICT,code,message);}
}

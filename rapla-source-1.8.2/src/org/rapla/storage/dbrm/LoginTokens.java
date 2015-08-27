package org.rapla.storage.dbrm;

import java.util.Date;

public class LoginTokens {
 
    String accessToken;
    Date validUntil;
    
    public LoginTokens() {
    }
    
    public LoginTokens(String accessToken, Date validUntil) {
        this.accessToken = accessToken;
        this.validUntil = validUntil;
    }
    
    public String getAccessToken()
    {
        return accessToken;
    }
    
    public Date getValidUntil()
    {
        return validUntil;
    }
    
    public String toString()
    {
        return accessToken + "#" + validUntil.getTime();
    }
   
    public static LoginTokens fromString(String s){
        String[] split = s.split("#");
        String accessToken2 = split[0];
        long parseLong = Long.parseLong(split[1]);
        Date validUntil2 = new Date(parseLong);
        return       new LoginTokens(accessToken2, validUntil2);
    }

    public boolean isValid() {
        long currentTimeMillis = System.currentTimeMillis();
        long time = this.validUntil.getTime();
        boolean valid = currentTimeMillis < time;
        return valid;
    }
}

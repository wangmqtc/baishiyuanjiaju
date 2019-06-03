package com.baishiyuan.utils;

/**
 * Created by mogu on 2018/3/28.
 */
public class AuthorityUtils {

    public static boolean checkUserInfoAuth(int type, Integer authority) {
        if(type == 0){
            return  true;
        }

        if(type == 3){
            if(authority == null){
                return false;
            }
            int auth =  authority.intValue() & 1;
            if(auth == 1){
                return true;
            }
        }
        return false;
    }

    public static boolean checkGoodsAuth(int type, Integer authority) {
        if(type == 0){
            return  true;
        }

        if(type == 3){
            if(authority == null){
                return false;
            }
            int auth =  authority.intValue() & 4;
            if(auth == 4){
                return true;
            }
        }
        return false;
    }

    public static boolean checkUserAccountAuth(int type, Integer authority) {
        if(type == 0){
            return  true;
        }

        if(type == 3){
            if(authority == null){
                return false;
            }
            int auth =  authority.intValue() & 8;
            if(auth == 8){
                return true;
            }
        }
        return false;
    }

    public static boolean checkClientBenefitAuth(int type, Integer authority) {
        if(type == 0){
            return  true;
        }

        if(type == 3){
            if(authority == null){
                return false;
            }
            int auth =  authority.intValue() & 2;
            if(auth == 2){
                return true;
            }
        }
        return false;
    }

    public static boolean checkClientOrderAuth(int type, Integer authority) {
        if(type == 0){
            return  true;
        }

        if(type == 1){
            return  true;
        }

        if(type == 3){
            if(authority == null){
                return false;
            }
            int auth =  authority.intValue() & 32;
            if(auth == 32){
                return true;
            }
        }
        return false;
    }

    public static boolean checkUserAccountScanAuth(int type, Integer authority) {
        if(type == 0){
            return  true;
        }

        if(type == 3){
            if(authority == null){
                return false;
            }
            int auth =  authority.intValue() & 16;
            if(auth == 16){
                return true;
            }
        }
        return false;
    }
}

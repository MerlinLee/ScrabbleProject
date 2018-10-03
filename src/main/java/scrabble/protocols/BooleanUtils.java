package scrabble.protocols;

public class BooleanUtils {

    /**
     * 除了TURE,Y和1,其他都为false
     * @param s
     * @return
     */
    public static boolean toBoolFalse(String s){
        if(s!=null&&(s.equals('Y')||s.equals("1")||s.toUpperCase().equals("TRUE"))){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 除了FALSE,N,0,null,其他都为true
     * @param s
     * @return
     */
    public static boolean toBoolTrue(String s){
        if(s!=null){
            if((s.equals('N')||s.equals("0")||s.toUpperCase().equals("FALSE"))){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    /**
     * 将布尔值转换为Y或者N
     * @param b
     * @return
     */
    public static String boolToStr(boolean b){
        if(b){
            return "Y";
        }else{
            return "N";
        }
    }

    /**
     * 将布尔值转换为1或者0
     * @param b
     * @return
     */
    public static int boolToInt(boolean b){
        if(b){
            return 1;
        }else{
            return 0;
        }
    }
}

package server.rest;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import server.comm.DataMap;

import java.util.List;
import java.util.Vector;

/**
 * @author 함의진
 * @version 1.5.0
 * DataMap 검증을 위한 Validation 클래스
 */
public class DataMapUtil {

    private static final String EMPTY_STRING = "";
    private static final String MASK_STRING = "**********";

    /**
     * String 가변 파라미터에 대해 Null 여부를 반환한다.
     * @param map 검증 대상 맵
     * @param args 가변 파라미터 - String 기반 키
     * @return
     */
    public static boolean isValid(DataMap map, String... args){
        for(String arg : args) if(map.get(arg) == null) return false;
        return true;
    }

    public static <T> List<T> getListOf(List<DataMap> maps, String key){
        List<T> list = new Vector<>();
        for(DataMap map : maps) list.add((T) map.get(key));

        return list;
    }

    /**
     * String 파라미터 값의 키-밸류에 대해 밸류를 특정 값으로 소거한다.
     * @param map 목표 데이터맵
     * @param key 목표 키
     */
    public static void mask(DataMap map, String key){
        if(map != null) map.put(key, MASK_STRING);
    }

    public static boolean isSatisfied(DataMap map, String... args){
        for(String arg : args)
            if(map.get(arg) == null){
                return false;
            }else{
                if(map.getString(arg).equals(EMPTY_STRING)) return false;
            }
        return true;
    }

    public static boolean isVoid(String str){
        return str == null || str.equals("");
    }


    public static Pair<Integer, Integer> extractIndice(DataMap map){
        int page = 1;
        int limit = 20;
        if(isValid(map, "page")) page = map.getInt("page");
        if(page == 0 || page < 0) page = 1;
        if(isValid(map, "limit")) limit = map.getInt("limit");
        Pair<Integer, Integer> pair = new ImmutablePair<>(page, limit);

        return pair;
    }

}

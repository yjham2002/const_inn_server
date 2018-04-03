package utils.kopas;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * 코파스 장비와의 HTTPS 통신을 위한 유틸리티 및 캡슐화 클래스
 * @author 함의진
 * @version 1.0.0
 */
public class KopasTransmitter extends AKopasTransmitter{

    public KopasTransmitter(){
        super();
    }

    public String send(PID pid, String productNo, ACTION action){
        return super.send(pid, productNo, action);
    }

    public KopasReturn sendAndGet(PID pid, String productNo, ACTION action){
        ObjectMapper objectMapper = new ObjectMapper();
        KopasReturn kopasReturn = null;
        try{
            kopasReturn = objectMapper.readValue(send(pid, productNo, action), KopasReturn.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return kopasReturn;
    }

}

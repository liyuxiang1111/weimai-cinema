package com.liyuxiang.film.service;

import com.liyuxiang.film.entity.Order;
import com.liyuxiang.film.entity.Seat;
import com.liyuxiang.film.entity.Times;
import com.liyuxiang.film.mapper.OrderMapper;
import com.liyuxiang.film.mapper.SeatMapper;
import com.liyuxiang.film.mapper.TimesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class SeatService {
    @Autowired
    private SeatMapper seatMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private TimesMapper timesMapper;


    public List<Seat> getSeatByHallId(Integer hallId, Integer timesId) {
        List<Seat> seats = seatMapper.getSeatByHallId(hallId);
        Times times = timesMapper.selectById(timesId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String date = simpleDateFormat.format(times.getStartTime());
        List<Order> orders = orderMapper.getByTimesId(timesId,date);
        String tmp = "";
        for(Order order : orders){
            tmp += order.getDescribe();
        }
        for(Seat seat : seats){
            if(tmp.contains(seat.getyCoord() + "排" + seat.getxCoord() + "座"))
                seat.setStatus("booked");
        }
        return  seats;
    }

    public void update(Seat s) {
        seatMapper.updateById(s);
    }

    public List<Seat> getSeats(Integer hallId) {
        return seatMapper.getSeatByHallId(hallId);
    }

    public void commitSeat(Integer hallId, List<Seat> newSeatList, List<Seat> changeSeatList) {
        //改变座位
        List<Seat> seats = getSeats(hallId);
        Seat[][] seatInfo = new Seat[16][16];
        // 16x16 表示的是 最外面一次没有使用
        for(Seat s : seats) {
            seatInfo[s.getyCoord()][s.getxCoord()] = s;
        }
        for(Seat s : changeSeatList){
            Seat tmp = seatInfo[s.getyCoord()][s.getxCoord()];
            if(tmp.getType().equals("danren")){
                // 如果作为是单人 ---》 改变为过道
                tmp.setType("road");
                seatMapper.updateById(tmp);
            }else{
                tmp.setType("danren");
                seatMapper.updateById(tmp);
            }
        }
        // 增添新的座位
        for(Seat s : newSeatList){
            int x = s.getxCoord()+1;
            int y = s.getyCoord()+1;
            s.setyCoord(y);
            s.setxCoord(x);
            s.setType("danren");
            s.setStatus("ok");
            s.setHallId(hallId);
            if (seatInfo[y][x] == null) {
                seatMapper.insert(s);
            }
        }
    }
}

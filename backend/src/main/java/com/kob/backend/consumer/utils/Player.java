package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;//存储每一步的方向

    private boolean check_tail_increasing(int step){//检查当前回合，蛇的身体是不是要变长
        if(step <= 10)return true;
        return step%3 == 1;
    }

    public List<Cell> getCells(){
        List<Cell> res = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx,y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for(int d:steps){
            x+=dx[d];
            y+=dy[d];
            res.add(new Cell(x,y));
            if(!check_tail_increasing(++step)){//如果当前这一步不需要增加蛇尾的话，就把蛇尾去掉，蛇头向前走一个，变相位移
                res.remove(0);
            }
        }
        return res;
    }


    public String getStepsString(){
        StringBuilder res = new StringBuilder();
        for(int d:steps){
            res.append(d);
        }
        return res.toString();
    }

}

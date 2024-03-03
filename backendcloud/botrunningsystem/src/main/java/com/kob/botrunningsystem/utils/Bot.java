package com.kob.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;

public class Bot implements com.kob.botrunningsystem.utils.BotInterface {
    static class Cell{
        public int x,y;
        public Cell(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    private boolean check_tail_increasing(int step){//检查当前回合，蛇的身体是不是要变长
        if(step <= 10)return true;
        return step%3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps){
        List<Cell> res = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx,y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for(int i = 1;i<steps.length()-1;i++){
            int d = steps.charAt(i) - '0';
            x+=dx[d];
            y+=dy[d];
            res.add(new Cell(x,y));
            if(!check_tail_increasing(++step)){//如果当前这一步不需要增加蛇尾的话，就把蛇尾去掉，蛇头向前走一个，变相位移
                res.remove(0);
            }
        }
        return res;
    }

    @Override
    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        int[][] g = new int[13][14];
        for(int i = 0,k = 0;i<13;i++){
            for(int j=0;j<14;j++,k++){
                if(strs[0].charAt(k) == '1'){
                    g[i][j] = 1;
                }
            }
        }
        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);

        List<Cell> aCell = getCells(aSx, aSy, strs[3]);
        List<Cell> bCell = getCells(bSx, bSy, strs[6]);

        for(Cell c:aCell)g[c.x][c.y] = 1;
        for(Cell c:bCell)g[c.x][c.y] = 1;
        
        int[] dx = {-1,0,1,0},dy = {0,1,0,-1};
        for(int i = 0;i<4;i++){
            int x = aCell.get(aCell.size()-1).x + dx[i];
            int y = aCell.get(aCell.size()-1).y + dy[i];
            if(x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0){
                return i;
            }
        }
        return 0;
    }
}

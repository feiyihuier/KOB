package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocket.WebSocketServer;
import com.kob.backend.pojo.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{//实现多线程
    //1.final修饰变量表示变量的值不可改变，被final修饰过的变量就是常量。
    //2.final修饰方法表示此方法不可以被重写。
    //3.final修饰类表示此类不能被继承。
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1,0,1,0},dy = {0,1,0,-1};
    private final Player playerA, playerB;
    private Integer nextStepA = null;//两名玩家的下一步操作
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();//给线程加锁
    private String status = "playing";//存储这一局的游戏状态 playing->finished
    private String loser = ""; //all：平局， A：A输，B：B输


    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        playerA = new Player(idA, rows-2, 1, new ArrayList<>());//左下角的蛇
        playerB = new Player(idB, 1, cols-2, new ArrayList<>());//右上角的蛇
    }

    public Player getPlayerA(){
        return playerA;
    }

    public Player getPlayerB(){
        return playerB;
    }

    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try{
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }

    }
    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try{
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }

    }

    public int[][] getG(){
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        //检查生成的地图的连通性，两条蛇的出生点是否能连在一起
        if(sx == tx && sy == ty)return true;
        g[sx][sy] = 1;
        for(int i = 0;i < 4;i++){
            int x = sx + dx[i],y = sy + dy[i];
            if(x>=0 && x<this.rows && y>=0 && y<this.cols && g[x][y] == 0){
                if(check_connectivity(x, y, tx, ty)){
                    g[sx][sy] = 0 ;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    public boolean draw(){
        //1表示墙，0表示空地
        for(int i=0;i<this.rows;i++){//清空
            for(int j=0;j<this.cols;j++){
                g[i][j] = 0;
            }
        }

        for(int r=0;r<this.rows;r++){//画左右的墙
            g[r][0] = g[r][this.cols-1] = 1;
        }

        for(int c=0;c<this.cols;c++){//画上下的墙
            g[0][c] = g[this.rows-1][c] = 1;
        }

        Random random = new Random();
        for(int i = 0; i<this.inner_walls_count /2; i++){
            for(int j=0;j<1000;j++){
                int r = random.nextInt(this.rows);//返回0-this.rows之间的整数
                int c = random.nextInt(this.cols);//返回0-this.cols之间的整数
                //有过的点跳过
                if(g[r][c] == 1|| g[this.rows-1-r][this.cols-1-c] ==1)continue;
                //放过左下角和右上角，这是蛇的出生点
                if((r == this.rows-2 && c==1 )||( r==1 && c == this.cols-2))continue;
                g[r][c] = g[this.rows-1-r][this.cols-1-c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows-2, 1, 1, this.cols-2);
    }

    public void createMap(){
        //创建地图一千次，直到生成连通性地图
        for(int i=0;i<1000;i++){
            if(draw()){
                break;
            }
        }
    }

    private boolean nextStep(){//等待两名玩家的下一步操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(int i =0; i<50;i++){
            try {
                Thread.sleep(100);
                lock.lock();
                try{
                    if(nextStepA != null && nextStepB !=null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;//表示两名玩家的下一步操作均被获取到
                    }
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendAllMessage(String message){
        WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendResult(){//向两名玩家广播结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }
    private void saveToDatabase(){
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );//向数据库插入数据
        WebSocketServer.recordMapper.insert(record);
    }


    private void sendMove(){//向两名玩家广播对方的next step
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n-1);
        if(g[cell.x][cell.y] == 1)return false;//表示蛇头撞到了墙
        for(int i = 0;i<n-1;i++){
            //如果A蛇头与A身体的某一个格子重合，表示头撞到了自己，也是失败
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)return false;
        }
        for(int i=0;i<n-1;i++){
            //如果A的蛇头与B的身体的某一个格子重合，表示头撞到了对面的身体，也是失败
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)return false;
        }
        return true;
    }

    private void judge(){//判断两名玩家的操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = check_valid(cellsA, cellsB);//检查A的头有没有撞上B，或者墙
        boolean validB = check_valid(cellsB, cellsA);//检查B的头有没有撞上A，或者墙
        if(!validA || !validB){
            status = "finished";
            if(!validA && !validB){
                loser = "All";
            }else if(!validA){
                loser = "A";
            }else {
                loser = "B";
            }

        }
    }

    @Override
    public void run() {
        for(int i=0;i<1000;i++){
            if(nextStep()){//是否获取了两条蛇的下一步操作
                judge();
                if(status.equals("playing")){
                    sendMove();
                }else{
                    sendResult();
                    break;
                }
            }else{
                status = "finished";
                lock.lock();
                try{
                    if(nextStepA == null && nextStepB == null){
                        loser = "All";
                    }else if(nextStepA == null){
                        loser = "A";
                    }else if(nextStepB == null){
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}

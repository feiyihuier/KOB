package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocket.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer bId) {
        System.out.println("Impl处 startgame" + aId + "  " + bId);
        WebSocketServer.startGame(aId,bId);
        return "start game success";
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pong.environment;

import pong.agent.Agent;

/**
 *
 * @author eduarc
 */
public class Simulator {
    
    private Pong pong;
    
    public Simulator(Pong pong) {
        this.pong = pong;
    }
    
    public double run() {
        
        int deltaX[] = {-1, -2};
        int deltaY[] = {-1, +1};
        
        int agentSize = pong.agents[Pong.LEFT].getSize();
        int fieldHeight = pong.fieldSize.height;
        
        double ok = 0;
        double tries = 0;
        
        for (int playerY = 0; playerY+agentSize < fieldHeight; playerY++) {
            for (int ballY = 0; ballY < fieldHeight; ballY++) {
                for (int dy : deltaY) {
                    for (int dx : deltaX) {
                        pong.init(playerY, ballY, dy, dx);
                        while (pong.getWinner() == -1) {
                            pong.moveAgent(Pong.LEFT);
                            pong.moveBall();
                            if (pong.ball.direction.x > 0) {
                                ok++;
                                break;
                            }
                        }
                        tries++;
                    }
                }
            }
        }
        return ok/tries;
    }
}

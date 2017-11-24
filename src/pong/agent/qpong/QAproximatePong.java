/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pong.agent.qpong;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.environment.Pong;

/**
 *
 * @author eduarc
 */
public class QAproximatePong extends Agent {

    private static final int[] deltaX = {-2, -1};
    private static final int[] deltaY = {-1, 1};
    private static final int[] actions = {0, -1, 1};
    
    private double w1, w2;
    private final double gamma;
    private final double alpha;
    private final int iterations;
    private final double testWindow;
    
    private boolean firstTime = true;
    
    public QAproximatePong(double gamma, double alpha, double testWindow, int iterations) {
        
        this.gamma = gamma;
        this.alpha = alpha;
        this.iterations = iterations;
        this.testWindow = testWindow;
        w1 = w2 = 1;
    }
    
    @Override
    public int compute(Percept percept) {
        
        int ballDeltaX = percept.ball.direction.x;
		int ballX = percept.ball.position.x;
        int ballY = percept.ball.position.y;
		int ballDeltaY = percept.ball.direction.y;
		int playerY = percept.left;
        int opponentY = percept.right;

		if (side == Pong.RIGHT) {
            ballDeltaX *= -1;
            ballX = fieldSize.width-ballX-1;
            int tmp = playerY;
            playerY = percept.right;
            opponentY = tmp;
        }
        
        double bestQ = Long.MIN_VALUE;
        int bestAct = 0;
        for (int act : actions) {
            double v = Q(playerY, opponentY, ballY, ballX, ballDeltaY, ballDeltaX, act);
            if (bestQ < v) {
                bestQ = v;
                bestAct = act;
            }
        }
        return bestAct;
    }

    @Override
    public void init() {
        
        if (firstTime) {
            firstTime = false;
            
            int playerRange = fieldSize.height-size+1;

            for (int i = 1; i <= iterations; i++) {
                System.err.println("qIteration: "+i);
                learn();
                System.err.println("W1:"+w1);
                System.err.println("W2:"+w2);
            }
        }
    }
    
    public void learn() {
        
        int playerRange = fieldSize.height-size+1;
        
        for (int playerY = 0; playerY < playerRange; playerY++) {
            /*if (Math.random() < 0.5) {
                continue;
            }*/
            for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                for (int ballY = 0; ballY < fieldSize.height; ballY++) {
                    /*if (Math.random() < 0.5) {
                        continue;
                    }*/
                    for (int ballX = 0; ballX < fieldSize.width; ballX++) {
                        /*if (Math.random() < 0.5) {
                            continue;
                        }*/
                        for (int dx : deltaX) {
                            for (int dy : deltaY) {
                                for (int act : actions) {
                                    /*if (Math.random() < 0.5) {
                                        continue;
                                    }*/
                                    calculateQ(playerY, opponentY, ballY, ballX, dy, dx, act);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void calculateQ(int playerY, int opponentY, int ballY, int ballX, int ballDeltaY, int ballDeltaX, int act) {
        
            // next state
        int nextBallX = moveBallX(ballX, ballDeltaX);
        int nextBallY = moveBallY(ballY, ballDeltaY);
        int nextDx = updateDeltaX(playerY, opponentY, ballY, ballX, ballDeltaX);
        int nextDy = updateDeltaY(ballY, ballDeltaY);
        int nextPlayerY = movePlayer(playerY, act);
        int nextOpponentY = opponentY;
            // reward
        int r = qReward(playerY, opponentY, ballY, ballX, ballDeltaY, ballDeltaX,
                        nextPlayerY, nextOpponentY, nextBallY, nextBallX, nextDy, nextDx);
        
        double bestQ = Long.MIN_VALUE;
        
        for (int nextAct : actions) {
            double nextQ = Q(nextPlayerY, nextOpponentY, nextBallY, nextBallX, nextDx, nextDy, nextAct);
            bestQ = Math.max(bestQ, nextQ);
        }
        double diff = (r+gamma*bestQ)-Q(playerY, opponentY, ballY, ballX, ballDeltaY, ballDeltaX, act);
        
        w1 += alpha*diff*func1(playerY, ballX, ballY, ballDeltaX);
        w2 += alpha*diff*func2(playerY, ballDeltaX);
    }
    
    private int qReward(int playerY, int opponentY, int ballY, int ballX, int dy, int dx,
                        int nextPlayerY, int nextOpponentY, int nextBallY, int nextBallX,int nextDy, int nextDx) {

        int reward = 0;
            // ball coming
        if (nextDx < 0) {
            if (nextBallX == 0) {
                reward = -1;
            }
            else if (nextBallX == 1 && nextBallY >= nextPlayerY && nextBallY < nextPlayerY+size) {
                reward = 1;
            }
        }
            // ball going away
        else {
            if (nextBallX == fieldSize.width-1) {
                reward = 1;
            }
            else if (nextBallX == fieldSize.width-2 && nextBallY >= nextOpponentY && nextBallY < nextOpponentY+size) {
                reward = -1;
            }
        }
        return reward;
    }
    
    public double Q(int playerY, int opponentY, int ballY, int ballX, int ballDeltaY, int ballDeltaX, int action) {
        
            // next state given action
        int nextBallX = moveBallX(ballX, ballDeltaX);
        int nextBallY = moveBallY(ballY, ballDeltaY);
        int nextDx = updateDeltaX(playerY, opponentY, ballY, ballX, ballDeltaX);
        int nextDy = updateDeltaY(ballY, ballDeltaY);
        int nextPlayerY = movePlayer(playerY, action);
        int nextOpponentY = opponentY;
        
        return w1*func1(nextPlayerY, nextBallX, nextBallY, nextDx)+w2*func2(nextPlayerY, nextDx);
    }
    
    public double func1(int playerY, int ballX, int ballY, int ballDeltaX) {
        
        if (ballDeltaX < 0 && ballX < fieldSize.width*testWindow) {
            double dy = Math.abs((playerY+size/2)-ballY);
            return fieldSize.height-dy;
        }
        return 0;
    }
    
    public double func2(int playerY, int ballDeltaX) {
        
        if (ballDeltaX > 0) {
            double cy = this.fieldSize.height/2;
            double py = playerY+(size/2);
            return fieldSize.height-Math.abs(cy-py);
        }
        return 0;
    }
    
    private int movePlayer(int y, int dy) {
        
        y += dy;
        if (y < 0) {
            y = 0;
        }
        else if (y > fieldSize.height-size) {
            y = fieldSize.height-size;
        }
        return y;
    }
    
    private int moveBallX(int ballX, int dx) {
        
        ballX += dx;
        if (ballX < 0) ballX = 0;
        if (ballX >= fieldSize.width) {
            ballX = fieldSize.width-1;
        }
        return ballX;
    }
    
    private int moveBallY(int ballY, int dy) {
        
        if (ballY == 0 && dy < 0 || ballY == fieldSize.height-1 && dy > 0) {
            dy = -dy;
        }
        ballY += dy;
        return ballY;
    }
    
    private int updateDeltaX(int playerY, int opponentY, int ballY, int ballX, int dx) {
        
        if (dx < 0 && ballX == 1 && ballY >= playerY && ballY < playerY+size) {
            return -dx;
        }
        else if (dx > 0 && ballX == fieldSize.width-2 && ballY >= opponentY && ballY < opponentY+size) {
            return -dx;
        }
        return dx;
    }
    
    private int updateDeltaY(int ballY, int dy) {
        
        if (ballY == 0 && dy < 0 || ballY == fieldSize.height-1 && dy > 0) {
            return -dy;
        }
        return dy;
    }
    
}

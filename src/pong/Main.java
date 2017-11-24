package pong;

import javax.swing.JFrame;
import pong.agent.BallFollower;
import pong.agent.qpong.QAproximatePong;
import pong.agent.qpong.QPong;
import pong.agent.qpong.QPongNoDirection;
import pong.environment.Bound;
import pong.environment.Pong;
import pong.environment.Simulator;

import pong.gui.MainFrame;

public class Main {

	public static void main(String[] args) {
		MainFrame main = new MainFrame();
		main.setFocusable(true);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.pack();
		main.setLocationRelativeTo(null);
		main.setVisible(true);
        
        /*testQPong(0.5, 10, 2);
        testQPong(0.9, 10, 2);
        testQPong(0.1, 10, 2);
        
        System.err.println("-------------");
        
        testQPongNoDirection(0.5, 10, 2);
        testQPongNoDirection(0.9, 10, 2);
        testQPongNoDirection(0.1, 10, 2);
        
        System.err.println("-------------");
        
        testQAproximatePong(0.5, 0.0001, 0.9, 10, 2);
        testQAproximatePong(0.9, 0.0001, 0.9, 10, 2);
        testQAproximatePong(0.1, 0.0001, 0.9, 10, 2);
        
        System.err.println("-------------");
        
        testQAproximatePong(0.5, 0.0001, 0.5, 10, 2);
        testQAproximatePong(0.9, 0.0001, 0.5, 10, 2);
        testQAproximatePong(0.1, 0.0001, 0.5, 10, 2);
        
        System.err.println("-------------");
        
        testQAproximatePong(0.5, 0.0001, 0.2, 10, 2);
        testQAproximatePong(0.9, 0.0001, 0.2, 10, 2);
        testQAproximatePong(0.1, 0.0001, 0.2, 10, 2);*/
	}
    
    public static void testQPong(double gamma, int maxIterations, int learnSteps) {
        
        QPong qpong = new QPong(gamma, 0);
        Pong field = new Pong(new Bound(49, 30), qpong, new BallFollower(0.1));
        Simulator simulator = new Simulator(field);
        
        System.err.println("*** QPong Agent ***");
        System.err.println("Iteration\tPerformance");
        for (int iterations = 0; iterations <= maxIterations; iterations += learnSteps) {
            System.err.println(iterations+"\t"+simulator.run());
            for (int i = 0; i < learnSteps; i++) {
                qpong.learn();
            }
        }
    }
    
    public static void testQPongNoDirection(double gamma, int maxIterations, int learnSteps) {
        
        QPongNoDirection qpong = new QPongNoDirection(gamma, 0);
        Pong field = new Pong(new Bound(49, 30), qpong, new BallFollower(0.1));
        Simulator simulator = new Simulator(field);
        
        System.err.println("*** QPongNoDirection Agent ***");
        System.err.println("Iteration\tPerformance");
        for (int iterations = 0; iterations <= maxIterations; iterations += learnSteps) {
            System.err.println(iterations+"\t"+simulator.run());
            for (int i = 0; i < learnSteps; i++) {
                qpong.learn();
            }
        }
    }
    
    public static void testQAproximatePong(double gamma, double alpha, double testWindow, int maxIterations, int learnSteps) {
        
        QAproximatePong qpong = new QAproximatePong(gamma, alpha, testWindow, 0);
        Pong field = new Pong(new Bound(49, 30), qpong, new BallFollower(0.1));
        Simulator simulator = new Simulator(field);
        
        System.err.println("*** QAproximatePong Agent ***");
        System.err.println("Iteration\tPerformance");
        for (int iterations = 0; iterations <= maxIterations; iterations += learnSteps) {
            System.err.println(iterations+"\t"+simulator.run());
            for (int i = 0; i < learnSteps; i++) {
                qpong.learn();
            }
        }
    }
}

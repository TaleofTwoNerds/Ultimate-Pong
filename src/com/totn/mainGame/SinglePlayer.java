package com.totn.mainGame;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

import com.totn.entities.AbstractMoveableEntity;

public class SinglePlayer {
	
    private static final int width = 640;
    private static final int height = 480;
    private static int pIs = 0;
    private static int hit = 0;
    private static int pIIs = 0;
    private static boolean isRunning = true;
    private static boolean paused = false;
    private static Ball ball;
    private static Bat bat;
    private static Bat opbat;
    private static double velocity = .25;
    private static double yvelocity = velocity / 2;
    private static final double pSpeed = .25;
    private static double pISpeed = .25;
    private static double pIISpeed = .25;
    private static State state = State.PRE;

    public static enum State {
        PRE, MAIN, PAUSE
    }
    
    public SinglePlayer() 
    {
//   	"Main" method which the game repeats until the loop is broken 	
        setUpDisplay();
        setUpOpenGL();
        setUpEntities();
        setUpTimer();
//        setup title
        Display.setTitle("Pong" + " | " + "P1: " + pIs + " | " + "P2: " + pIIs + " | " + "Hit: " + hit);
        while (isRunning) 
        {
        	if (state == state.MAIN){
        		Display.setTitle("Pong" + " | " + "P1: " + pIs + " | " + "P2: " + pIIs + " | " + "Hit: " + hit);
        	} else if (state == state.PAUSE)
        	{
        		Display.setTitle("Pong" + " | " + "Paused");
        	}
        	System.out.println(velocity);
            render();
            logic(getDelta());
            input();
            Display.update();
            Display.sync(60);
            if (Display.isCloseRequested()) {
                isRunning = false;
            }
        }
        Display.destroy();
        System.exit(0);
    }
    
    private static void input() 
    {
//    	User Input method. All input from the user should go here
//    	Move the bat up or down depending on the W/S keys
        if (state == State.MAIN){
        	if (!paused && bat.getY() < height - 90 && Keyboard.isKeyDown(Keyboard.KEY_W) ) 
        	{
        		bat.setDY(pISpeed);
        	} else if (!paused && bat.getY() > 0 + 10 && Keyboard.isKeyDown(Keyboard.KEY_S)) 
        	{
        		bat.setDY(-pISpeed);
        	} else {
                bat.setDY(0);
        	}
//      Move the bat up or down depending on the Up/Down arrows
        	if (!paused && opbat.getY() < height - 90 && Keyboard.isKeyDown(Keyboard.KEY_UP) ) 
        	{
        		opbat.setDY(pIISpeed);
        	} else if (!paused && opbat.getY() > 0 + 10 && Keyboard.isKeyDown(Keyboard.KEY_DOWN)) 
        	{
        		opbat.setDY(-pIISpeed);
        	} else {
        		opbat.setDY(0);
        	}
//      Closes the display when escape is hit
//      REMOVE IN MULTIPLAYER VERSION
        	if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) 
        	{
        		Display.destroy();
        		System.exit(0);
        	}
        	if(!paused && Keyboard.isKeyDown(Keyboard.KEY_Q))
        	{
        		pIISpeed = .1;
        	} else {
        		pIISpeed = pSpeed; 
        	}
        }

        switch (state) {
        case PRE:
            if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
                state = State.MAIN;
                ball.setDX(velocity);
                ball.setDY(yvelocity);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                Display.destroy();
                System.exit(0);
            }
            break;
        case PAUSE:
            if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
                state = State.MAIN;
                ball.setDX(velocity);
                ball.setDY(yvelocity);
            }
            break;
        case MAIN:
            if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
                state = State.PAUSE;
                ball.setDX(0);
                ball.setDY(0);
                bat.setDY(0);
                opbat.setDY(0);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                Display.destroy();
                System.exit(0);
            }
            break;
        }
    }

    private static long lastFrame;

    private static long getTime() 
    {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private static int getDelta() 
    {
//    	Delta (change) in time
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = getTime();
        return delta;
    }

    private static void setUpDisplay() 
    {
//    	Starts the display
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
    }

    private static void setUpOpenGL() 
    {
//    	Sets up the openGL
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 640, 0, 480, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    private static void setUpEntities() 
    {
//    	Creates a new version of everything in the world
        bat = new Bat(10, height / 2 - 80 / 2, 80, 10);
        opbat = new Bat(width - 20, height / 2 - 80 / 2, 80, 10);
        ball = new Ball(width / 2 - 10 / 2, height / 2 - 10 / 2, 10, 10);
    }

    private static void setUpTimer() 
    {
        lastFrame = getTime();
    }

    private static void render() 
    {
        glClear(GL_COLOR_BUFFER_BIT);
        ball.draw();
        bat.draw();
        opbat.draw();
    }
    
    private static void logic(int delta) 
    {
        ball.update(delta);
        bat.update(delta);
        opbat.update(delta);
        
//        Increases the speed of the ball as the amount of hits increases
        if(hit==0)
        {
        	velocity = .3;
        } else {
            velocity = .3 + (hit + 1) * .01;
        }
        
//        Sets the display title before every tick
        
//        If ball hits bat
        if (ball.getX() <= bat.getX() + bat.getWidth() && ball.getX() >= bat.getX()/* && ball.getY() >= bat.getY() - ball.getHeight() &&
                ball.getY() <= bat.getY() + bat.getHeight()*/) 
        {
            ball.setDX(velocity);
            if(ball.getY() <= bat.getY()+bat.getHeight()/2)
            {
            	yvelocity = -Math.abs(yvelocity);
                ball.setDY(yvelocity);
            } else if(ball.getY() >= bat.getY()+bat.getHeight()/2) {
            	yvelocity = Math.abs(yvelocity);
                ball.setDY(yvelocity);
            }
            hit++;
        } 
//        If ball hits opbat
        else if (ball.getX() <= opbat.getX() && ball.getX() >= opbat.getX() - opbat.getWidth()/* && ball.getY() >= opbat.getY() - ball.getHeight() &&
                ball.getY() <= opbat.getY() + opbat.getHeight()*/)
        {
        	velocity=-velocity;
            ball.setDX(velocity);
            if(ball.getY() <= opbat.getY()+bat.getHeight()/2)
            {
            	yvelocity = -Math.abs(yvelocity);
                ball.setDY(yvelocity);
            } else if(ball.getY() >= opbat.getY()+bat.getHeight()/2) {
            	yvelocity = Math.abs(yvelocity);
                ball.setDY(yvelocity);
            }
            hit++;
        }
//        If the ball bounces of the top or the bottom
        if (ball.getY() >= height - ball.getHeight())
        {
        	yvelocity = -yvelocity;
        	ball.setDY(yvelocity);
        } else if (ball.getY() <= 0) 
        {
        	yvelocity = -yvelocity;
        	ball.setDY(yvelocity);
        }
//        For when the ball scores
        if(ball.getX() > width)
        {
            ball.setX(width / 2 + 150);
            ball.setY(height / 2 - 10 / 2);

            pIs++;
            hit = -1;
            hit++;
            
       		if(opbat.getY() + opbat.getHeight()/2< height / 2)
       		{
       			yvelocity = 0 - Math.abs(yvelocity);
       			ball.setDY(yvelocity);
       			velocity = -Math.abs(velocity);
            	ball.setDX(velocity);
       		} else 
       		{
       			yvelocity = Math.abs(yvelocity);
       			ball.setDY(yvelocity);
       			velocity = -Math.abs(velocity);
            	ball.setDX(velocity);
       		}
        } else if (ball.getX() < 0)
        {
        	if(bat.getY() + bat.getHeight()/2< height / 2)
        	{
        		yvelocity = 0 - Math.abs(yvelocity);
        		ball.setDY(yvelocity);
       			velocity = Math.abs(velocity);
            	ball.setDX(velocity);
        	} else 
        	{
        		yvelocity = Math.abs(yvelocity);
        		ball.setDY(yvelocity);
       			velocity = Math.abs(velocity);
            	ball.setDX(velocity);
        	}
            ball.setX(width / 2 - 150);
            ball.setY(height / 2 - 10 / 2);
            
            pIIs++;
            hit = -1;
            hit++;
        }
    }
    
//    Creates the bat and ball classes
    public static class Bat extends AbstractMoveableEntity 
    {

        public Bat(double x, double y, double width, double height) 
        {
            super(x, y, width, height);
        }

        @Override
        public void draw() 
        {
            glRectd(x, y, x + width, y + height);
        }
    }

    public static class Ball extends AbstractMoveableEntity 
    {
        public Ball(double x, double y, double width, double height) 
        {
            super(x, y, width, height);
        }

        @Override
        public void draw() 
        {
            glRectd(x, y, x + width, y + height);
        }
    }
    public static void main(String[] args) 
    {
    	new SinglePlayer();
    }
}
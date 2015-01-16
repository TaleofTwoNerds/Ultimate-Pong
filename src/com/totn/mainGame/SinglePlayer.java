package com.totn.mainGame;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.totn.entities.AbstractMoveableEntity;

import static org.lwjgl.opengl.GL11.*;

public class SinglePlayer {
	
    private static final int width = 640;
    private static final int height = 480;
    private static boolean isRunning = true;
    private static Ball ball;
    private static Bat bat;
    private static Bat opbat;
    public static double velocity = .25;
    public static double yvelocity = velocity / 2;

    public SinglePlayer() 
    {
//    	"Main" method which the game repeats until the loop is broken
        setUpDisplay();
        setUpOpenGL();
        setUpEntities();
        setUpTimer();
        while (isRunning) 
        {
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
        if (bat.getY() < height - 90 && Keyboard.isKeyDown(Keyboard.KEY_W) ) 
        {
                bat.setDY(.2);
        	} else if (bat.getY() > 0 + 10 && Keyboard.isKeyDown(Keyboard.KEY_S)) 
        	{
                bat.setDY(-.2);
        	} else {
                bat.setDY(0);
        	}
//      Move the bat up or down depending on the Up/Down arrows
        if (opbat.getY() < height - 90 && Keyboard.isKeyDown(Keyboard.KEY_UP) ) 
        {
        		opbat.setDY(.2);
        	} else if (opbat.getY() > 0 + 10 && Keyboard.isKeyDown(Keyboard.KEY_DOWN)) 
        	{
        		opbat.setDY(-.2);
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
//      Begins the game, will be set to automatic in future updates
        if(Keyboard.isKeyDown(Keyboard.KEY_T))
        {
            ball.setDX(velocity);
            yvelocity = Math.random(); 
            ball.setDY(-yvelocity);
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
            Display.setTitle("Pong");
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
//        If ball hits bat
        if (ball.getX() <= bat.getX() + bat.getWidth() && ball.getX() >= bat.getX() && ball.getY() >= bat.getY() &&
                ball.getY() <= bat.getY() + bat.getHeight()) 
        {
            ball.setDX(velocity);
        }
//        If ball hits opbat
        if (ball.getX() <= opbat.getX() && ball.getX() >= opbat.getX() - opbat.getWidth() && ball.getY() >= opbat.getY() &&
                ball.getY() <= opbat.getY() + opbat.getHeight())
        {
            ball.setDX(- velocity);
        }
//        If the ball bounces of the top or the bottom
        if (ball.getY() >= height - ball.getHeight())
        {
        	ball.setDY(- yvelocity);
        } else if (ball.getY() <= 0) { 
        	ball.setDY(yvelocity);
        }
//        For when the ball goes off the page
        if (ball.getX() > width || ball.getX() < 0)
        {
        	ball.setX(width / 2 - 10 / 2);
        	ball.setY(height / 2 - 10 / 2);
        }
    }

    private static class Bat extends AbstractMoveableEntity 
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

    private static class Ball extends AbstractMoveableEntity 
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
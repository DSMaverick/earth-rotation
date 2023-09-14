package SunEarthMoon;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

import com.jogamp.opengl.util.Animator;


public class SunEarthMoon
        extends JFrame
        implements GLEventListener,
        KeyListener,
		MouseListener,
		MouseMotionListener,
		MouseWheelListener
{

    private GLCanvas canvas;
    private Animator animator;

    private GLU glu;
    
    private Texture earthTexture;
    private Texture cloudTexture;
    private Texture moonTexture;
    private Texture sunTexture;
    
    private float cameraX = 0.0f;
    private float cameraY = 0.0f;
    private float cameraZ = 5.0f;
    private float cameraRotX = 0.0f;
    private float cameraRotY = 0.0f;

    private int lastMouseX = -1;
    private int lastMouseY = -1;


    // For specifying the positions of the clipping planes (increase/decrease the distance) modify this variable.
    // It is used by the glOrtho method.
    private double v_size = 1.0;

    // Application main entry point
    public static void main(String args[])
    {
        new SunEarthMoon();
    }

    // Default constructor
    public SunEarthMoon()
    {
        super("Java OpenGL");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(800, 600);

        this.initializeJogl();

        this.setVisible(true);
    }

    private void initializeJogl()
    {
        // Creating a new GL profile.
        GLProfile glprofile = GLProfile.getDefault();
        // Creating an object to manipulate OpenGL parameters.
        GLCapabilities capabilities = new GLCapabilities(glprofile);

        // Setting some OpenGL parameters.
        capabilities.setHardwareAccelerated(true);
        capabilities.setDoubleBuffered(true);

        // Try to enable 2x anti aliasing. It should be supported on most hardware.
        capabilities.setNumSamples(2);
        capabilities.setSampleBuffers(true);

        // Creating an OpenGL display widget -- canvas.
        this.canvas = new GLCanvas(capabilities);

        // Adding the canvas in the center of the frame.
        this.getContentPane().add(this.canvas);

        // Adding an OpenGL event listener to the canvas.
        this.canvas.addGLEventListener(this);

        // Creating an animator that will redraw the scene 40 times per second.
        this.animator = new Animator(this.canvas);

        // Starting the animator.
        this.animator.start();

        this.glu = new GLU();
        
        // Adding the keyboard and mouse event listeners to the canvas.
     	this.canvas.addKeyListener(this);
     	this.canvas.addMouseListener(this);
    	this.canvas.addMouseMotionListener(this);
    	this.canvas.addMouseWheelListener(this);
        
    }
    
    // The x position
    private float xpos;

    // The rotation value on the y axis
    private float yrot;

    // The z position
    private float zpos;

    private float heading;

    // Walkbias for head bobbing effect
    private float walkbias = 0.0f;

    // The angle used in calculating walkbias */
    private float walkbiasangle = 0.0f;

    // The value used for looking up or down pgup or pgdown
    private float lookupdown = 0.0f;

    // Define an array to keep track of the key that was pressed
    private boolean[] keys = new boolean[250];

    public void keyPressed(KeyEvent ke)
    {
    	if(ke.getKeyCode() < 250)
    		keys[ke.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent ke)
    {
    	if (ke.getKeyCode() < 250)
    		keys[ke.getKeyCode()] = false;
    	
    }
	
	public void keyTyped(KeyEvent event)
	{
		return;
	}
	
	public void mousePressed(MouseEvent event)
	{
		return;
	}
	
	public void mouseReleased(MouseEvent event) {
	    lastMouseX = -1;
	    lastMouseY = -1;
	}
	
	public void mouseClicked(MouseEvent event)
	{
		return;
	}
	
	public void mouseMoved(MouseEvent event)
	{
		return;
	}
	
	public void mouseDragged(MouseEvent event) {
	    int x = event.getX();
	    int y = event.getY();
	    
	    if (lastMouseX != -1 && lastMouseY != -1) {
	        cameraRotX += (y - lastMouseY) * 0.5f;
	        cameraRotY += (x - lastMouseX) * 0.5f;
	    }
	    
	    lastMouseX = x;
	    lastMouseY = y;
	}
	
	public void mouseEntered(MouseEvent event)
	{
		return;
	}
	
	public void mouseExited(MouseEvent event)
	{
		return;
	}

    public void init(GLAutoDrawable canvas)
    {
        // Obtaining the GL instance associated with the canvas.
        GL2 gl = (GL2) canvas.getGL();

        // Setting the clear color -- the color which will be used to erase the canvas.
        gl.glClearColor(0, 0, 0, 0);
        
        try {
            earthTexture = TextureIO.newTexture(new File("F://Eclipse-Workspace/LaboratorGIU/src/SunEarthMoon/textures/earthTexture.jpg"), true);
            cloudTexture = TextureIO.newTexture(new File("F://Eclipse-Workspace/LaboratorGIU/src/SunEarthMoon/textures/cloudTexture.jpg"), true);
            moonTexture = TextureIO.newTexture(new File("F://Eclipse-Workspace/LaboratorGIU/src/SunEarthMoon/textures/moonTexture.jpg"), true);
            sunTexture = TextureIO.newTexture(new File("F://Eclipse-Workspace/LaboratorGIU/src/SunEarthMoon/textures/sunTexture.jpg"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }



       
        
        // Choose the shading model.
     		gl.glShadeModel(GL2.GL_SMOOTH);
     		
     		// Activate the depth test and set the depth function.
     		gl.glEnable(GL.GL_DEPTH_TEST);
     		gl.glDepthFunc(GL.GL_LESS);
     		
     		gl.glEnable(GL2.GL_LIGHTING);
    		gl.glEnable(GL2.GL_LIGHT0);
    		gl.glEnable(GL2.GL_LIGHT1);
    		
    		gl.glEnable(GL2.GL_COLOR_MATERIAL);
    		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);



        // Selecting the modelview matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        
        // Set The Texture Generation Mode For S To Sphere Mapping (NEW)
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);                
        // Set The Texture Generation Mode For T To Sphere Mapping (NEW) 
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);

    }
    
    float angle = 0.0f;
	private Object texture;

	public void display(GLAutoDrawable canvas)
    {
        GL gl = canvas.getGL();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        
     // Compute rotation and translation angles
        float xTrans = -xpos;
        float yTrans = -walkbias - 0.43f;
        float zTrans = -zpos;
        float sceneroty = 360.0f - yrot;

        // Perform operations on the scene
        ((GLMatrixFunc) gl).glRotatef(lookupdown, 1.0f, 0.0f, 0.0f);
        ((GLMatrixFunc) gl).glRotatef(sceneroty, 0.0f, 1.0f, 0.0f);

        ((GLMatrixFunc) gl).glTranslatef(xTrans, yTrans, zTrans);
        
        // Apply camera transformations
        ((GLMatrixFunc) gl).glTranslatef(0, 0, -cameraZ);
        ((GLMatrixFunc) gl).glRotatef(cameraRotX, 1, 0, 0);
        ((GLMatrixFunc) gl).glRotatef(cameraRotY, 0, 1, 0);
        ((GLMatrixFunc) gl).glTranslatef(-cameraX, -cameraY, 0);
      
        // Ambient light
        float[] ambientLight = { 0.3f, 0.3f, 0.3f, 1.0f };
        ((GL2ES1) gl).glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, ambientLight, 0);

        // Enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        // Start with a fresh transformation i.e. the 4x4 identity matrix.
        ((GLMatrixFunc) gl).glLoadIdentity();

        // Save (push) the current matrix on the stack.
        ((GLMatrixFunc) gl).glPushMatrix();


        // Translate the first sphere to coordinates (0,0,0).
        ((GLMatrixFunc) gl).glTranslatef (0.0f, 0.0f, 0.0f);
        
        // Sunlight (diffuse light)
        float[] diffuseLight = { 1.5f, 1.5f, 1.5f, 1.0f };
        float[] lightPos = { 0.0f, 0.0f, 0.0f, 1.0f }; // The sun is at the origin
        ((GLLightingFunc) gl).glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 1);
        ((GLLightingFunc) gl).glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        
        // Then draw it.
        this.drawSphere(gl, glu, 0.4f, sunTexture);

        // Save (push) on the stack the matrix holding the transformations produced by translating the first sphere.
        ((GLMatrixFunc) gl).glPushMatrix();

        // NOTE THE FOLLOWING ORDER OF OPERATIONS. THEY ACHIEVE A TRANSLATION FOLLOWED BY A ROTATION IN REALITY.

        // Rotate it with angle degrees around the X axis.
        ((GLMatrixFunc) gl).glRotatef (angle, 0, 0, 1);
        // Translate the second sphere to coordinates (10,0,0).
        ((GLMatrixFunc) gl).glTranslatef (0.9f, 0.0f, 0.0f);
        // Scale it to be half the size of the first one.
        ((GLMatrixFunc) gl).glScalef (0.5f, 0.5f, 0.5f);
        // Draw the second sphere.
        this.drawSphere(gl, glu, 0.3f, earthTexture);

        ((GLMatrixFunc) gl).glRotatef (angle, 0, 0, 1);
        // Translate the second sphere to coordinates (10,0,0).
        ((GLMatrixFunc) gl).glTranslatef (0.7f, 0.0f, 0.0f);
        // Scale it to be half the size of the first one.
        ((GLMatrixFunc) gl).glScalef (0.5f, 0.5f, 0.5f);
        // Draw the second sphere.
        this.drawSphere(gl, glu, 0.2f, moonTexture);

        // Restore (pop) from the stack the matrix holding the transformations produced by translating the first sphere.
        ((GLMatrixFunc) gl).glPopMatrix();

        // Restore (pop) from the stack the matrix holding the transformations prior to our translation of the first sphere.
        ((GLMatrixFunc) gl).glPopMatrix();

        gl.glFlush();

        // Increase the angle of rotation by 5 degrees.
        angle += 5;

        // Check which key was pressed
        if (keys[KeyEvent.VK_RIGHT]) {
            heading -= 3.0f;
            yrot = heading;
        }

        if (keys[KeyEvent.VK_LEFT]) {
            heading += 3.0f;
            yrot = heading;
        }

        if (keys[KeyEvent.VK_UP]) {

            xpos -= (float)Math.sin(Math.toRadians(heading)) * 0.1f; // Move On The X-Plane Based On Player Direction
            zpos -= (float)Math.cos(Math.toRadians(heading)) * 0.1f; // Move On The Z-Plane Based On Player Direction

            if (walkbiasangle >= 359.0f)
                walkbiasangle = 0.0f;
            else
                walkbiasangle += 10.0f;

            walkbias = (float)Math.sin(Math.toRadians(walkbiasangle))/20.0f; // Causes The Player To Bounce
        }

        if (keys[KeyEvent.VK_DOWN]) {

            xpos += (float)Math.sin(Math.toRadians(heading)) * 0.1f; // Move On The X-Plane Based On Player Direction
            zpos += (float)Math.cos(Math.toRadians(heading)) * 0.1f; // Move On The Z-Plane Based On Player Direction

            if (walkbiasangle <= 1.0f)
                walkbiasangle = 359.0f;
            else
                walkbiasangle -= 10.0f;

            walkbias = (float)Math.sin(Math.toRadians(walkbiasangle))/20.0f; // Causes The Player To Bounce
        }

        if (keys[KeyEvent.VK_PAGE_UP]) {
            lookupdown += 2.0f;
        }

        if (keys[KeyEvent.VK_PAGE_DOWN]) {
            lookupdown -= 2.0f;
        }
    }
    
    @SuppressWarnings("deprecation")
    public void drawSphere(GL gl, GLU glu, float radius, Texture texture) {
        ((GL2) gl).glEnable(GL.GL_TEXTURE_2D);
        texture.bind((GL2) gl);
        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricTexture(quadric, true);
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
        glu.gluSphere(quadric, radius, 32, 32);
        glu.gluDeleteQuadric(quadric);
        ((GL2) gl).glDisable(GL.GL_TEXTURE_2D);
    }

    public void reshape(GLAutoDrawable canvas, int left, int top, int width, int height)
    {
        GL2 gl = canvas.getGL().getGL2();

        // Selecting the viewport -- the display area -- to be the entire widget.
        gl.glViewport(0, 0, width, height);

        // Determining the width to height ratio of the widget.
        double ratio = (double) width / (double) height;

        // Selecting the projection matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

        gl.glLoadIdentity();

        // Selecting the view volume to be x from 0 to 1, y from 0 to 1, z from -1 to 1.
        // But we are careful to keep the aspect ratio and enlarging the width or the height.
        if (ratio < 1)
            gl.glOrtho(-v_size, v_size, -v_size, v_size / ratio, -1, 1);
        else
            gl.glOrtho(-v_size, v_size * ratio, -v_size, v_size, -1, 1);

        // Selecting the modelview matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);


    }

    public void displayChanged(GLAutoDrawable canvas, boolean modeChanged, boolean deviceChanged)
    {
        return;
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub
    }

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}
    
   }

    

/*
 * * BatchNode test    ##OK
 * 
 * 
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class Test_BatchNode extends SimpleApplication implements ActionListener
{
    private static int _count = 0;
    public static boolean isMousePressed = false;
    public static Vector2f mouseCoords = null;
    public static int score = 0;
    private BatchNode floor, objects;
    private Spatial mousePointer;
    private BitmapText scoreText;
    
    public static void main(String[] args)
    {
        final int ANDROID = 0;
        if (ANDROID == 0)
        {
            
            AppSettings settings = new AppSettings(true);
            settings.setResolution(1024, 768);
            settings.setTitle("Test 2");
            Test_BatchNode app = new Test_BatchNode();
            app.setSettings(settings);
            app.start();
        } else
        {
            Test_BatchNode app = new Test_BatchNode();
            app.start();
        }
    }
    
    @Override
    public void simpleInitApp()
    {
        // setup camera for 2D
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0, 0, 0.5f));
        getFlyByCamera().setEnabled(false);

        // turn off stats view (you can leave it on, if you want)
        //setDisplayStatView(false);
        //setDisplayFps(false);
        
        
        final int MAX = 10;
        floor = new BatchNode("floor");
        guiNode.attachChild(floor);
        for (int x = 0; x < MAX; x++)
        {
            for (int y = 0; y < MAX; y++)
            {
                Spatial s = getSpatial("tile");
                float width = s.getUserData("width");
                s.setLocalTranslation(x * width, y * width, 0);
                floor.attachChild(s);
            }
        }
        floor.move(-100, -100, 0);
        floor.batch();
        
        objects = new BatchNode("objects");
        guiNode.attachChild(objects);
        for (int c = 0; c < 10; c++)
        {
            Spatial s;
            if (Math.random() < 0.8)
            {
                s = getSpatial("head");
            } else
            {
                s = getSpatial("star");
            }
            float x = (float) (Math.random() * settings.getWidth());
            float y = (float) (Math.random() * settings.getHeight());
            s.setLocalTranslation(x, y, 0);
            objects.attachChild(s);
        }
        objects.batch();
        
        mousePointer = getSpatial("star");
        guiNode.attachChild(mousePointer);

        
        inputManager.addMapping("mousePressed", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "mousePressed");

        // Display a line of text with a default font
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize());
        scoreText.setLocalTranslation(10, settings.getHeight()-10, 0);
        guiNode.attachChild(scoreText);
    }
    
    private Spatial getSpatial(String name)
    {
        _count++;
        Node node = new Node(name + _count);
        Picture pic = new Picture(name + _count);
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/" + name + ".png");
        pic.setTexture(assetManager, tex, true);

        // adjust picture
        float width = tex.getImage().getWidth();
        float height = tex.getImage().getHeight();
        pic.setWidth(width);
        pic.setHeight(height);
        pic.move(-width / 2f, -height / 2f, 0);

        // add a material to the picture
        Material picMat = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        picMat.getAdditionalRenderState().setBlendMode(BlendMode.AlphaAdditive);
        node.setMaterial(picMat);

        // set the radius of the spatial
        // (using width only as a simple approximation)
        node.setUserData("radius", width / 2);
        node.setUserData("width", width);

        // attach the picture to the node and return it
        node.attachChild(pic);
        return node;
    }
    float fX = 0, fY = 0;
    
    @Override
    public void simpleUpdate(float tpf)
    {
        fX += 1f * tpf;
        fY += 1f * tpf;
        floor.setLocalTranslation((float) Math.sin(fX) * 100, (float) Math.cos(fY) * 100, 0);
        
    }
    
    @Override
    public void simpleRender(RenderManager rm)
    {
        scoreText.setText("TestText: " + score++);
        
        mouseCoords = inputManager.getCursorPosition();
        mousePointer.setLocalTranslation(mouseCoords.x, mouseCoords.y, 0);
        isMousePressed = false;
    }
    
    public void onAction(String name, boolean isPressed, float tpf)
    {
        if (name.equals("mousePressed"))
        {
            if (isPressed)
            {
                mouseCoords = inputManager.getCursorPosition();
                isMousePressed = true;
            }
        }
    }
}

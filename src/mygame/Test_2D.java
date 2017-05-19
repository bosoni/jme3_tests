/*
 * TODO:
 * * 2d testipeli eri kuvilla       ##OK
 * * androidilla sama               ##OK
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
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;


public class Test_2D extends SimpleApplication implements ActionListener
{
    private static int _count = 0;
    public static boolean isMousePressed = false;
    public static Vector2f mouseCoords = null;
    public static int score = 0;
    private Node floor, objects;
    private BitmapText scoreText;

    public static void main(String[] args)
    {
        final int ANDROID = 0;
        if (ANDROID == 0)
        {

            AppSettings settings = new AppSettings(true);
            settings.setResolution(1024, 768);
            settings.setTitle("Test 1");
            Test_2D app = new Test_2D();
            app.setSettings(settings);
            app.start();
        } else
        {
            Test_2D app = new Test_2D();
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
        setDisplayStatView(false);
        setDisplayFps(false);


        final int MAX = 10;
        floor = new Node("floor");
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

        objects = new Node("objects");
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
            s.addControl(new ObjectsControl(settings.getWidth(), settings.getHeight(),
                    (float) (Math.random() * settings.getWidth()), (float) (Math.random() * settings.getHeight()),
                    s));

            objects.attachChild(s);
        }

        inputManager.addMapping("mousePressed", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "mousePressed");

        // Display a line of text with a default font
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize());
        scoreText.setText("Score: 0");
        scoreText.setLocalTranslation(10, scoreText.getLineHeight(), 0);
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
        scoreText.setText("Score: " + score);

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
class ObjectsControl extends AbstractControl
{
    private int screenWidth, screenHeight;
    private float x, y, xp, yp, rotAdd;
    private boolean star;

    public ObjectsControl(int width, int height, float x, float y, Spatial obj)
    {
        this.screenWidth = width;
        this.screenHeight = height;
        this.x = x;
        this.y = y;
        this.star = obj.getName().contains("star");

        this.rotAdd = (float) Math.random() - 0.5f;
        rotAdd *= 0.01f;

        this.xp = (float) Math.random() - 0.5f;
        this.yp = (float) Math.random() - 0.5f;
        xp *= 100f;
        yp *= 100f;

    }

    @Override
    protected void controlUpdate(float tpf)
    {
        float r = spatial.getUserData("radius");

        if (x < -r || y < -r || x > screenWidth + r || y > screenHeight + r)
        {
            this.xp = (float) Math.random() - 0.5f;
            this.yp = (float) Math.random() - 0.5f;
            xp *= 100f;
            yp *= 100f;
            x += xp * tpf;
            y += yp * tpf;
        }

        x += xp * tpf;
        y += yp * tpf;
        spatial.setLocalTranslation(x, y, 0);
        spatial.rotate(0, 0, rotAdd);


        // tsekkaa jos painetaan kuvan päältä, hävitetään se
        if (Test_2D.isMousePressed)
        {
            if (Test_2D.mouseCoords.x > x - r && Test_2D.mouseCoords.x < x + r
                    && Test_2D.mouseCoords.y > y - r && Test_2D.mouseCoords.y < y + r)
            {
                spatial.removeFromParent();
                if (star)
                {
                    Test_2D.score += 100;
                } else
                {
                    Test_2D.score += 10;
                }
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp)
    {
    }
}

/*
 * * Texture Atlas -test        ##OK
 * 
 * ok atlas näyttäis toimivan mutta vain jos planet laittaa
 * rootNodeen. guiNodessa ei näy mitään. Mutta vaihdoin rootNoden bucketin GUI:ksi.
 * 
 * objects optimoidaan makeAtlasBatch metodilla, joka luo 1 geometryn ja 1 texturen
 * (eli tulee staattinen texture).
 * 
 * 
 * AtlasLoader luokka pystyy lataamaan atlas.txt:n jossa on x,y,w,h uv tiedot
 * ja luo oikeankokoisen imagen.
 * 
 * 
 * 
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.optimize.TextureAtlas;

public class Test_Atlas extends SimpleApplication implements ActionListener
{
    private static int _count = 0;
    public static boolean isMousePressed = false;
    public static Vector2f mouseCoords = null;
    private Node floor, objects;
    private Spatial mousePointer;
    private BitmapText text;
    AtlasLoader ukko = new AtlasLoader();

    public static void main(String[] args)
    {
        final int ANDROID = 0;
        if (ANDROID == 0)
        {
            AppSettings settings = new AppSettings(true);
            settings.setResolution(1024, 768);
            settings.setTitle("Test 2");
            Test_Atlas app = new Test_Atlas();
            app.setSettings(settings);
            app.start();
        } else
        {
            Test_Atlas app = new Test_Atlas();
            app.start();
        }
    }

    @Override
    public void simpleInitApp()
    {
        //flyCam.setMoveSpeed(500);
        //cam.setFrustumFar(10000);
        // setup camera for 2D
        //cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0, 0, 1000f));
        getFlyByCamera().setEnabled(false);


        // turn off stats view (you can leave it on, if you want)
        //setDisplayStatView(false);
        //setDisplayFps(false);

        final int MAX = 10;
        floor = new Node("floor");
        rootNode.attachChild(floor);
        for (int x = 0; x < MAX; x++)
        {
            for (int y = 0; y < MAX; y++)
            {
                Spatial s = createImage("tile");
                float width = s.getUserData("width");
                s.setLocalTranslation(x * width, y * width, 0);
                floor.attachChild(s);
            }
        }
        floor.move(-100, -100, 0);

        objects = new Node("objects");
        for (int c = 0; c < 20; c++)
        {
            Spatial s;
            if (Math.random() < 0.5)
            {
                s = createImage("head");
            } else
            {
                s = createImage("star");
            }
            float x = (float) (Math.random() * settings.getWidth());
            float y = (float) (Math.random() * settings.getHeight());
            s.setLocalTranslation(x, y, 0);
            objects.attachChild(s);
        }

        rootNode.setQueueBucket(RenderQueue.Bucket.Gui);


        final float MUL = 5;
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(MUL, MUL, MUL, 1));
        rootNode.addLight(al);

        Geometry geom = TextureAtlas.makeAtlasBatch(objects, assetManager, 1024);
        geom.getMaterial().getAdditionalRenderState().setDepthTest(false);
        geom.getMaterial().getAdditionalRenderState().setDepthWrite(false);
        //rootNode.attachChild(geom);


        //ukko.loadAtlas("Textures/textureAtlas.png.atlas", assetManager);
        ukko.loadAtlas("Textures/atlas.txt", assetManager);
        Spatial s = ukko.getImage("head");
        rootNode.attachChild(s);


        mousePointer = createImage("star");
        guiNode.attachChild(mousePointer);


        inputManager.addMapping("mousePressed", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "mousePressed");

        // Display a line of text with a default font
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        text.setLocalTranslation(10, settings.getHeight() - 10, 0);
        guiNode.attachChild(text);
    }

    private Spatial createImage(String name)
    {
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/" + name + ".png");
        float width = tex.getImage().getWidth();
        float height = tex.getImage().getHeight();

        Spatial obj = assetManager.loadModel("Models/plane.j3o");
        obj.setLocalScale(width / 2, height / 2, 1);

        // add a material to the picture
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        material.getAdditionalRenderState().setDepthWrite(false);
        material.getAdditionalRenderState().setDepthTest(false);
        material.setTexture("ColorMap", tex);
        obj.setMaterial(material);

        // set the radius of the spatial
        // (using width only as a simple approximation)
        obj.setUserData("radius", width / 2);
        obj.setUserData("width", width);

        return obj;
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
        text.setText("Cam: " + cam.getLocation().toString());

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

class AtlasLoader
{
    private HashMap<String, Spatial> images;

    public AtlasLoader()
    {
        images = new HashMap<String, Spatial>();
    }

    public void loadAtlas(String name, AssetManager assetManager)
    {
        images.clear();

        String atlasImg = "";
        String imgName = "";
        float x = 0, y = 0, w = 0, h = 0; // uv koordinaatit
        Texture2D tex = null;
        float width = 1, height = 1;
        try
        {
            InputStream inp = getClass().getResourceAsStream("/" + name);
            BufferedReader in = new BufferedReader(new InputStreamReader(inp));
            String str;
            while ((str = in.readLine()) != null)
            {
                // ota atlas kuvatiedoston nimi ekana
                if (str.length() > 0 && atlasImg.length() == 0)
                {
                    atlasImg = str;
                    tex = (Texture2D) assetManager.loadTexture(new TextureKey("Textures/" + atlasImg, false));
                    width = tex.getImage().getWidth();
                    height = tex.getImage().getHeight();
                    System.out.println("atlasImg> " + atlasImg);
                    continue;
                }

                // jos rivillä EI ole  :  merkkiä, se on kuvan nimi
                if (str.contains(":") == false)
                {
                    // jos ollaan otettu jo jonkun kuvan tiedot
                    if (imgName.length() > 0)
                    {
                        System.out.println("# luo spatial. ");
                        createSpatial(assetManager, tex, imgName, x, y, w, h, width, height);
                    }
                    imgName = str;
                    System.out.println("imgName> " + imgName);
                    continue;
                }

                if (str.contains("xy"))
                {
                    String[] tmp = str.split(":")[1].split(",");
                    x = Float.parseFloat(tmp[0]);
                    y = Float.parseFloat(tmp[1]);
                    System.out.println("xy = " + x + "  " + y);
                    continue;
                }

                if (str.contains("size"))
                {
                    String[] tmp = str.split(":")[1].split(",");
                    w = Float.parseFloat(tmp[0]);
                    h = Float.parseFloat(tmp[1]);
                    System.out.println("wh = " + w + "  " + h);
                    continue;
                }

            }
        } catch (IOException ex)
        {
            Logger.getLogger(AtlasLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        createSpatial(assetManager, tex, imgName, x, y, w, h, width, height);

    }

    private void createSpatial(AssetManager assetManager, Texture2D tex, String imgName, float x, float y, float w, float h, float width, float height)
    {
        // luo spatial image tiedoista
        Spatial tmp = assetManager.loadModel("Models/plane.j3o");
        Spatial obj = tmp.deepClone();
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        material.getAdditionalRenderState().setDepthWrite(false);
        material.getAdditionalRenderState().setDepthTest(false);
        material.setTexture("ColorMap", tex);
        obj.setMaterial(material);

        obj.setUserData("width", w);
        obj.setLocalScale(w, h, 0);

        Geometry g = (Geometry) obj;
        VertexBuffer uv = g.getMesh().getBuffer(Type.TexCoord);
        float[] uvArray = BufferUtils.getFloatArray((FloatBuffer) uv.getData());

        int i = 0;
        uvArray[i++] = (x + w) / width;
        uvArray[i++] = (y + h) / height;

        uvArray[i++] = (x + w) / width;
        uvArray[i++] = y / height;

        uvArray[i++] = x / width;
        uvArray[i++] = y / height;

        uvArray[i++] = x / width;
        uvArray[i++] = (y + h) / height;

        uv.updateData(BufferUtils.createFloatBuffer(uvArray));
        images.put(imgName, obj);
    }

    public Spatial getImage(String name)
    {
        return images.get(name);
    }
}

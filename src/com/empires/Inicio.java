/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires;

import com.empires.ControllerTerrain.LoadTerrain;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;


        //Logger. getLogger ( "" ) . setLevel ( Level. SEVERE ) ;
/**
 *
 * @author karlos
 */
public class Inicio extends SimpleApplication{
    private LoadTerrain loadTerrain;
    
    public static void main(String[] args) {
        AppSettings settingst = new AppSettings(true);
        //settingst.setResolution(1920, 1080);
        settingst.setResolution(1280, 700);
        //settingst.setResolution(800, 600);
        settingst.setVSync(false);
        //settingst.setFullscreen(true);
        settingst.setFullscreen(false);
        settingst.setTitle("LastSurvivor Editor");
        Inicio app=new Inicio();
        app.setSettings(settingst);
        app.setShowSettings(false);
        
        app.start();
    }
    public boolean isOgl() {
            return true;//Thread.currentThread() == thread;
    }
    private static Inicio staticApplication;

    public Inicio(){
        staticApplication = this;
    }

    public static Inicio getApplication(){
        return staticApplication;
    }

    @Override
    public void simpleInitApp() {
                // load sky
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/sky/BrightSky.dds", false));

        inputManager.setCursorVisible(true);
        
        addLights();
        //addFirstLevel();

        flyCam.setMoveSpeed(150);
        loadTerrain=new LoadTerrain(this,rootNode,viewPort,assetManager);
        loadTerrain.start();
    }
    
    
    public void addLights(){
        //Sun Light
        /*DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        rootNode.addLight(sun);*/
         DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        rootNode.addLight(sun);
    }
    
    public void addFirstLevel(){
        //loadTerrain("level1");
        //rootNode.attachChild(Terreno);
    }
    
    
   /* BufferedInputStream files;
    TerrainQuad Terreno;*
    private void loadTerrain(String level) {
        FileInputStream fis = null;
        BinaryImporter imp;
        fis = null;
        
        BufferedInputStream files;
        try {
            long start = System.currentTimeMillis();

            // import the saved terrain, and attach it back to the root node
            fis = new FileInputStream(new File("assets/Scenes/terrainsave.jme"));
            imp = BinaryImporter.getInstance();
            imp.setAssetManager(assetManager);
            //rootNode.attachChild(terrain);

            //float duration = (System.currentTimeMillis() - start) / 1000.0f;
            try{
                Texture alphe=assetManager.loadTexture("Textures/terrain-alpha/level1-terrain-level1-alphablend0.png");
                matRock.setTexture("AlphaMap",alphe);
               
                Terreno=(TerrainQuad) imp.load(new BufferedInputStream(fis));
                Terreno.setMaterial(matRock);
                float duration = (System.currentTimeMillis() - start) / 1000.0f;
                System.out.println("Terreno Carregado em "+duration+" segundos");
                //Terreno.setLocalTranslation(new Vector3f(pos.x,0,pos.z));
                //main.TCache.addTerrain(pos, Terreno);
               // System.out.println("Load took " + duration + " seconds");
            } catch(OutOfMemoryError ex){
               // return loadTerrain(QX, QZ);
               Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    imp=null;
                }
            } catch (IOException ex) {
                Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }*/
}
        
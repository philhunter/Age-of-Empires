/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires;

import com.empires.ControllerAction.ControllerKeys;
import com.empires.ControllerTerrain.LoadTerrain;
import com.empires.ControllerTerrain.TerrainEditor;
import com.empires.Debuger.Debuger;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import java.util.ConcurrentModificationException;


        //Logger. getLogger ( "" ) . setLevel ( Level. SEVERE ) ;
/**
 *
 * @author karlos
 */

public class Inicio extends SimpleApplication{
    
    LoadTerrain loadTerrain;
    ControllerKeys controllerKeys;
    public Debuger debug=new Debuger();
    private boolean debuging=true;
    private boolean activateTool=false;
    private static Inicio staticApplication;
    TerrainEditor terrainEditor;
    private Node SceneTerrain=new Node("Terrenos");
    private Node SceneObjects=new Node("Objectos");
    
    public Inicio(){
        staticApplication = this;
    }

    public void activateTerrainTool(boolean activate){
        activateTool=activate;
        
    }
    public static Inicio getApplication(){
        return staticApplication;
    }
    
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
    
    public boolean isDebug(){
        return debuging;
    }
    
    public Node getTerrain(){
        return SceneTerrain;
    }
    
    public Node getObjects(){
        return SceneObjects;
    }
    
    public TerrainEditor getTerrainEditor(){ 
        return terrainEditor;
    }
    public Vector3f getWorldIntersection() {
        Vector3f origin= cam.getWorldCoordinates(inputManager.getCursorPosition(), 3.0f);
        Vector3f direction= cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        try{
            getTerrain().collideWith(ray, results);
        
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            
            Quaternion q = new Quaternion();
            q.lookAt(closest.getContactNormal(), Vector3f.UNIT_Y);
            //mark.setLocalRotation(q);
            //Terrain.attachChild(mark);
            return closest.getContactPoint();
        }else{
            //Terrain.detachChild(mark);r
            //ToutchTerrain=false;
            return null;
        }
        }catch(ConcurrentModificationException ex){
        
        }
        return null;
    }
     //Posicao do rato
    /*public void getPositionMouse(){
        Vector3f origin= cam.getWorldCoordinates(inputManager.getCursorPosition(), 3.0f);
        Vector3f direction= cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        try{
            rootNode.collideWith(ray, results);
        
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            PosMouse=closest.getContactPoint();
            ToutchTerrain=true;
            mark.setLocalTranslation(PosMouse);
            Quaternion q = new Quaternion();
            q.lookAt(closest.getContactNormal(), Vector3f.UNIT_Y);
            mark.setLocalRotation(q);
            Terrain.attachChild(mark);
        }else{
            Terrain.detachChild(mark);
            ToutchTerrain=false;
        }
        }catch(ConcurrentModificationException ex){
        
        }
        
    }*/
    @Override
    public void simpleUpdate(float tpf) {
        if(activateTool){
            Vector3f pos=getWorldIntersection();
            if(pos!=null){
                terrainEditor.markerMoved(pos);
                textTopRight.setText("X: "+pos.x+"\nY: "+pos.y+"\nZ: "+pos.z);
                textTopLeft.setText("X: "+terrainEditor.getMarker().x+"\nY: "+terrainEditor.getMarker().y+"\nZ: "+terrainEditor.getMarker().z);
                
            }
            controllerKeys.simpleUpdate(tpf);
        }
            
    }

    @Override
    public void simpleInitApp() {
        loadWriteScreen();
        controllerKeys=new ControllerKeys(this);
        loadTerrain=new LoadTerrain(this,rootNode,viewPort,assetManager);
        rootNode.attachChild(getTerrain());
        rootNode.attachChild(getObjects());
        
        // load sky
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/sky/BrightSky.dds", false));

        controllerKeys.setCursor(true);
        
        
        flyCam.setMoveSpeed(150);
        
        loadTerrain.start();
        
        
        terrainEditor=new TerrainEditor(this);
        terrainEditor.addMarker(rootNode);
        
        textTopLeft.setText("X: "+terrainEditor.getMarker().x+"\nY: "+terrainEditor.getMarker().y+"\nZ: "+terrainEditor.getMarker().z);
        
    }
    Geometry markerPrimary;
    int radius=6;
    public void addMarker(Node parent) {
        if (markerPrimary == null) {
            markerPrimary = new Geometry("edit marker primary");
            Mesh m = new Sphere(8, 8, radius);
            markerPrimary.setMesh(m);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.getAdditionalRenderState().setWireframe(true);
            markerPrimary.setMaterial(mat);
            markerPrimary.setLocalTranslation(0,0,0);
            mat.setColor("Color", ColorRGBA.Green);
            parent.attachChild(markerPrimary);
            //debug("Adicionado Marker");
        }
        
    }
    protected BitmapText textTopRight;
    protected BitmapText textTopLeft;
    protected BitmapText textTopCenter;
    //Carrega o que pode escrever o ecra
    public void loadWriteScreen() {
        textTopRight = new BitmapText(guiFont, false);
        textTopRight.setSize(guiFont.getCharSet().getRenderedSize());
        textTopRight.setLocalTranslation(0, getCamera().getHeight(), 0);
        textTopRight.setText("");
        guiNode.attachChild(textTopRight);
        textTopLeft = new BitmapText(guiFont, false);
        textTopLeft.setSize(guiFont.getCharSet().getRenderedSize());
        textTopLeft.setLocalTranslation(getCamera().getWidth()-150, getCamera().getHeight(), 0);
        textTopLeft.setText("");
        guiNode.attachChild(textTopLeft);
        textTopCenter= new BitmapText(guiFont, false);
        
        textTopCenter.setSize(guiFont.getCharSet().getRenderedSize());

        textTopCenter.setLocalTranslation((getCamera().getWidth())/2-50, (getCamera().getHeight()), 0);

        textTopCenter.setText("");

        guiNode.attachChild(textTopCenter);
    }
    
    public void setTextCenter(String texto){
        textTopCenter.setText(texto);
    }
    

}
        
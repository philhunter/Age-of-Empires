/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerTerrain;

import com.empires.Inicio;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author karlos
 */
public class LoadTerrain extends Thread{
    private Terrain terrain;
    protected BitmapText hintText;
    private float grassScale = 64;
    private float dirtScale = 16;
    private float rockScale = 128;
    private Material matTerrain;
    private Material matWire;
    private AssetManager assetManager;
    private Inicio main;
    private Node rootNode;
    private Node sceneNode=new Node("Terreno");
    private ViewPort viewPort;
    private Material matRock;
    
    public LoadTerrain(Inicio main,Node rootNode,ViewPort viewPort,AssetManager assetManager){
       this.main=main;
       this.rootNode=rootNode;
       this.viewPort=viewPort;
       this.assetManager=assetManager;
    }
    
    FilterPostProcessor fpp;  
    public void iniciaFrog(){
        fpp=new FilterPostProcessor(assetManager);
        FogFilter fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(155);
        fog.setFogDensity(2.0f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);

    }
    @Override
    public void run(){
        LoadMaterial();
       
        addLights();
        loadTerrain("level1");
        //iniciaFrog();
        //createMap();
    }
    private void LoadMaterial(){
         // TERRAIN TEXTURE material
        //matRock = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        //matRock.setBoolean("useTriPlanarMapping", false);
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);
        matRock.setBoolean("WardIso", true);
        
        matRock.setTexture("AlphaMap",assetManager.loadTexture("Textures/terrain-alpha/level1-terrain-level1-alphablend0.png"));

        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/level1/Grass0146_5_thumbhuge.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap", grass);
        matRock.setFloat("DiffuseMap_0_scale", grassScale);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_1", dirt);
        matRock.setFloat("DiffuseMap_1_scale", dirtScale);

        
        // WIREFRAME material
        matWire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWire.getAdditionalRenderState().setWireframe(true);
        matWire.setColor("Color", ColorRGBA.Green);
    }
    BufferedInputStream files;
    TerrainQuad Terreno;
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
                Terreno.setName(level);
                float duration = (System.currentTimeMillis() - start) / 1000.0f;
                System.out.println("Terreno Carregado em "+duration+" segundos");
                main.getTerrain().attachChild((Node)Terreno);
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
    }

    private Camera getCamera(){
        return main.getCamera();
    }
    
    public void addLights(){
        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        main.getTerrain().addLight(sun);
        /*DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        rootNode.addLight(sun);*/
    }
}

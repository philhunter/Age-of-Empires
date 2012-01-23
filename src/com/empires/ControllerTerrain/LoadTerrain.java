/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerTerrain;

import com.empires.Inicio;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
    private ViewPort viewPort;
    private Material matRock;
    
    public LoadTerrain(Inicio main,Node rootNode,ViewPort viewPort,AssetManager assetManager){
       this.main=main;
       this.rootNode=rootNode;
       this.viewPort=viewPort;
       this.assetManager=assetManager;
    }
    
    FilterPostProcessor fpp;
    FogFilter fog;
    public void iniciaFrog(){
        fpp=new FilterPostProcessor(assetManager);
        //fpp.setNumSamples(4);
        fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(300);
        fog.setFogDensity(1.0f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }
    @Override
    public void run(){
        LoadMaterial();
        iniciaFrog();
        loadTerrain("level1");
        //createMap();
    }
    private void LoadMaterial(){
         // TERRAIN TEXTURE material
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        //matRock.setBoolean("useTriPlanarMapping", false);

        matRock.setTexture("AlphaMap",assetManager.loadTexture("Textures/terrain-alpha/level1-terrain-level1-alphablend0.png"));

        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/level1/Grass0146_5_thumbhuge.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_1", grass);
        matRock.setFloat("DiffuseMap_1_scale", grassScale);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap", dirt);
        matRock.setFloat("DiffuseMap_0_scale", dirtScale);

        
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
                float duration = (System.currentTimeMillis() - start) / 1000.0f;
                System.out.println("Terreno Carregado em "+duration+" segundos");
                rootNode.attachChild((Node)Terreno);
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
    private void createMap() {
        matTerrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matTerrain.setBoolean("useTriPlanarMapping", false);
        matTerrain.setBoolean("WardIso", true);

        // ALPHA map (for splat textures)
        matTerrain.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");

        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap", grass);
        matTerrain.setFloat("DiffuseMap_0_scale", grassScale);


        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_1", dirt);
        matTerrain.setFloat("DiffuseMap_1_scale", dirtScale);

        // ROCK texture
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_2", rock);
        matTerrain.setFloat("DiffuseMap_2_scale", rockScale);


        Texture normalMap0 = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
        normalMap0.setWrap(WrapMode.Repeat);
        Texture normalMap1 = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        normalMap1.setWrap(WrapMode.Repeat);
        Texture normalMap2 = assetManager.loadTexture("Textures/Terrain/splat/road_normal.png");
        normalMap2.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("NormalMap", normalMap0);
        matTerrain.setTexture("NormalMap_1", normalMap2);
        matTerrain.setTexture("NormalMap_2", normalMap2);

        matWire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWire.getAdditionalRenderState().setWireframe(true);
        matWire.setColor("Color", ColorRGBA.Green);


        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
            heightmap.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (new File("terrainsave.jme").exists()) {
            loadTerrain();
        } else {
            // create the terrain as normal, and give it a control for LOD management
            TerrainQuad terrainQuad = new TerrainQuad("terrain", 65, 129, heightmap.getHeightMap());//, new LodPerspectiveCalculatorFactory(getCamera(), 4)); // add this in to see it use entropy for LOD calculations
            TerrainLodControl control = new TerrainLodControl(terrainQuad, getCamera());
            control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
            terrainQuad.addControl(control);
            terrainQuad.setMaterial(matTerrain);
            terrainQuad.setLocalTranslation(0, -100, 0);
            terrainQuad.setLocalScale(4f, 0.25f, 4f);
            rootNode.attachChild(terrainQuad);
            
            this.terrain = terrainQuad;
        }
    }
    private void loadTerrain() {
        FileInputStream fis = null;
        try {
            long start = System.currentTimeMillis();
            // remove the existing terrain and detach it from the root node.
            if (terrain != null) {
                Node existingTerrain = (Node)terrain;
                existingTerrain.removeFromParent();
                existingTerrain.removeControl(TerrainLodControl.class);
                existingTerrain.detachAllChildren();
                terrain = null;
            }

            // import the saved terrain, and attach it back to the root node
            File f = new File("terrainsave.jme");
            fis = new FileInputStream(f);
            BinaryImporter imp = BinaryImporter.getInstance();
            imp.setAssetManager(assetManager);
            terrain = (TerrainQuad) imp.load(new BufferedInputStream(fis));
            rootNode.attachChild((Node)terrain);

            float duration = (System.currentTimeMillis() - start) / 1000.0f;
            System.out.println("Load took " + duration + " seconds");

            // now we have to add back the camera to the LOD control
            TerrainLodControl lodControl = ((Node)terrain).getControl(TerrainLodControl.class);
            if (lodControl != null)
                lodControl.setCamera(getCamera());

        } catch (IOException ex) {
            Logger.getLogger(LoadTerrain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LoadTerrain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private Camera getCamera(){
        return main.getCamera();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerTerrain;

import com.empires.Inicio;
import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Texture;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import jme3tools.converters.ImageToAwt;

/**
 *
 * @author karlos
 */
public class TerrainEditor {
    Node rootNode;
    Inicio main;
    private Node terrainNode;
    AssetManager assetManager;
    
    private synchronized void doSaveAlphaImages(Terrain terrain) {

        if (terrain == null) {
            getTerrain(rootNode);
            return;
        }

        Texture alpha1 = doGetAlphaTexture(terrain, 0);
        BufferedImage bi1 = ImageToAwt.convert(alpha1.getImage(), false, true, 0);
        File imageFile1 = new File("assets/"+alpha1.getKey().getName());
        Texture alpha2 = doGetAlphaTexture(terrain, 1);
        BufferedImage bi2 = ImageToAwt.convert(alpha2.getImage(), false, true, 0);
        File imageFile2 = new File("assets/"+alpha2.getKey().getName());
        Texture alpha3 = doGetAlphaTexture(terrain, 2);
        BufferedImage bi3 = ImageToAwt.convert(alpha3.getImage(), false, true, 0);
        File imageFile3 = new File("assets/"+alpha3.getKey().getName());
        try {
            ImageIO.write(bi1, "png", imageFile1);
            ImageIO.write(bi2, "png", imageFile2);
            ImageIO.write(bi3, "png", imageFile3);
        } catch (IOException ex) {
            //Exceptions.printStackTrace(ex);
        }
        
    }
        
    private Texture doGetAlphaTexture(Terrain terrain, int alphaLayer) {
        if (terrain == null)
            return null;
        MatParam matParam = null;
        if (alphaLayer == 0)
            matParam = terrain.getMaterial().getParam("AlphaMap");
        else if(alphaLayer == 1)
            matParam = terrain.getMaterial().getParam("AlphaMap_1");
        else if(alphaLayer == 2)
            matParam = terrain.getMaterial().getParam("AlphaMap_2");
        
        if (matParam == null || matParam.getValue() == null) {
            return null;
        }
        Texture tex = (Texture) matParam.getValue();
        return tex;
    }
        
    public Node getTerrain(Spatial root) {
        if (terrainNode != null)
            return terrainNode;

        if (root == null)
            root = rootNode;

        // is this the terrain?
        if (root instanceof Terrain && root instanceof Node) {
            terrainNode = (Node)root;
            return terrainNode;
        }

        if (root instanceof Node) {
            Node n = (Node) root;
            for (Spatial c : n.getChildren()) {
                if (c instanceof Node){
                    Node res = getTerrain(c);
                    if (res != null)
                        return res;
                }
            }
        }

        return terrainNode;
    }
}

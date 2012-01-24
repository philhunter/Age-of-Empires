/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerTerrain;

import com.empires.ControllerTerrain.Tools.TerrainTool;
import com.empires.Inicio;
import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.ProgressMonitor;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.IntMap.Entry;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import jme3tools.converters.ImageToAwt;

/**
 *
 * @author karlos
 */
public class TerrainEditor {
    Node rootNode;
    Inicio main;
    TerrainTool terrainTool;
    
    
    private Node terrainNode;
    AssetManager assetManager;
    private boolean NeedToSave=false;
    private String DEFAULT_TERRAIN_TEXTURE="";
    
    public TerrainEditor(Inicio main){
        this.main=main;
        this.assetManager=main.getAssetManager();
        this.rootNode=main.getTerrain();
        
    }
    
    public void moveMarke(Vector3f newLoc){
        markerMoved(newLoc);
    }
    
    public void setNeedsSave(boolean save){
        this.NeedToSave=save;
    }
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
        
    /**
     * Perform the actual height modification on the terrain.
     * @param worldLoc the location in the world where the tool was activated
     * @param radius of the tool, terrain in this radius will be affected
     * @param heightFactor the amount to adjust the height by
     */
    public void doModifyTerrainHeight(Vector3f worldLoc, float radius, float heightFactor) {

        Terrain terrain = (Terrain) getTerrain(null);
        if (terrain == null)
            return;

        setNeedsSave(true);

        int radiusStepsX = (int) (radius / ((Node)terrain).getLocalScale().x);
        int radiusStepsZ = (int) (radius / ((Node)terrain).getLocalScale().z);

        float xStepAmount = ((Node)terrain).getLocalScale().x;
        float zStepAmount = ((Node)terrain).getLocalScale().z;

        List<Vector2f> locs = new ArrayList<Vector2f>();
        List<Float> heights = new ArrayList<Float>();

        for (int z=-radiusStepsZ; z<radiusStepsZ; z++) {
            for (int x=-radiusStepsZ; x<radiusStepsX; x++) {

                float locX = worldLoc.x + (x*xStepAmount);
                float locZ = worldLoc.z + (z*zStepAmount);

                // see if it is in the radius of the tool
                if (isInRadius(locX-worldLoc.x,locZ-worldLoc.z,radius)) {
                    // adjust height based on radius of the tool
                    float h = calculateHeight(radius, heightFactor, locX-worldLoc.x, locZ-worldLoc.z);
                    // increase the height
                    locs.add(new Vector2f(locX, locZ));
                    heights.add(h);
                }
            }
        }

        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);

        ((Node)terrain).updateModelBound(); // or else we won't collide with it where we just edited
        
    }

    /**
     * See if the X,Y coordinate is in the radius of the circle. It is assumed
     * that the "grid" being tested is located at 0,0 and its dimensions are 2*radius.
     * @param x
     * @param z
     * @param radius
     * @return
     */
    private boolean isInRadius(float x, float y, float radius) {
        Vector2f point = new Vector2f(x,y);
        // return true if the distance is less than equal to the radius
        return Math.abs(point.length()) <= radius;
    }

    /**
     * Interpolate the height value based on its distance from the center (how far along
     * the radius it is).
     * The farther from the center, the less the height will be.
     * This produces a linear height falloff.
     * @param radius of the tool
     * @param heightFactor potential height value to be adjusted
     * @param x location
     * @param z location
     * @return the adjusted height value
     */
    private float calculateHeight(float radius, float heightFactor, float x, float z) {
        float val = calculateRadiusPercent(radius, x, z);
        return heightFactor * val;
    }

    private float calculateRadiusPercent(float radius, float x, float z) {
         // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x,z);
        float val = Math.abs(point.length()) / radius;
        val = 1f - val;
        return val;
    }

    public void cleanup() {
        terrainNode = null;
        rootNode = null;
    }

    /**
     * pre-calculate the terrain's entropy values
     */
    public void generateEntropies(final ProgressMonitor progressMonitor) {
        if (Inicio.getApplication().isOgl()) {//
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;

            terrain.generateEntropy(progressMonitor);
        } else {
            Inicio.getApplication().enqueue(new Callable<Object>() {

                public Object call() throws Exception {
                    generateEntropies(progressMonitor);
                    return null;
                }
            });
        }
    }

    /**
     * Get the scale of the texture at the specified layer.
     * Blocks on the OGL thread
     */
    public Float getTextureScale(final int layer) {
        if (Inicio.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return 1f;
            MatParam matParam = null;
            matParam = terrain.getMaterial().getParam("DiffuseMap_"+layer+"_scale");
            if (matParam == null)
                return -1f;
            return (Float) matParam.getValue();
        } else {
            try {
                Float scale =
                    Inicio.getApplication().enqueue(new Callable<Float>() {
                        public Float call() throws Exception {
                            return getTextureScale(layer);
                        }
                    }).get();
                    return scale;
            } catch (InterruptedException ex) {
                //Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }


    /**
     * Set the scale of a texture at the specified layer
     * Blocks on the OGL thread
     */
    public void setTextureScale(final int layer, final float scale) {
        if (Inicio.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            terrain.getMaterial().setFloat("DiffuseMap_"+layer+"_scale", scale);
            setNeedsSave(true);
        } else {
            try {
                Inicio.getApplication().enqueue(new Callable() {
                    public Object call() throws Exception {
                        setTextureScale(layer, scale);
                        return null;
                    }
                }).get();
            } catch (InterruptedException ex) {
                //Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
            }
        }
    }


    /**
     * Get the diffuse texture at the specified layer.
     * Blocks on the GL thread!
     */
    public Texture getDiffuseTexture(final int layer) {
        if (Inicio.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return null;
            MatParam matParam = null;
            if (layer == 0)
                matParam = terrain.getMaterial().getParam("DiffuseMap");
            else
                matParam = terrain.getMaterial().getParam("DiffuseMap_"+layer);

            if (matParam == null || matParam.getValue() == null) {
                return null;
            }
            Texture tex = (Texture) matParam.getValue();

            return tex;
        } else {
            try {
                Texture tex =
                    Inicio.getApplication().enqueue(new Callable<Texture>() {
                        public Texture call() throws Exception {
                            return getDiffuseTexture(layer);
                        }
                    }).get();
                    return tex;
            } catch (InterruptedException ex) {
                //Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
            }
            return null;
        }
    }

    /**
     * Set the diffuse texture at the specified layer.
     * Blocks on the GL thread
     * @param layer number to set the texture
     * @param texturePath if null, the default texture will be used
     */
    public void setDiffuseTexture(final int layer, final String texturePath) {
        String path = texturePath;
        if (texturePath == null || texturePath.equals(""))
            path = DEFAULT_TERRAIN_TEXTURE;
        
        Texture tex = Inicio.getApplication().getAssetManager().loadTexture(path);
        setDiffuseTexture(layer, tex);
    }
    
    /**
     * Set the diffuse texture at the specified layer.
     * Blocks on the GL thread
     * @param layer number to set the texture
     */
    public void setDiffuseTexture(final int layer, final Texture texture) {
        if (Inicio.getApplication().isOgl()) {
            texture.setWrap(WrapMode.Repeat);
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            if (layer == 0)
                terrain.getMaterial().setTexture("DiffuseMap", texture);
            else
                terrain.getMaterial().setTexture("DiffuseMap_"+layer, texture);

            setNeedsSave(true);
        } else {
            try {
                Inicio.getApplication().enqueue(new Callable() {
                    public Object call() throws Exception {
                        setDiffuseTexture(layer, texture);
                        return null;
                    }
                }).get();
            } catch (InterruptedException ex) {
                //Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
            //    Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Remove a whole texture layer: diffuse and normal map
     * @param layer
     * @param texturePath
     */
    public void removeTextureLayer(final int layer) {
        if (Inicio.getApplication().isOgl()) {
            doRemoveDiffuseTexture(layer);
            doRemoveNormalMap(layer);
        } else {
            try {
                Inicio.getApplication().enqueue(new Callable() {
                    public Object call() throws Exception {
                        removeTextureLayer(layer);
                        return null;
                    }
                }).get();
            } catch (InterruptedException ex) {
                //Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
            }
        }
    }

    private void doRemoveDiffuseTexture(int layer) {
        Terrain terrain = (Terrain) getTerrain(null);
        if (terrain == null)
            return;
        if (layer == 0)
            terrain.getMaterial().clearParam("DiffuseMap");
        else
            terrain.getMaterial().clearParam("DiffuseMap_"+layer);

        setNeedsSave(true);
    }

    
    private void doRemoveNormalMap(int layer) {
        Terrain terrain = (Terrain) getTerrain(null);
        if (terrain == null)
            return;
        if (layer == 0)
            terrain.getMaterial().clearParam("NormalMap");
        else
            terrain.getMaterial().clearParam("NormalMap_"+layer);

        setNeedsSave(true);
    }

    /**
     * Get the normal map texture at the specified layer.
     * Run this on the GL thread!
     */
    public Texture getNormalMap(final int layer) throws ExecutionException {
        if (Inicio.getApplication().isOgl()) {//Inicio.getApplication().isOgl()
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return null;
            MatParam matParam = null;
            if (layer == 0)
                matParam = terrain.getMaterial().getParam("NormalMap");
            else
                matParam = terrain.getMaterial().getParam("NormalMap_"+layer);

            if (matParam == null || matParam.getValue() == null) {
                return null;
            }
            Texture tex = (Texture) matParam.getValue();
            return tex;
        } else {
            try {
                Texture tex =
                    Inicio.getApplication().enqueue(new Callable<Texture>() {
                        public Texture call() throws Exception {
                            return getNormalMap(layer);
                        }
                    }).get();
                    return tex;
            } catch (InterruptedException ex) {
                //Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    /**
     * Set the normal map at the specified layer.
     * Blocks on the GL thread
     */
    public void setNormalMap(final int layer, final String texturePath) {
        if (texturePath != null) {
            Texture tex = assetManager.loadTexture(texturePath);
            setNormalMap(layer, tex);
        } else {
            setNormalMap(layer, (Texture)null);
        }
        /*try {
            SceneApplication.getApplication().enqueue(new Callable() {
                public Object call() throws Exception {
                    doSetNormalMap(layer, texturePath);
                    return null;
                }
            }).get();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }*/
    }

    /**
     * Set the normal map texture at the specified layer
     */
    public void setNormalMap(final int layer, final Texture texture) {
        if (Inicio.getApplication().isOgl()) {//SceneApplication.getApplication().isOgl()
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            if (texture == null) {
                // remove the texture if it is null
                if (layer == 0)
                    terrain.getMaterial().clearParam("NormalMap");
                else
                    terrain.getMaterial().clearParam("NormalMap_"+layer);
                return;
            }

            texture.setWrap(WrapMode.Repeat);

            if (layer == 0)
                terrain.getMaterial().setTexture("NormalMap", texture);
            else
                terrain.getMaterial().setTexture("NormalMap_"+layer, texture);

            setNeedsSave(true);
        } else {
            try {
                Inicio.getApplication().enqueue(new Callable() {
                    public Object call() throws Exception {
                        setNormalMap(layer, texture);
                        return null;
                    }
                }).get();
            } catch (InterruptedException ex) {
                //Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
            }
        }
    }
    
    public float getRadius(){
        return radius;
    }
    public float getHeight(){
        return height;
    }
    
    public void setRadius(float radius){
        this.radius=radius;
    }
    public void setHeight(float height){
        this.height=height;
    }
    
    
    Geometry markerPrimary;
    Geometry markerSecondary;
    float radius=10;
    float weight;
    float height=10;
    float maxToolSize = 20; // override in sub classes
    
    /**
     * The tool was selected, start showing the marker.
     * @param manager
     * @param parent node that the marker will attach to
     */
    public void activate(Node parent) {
        //addMarker(parent);
    }
    

    /**
     * Location of the primary editor marker
     */
    public Vector3f getMarkerPrimaryLocation() {
        if (markerPrimary != null)
            return markerPrimary.getLocalTranslation();
        else
            return null;
    }
    
    /**
     * Move the marker to a new location, usually follows the mouse
     * @param newLoc 
     */
    public void markerMoved(Vector3f newLoc) {
        if (markerPrimary != null)
            markerPrimary.setLocalTranslation(newLoc);
    }
    
    /**
     * The radius of the tool has changed, so update the marker
     * @param radius percentage of the max radius
     */
    public void radiusChanged(float radius) {
        this.radius = maxToolSize*radius;
        
        if (markerPrimary != null) {
            for (Entry e: markerPrimary.getMesh().getBuffers())
                ((VertexBuffer)e.getValue()).resetObject();
            ((Sphere)markerPrimary.getMesh()).updateGeometry(8, 8, this.radius);
        }
    }
    
    /**
     * The weight of the tool has changed. Optionally change
     * the marker look.
     * @param weight percent
     */
    public void weightChanged(float weight) {
        this.weight = weight;
    }
    
    /**
     * Create the primary marker mesh, follows the mouse.
     * @param parent it will attach to
     */
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
            debug("Adicionado Marker");
        }
        
    }
    public Vector3f getMarker(){
        return markerPrimary.getLocalTranslation();
    }
    /**
     * Create the secondary marker mesh, placed
     * with the right mouse button.
     * @param parent it will attach to
     */
    public void addMarkerSecondary(Node parent) {
        if (markerSecondary == null) {
            markerSecondary = new Geometry("edit marker secondary");
            Mesh m2 = new Sphere(8, 8, 0.5f);
            markerSecondary.setMesh(m2);
            Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat2.getAdditionalRenderState().setWireframe(false);
            markerSecondary.setMaterial(mat2);
            markerSecondary.setLocalTranslation(0,0,0);
            mat2.setColor("Color", ColorRGBA.Red);
        }
        parent.attachChild(markerSecondary);
    }
    
    /**
     * Remove the markers from the scene.
     */
    public void hideMarkers() {
        if (markerPrimary != null)
            markerPrimary.removeFromParent();
        if (markerSecondary != null)
            markerSecondary.removeFromParent();
    }


    public void debug(String texto){
        if(main.isDebug()){
            main.debug.print(texto);
        }
    }
}

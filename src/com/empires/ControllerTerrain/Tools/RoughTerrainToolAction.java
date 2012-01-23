/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerTerrain.Tools;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.noise.Basis;
import com.jme3.terrain.noise.ShaderUtils;
import com.jme3.terrain.noise.basis.FilteredBasis;
import com.jme3.terrain.noise.filter.IterativeFilter;
import com.jme3.terrain.noise.filter.OptimizedErode;
import com.jme3.terrain.noise.filter.PerturbFilter;
import com.jme3.terrain.noise.filter.SmoothFilter;
import com.jme3.terrain.noise.fractal.FractalSum;
import com.jme3.terrain.noise.modulator.NoiseModulator;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sploreg
 */
public class RoughTerrainToolAction extends AbstractTerrainToolAction {
    
    private Vector3f worldLoc;
    private float radius;
    private float weight;
    private RoughExtraToolParams params;
    String name;
    List<Vector2f> undoLocs;
    List<Float> undoHeights;

    public RoughTerrainToolAction(Vector3f markerLocation, float radius, float weight, ExtraToolParams params) {
        this.worldLoc = markerLocation.clone();
        this.radius = radius;
        this.weight = weight;
        this.params = (RoughExtraToolParams)params;
        name = "Rough terrain";
    }

    protected Object doApplyTool(Node rootNode) {
        Terrain terrain =null;// getTerrain(rootNode.getLookup().lookup(Node.class));
        if (terrain == null)
            return null;
        roughen(terrain, radius, weight, params);
        return terrain;
    }

    protected void doUndoTool(Node rootNode, Object undoObject) {
        if (undoObject == null)
            return;
        if (undoLocs == null || undoHeights == null)
            return;
        resetHeight((Terrain)undoObject, undoLocs, undoHeights);
    }
    
    private void roughen(Terrain terrain, float radius, float weight, RoughExtraToolParams params) {
        Basis fractalFilter = createFractalGenerator(params, weight);
        
        List<Vector2f> locs = new ArrayList<Vector2f>();
        List<Float> heights = new ArrayList<Float>();
        
        // offset it by radius because in the loop we iterate through 2 radii
        int radiusStepsX = (int) (radius / ((Node)terrain).getLocalScale().x);
        int radiusStepsZ = (int) (radius / ((Node)terrain).getLocalScale().z);
        float xStepAmount = ((Node)terrain).getLocalScale().x;
        float zStepAmount = ((Node)terrain).getLocalScale().z;
        
        int r2 = (int) (radius*2);
        FloatBuffer fb = fractalFilter.getBuffer(worldLoc.x, worldLoc.z, 0, r2);
        
        //for (int y=0; y<r2; y++) {
        //    for (int x=0; x<r2; x++) {
        int xfb=0,yfb=0;
        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {
                
                float locX = worldLoc.x + (x * xStepAmount);
                float locZ = worldLoc.z + (z * zStepAmount);
                
                float height = fb.get(yfb*r2 + xfb);
                
                if (isInRadius(locX - worldLoc.x, locZ - worldLoc.z, radius)) {
                    // see if it is in the radius of the tool
                    float h = calculateHeight(radius, height, locX - worldLoc.x, locZ - worldLoc.z);
                    locs.add(new Vector2f(locX, locZ));
                    heights.add(h);
                }
                xfb++;
            }
            yfb++;
            xfb = 0;
        }
        
        undoLocs = locs;
        undoHeights = heights;
        
        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);

        ((Node)terrain).updateModelBound(); // or else we won't collide with it where we just edited
    }
    
    private boolean isInRadius(float x, float y, float radius) {
        Vector2f point = new Vector2f(x, y);
        // return true if the distance is less than equal to the radius
        return point.length() <= radius;
    }

    private float calculateHeight(float radius, float heightFactor, float x, float z) {
        // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x, z);
        float val = point.length() / radius;
        val = 1 - val;
        if (val <= 0) {
            val = 0;
        }
        return heightFactor * val * 0.1f; // 0.1 scales it down a bit to lower the impact of the tool
    }
    
    private void resetHeight(Terrain terrain, List<Vector2f> undoLocs, List<Float> undoHeights) {
        List<Float> neg = new ArrayList<Float>();
        for (Float f : undoHeights)
            neg.add( f * -1f );
        
        terrain.adjustHeight(undoLocs, neg);
        ((Node)terrain).updateModelBound();
    }
    
    private Basis createFractalGenerator(RoughExtraToolParams params, float weight) {
        FractalSum base = new FractalSum();
        base.setRoughness(params.roughness);
        base.setFrequency(params.frequency);
        base.setAmplitude(weight);
        base.setLacunarity(params.lacunarity <= 1 ? 1.1f : params.lacunarity); // make it greater than 1.0f
        base.setOctaves(params.octaves);
        float scale = params.scale;
        if (scale > 1.0f)
            scale = 1.0f;
        if (scale < 0)
            scale = 0;
        base.setScale(scale);//0.02125f
        base.addModulator(new NoiseModulator() {
            @Override
            public float value(float... in) {
                return ShaderUtils.clamp(in[0] * 0.5f + 0.5f, 0, 1);
            }
        });

        FilteredBasis ground = new FilteredBasis(base);

        PerturbFilter perturb = new PerturbFilter();
        perturb.setMagnitude(0.2f);//0.119 the higher, the slower it is

        OptimizedErode therm = new OptimizedErode();
        therm.setRadius(5);
        therm.setTalus(0.011f);

        SmoothFilter smooth = new SmoothFilter();
        smooth.setRadius(1);
        smooth.setEffect(0.1f); // 0.7

        IterativeFilter iterate = new IterativeFilter();
        iterate.addPreFilter(perturb);
        iterate.addPostFilter(smooth);
        iterate.setFilter(therm);
        iterate.setIterations(1);

        ground.addPreFilter(iterate);
        
        return ground;
    }
}

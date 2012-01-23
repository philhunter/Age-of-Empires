/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerTerrain.Tools;


import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;


/**
 * Roughens the terrain using a fractal noise routine.
 * @author Brent Owens
 */
public class RoughTerrainTool extends TerrainTool {
    
    private RoughExtraToolParams params;
    
    @Override
    public void actionPrimary(Vector3f point, int textureIndex, Node rootNode) {
        if (radius == 0 || weight == 0)
            return;
        RoughTerrainToolAction action = new RoughTerrainToolAction(point, radius, weight, (RoughExtraToolParams)params);
        //action.doActionPerformed(rootNode, dataObject);
    }

    @Override
    public void actionSecondary(Vector3f point, int textureIndex, Node rootNode) {
        // do nothing
    }
    
    @Override
    public void addMarkerPrimary(Node parent) {
        super.addMarkerPrimary(parent);
        markerPrimary.getMaterial().setColor("Color", ColorRGBA.Yellow);
    }
    
    @Override
    public void setExtraParams(ExtraToolParams params) {
        this.params = (RoughExtraToolParams) params;
    }
    
    @Override
    public ExtraToolParams getExtraParams() {
        return params;
    }
    
    @Override
    public void extraParamsChanged(ExtraToolParams params) {
        this.params = (RoughExtraToolParams) params;
    }
}

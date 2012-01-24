/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerTerrain;

import java.util.ArrayList;

/**
 *
 * @author karlos
 */
public class TerrainActionCache {
    ArrayList<String[]> Cache=new ArrayList<String[]>();
    boolean addedNew=false;
    int positionCache=0;
    public void addNew(String[] Cache){
        if(!addedNew && positionCache>0){
            for (int i=positionCache;i>=this.Cache.size();i++)
                this.Cache.remove(i);
        }
        this.Cache.add(Cache);
        addedNew=true;
        positionCache++;
    }
    public String[] getAfterChange(){
        addedNew=false;
        positionCache++;
        return Cache.get(positionCache);
    }
    public String[] getLastChange(){
        addedNew=false;
        positionCache--;
        return Cache.get(positionCache);
    }
    
}

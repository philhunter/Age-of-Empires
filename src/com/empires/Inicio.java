/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

        //Logger. getLogger ( "" ) . setLevel ( Level. SEVERE ) ;
/**
 *
 * @author karlos
 */
public class Inicio extends SimpleApplication{

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
    @Override
    public void simpleInitApp() {
        
    }
    
    
}
        
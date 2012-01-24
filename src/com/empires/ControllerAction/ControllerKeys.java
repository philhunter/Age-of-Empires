/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerAction;

import com.empires.ControllerTerrain.TerrainActionCache;
import com.empires.ControllerTerrain.Tools.LevelTerrainToolAction;
import com.empires.ControllerTerrain.Tools.PaintTerrainToolAction;
import com.empires.ControllerTerrain.Tools.RaiseTerrainToolAction;
import com.empires.ControllerTerrain.Tools.RoughTerrainToolAction;
import com.empires.ControllerTerrain.Tools.SmoothTerrainToolAction;
import com.empires.Inicio;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;


/**
 *
 * @author karlos
 */
public class ControllerKeys implements ActionListener{
    Inicio main;
    private RaiseTerrainToolAction raiseTerrain;
    private TerrainActionCache terrainActionCache=new TerrainActionCache();
    private boolean cursor=false;
    private String toolSelected="Raise";
    private boolean click=false;
    public ControllerKeys(Inicio main){
        this.main=main;
        initializeKeys();
        
    }
    public void simpleUpdate(float tpf){
        if(click){
            Vector3f pos=main.getWorldIntersection();
            if(pos!=null)
                if(toolSelected.equals("Raise")){
                    raiseTerrain=new RaiseTerrainToolAction(pos, main.getTerrainEditor().getRadius(), main.getTerrainEditor().getHeight());
                    raiseTerrain.Raise(main);
                    debug("Aumentado a Altura");
                }else if(toolSelected.equals("Lower")){
                    raiseTerrain=new RaiseTerrainToolAction(pos, main.getTerrainEditor().getRadius(), -main.getTerrainEditor().getHeight());
                    raiseTerrain.Raise(main);
                    debug("Diminuido a Altura");
                }else if(toolSelected.equals("Paint")){
                    PaintTerrainToolAction paint=new PaintTerrainToolAction(pos, main.getTerrainEditor().getRadius(), main.getTerrainEditor().getHeight(),1);
                    paint.Paint(main);
                    debug("Pintado");
                }else if(toolSelected.equals("RemovePaint")){
                    PaintTerrainToolAction paint=new PaintTerrainToolAction(pos, main.getTerrainEditor().getRadius(), -main.getTerrainEditor().getHeight(),1);
                    paint.Paint(main);
                    debug("Removido Pintura");
                }else if(toolSelected.equals("Level")){
                    LevelTerrainToolAction level=new LevelTerrainToolAction(pos, main.getTerrainEditor().getRadius(), 0.1f,pos.mult(15));
                    level.Level(main);
                    debug("Nivelar");
                }else if(toolSelected.equals("Smooth")){
                    SmoothTerrainToolAction Smooth=new SmoothTerrainToolAction(pos, main.getTerrainEditor().getRadius(), 0.50f);
                    Smooth.Smooth(main);
                    debug("Smooth");
                }/*else if(toolSelected.equals("Roughen")){
                    RoughTerrainToolAction paint=new RoughTerrainToolAction(pos, main.getTerrainEditor().getRadius(), -main.getTerrainEditor().getHeight());
                    paint.Paint(main);
                    debug("Removido Pintura");
                }*/
        }
    }
    public void setCursor(boolean val){
        main.getInputManager().setCursorVisible(val);
        cursor=val;
        debug("Cursor alterado");
    }

    /**
     * Adiciona Nova Tecla Ao InputManager
     * @param name Nome Da Accao
     * @param key Tecla da Accao
     * @return 
     * 
     */
    public void addInput(String name,int key){
        main.getInputManager().addMapping(name,  new KeyTrigger(key));
        main.getInputManager().addListener(this, name);
    }
    
    public void removeInput(String name){
        main.getInputManager().deleteMapping(name);
    }
    
    private void initializeKeys(){
        main.getInputManager().addMapping("Raise",  new KeyTrigger(KeyInput.KEY_N));
        main.getInputManager().addListener(this, "Raise");
        main.getInputManager().addMapping("changecursor",  new KeyTrigger(KeyInput.KEY_T));
        main.getInputManager().addListener(this, "changecursor");
        main.getInputManager().addMapping("changeTool",  new KeyTrigger(KeyInput.KEY_G));
        main.getInputManager().addListener(this, "changeTool");
        main.getInputManager().addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        main.getInputManager().deleteTrigger("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        main.getInputManager().addMapping("ClickMiddle", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        main.getInputManager().addListener(this, "ClickMiddle");
        main.getInputManager().addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        main.getInputManager().addListener(this, "Click");
        addInput("activate", KeyInput.KEY_Q);
        addInput("Menos",KeyInput.KEY_SUBTRACT);
        addInput("Mais",KeyInput.KEY_ADD);
        //deleteTrigger(name, key)
        
    }
    public void onAction(String name, boolean isPressed, float tpf){
        if(name.equals("changecursor")){
            if(isPressed){
                setCursor(!cursor);
            }
        }else if(name.equals("changeTool")) {
            if(isPressed){
                if(toolSelected.equals("Smooth")){
                    toolSelected="Raise";
                }else if(toolSelected.equals("Raise")){
                    toolSelected="Lower";
                }else if(toolSelected.equals("Lower")){
                    toolSelected="Paint";
                }else if(toolSelected.equals("Paint")){
                    toolSelected="RemovePaint";
                }else if(toolSelected.equals("RemovePaint")){
                    toolSelected="Level";
                }else if(toolSelected.equals("Level")){
                    toolSelected="Smooth";
                }/*else if(toolSelected.equals("Smooth")){
                    toolSelected="Roughen";
                }*/
                
                main.setTextCenter("Tool: "+toolSelected+" Height: "+main.getTerrainEditor().getHeight()+
                " Radius: "+main.getTerrainEditor().getRadius());
            }
             
            
            
        }else if(name.equals("Click")) {
            if(isPressed){
                click=(true);
            }else{
                click=(false);
            }
            
        }else if(name.equals("activate")) {
            if(isPressed){
                this.main.setTextCenter("Tool: "+toolSelected+" Height: "+main.getTerrainEditor().getHeight()+
                " Radius: "+main.getTerrainEditor().getRadius());
                main.activateTerrainTool(true);
            }
            
        }else if(name.equals("Mais")) {
            if(isPressed){
                main.getTerrainEditor().setHeight(main.getTerrainEditor().getHeight()+1);
                main.setTextCenter("Tool: "+toolSelected+" Height: "+main.getTerrainEditor().getHeight()+
                " Radius: "+main.getTerrainEditor().getRadius());
            }
            
        }else if(name.equals("Menos")) {
            if(isPressed){
                main.getTerrainEditor().setHeight(main.getTerrainEditor().getHeight()-1);
                main.setTextCenter("Tool: "+toolSelected+" Height: "+main.getTerrainEditor().getHeight()+
                " Radius: "+main.getTerrainEditor().getRadius());
            }
            
        }else{
            debug(name);
        }
    }

    public void debug(String texto){
        if(main.isDebug()){
            main.debug.print(texto);
        }
    }
}

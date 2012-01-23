/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.empires.ControllerScreen;

import com.empires.Inicio;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ChatTextSendEvent;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.NiftyInputControl;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.controls.textfield.TextFieldControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 *
 * @author karlos
 */


public class ControllerScreen implements ScreenController{
    
   // <editor-fold defaultstate="collapsed" desc="Variaveis Globais">
   
   NiftyJmeDisplay niftyDisplay;
   AssetManager assetManager;
   InputManager inputManager;
   AudioRenderer audioRenderer;
   ViewPort guiViewPort;
   Nifty nifty;
   Inicio main;
   String ModelSelected="None";
   String ToolSelected="None";
   String SugestionSelected="Sugestoes";
   float lastScale=1f;
   float RotationX=0f;
   float RotationY=0f;
   float RotationZ=0f;
   float lastRadius=6f;
   float lastHeight=1f;
   
   // </editor-fold>
   Screen screen;
   public ControllerScreen(AssetManager assetManager,InputManager inputManager,AudioRenderer audioRenderer,ViewPort guiViewPort,Inicio main){
       this.assetManager=assetManager;
       this.inputManager=inputManager;
       this.audioRenderer=audioRenderer;
       this.guiViewPort=guiViewPort;
       this.main=main;
       startNifty();
   }
      
   // <editor-fold defaultstate="collapsed" desc="Carregamento da Interface">
   
   /*
    * 
    * Carregamento da Interface
    * 
    */
   private void startNifty() {

        niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
        try {
            nifty.fromXml("Interface/NiftyModels.xml", "Autentication", this);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        guiViewPort.addProcessor(niftyDisplay);
        LoadDropSugestion();
        
    }

   public void bind(Nifty nifty, Screen screen) {
       this.screen=screen;

       
       
        //throw new UnsupportedOperationException("Not supported yet.");
    }

   public void onStartScreen() {
        //System.out.println("Interface Iniciada");
    }

   public void onEndScreen() {
        //System.out.println("Interface Termindada");
    }
   
   /*
    * Fim do Carregamento da interface
    */
   // </editor-fold>
   
   // <editor-fold defaultstate="collapsed" desc="Permite buscar Modelo e Ferramenta Selecionada">
   //Permite buscar o modelo Selecionado
   public String getModelSelected(){
       return ModelSelected;
   }
   //Permite buscar O que fazer com os modelos (adiciona,mover,redimensionar,apagar)
   public String getToolSelected(){
       return ToolSelected;
   }
   
   // </editor-fold>
   
   // <editor-fold defaultstate="collapsed" desc="Permite alterar a screen">
   /*
    * 
    * Permite alterar a screen
    * 
    */
   public void goScreen(String qual){
        nifty.gotoScreen(qual);
       
       if(qual.equals("models"))
            LoadDropDown();
       
   }
   
   public void changeScreen(String qual){
       if(qual.equals("null"))
           LoadDropSugestion();
       //else
            //main.changeScreen(qual);
   }
   /*
    * Fim alterar Screen
    */
   // </editor-fold>
   
   // <editor-fold defaultstate="collapsed" desc="Altera os paineis para visivel ou nao">
   //Altera os paineis para visivel ou nao
   public void setVisible(String id,boolean valor){
       /*if(!nifty.getCurrentScreen().equals("models"))
           nifty.gotoScreen("models");*/
       nifty.getCurrentScreen().findElementByName(id).setVisible(valor);
   }
   
   public void changeVisibleT(String id){
       //main.setPanelVisible(id, true);
       if(id.equals("sugestoesPanel"))
           LoadDropSugestion();
   }
   
   public void changeVisibleT(String id,boolean valor){
       //main.setPanelVisible(id, valor);
   }
   // </editor-fold>
   
   // <editor-fold defaultstate="collapsed" desc="Carregamento Das Combobox">
   /*
    * Carregamento Das Combobox
    */

   private void LoadDropDown(){
        //Modelos
        nifty.getCurrentScreen().findNiftyControl("modelos", DropDown.class).addItem("");
        nifty.getCurrentScreen().findNiftyControl("modelos", DropDown.class).addItem("Cubo");
        nifty.getCurrentScreen().findNiftyControl("modelos", DropDown.class).addItem("Pedra");
        nifty.getCurrentScreen().findNiftyControl("modelos", DropDown.class).addItem("Arvore");
        nifty.getCurrentScreen().findNiftyControl("modelos", DropDown.class).addItem("Palmeira");
        
        //Ferramentas disponiveis
        nifty.getCurrentScreen().findNiftyControl("ferramenta", DropDown.class).addItem("");
        nifty.getCurrentScreen().findNiftyControl("ferramenta", DropDown.class).addItem("Selecionar");
        nifty.getCurrentScreen().findNiftyControl("ferramenta", DropDown.class).addItem("Adicionar");
        nifty.getCurrentScreen().findNiftyControl("ferramenta", DropDown.class).addItem("Mover");
        nifty.getCurrentScreen().findNiftyControl("ferramenta", DropDown.class).addItem("Redimensionar");
        nifty.getCurrentScreen().findNiftyControl("ferramenta", DropDown.class).addItem("Eliminar");
        
        //Ferramentas do Terreno
        nifty.getCurrentScreen().findNiftyControl("terrain", DropDown.class).addItem("");
        nifty.getCurrentScreen().findNiftyControl("terrain", DropDown.class).addItem("Elevar");
        nifty.getCurrentScreen().findNiftyControl("terrain", DropDown.class).addItem("Baixar");
        nifty.getCurrentScreen().findNiftyControl("terrain", DropDown.class).addItem("Pintar");
        nifty.getCurrentScreen().findNiftyControl("terrain", DropDown.class).addItem("Remover Tinta");
        
        //veiculos e Chars
        nifty.getCurrentScreen().findNiftyControl("veiculos", DropDown.class).addItem("");
        nifty.getCurrentScreen().findNiftyControl("veiculos", DropDown.class).addItem("Char");
        nifty.getCurrentScreen().findNiftyControl("veiculos", DropDown.class).addItem("Carro");
        LoadDropColors();
    }
    
   private void LoadDropSugestion(){
       
       nifty.getCurrentScreen().findNiftyControl("sugestiondrop", DropDown.class).addItem("Sugestoes");
       nifty.getCurrentScreen().findNiftyControl("sugestiondrop", DropDown.class).addItem("Historia");
       nifty.getCurrentScreen().findNiftyControl("sugestiondrop", DropDown.class).addItem("Reclamacoes");
   }
   
   private void LoadDropColors(){
       nifty.getCurrentScreen().findNiftyControl("colorPaint", DropDown.class).addItem("");
       nifty.getCurrentScreen().findNiftyControl("colorPaint", DropDown.class).addItem("Relva");
       nifty.getCurrentScreen().findNiftyControl("colorPaint", DropDown.class).addItem("Areia");
       nifty.getCurrentScreen().findNiftyControl("colorPaint", DropDown.class).addItem("Estrada");
   }
   
   /*
    * Fim Dos Carregamentos das Combobox
    */
   // </editor-fold>
   
   // <editor-fold defaultstate="collapsed" desc="Controla todos os Sliders e combobox">
   /*
    * Controla todos os Sliders e combobox
    */
   @NiftyEventSubscriber(id="modelos")
   public void onChangeModel(final String id, final DropDownSelectionChangedEvent<String> event){
       if(event.getSelection().equals("Pedra")){
            ModelSelected="stoneL";
        }else if(event.getSelection().equals("Arvore")){
            ModelSelected="tree";
        }else if(event.getSelection().equals("Palmeira")){
            ModelSelected="palmTree";
        }else if(event.getSelection().equals("Cubo")){
            ModelSelected="box";
        }
            //Palmeira

    }
   @NiftyEventSubscriber(id="email")
   public void onPressEmail(final String id, final NiftyInputControl event){
       System.out.println("");
       
    }
    
   @NiftyEventSubscriber(id="sliderV")
   public void onChangeScale(final String id, final SliderChangedEvent event){
        //System.out.println(event.getValue());
        if(lastScale!=event.getValue()){
           float a= (event.getValue());
           //main.setScaleModelSelected(a);
            
            lastScale=event.getValue();
        }
        //Main.getApplication().getModelSelected().scale(event.getValue());
        
    }
    
   @NiftyEventSubscriber(id="raioT")
   public void onChangeRadius(final String id, final SliderChangedEvent event){
        //System.out.println(event.getValue());
        if(lastRadius!=event.getValue()){
           float a= (event.getValue());
           //main.changeRadiusEditor((int) a);
        }
        //Main.getApplication().getModelSelected().scale(event.getValue());
        
    }
   
   @NiftyEventSubscriber(id="AlturaraioT")
   public void onChangeHeight(final String id, final SliderChangedEvent event){
        //System.out.println(event.getValue());
        if(lastHeight!=event.getValue()){
           float a= (event.getValue());
           //main.changeHeightEditor((int) a);
        }
        //Main.getApplication().getModelSelected().scale(event.getValue());
        
    }
    
   @NiftyEventSubscriber(id="RotationX")
   public void onChangeRotationX(final String id, final SliderChangedEvent event){
        //System.out.println(event.getValue());
        if(RotationX!=event.getValue()){
           float a= (event.getValue());
           //main.setRotationXModelSelected(a);
            nifty.getCurrentScreen().findElementByName("RotacaoXs").getRenderer(TextRenderer.class).setText("          "+a+"");
            RotationX=event.getValue();
        }
        //Main.getApplication().getModelSelected().scale(event.getValue());
        
    }
    
   @NiftyEventSubscriber(id="RotationY")
   public void onChangeRotationY(final String id, final SliderChangedEvent event){
        //System.out.println(event.getValue());
        if(RotationY!=event.getValue()){
           float a= (event.getValue());
           //main.setRotationYModelSelected(a);
            nifty.getCurrentScreen().findElementByName("RotacaoYs").getRenderer(TextRenderer.class).setText("          "+a+"");
            RotationY=event.getValue();
        }
        //Main.getApplication().getModelSelected().scale(event.getValue());
        
    }
    
   @NiftyEventSubscriber(id="RotationZ")
   public void onChangeRotationZ(final String id, final SliderChangedEvent event){
        //System.out.println(event.getValue());
        if(RotationZ!=event.getValue()){
           float a= (event.getValue());
           //main.setRotationZModelSelected(a);
           nifty.getCurrentScreen().findElementByName("RotacaoZs").getRenderer(TextRenderer.class).setText("          "+a+"");
           RotationZ=event.getValue();
        }
        //Main.getApplication().getModelSelected().scale(event.getValue());
        
    }
    
   @NiftyEventSubscriber(id="ferramenta")
   public void OnChangeTool(final String id, final DropDownSelectionChangedEvent<String> event){
        if(event.getSelection().equals("Selecionar")){
            ToolSelected="select";
        }else if(event.getSelection().equals("Adicionar")){
            ToolSelected="addNew";
        }else if(event.getSelection().equals("Mover")){
            ToolSelected="move";
        }else if(event.getSelection().equals("Redimensionar")){
            ToolSelected="scale";
        }else if(event.getSelection().equals("Eliminar")){
            ToolSelected="del";
        }
       
    }
    
   @NiftyEventSubscriber(id="terrain")
   public void OnChangeTooltoTerrain(final String id, final DropDownSelectionChangedEvent<String> event){
        if(event.getSelection().equals("")){
            //main.changeTool("select","");
            changeVisibleT("colorPaint");
        }else if(event.getSelection().equals("Elevar")){
           // main.changeTool("terrain","raise");
        }else if(event.getSelection().equals("Baixar")){
           // main.changeTool("terrain","lower");
        }else if(event.getSelection().equals("Pintar")){
           // main.changeTool("terrain","paint");
            changeVisibleT("colorPaint",true);
        }else if(event.getSelection().equals("Remover Tinta")){
           // main.changeTool("terrain","Rpaint");
            changeVisibleT("colorPaint",true);
        }
        
    }
   
   @NiftyEventSubscriber(id="sugestiondrop")
   public void OnChangeSugestion(final String id, final DropDownSelectionChangedEvent<String> event){
        
            SugestionSelected=event.getSelection();
        
    }
    
   @NiftyEventSubscriber(id="veiculos")
   public void OnChangeVeiculo(final String id, final DropDownSelectionChangedEvent<String> event){
            ModelSelected=event.getSelection();
    }
   
   @NiftyEventSubscriber(id="colorPaint")
   public void OnChangePaint(final String id, final DropDownSelectionChangedEvent<String> event){
            if(event.getSelection().equals("")){
               // main.changeColorPaint(-1);
            }else if(event.getSelection().equals("Relva")){
               // main.changeColorPaint(0);
            }else if(event.getSelection().equals("Areia")){
               // main.changeColorPaint(1);
            }else if(event.getSelection().equals("Estrada")){
               // main.changeColorPaint(2);
            }
    }
   
   /*
    * Fim dos Controladores
    */
   // </editor-fold>
   
   @NiftyEventSubscriber(id="chatPanel")
    public final void onSendText(final String id, final ChatTextSendEvent event) {
        if (!event.getText().equals("")) {
            System.out.println("chat event received: " + event.getText());
        }
        
    }
   
   public void createPopUp(String id){
        Element exitPopup = nifty.createPopup(id);
        nifty.showPopup(screen, exitPopup.getId(), null);
   }
   
   //Permite saber em qual screen se encontra
   public String getCurrentScreen(){
        return nifty.getCurrentScreen().getScreenId();
    }
    
   //Screen de login
   public void autentication(){
       String user=nifty.getCurrentScreen().findElementByName("username").getControl(TextFieldControl.class).getText();
       String pass=nifty.getCurrentScreen().findElementByName("password").getControl(TextFieldControl.class).getText();
       boolean serverState;//=main.getConnected();
       changeVisibleT("dadosLogin",false);
       changeVisibleT("aentrar",true);
       changeVisibleT("autentication",false);
       
       if(user.equals("") ){
           nifty.getCurrentScreen().findElementByName("loginError").getRenderer(TextRenderer.class).setText("Preencha o Username");
       }
       if(pass.equals("")){
           if(!user.equals(""))
                nifty.getCurrentScreen().findElementByName("loginError").getRenderer(TextRenderer.class).setText("Preencha a Password");
           else
               nifty.getCurrentScreen().findElementByName("loginError").getRenderer(TextRenderer.class).setText("Preencha o Username\nPreencha a Password");
       }
       if(!user.equals("") && !pass.equals("")){
           //if(serverState)
                //main.checkAutentication(user,pass);
           //else
               //main.changeScreen("null");
       }
           
   }
   //Screen de registo
   public void register(){
       String user=nifty.getCurrentScreen().findElementByName("username").getControl(TextFieldControl.class).getText();
       String pass=nifty.getCurrentScreen().findElementByName("password").getControl(TextFieldControl.class).getText();
       String confpass=nifty.getCurrentScreen().findElementByName("confpassword").getControl(TextFieldControl.class).getText();
       String email=nifty.getCurrentScreen().findElementByName("email").getControl(TextFieldControl.class).getText();
       //boolean serverState=main.getConnected();
       
       boolean registar=false;
       String mensagem="";
       //Verifica os campos vazios
       if(user.equals("") || pass.equals("") || confpass.equals("")|| email.equals("")){
           mensagem="Preencha todos os campos";
       }else {
           registar=true;
       }
        //Verifica a password
       if(registar && pass.equals(confpass)){
           registar=true;
       }else{
           if(mensagem.equals("")) 
               mensagem="Senhas nao coincidem";
       } 
       
       //Verifica se ´´e um email
       if(registar && email.contains("@") && email.contains(".")){
           registar=true;
       }else{
           registar=false;
           if(mensagem.equals("")) 
               mensagem="Nao e um email";
       }
           
         
       
       if(registar){
           changeVisibleT("registerpanel",false);
           changeVisibleT("aentrar",true);
           if(!user.equals("") && !pass.equals("")){
               //if(serverState)
              //      main.RegisterNewPlayer(user,pass,email);
              // else
              //     main.changeScreen("Autentication");
           }
       }else{
           nifty.getCurrentScreen().findElementByName("RegisterError").getRenderer(TextRenderer.class).setText(mensagem);
       }
   }
   
    public Nifty getNify(){
       return nifty;
   }
   
   // <editor-fold defaultstate="collapsed" desc="Tudo a ver com Sugestoes">
   /*
    *Sugestoes, sugestoes de historia e reclamacoes  
    */
   public void SendSugestion(){
      String a= nifty.getCurrentScreen().findElementByName("text_input").getControl(TextFieldControl.class).getText();
       System.out.println("Tipo: "+SugestionSelected+"\nMensagem: "+a);
       //main.send(SugestionSelected, a);
       nifty.getCurrentScreen().findElementByName("text_input").getControl(TextFieldControl.class).setText("");
       //main.setPanelVisible("sugestoesPanel", false);
       
   }
   
   public void closeSugestion(){
       nifty.getCurrentScreen().findElementByName("text_input").getControl(TextFieldControl.class).setText("");
       //main.setPanelVisible("sugestoesPanel", false);
   }
   
   
   
   /*
    * Fim de Sugestoes
    */
   // </editor-fold>
   
}

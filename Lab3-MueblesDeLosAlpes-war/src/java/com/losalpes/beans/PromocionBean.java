/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losalpes.beans;

import com.losalpes.entities.Mueble;
import com.losalpes.entities.Promocion;
import com.losalpes.servicios.IServicioCatalogoMockLocal;
import com.losalpes.servicios.ServicioCatalogoMock;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;


/**
 *
 * @author de.abril10
 */
@SessionScoped
public class PromocionBean {

    
    /**
     * Relación con la interfaz que provee los servicios necesarios del catálogo.
     */
    @EJB
    private IServicioCatalogoMockLocal catalogo;
    private Mueble mueble;
    private Promocion promocion;
    
    /**
     * Creates a new instance of PromocionBean
     */
    public PromocionBean() {
        promocion = new Promocion();
        mueble = new Mueble();
    }
    
    
   public String agregarPromocion(long referencia){
     List<Mueble> listaMuebles = catalogo.darMuebles();
      for(int i=0;i<listaMuebles.size();i++){
       if(listaMuebles.get(i).getReferencia()==referencia){
            this.mueble = listaMuebles.get(i);
            this.promocion.setMueble(this.mueble);
       }
      }
      return "promocion";
    }
   
   public String registrarPromocion(){
       try{
          this.promocion.setMueble(this.mueble); 
          this.catalogo.agregarPromocion(this.promocion);
          this.promocion.fechaFin = null;
          this.promocion.fechaInicio= null;
          this.promocion.setMueble(new Mueble());
       }catch(Exception ex){
           ex.printStackTrace(); 
       }
   return "catalogo";
   }

    public Mueble getMueble() {
        return mueble;
    }

    public void setMueble(Mueble mueble) {
        this.mueble = mueble;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }
   
   
    
}

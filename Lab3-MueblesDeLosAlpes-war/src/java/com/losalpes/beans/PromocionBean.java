/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losalpes.beans;

import com.losalpes.entities.Mueble;
import com.losalpes.servicios.IServicioCatalogoMockLocal;
import com.losalpes.servicios.ServicioCatalogoMock;
import java.util.List;

/**
 *
 * @author de.abril10
 */
public class PromocionBean {

    
    /**
     * Relación con la interfaz que provee los servicios necesarios del catálogo.
     */
    private IServicioCatalogoMockLocal catalogo;
    private Mueble mueble;
    
    /**
     * Creates a new instance of PromocionBean
     */
    public PromocionBean() {
        catalogo=new ServicioCatalogoMock();
    }
    
    
   public String agregarPromocion(long referencia){
     List<Mueble> listaMuebles = catalogo.darMuebles();
      for(int i=0;i<listaMuebles.size();i++){
       if(listaMuebles.get(i).getReferencia()==referencia){
            mueble = listaMuebles.get(i);
       }
      }
      return "promocion";
    }
    
}

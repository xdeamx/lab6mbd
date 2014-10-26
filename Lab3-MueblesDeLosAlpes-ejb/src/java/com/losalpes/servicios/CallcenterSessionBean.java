/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losalpes.servicios;

import com.losalpes.jms.PromocionesMessageCallcenter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author de.abril10
 */
@Stateless
public class CallcenterSessionBean implements CallcenterSessionBeanLocal, CallcenterSessionBeanRemote {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    public void anunciarNuevaPromocion(String msj){
         Logger.getLogger(CallcenterSessionBean.class.getName()).log(Level.INFO,
                        "Promociones Call center: ha recibido una nueva promocion \n"
                        + msj);
    }
}

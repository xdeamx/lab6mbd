/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losalpes.servicios;

import javax.ejb.Local;

/**
 *
 * @author de.abril10
 */
@Local
public interface VentasSessionBeanLocal {
     public void anunciarNuevaPromocion(String msj);
}

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ ServicioCatalogoMock.java
 * Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación
 * Licenciado bajo el esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 * Autor: Juan Sebastián Urrego
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.losalpes.servicios;

import com.losalpes.entities.Mueble;
import com.losalpes.entities.Promocion;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * Implementacion de los servicios del catálogo de muebles que se le prestan al sistema.
 * @author Juan Sebastián Urrego
 */
@Stateless
public class ServicioCatalogoMock implements IServicioCatalogoMockRemote,IServicioCatalogoMockLocal
{

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------

    /**
     * Interface con referencia al servicio de persistencia en el sistema
     */
    @EJB
    private IServicioPersistenciaMockLocal persistencia;
    private Promocion promocion;
    
    @Resource(mappedName="jms/creacionPromocionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(mappedName="jms/nuevaPromocionTopic")
    private Topic nuevaPromocionTopic;
    
    @Resource(mappedName="jms/nuevaPromocionCallCenterTopic")
    private Topic nuevaPromocionCallCenterTopic;
            
    @Resource(mappedName="jms/nuevaPromocionMercadeoTopic")
     private Topic nuevaPromocionMercadeoTopic;
    

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------

    /**
     * Constructor sin argumentos de la clase
     */
    public ServicioCatalogoMock()
    {

    }

    //-----------------------------------------------------------
    // Métodos
    //-----------------------------------------------------------

    /**
     * Agrega un mueble al sistema
     * @param mueble Nuevo mueble
     */
    @Override
    public void agregarMueble(Mueble mueble)
    {
        try
        {
            persistencia.create(mueble);
        }
        catch (OperacionInvalidaException ex)
        {
            Logger.getLogger(ServicioCatalogoMock.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    /**
     * Se elimina un mueble del sistema dado su identificador único
     * @param id Identificador único del mueble
     */
    @Override
    public void eliminarMueble(long id)
    {
        Mueble m=(Mueble) persistencia.findById(Mueble.class, id);
        try
        {
            persistencia.delete(m);
        }
        catch (OperacionInvalidaException ex)
        {
            Logger.getLogger(ServicioCatalogoMock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Remueve un ejemplar del mueble (no el mueble)
     * @param id Identificador único del mueble
     */
    @Override
    public void removerEjemplarMueble(long id)
    {
        ArrayList<Mueble>muebles=(ArrayList<Mueble>) persistencia.findAll(Mueble.class);
        Mueble mueble;
        for (int i = 0; i < muebles.size(); i++)
        {
            mueble = muebles.get(i);
            if(mueble.getReferencia()==id)
            {
                int cantidad=mueble.getCantidad();
                mueble.setCantidad(cantidad-1);
                persistencia.update(mueble);
                break;
            }
        }
    }

    /**
     * Devuelve los muebles del sistema
     * @return muebles Arreglo con todos los muebles del sistema
     */
    @Override
    public List<Mueble> darMuebles()
    {
        return persistencia.findAll(Mueble.class);
    }
    
   
    @Override
    public void agregarPromocion(Promocion promo) throws OperacionInvalidaException{
        
        try
        {
           this.promocion=promo;
           persistencia.create(this.promocion);
        }
        catch (OperacionInvalidaException ex)
        {
            throw new OperacionInvalidaException(ex.getMessage());
        }
         try
        {
            notificarAplicaciones(this.promocion);
        } catch (JMSException ex)
        {
            Logger.getLogger(ServicioVendedoresMock.class.getName()).log(Level.SEVERE, "Error "
                    + "enviando la notificación de creación de un vendedor", ex);
        }
    
    }
    
    private Message getCallCenterMessage(Session session) throws JMSException
    {
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        String msg = "Una nueva promoción registrada con Fecha Inicio: " + df.format(promocion.getFechaInicio())+" - Fecha Fin:"+ df.format(promocion.getFechaFin()) + "\n";
        TextMessage tm = session.createTextMessage();   
        tm.setText(msg);
        return tm;
    }
    
    private Message getVentasMessage(Session session) throws JMSException
    {
        String msg = "Se ha registrado una nueva promoción";
        TextMessage tm = session.createTextMessage();   
        tm.setText(msg);
        return tm;
    }
    
     private Message getMercadeoMessage(Session session) throws JMSException
    {
        String msg = "Una nueva promoción se ha creado del producto:"+this.promocion.getMueble().getNombre()+" , Referencia:"+this.promocion.getMueble().getReferencia();
        TextMessage tm = session.createTextMessage();   
        tm.setText(msg);
        return tm;
    }
     
     private Message getPromocionMessage(Session session,Promocion promo)throws JMSException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        TextMessage tm = session.createTextMessage(); 
        tm.setStringProperty("productonombre", promo.getMueble().getNombre());
        tm.setLongProperty("productoreferencia", promo.getMueble().getReferencia());
        tm.setStringProperty("fechainicio",  sdf.format(promo.getFechaInicio()));
        tm.setStringProperty("fechafin",  sdf.format(promo.getFechaFin()));
        return tm;
    }
    
    private void notificarAplicaciones(Promocion promo) throws JMSException 
     {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer messageProducer = session.createProducer((Destination) nuevaPromocionTopic);
        try 
        {
            messageProducer.send(getPromocionMessage(session, promo));
        } 
        catch (JMSException ex) 
        {
            Logger.getLogger(ServicioCatalogoMock.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally 
        {
            if (session != null) 
            {
                try 
                {
                    session.close();
                } 
                catch (JMSException e) 
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error cerrando la"
                            + " sesión", e);
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

}

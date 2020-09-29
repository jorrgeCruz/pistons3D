
package pistones;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class Fr3D extends javax.swing.JFrame implements Runnable
{
    private JPopupMenu popup;
    JMenuItem[] items = {new JMenuItem("1 Piston"),new JMenuItem("2 Pistones"),
                         new JMenuItem("3 Pistones"),new JMenuItem("4 Pistones")};
    private Thread hilo;
    protected Canvas3D cv;
    public Fr3D()
    {
        //Titulo de la ventana
        super("Pistones");
        //Evento de escucha de ventana para cerrarla
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e){System.exit(0);}
        });
        //Obtenemos canvas con la figura
        this.cv = new Graficador();
        //Inicializamos el hilo de animación
        hilo = new Thread(this);
        //Creamos el menu de seleccion de pistones
        popup = new JPopupMenu();
        //agregamos elementos al menu
        for (JMenuItem item : items) {
            popup.add(item); 
        }
        //Evento de escucha de teclado para mover el ojo
        cv.addKeyListener(new KeyAdapter()
        {
            //subevento de escucha al soltar una tecla
            @Override
            public void keyReleased(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_DOWN: vp(0, -.1F, 1);
                    break;
                    case KeyEvent.VK_UP: vp(0, .1F, 1);
                    break;
                    case KeyEvent.VK_LEFT: vp(.1F, 0, 1);
                    break;
                    case KeyEvent.VK_RIGHT: vp(-.1F, 0, 1);
                    break;
                    //Con la tecla ESC salimos de la aplicación
                    case KeyEvent.VK_ESCAPE:
                        if (JOptionPane.showConfirmDialog(cv,"Esta seguro que desea salir?",
                                "Confirmación",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION)
                        {
                            System.exit(0);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //Evento de escucha de raton para mover el ojo
        cv.addMouseMotionListener(new MouseMotionAdapter()
        {
            //Bandera para conocer cuando se mueve el puntero mientras esta presionado algun boton del raton
            private boolean dragging = false;
            //Puntos X y Y del puntero al momento de presionar un boton del raton
            private int xPreviousMouse;
            private int yPreviousMouse;
            //subevento de escucha activo cuando el puntero se mueve dentro del canvas mientras un boton se mantiene presionado
            @Override
            public void mouseDragged(MouseEvent me)
            {
                /*si el puntero esta en movimiento mientras esta presionado algun boton del raton verificamos la direccion,
                    movemos el ojo y actualizamos las coordenadas del puntero*/
                if (dragging)
                {
                    if (me.getY() < yPreviousMouse){ vp(0, .1F, 1); yPreviousMouse = me.getY(); }
                    else if (me.getY() > yPreviousMouse){ vp(0, -.1F, 1); yPreviousMouse = me.getY(); }
                    else
                    if (me.getX() < xPreviousMouse){ vp(.1F, 0, 1); xPreviousMouse = me.getX(); }
                    else
                    if (me.getX() > xPreviousMouse){ vp(-.1F, 0, 1); xPreviousMouse = me.getX(); }
                }
                /*en caso contrario significa que se acaba de presionar algun boton del mouse y entonces actualizamos
                    las coordenadas del puntero en su pocicion actual y activamos la bandera*/
                else
                {
                    xPreviousMouse = me.getX();
                    yPreviousMouse = me.getY();
                    dragging = true;
                }
            }
            //subevento de escucha activo cuando el puntero se mueve dentro del canvas sin presionar ningun boton
            @Override
            public void mouseMoved(MouseEvent me){
                //desactivamos la bandera porque no hay nungun boton presionado
                dragging = false;
            }
        });
        //Evento de escucha de raton para mostrar menú contextual
        cv.addMouseListener(new MouseAdapter()
        {
            //subevento de escucha cuando se libera el boton del raton dentro del canvas
            @Override
            public void mouseReleased(MouseEvent me) {
                //si se desea mostrar el menu saliente
                if (me.isPopupTrigger()) {
                    //mostramos el menu en la ubicacion del raton
                    popup.show(me.getComponent(),me.getX(), me.getY());
                }
            }
            //subevento de escucha cuando se presiona el boton del raton dentro del canvas
            @Override
            public void mousePressed(MouseEvent me) {
                //si se desea mostrar el menu saliente
                if (me.isPopupTrigger()) {
                    //mostramos el menu en la ubicacion del raton
                    popup.show(me.getComponent(),me.getX(), me.getY());
                }
            }
        });
        //Evento de escucha cuando se presiona sobre el elemento 1 del menu de opciones
        items[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //cambiamos las caras a pintar
                cv.setNFaces(000, 156);
            }
        });
        //Evento de escucha cuando se presiona sobre el elemento 2 del menu de opciones
        items[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //cambiamos las caras a pintar
                cv.setNFaces(000, 297);
            }
        });
        //Evento de escucha cuando se presiona sobre el elemento 3 del menu de opciones
        items[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //cambiamos las caras a pintar
                cv.setNFaces(000, 438);
            }
        });
        //Evento de escucha cuando se presiona sobre el elemento 4 del menu de opciones
        items[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //cambiamos las caras a pintar
                cv.setNFaces(000, 579);
            }
        });
        //centramos el canvas
        add("Center", cv);
        //obtenemos las dimensiones de la pantalla
        Dimension dim = getToolkit().getScreenSize();
        //redimencionamos la ventana segun el tamaño de la pantalla
        setSize(dim.width/2, dim.height/2);
        //centramos la ventana en la pantalla
        setLocation(dim.width/4, dim.height/4);
        //creamos una nueva figura 3D
        Obj3D obj = new Obj3D();
        //comprobamos que exista nuestro archivo de datos y lo agregamos al canvas 153
        if (obj.read("src/pistones/Pistones.dat")){cv.setObj(obj); cv.repaint();}
        //ajustamos la posición de los pistones
        adjust();
        //mostramos la ventana
        setVisible(true);
        //damos el foco al canvas para poder usar las teclas y el puntero directamente
        cv.requestFocus();
        //iniciamos el hilo de animacion
        hilo.start();
    }
    //metodo de actualizacion de coordenadas de ojo
    private void vp(float dTheta, float dPhi, float fRho)
    {
        Obj3D obj = cv.getObj();
        if (obj == null  ||   !obj.vp(cv, dTheta, dPhi, fRho))
            Toolkit.getDefaultToolkit().beep();
    }
    //ejecucion del hilo de animacion
    @Override
    public void run(){
        //ejecutamos la animación mientras el hilo este vivo
        while(hilo.isAlive()){
            rotation(cv.getObj(),new Point3D (0, -2, -2),new Point3D (0, 6, -2), 10,101,1050,true);
            rotation(cv.getObj(),new Point3D (0, -2, -2),new Point3D (0, 6, -2), 10,101,1050,false);
        }
    }
    /**
     * Metodo de rotación de brazos y muñeca:
     * @param obj -> objeto o figura de origen
     * @param vra -> coordenada inicial del eje de rotación
     * @param vrb -> coordenada final del eje de rotación
     * @param limR -> número de rotaciones
     * @param cI -> coordenada inicial del fragmento a rotar
     * @param cF -> coordenada final del fragmento a rotar
     * @param updown -> dirección de la traslación del piston 1 (true = bajar, false = subir) el resto dependen del mismo
     */
    private void rotation(Obj3D obj, Point3D vra, Point3D vrb, int limR, int cI, int cF, boolean updown)
    {
        //declaración de las nuevas coordenadas
        float x=0, y=0, z=0;
        //ciclo de actualización de caras y repintado
        for (int r=0; r<limR; r++){
            //distancia de rotacion por ciclo PI/10=18°
            double alpha = Math.PI/10;
            //llamada al metodo de inicio de rotación de la clase Rota3D
            Rota3D.initRotate(vra, vrb, alpha);
            //ciclo de actualización de coordenadas
            for (int i=cI; i<=cF; i++) {
                //comprobamos que los vertices a rotar solo sean de los brazos y la muñeca
                if((i<231||i>400)&&(i<531||i>650)&&(i<781||i>920)){
                    //comprobamos que la coordenada exista
                    if (obj.w.elementAt(i) != null){
                        //actualizamos x
                        x = ((Point3D)obj.w.elementAt(i)).x;
                        //actualizamos y
                        y = ((Point3D)obj.w.elementAt(i)).y;
                        //actualizamos z
                        z = ((Point3D)obj.w.elementAt(i)).z;
                        //actualizamos la coordenada con sus nuevos puntos
                        obj.w.setElementAt(Rota3D.rotate(new Point3D (x, y, z)), i);
                    }
                }
            }
            //movemos las cabezas con sus barras (solo si ya inicio la animación)
            if(hilo.isAlive()){
                moveHeads(updown);
                if(r<limR/2)
                    moveRods(updown,true);
                else
                    moveRods(updown,false);
                //actualizamos el canvas
                cv.repaint();
                try{
                    //retardamos el hilo una decima de segundo
                    Thread.sleep(100);
                }catch(InterruptedException ex){
                    JOptionPane.showMessageDialog(null,ex,"Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    /**
     * Metodo de traslación:
     * @param obj -> objeto o figura de origen
     * @param limM -> numero de traslaciones divididas entre 10
     * @param mX -> dirección de la traslación en x (negativa = -1, positiva = 1, nula = 0)
     * @param mY -> dirección de la traslación en y (negativa = -1, positiva = 1, nula = 0)
     * @param mZ -> dirección de la traslación en z (negativa = -1, positiva = 1, nula = 0)
     * @param cI -> coordenada inicial del fragmento a rotar
     * @param cF -> coordenada final del fragmento a rotar
     * @return 
     */
    private void traslation(Obj3D obj, float limM, int mX, int mY, int mZ, int cI, int cF)
    {
        //declaración de las nuevas coordenadas
        float x=0, y=0, z=0;
        //ciclo de actualización de caras y repintado
        for (float mov=0; mov<limM; mov++){
            //obj = cv.getObj();
            //ciclo de actualización de coordenadas
            for (int i=cI; i<=cF; i++) {
                //comprobamos que la coordenada exista
                if (obj.w.elementAt(i) != null){
                    //actualizamos x de acuerdo a su direccion de traslado
                    switch(mX){
                        case -1: x = ((Point3D)obj.w.elementAt(i)).x - 0.1F; break;
                        case  1: x = ((Point3D)obj.w.elementAt(i)).x + 0.1F; break;
                        default: x = ((Point3D)obj.w.elementAt(i)).x; break;
                    }
                    //actualizamos y de acuerdo a su direccion de traslado
                    switch(mY){
                        case -1: y = ((Point3D)obj.w.elementAt(i)).y - 0.1F; break;
                        case  1: y = ((Point3D)obj.w.elementAt(i)).y + 0.1F; break;
                        default: y = ((Point3D)obj.w.elementAt(i)).y; break;
                    }
                    //actualizamos z de acuerdo a su direccion de traslado
                    switch(mZ){
                        case -1: z = ((Point3D)obj.w.elementAt(i)).z - 0.1F; break;
                        case  1: z = ((Point3D)obj.w.elementAt(i)).z + 0.1F; break;
                        default: z = ((Point3D)obj.w.elementAt(i)).z; break;
                    }
                    //actualizamos la coordenada con sus nuevos puntos
                    obj.w.setElementAt(new Point3D(x, y, z), i);
                }
            }
        }
    }
    //metodo para ajustar los pistones de manera que trabajen de forma impar
    private void adjust()
    {
        rotation(cv.getObj(),new Point3D (0, -2, -2),new Point3D (0, 13, -2), 10,401,530,false);
        traslation(cv.getObj(), 20, 0, 0, -1, 291,298);
        traslation(cv.getObj(), 20, 0, 0, -1, 301,400);
        rotation(cv.getObj(),new Point3D (0, -2, -2),new Point3D (0, 13, -2), 10,921,1050,false);
        traslation(cv.getObj(), 20, 0, 0, -1, 809,816);
        traslation(cv.getObj(), 20, 0, 0, -1, 821,920);
    }
    /**
     * Metodo para mover las cabezas:
     * @param updown -> dirección de la traslación de la cabeza 1 (true = bajar, false = subir) el resto dependen de la misma
     */
    private void moveHeads(boolean updown)
    {
        if(updown){
            traslation(cv.getObj(), 2, 0, 0, -1, 001,100);
            traslation(cv.getObj(), 2, 0, 0,  1, 301,400);
            traslation(cv.getObj(), 2, 0, 0, -1, 551,650);
            traslation(cv.getObj(), 2, 0, 0,  1, 821,920);
        }else{
            traslation(cv.getObj(), 2, 0, 0,  1, 001,100);
            traslation(cv.getObj(), 2, 0, 0, -1, 301,400);
            traslation(cv.getObj(), 2, 0, 0,  1, 551,650);
            traslation(cv.getObj(), 2, 0, 0, -1, 821,920);
        }
    }
    /**
     * metodo para mover las barras:
     * @param updown -> dirección de la traslación en y de la barra 1 (true = bajar, false = subir)
     * @param forwardback -> dirección de la traslación en x de la barra 1 (true = adelante, false = atras)
     * todas las barras dependen de la primera para moverse
     */
    private void moveRods(boolean updown, boolean forwardback)
    {
        if(forwardback){
            if(updown){
                //Barra 1
                traslation(cv.getObj(), 2,  0, 0, -1, 271,272);
                traslation(cv.getObj(), 2,  0, 0, -1, 277,278);
                traslation(cv.getObj(), 2,  1, 0, -1, 273,276);
                //Barra 2
                traslation(cv.getObj(), 2,  0, 0,  1, 291,292);
                traslation(cv.getObj(), 2,  0, 0,  1, 297,298);
                traslation(cv.getObj(), 2, -1, 0,  1, 293,296);
                //Barra 3
                traslation(cv.getObj(), 2,  0, 0, -1, 801,802);
                traslation(cv.getObj(), 2,  0, 0, -1, 807,808);
                traslation(cv.getObj(), 2,  1, 0, -1, 803,806);
                //Barra 4
                traslation(cv.getObj(), 2,  0, 0,  1, 809,810);
                traslation(cv.getObj(), 2,  0, 0,  1, 815,816);
                traslation(cv.getObj(), 2, -1, 0,  1, 811,814);
            }else{
                //Barra 1
                traslation(cv.getObj(), 2,  0, 0,  1, 271,272);
                traslation(cv.getObj(), 2,  0, 0,  1, 277,278);
                traslation(cv.getObj(), 2, -1, 0,  1, 273,276);
                //Barra 2
                traslation(cv.getObj(), 2,  0, 0, -1, 291,292);
                traslation(cv.getObj(), 2,  0, 0, -1, 297,298);
                traslation(cv.getObj(), 2,  1, 0, -1, 293,296);
                //Barra 3
                traslation(cv.getObj(), 2,  0, 0,  1, 801,802);
                traslation(cv.getObj(), 2,  0, 0,  1, 807,808);
                traslation(cv.getObj(), 2, -1, 0,  1, 803,806);
                //Barra 4
                traslation(cv.getObj(), 2,  0, 0, -1, 809,810);
                traslation(cv.getObj(), 2,  0, 0, -1, 815,816);
                traslation(cv.getObj(), 2,  1, 0, -1, 811,814);
            }
        }else{
            if(updown){
                //Barra 1
                traslation(cv.getObj(), 2,  0, 0, -1, 271,272);
                traslation(cv.getObj(), 2,  0, 0, -1, 277,278);
                traslation(cv.getObj(), 2, -1, 0, -1, 273,276);
                //Barra 2
                traslation(cv.getObj(), 2,  0, 0,  1, 291,292);
                traslation(cv.getObj(), 2,  0, 0,  1, 297,298);
                traslation(cv.getObj(), 2,  1, 0,  1, 293,296);
                //Barra 3
                traslation(cv.getObj(), 2,  0, 0, -1, 801,802);
                traslation(cv.getObj(), 2,  0, 0, -1, 807,808);
                traslation(cv.getObj(), 2, -1, 0, -1, 803,806);
                //Barra 4
                traslation(cv.getObj(), 2,  0, 0,  1, 809,810);
                traslation(cv.getObj(), 2,  0, 0,  1, 815,816);
                traslation(cv.getObj(), 2,  1, 0,  1, 811,814);
            }else{
                //Barra 1
                traslation(cv.getObj(), 2,  0, 0,  1, 271,272);
                traslation(cv.getObj(), 2,  0, 0,  1, 277,278);
                traslation(cv.getObj(), 2,  1, 0,  1, 273,276);
                //Barra 2
                traslation(cv.getObj(), 2,  0, 0, -1, 291,292);
                traslation(cv.getObj(), 2,  0, 0, -1, 297,298);
                traslation(cv.getObj(), 2, -1, 0, -1, 293,296);
                //Barra 3
                traslation(cv.getObj(), 2,  0, 0,  1, 801,802);
                traslation(cv.getObj(), 2,  0, 0,  1, 807,808);
                traslation(cv.getObj(), 2,  1, 0,  1, 803,806);
                //Barra 4
                traslation(cv.getObj(), 2,  0, 0, -1, 809,810);
                traslation(cv.getObj(), 2,  0, 0, -1, 815,816);
                traslation(cv.getObj(), 2, -1, 0, -1, 811,814);
            }
        }
    }
}
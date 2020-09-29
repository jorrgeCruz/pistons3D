
package pistones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Graficador extends Canvas3D
{
    //creamos un objeto Graphics para pintar fuera de la pantalla
    Graphics lienzo;
    //creamos un buffer sobre el cual se pintara el lienzo
    BufferedImage buffer;
    
    Graficador()
    {
        //obtenemos las dimenciones del canvas
        Dimension dim = getToolkit().getScreenSize();
        //inicializamos el buffer
        buffer = new BufferedImage(dim.width,dim.height,BufferedImage.TYPE_INT_RGB);
        //preparamos el buffer para ser pintado por medio del lienzo
        lienzo = buffer.createGraphics();
    }
    
    private int maxX, maxY, centerX, centerY, maxX0 = -1, maxY0 = -1;
    private float buf[][];
    private Obj3D obj;
    private Point2D imgCenter;
    //caras que se pintaran (inicialmente solo se pinta un pistón)
    private int minFace=0, maxFace=579;
    
    int iX(float x){return Math.round(centerX + x - imgCenter.x);}
    int iY(float y){return Math.round(centerY - y + imgCenter.y);}

    //ingresamos el numero de caras a pintar
    @Override
    void setNFaces(int min, int max){
        this.minFace = min;
        this.maxFace = max;
    }
    
    @Override
    Obj3D getObj(){return obj;}
    
    @Override
    void setObj(Obj3D obj){this.obj = obj;}
   
    @Override
    public void update(Graphics g)
    {
        //cambiamos color para pintar el fondo
        lienzo.setColor(new Color(50, 55, 50));
        //pintamos un rectangulo para usarlo como fondo
        lienzo.fillRect(0, 0, getWidth(), getHeight());
        
        if (obj == null) return;
        Vector polyList = obj.getPolyList();
        if (polyList == null) return;
        int nFaces = polyList.size();
        if (nFaces == 0) return;
        Dimension dim = getSize();
        maxX = dim.width - 1; maxY = dim.height - 1;
        centerX = maxX/2; centerY = maxY/2;
        if (maxX != maxX0 || maxY != maxY0)
        {
            buf = new float[dim.width][dim.height];
            maxX0 = maxX; maxY0 = maxY;
        }
        for (int iy=0; iy<dim.height; iy++)
            for (int ix=0; ix<dim.width; ix++)
                buf[ix][iy] = 1e30F;
        obj.eyeAndScreen(dim);
        imgCenter = obj.getImgCenter();
        obj.planeCoeff();
        Point3D[] e = obj.getE();
        Point2D[] vScr = obj.getVScr();
        //for (int j=0; j<nFaces; j++)
        for (int j=minFace; j<=maxFace; j++)
        {
            Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
            if (pol.getNrs().length < 3 || pol.getH() >= 0) continue;
            int cCode = obj.colorCode(pol.getA(), pol.getB(), pol.getC());
            //pintamos degradados para el color de la figura
            lienzo.setColor(new Color(cCode, cCode, cCode));
            //pintamos degradados pala el color de la base
            if(j==153||j==154||j==155||j==156) lienzo.setColor(new Color(cCode/4, cCode/2, cCode/2));
            //g.setColor(new Color(cCode, cCode, cCode));
            pol.triangulate(obj);
            Tria[] t = pol.getT();
            for (int i=0; i<t.length; i++)
            {
                Tria tri = t[i];
                int iA = tri.iA, iB = tri.iB, iC = tri.iC;
                Point2D a = vScr[iA], b = vScr[iB], c = vScr[iC];
                double zAi = 1/e[tri.iA].z, zBi = 1/e[tri.iB].z, zCi = 1/e[tri.iC].z;
                double u1 = b.x - a.x, v1 = c.x - a.x, u2 = b.y - a.y, v2 = c.y - a.y, cc=u1*v2-u2*v1;
                if (cc <= 0) continue;
                double xA = a.x, yA = a.y,
                    xB = b.x, yB = b.y,
                    xC = c.x, yC = c.y,
                    xD = (xA + xB + xC)/3,
                    yD = (yA + yB + yC)/3,
                    zDi = (zAi + zBi + zCi)/3,
                    u3 = zBi - zAi, v3 = zCi - zAi,
                    aa=u2*v3-u3*v2,
                    bb=u3*v1-u1*v3,
                    dzdx = -aa/cc, dzdy = -bb/cc,
                    yBottomR = Math.min(yA, Math.min(yB, yC)),
                    yTopR = Math.max(yA, Math.max(yB, yC));
                int yBottom = (int)Math.ceil(yBottomR), yTop = (int)Math.floor(yTopR);
                for (int y=yBottom; y<=yTop; y++)
                {
                    double xI, xJ, xK, xI1, xJ1, xK1, xL, xR;
                    xI = xJ = xK = 1e30;
                    xI1 = xJ1 = xK1 = -1e30;
                    if((y-yB)*(y-yC)<=0&&yB!=yC)
                        xI = xI1 = xC + (y - yC)/(yB - yC) * (xB - xC);
                    if((y-yC)*(y-yA)<=0&&yC!=yA)
                        xJ = xJ1 = xA + (y - yA)/(yC - yA) * (xC - xA);
                    if((y-yA)*(y-yB)<=0&&yA!=yB)
                        xK = xK1 = xB + (y - yB)/(yA - yB) * (xA - xB);
                    xL = Math.min(xI, Math.min(xJ, xK));
                    xR = Math.max(xI1, Math.max(xJ1, xK1));
                    int iy = iY((float)y), iXL = iX((float)(xL+0.5)), iXR = iX((float)(xR-0.5));
                    double zi = 1.01 * zDi + (y - yD) * dzdy + (xL - xD) * dzdx;
                    boolean leftmostValid = false;
                    int xLeftmost = 0;
                    for (int ix=iXL; ix<=iXR; ix++)
                    {
                        if (zi < buf[ix][iy])
                        {
                            if (!leftmostValid)
                            {
                                xLeftmost = ix;
                                leftmostValid = true;
                            }
                            buf[ix][iy] = (float)zi;
                        }
                        else
                            if (leftmostValid)
                            {
                                //g.drawLine(xLeftmost, iy, ix-1, iy);
                                lienzo.drawLine(xLeftmost, iy, ix-1, iy);
                                leftmostValid = false;
                            } 
                        zi += dzdx;
                    }
                    if (leftmostValid)
                        //g.drawLine(xLeftmost, iy, iXR, iy);
                        lienzo.drawLine(xLeftmost, iy, iXR, iy);
                }
            }
        }
        //pintamos sobre el canvas el buffer con la figura terminada
        g.drawImage(buffer,0,0,this);
    }
    
    @Override
    public void paint(Graphics g){
        update(g);
    }
    
}

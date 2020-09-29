
package pistones;

import java.awt.Canvas;

public abstract class Canvas3D extends Canvas
{
    abstract Obj3D getObj();
    abstract void setObj(Obj3D obj);
    abstract void setNFaces(int min, int max);
}

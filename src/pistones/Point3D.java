
package pistones;

public class Point3D
{
    float x, y, z;
    Point3D(double x, double y, double z)
    {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }
    public float getY(){return y;}
    public void setY(float y){this.y = y;}
}

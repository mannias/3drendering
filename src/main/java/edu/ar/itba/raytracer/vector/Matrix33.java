package edu.ar.itba.raytracer.vector;

public class Matrix33 {

    private double[][] m = new double[3][3];

    public Matrix33(double m00, double m01, double m02, double m10,
                    double m11, double m12, double m20, double m21,
                    double m22) {
        m[0][0] = m00;
        m[0][1] = m01;
        m[0][2] = m02;
        m[1][0] = m10;
        m[1][1] = m11;
        m[1][2] = m12;
        m[2][0] = m20;
        m[2][1] = m21;
        m[2][2] = m22;
    }

    public Matrix33(Vector4 vec1, Vector4 vec2, Vector4 vec3){
        this(vec1.x,vec1.y,vec1.z,vec2.x,vec2.y,vec2.z,vec3.x,vec3.y,vec3.z );
    }

    public Matrix33 transpose() {
        return new Matrix33(m[0][0], m[1][0], m[2][0],
                m[0][1], m[1][1], m[2][1],
                m[0][2], m[1][2], m[2][2]);
    }

    public Vector3 multiplyVec(final Vector4 vec) {
        final double[] res = new double[4];

        res[0] = m[0][0] * vec.x + m[0][1] * vec.y + m[0][2] * vec.z;
        res[1] = m[1][0] * vec.x + m[1][1] * vec.y + m[1][2] * vec.z;
        res[2] = m[2][0] * vec.x + m[2][1] * vec.y + m[2][2] * vec.z;
        return new Vector3(res[0], res[1], res[2]);
    }
}

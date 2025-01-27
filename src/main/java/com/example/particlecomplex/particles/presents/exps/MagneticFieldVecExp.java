package com.example.particlecomplex.particles.presents.exps;

public class MagneticFieldVecExp {
    StringBuilder vecExpX;
    StringBuilder vecExpY;
    StringBuilder vecExpZ;

    /**
     * 构造函数，生成粒子在磁场中的运动表达式
     *
     * @param q  粒子的电荷量
     * @param bx 磁场的 x 分量
     * @param by 磁场的 y 分量
     * @param bz 磁场的 z 分量
     */
    public MagneticFieldVecExp(double q, double bx, double by, double bz) {
        vecExpX = new StringBuilder();
        vecExpY = new StringBuilder();
        vecExpZ = new StringBuilder();

        // 洛伦兹力公式：F = q * (v x B)
        // X 分量
        vecExpX.append(String.format(
                "%f * ((vy * %f) - (vz * %f))", q, bz, by));

        // Y 分量
        vecExpY.append(String.format(
                "%f * ((vz * %f) - (vx * %f))", q, bx, bz));

        // Z 分量
        vecExpZ.append(String.format(
                "%f * ((vx * %f) - (vy * %f))", q, by, bx));
    }

    /**
     * 返回磁场作用下的速度表达式
     *
     * @return 包含 X、Y、Z 分量表达式的字符串数组
     */
    public String[] getStrings() {
        return new String[]{vecExpX.toString(), vecExpY.toString(), vecExpZ.toString()};
    }

    public static void main(String[] args) {
        // 示例：粒子电荷 q=1，磁场 B=(0, 0, 1)
        double q = 1.0;
        double bx = 0.0;
        double by = 0.0;
        double bz = 1.0;

        MagneticFieldVecExp exp = new MagneticFieldVecExp(q, bx, by, bz);
        String[] expressions = exp.getStrings();

        // 打印结果
        System.out.println("X 分量: " + expressions[0]);
        System.out.println("Y 分量: " + expressions[1]);
        System.out.println("Z 分量: " + expressions[2]);
    }
}

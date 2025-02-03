package com.example.particlecomplex.particles.base;

import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.utils.expression_utils.EquationSolver;
import com.example.particlecomplex.utils.expression_utils.ExpressionExtendBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.objecthunter.exp4j.Expression;

import java.util.*;


@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ParticleAreaSpawner {

    private final BaseParticleType type;
    private final double start;
    private final double end;
    private final double step;
    private static Level level;

    // 添加 ServerLevel 支持

    private static final ArrayList<ArrayList<double[]>> pointsFromParticles = new ArrayList<>();
    private static final ArrayList<BaseParticleType> particles = new ArrayList<>();

    private String xPositionExpression = null;
    private String yPositionExpression = null;
    private String zPositionExpression = null;

    // 添加极坐标系表达式
    private String rExpression = null;
    private String thetaExpression = null;
    private String phiExpression = null;

    // 重构构造函数以支持 ServerLevel
    public ParticleAreaSpawner(Level level, BaseParticleType type, double start, double end, double step) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.step = step;
        ParticleAreaSpawner.level = level;
    }
    public ParticleAreaSpawner(Level level, BaseParticleType type) {
        this.type = type;
        this.start = 0;
        this.end = 1;
        this.step = 1;
        ParticleAreaSpawner.level = level;
    }

    public void setPositionExpression(String expressionX, String expressionY, String expressionZ) {
        this.xPositionExpression = expressionX;
        this.yPositionExpression = expressionY;
        this.zPositionExpression = expressionZ;
    }

    // 添加设置极坐标表达式的方法
    public void setPolarPositionExpression(String rExpression, String thetaExpression, String phiExpression) {
        this.rExpression = rExpression;
        this.thetaExpression = thetaExpression;
        this.phiExpression = phiExpression;
    }

    public void createByPositionExpression(double o_x, double o_y, double o_z) {
        if (xPositionExpression == null || yPositionExpression == null || zPositionExpression == null) {
            ExampleMod.LOGGER.error("发现空表达式,全部替换为0");
            this.xPositionExpression="0";
            this.yPositionExpression="0";
            this.zPositionExpression="0";
        }
        ArrayList<double[]> points = new ArrayList<>();
        Expression expX = new ExpressionExtendBuilder(this.xPositionExpression)
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .build();

        Expression expY = new ExpressionExtendBuilder(this.yPositionExpression)
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .build();

        Expression expZ = new ExpressionExtendBuilder(this.zPositionExpression)
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .build();

        double relPosX=0;
        double relPosY=0;
        double relPosZ=0;

        for (double i = this.start; i <= end; i += step) {
            double offset_x = expX.setVariables(Map.of("t", (double) i,"x",relPosX,"y",relPosY,"z",relPosZ)).evaluate();

            double offset_y = expY.setVariables(Map.of("t", (double) i,"x",relPosX,"y",relPosY,"z",relPosZ)).evaluate();

            double offset_z = expZ.setVariables(Map.of("t", (double) i,"x",relPosX,"y",relPosY,"z",relPosZ)).evaluate();

            double[] newPoint = new double[]{
                    o_x + offset_x,
                    o_y + offset_y,
                    o_z + offset_z
            };
            points.add(newPoint);
        }
        particles.add(type);
        pointsFromParticles.add(points);
    }

    public void createByPositionEquation(double o_x, double o_y, double o_z, String Equation, double tolerance) {
        EquationSolver solver = new EquationSolver(Equation, tolerance);
        List<double[]> points = solver.solve(this.start, this.end, this.step);
        ArrayList<double[]> points_n = new ArrayList<>();
        for (double[] point : points) {
            double offset_x = point[0];
            double offset_y = point[1];
            double offset_z = point[2];
            double[] newPoint = new double[]{
                    o_x + offset_x,
                    o_y + offset_y,
                    o_z + offset_z
            };
            points_n.add(newPoint);
        }
        particles.add(type);
        pointsFromParticles.add(points_n);
    }
    public void createSingle(double o_x, double o_y, double o_z) {
        ArrayList<double[]> points_n = new ArrayList<>();
        double[] newPoint = new double[]{o_x , o_y, o_z};
        points_n.add(newPoint);
        particles.add(type);
        pointsFromParticles.add(points_n);
    }







    // 添加极坐标生成点的方法
    public void createByPolarPositionExpression(double o_x, double o_y, double o_z) {
        if (rExpression == null || thetaExpression == null || phiExpression == null) {
            this.rExpression="0";
            this.thetaExpression="0";
            this.phiExpression="0";
        }
        ArrayList<double[]> points = new ArrayList<>();
        Expression expR = new ExpressionExtendBuilder(this.rExpression)
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .build();

        Expression expTheta = new ExpressionExtendBuilder(this.thetaExpression)
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .build();

        Expression expPhi = new ExpressionExtendBuilder(this.phiExpression)
                .variable("t")
                .variable("x")
                .variable("y")
                .variable("z")
                .build();

        double relPosX=0;
        double relPosY=0;
        double relPosZ=0;

        for (double i = this.start; i <= end; i += step) {
            double r = expR.setVariables(Map.of("t", (double) i,"x",relPosX,"y",relPosY,"z",relPosZ)).evaluate();
            double theta = Math.toRadians(expTheta.setVariables(Map.of("t", (double) i,"x",relPosX,"y",relPosY,"z",relPosZ)).evaluate());
            double phi = Math.toRadians(expPhi.setVariables(Map.of("t", (double) i,"x",relPosX,"y",relPosY,"z",relPosZ)).evaluate());

            // 极坐标到直角坐标转换
            double offset_x = r * Math.sin(phi) * Math.cos(theta);
            double offset_y = r * Math.sin(phi) * Math.sin(theta);
            double offset_z = r * Math.cos(phi);
            relPosX=offset_x;
            relPosY=offset_y;
            relPosZ=offset_z;

            double[] newPoint = new double[]{
                    o_x + offset_x,
                    o_y + offset_y,
                    o_z + offset_z
            };
            points.add(newPoint);
        }
        particles.add(type);
        pointsFromParticles.add(points);
    }



    public void createByCurve(double step, double[][] controlPoints, String curveType) {
        ArrayList<double[]> points = new ArrayList<>();
        if (step <= 0) {
            throw new IllegalArgumentException("步长小于等于0会死循环,你的步长 " + step);
        }
        for (double i = 0; i < 1; i += step) {
            double[] pointsN = switch (curveType) {
                case "BezierCurve" -> BezierCurve3D.calculateBezier3D(i, controlPoints);
                case "NatureCubicSpline" -> NatureCubicSpline.calculateNaturalCubicSpline(i, controlPoints);
                case "CatmullRomSpline" -> CatmullRomSpline3D.calculateCatmullRom3D(i, controlPoints);
                case "ConnectedLines" -> ConnectedPoints.getPointOnPath(controlPoints, i);
                default -> throw new IllegalStateException("Unexpected value: " + curveType);
            };
            points.add(pointsN);
        }
        particles.add(type);
        pointsFromParticles.add(points);
    }

    static class ControlPointLineSegments {

        public static double[][][] generateUniqueLineSegments(double[][] controlPoints, int n) {
            List<double[][]> lineSegments = new ArrayList<>();
            boolean[] used = new boolean[controlPoints.length];
            double[][] currentPermutation = new double[n][3];
            Set<String> segmentSet = new HashSet<>(); // 存储唯一线段的集合
            generateLineSegments(controlPoints, used, currentPermutation, lineSegments, segmentSet, n, 0);
            return lineSegments.toArray(new double[0][][]);
        }

        private static void generateLineSegments(double[][] controlPoints, boolean[] used, double[][] currentPermutation, List<double[][]> lineSegments, Set<String> segmentSet, int n, int depth) {
            if (depth == n) {
                StringBuilder segmentKey = new StringBuilder();
                for (double[] point : currentPermutation) {
                    segmentKey.append(point[0]).append(",").append(point[1]).append(",").append(point[2]).append(";");
                }
                if (segmentSet.add(segmentKey.toString())) {
                    lineSegments.add(currentPermutation.clone());
                }
                return;
            }

            for (int i = 0; i < controlPoints.length; i++) {
                if (!used[i]) {
                    used[i] = true;
                    currentPermutation[depth] = controlPoints[i];
                    generateLineSegments(controlPoints, used, currentPermutation, lineSegments, segmentSet, n, depth + 1);
                    used[i] = false;
                }
            }
        }
    }

    public void createByConnectedCurve(double step, double[][] controlPoints, String curveType, int N) {
        double[][][] TcontrolPoints = ControlPointLineSegments.generateUniqueLineSegments(controlPoints, N);
        ArrayList<double[]> points = new ArrayList<>();
        if (step <= 0) {
            throw new IllegalArgumentException("步长小于等于0会死循环,你的步长 " + step);
        }
        for (double[][] NcontrolPoints : TcontrolPoints) {
            for (double i = 0; i < 1; i += step) {
                double[] pointsN = switch (curveType) {
                    case "BezierCurve" -> BezierCurve3D.calculateBezier3D(i, NcontrolPoints);
                    case "NatureCubicSpline" -> NatureCubicSpline.calculateNaturalCubicSpline(i, NcontrolPoints);
                    case "CatmullRomSpline" -> CatmullRomSpline3D.calculateCatmullRom3D(i, NcontrolPoints);
                    case "ConnectedLines" -> ConnectedPoints.getPointOnPath(NcontrolPoints, i);
                    default -> throw new IllegalStateException("Unexpected value: " + curveType);
                };
                points.add(pointsN);
            }
        }
        particles.add(type);
        pointsFromParticles.add(points);
    }



    @SubscribeEvent
    public static void onFrameTick(TickEvent.ClientTickEvent event) {
        List<BaseParticleType> particlesToRemove = new ArrayList<>();
        for (int i = 0; i < pointsFromParticles.size(); i++) {
            BaseParticleType particle = particles.get(i);
            if(particle==null){return;}
            double fps = particle.getFps();

            double ticksPerFrame = fps >= 1 ? Math.round(1.0 / fps) : 1.0 / fps;
            int framesToRender = (int) Math.round(fps >= 1 ? fps : 1.0 / ticksPerFrame);

            ArrayList<double[]> points = pointsFromParticles.get(i);
            if (points.isEmpty()) {
                particlesToRemove.add(particle);
                continue;
            }

            for (int j = 0; j < framesToRender; j++) {
                if (!points.isEmpty()) {
                    double[] point = points.get(0);
                    // 判断是否是服务端，调用不同的生成粒子方法
                    if (level != null &&!level.isClientSide) {
                        ServerLevel serverLevel=(ServerLevel) level;
                        for (ServerPlayer player : serverLevel.players()) {
                            serverLevel.sendParticles(player, particle, true, point[0], point[1], point[2],
                                    1, 0, 0, 0, 0);
                        }
                    } else if (level != null) {
                        level.addParticle(particle, true, point[0], point[1], point[2], 0, 0, 0);
                    }
                    points.remove(0);
                } else {
                    particlesToRemove.add(particle);
                    break;
                }
            }
        }

        for (BaseParticleType particle : particlesToRemove) {
            int index = particles.indexOf(particle);
            if (index != -1) {
                pointsFromParticles.remove(index);
                particles.remove(index);
            }
        }
    }

    static class BezierCurve3D {
        public static double[] calculateBezier3D(double t, double[][] controlPoints) {
            if (controlPoints.length < 2) {
                throw new IllegalArgumentException("传入点的数量少于2");
            }
            if (t < 0 || t > 1) {
                throw new IllegalArgumentException("参数 t 必须在 [0, 1] 范围内");
            }
            int n = controlPoints.length - 1;
            double[] result = new double[3];

            for (int i = 0; i <= n; i++) {

                double binomialCoeff = binomialCoefficient(n, i);
                double term = binomialCoeff * Math.pow(1 - t, n - i) * Math.pow(t, i);
                for (int j = 0; j < 3; j++) {
                    result[j] += term * controlPoints[i][j];
                }
            }

            return result;
        }

        private static double binomialCoefficient(int n, int k) {
            if (k > n) return 0;
            if (k == 0 || k == n) return 1;
            return factorial(n) / (factorial(k) * factorial(n - k));
        }

        private static double factorial(int n) {
            double result = 1;
            for (int i = 1; i <= n; i++) {
                result *= i;
            }
            return result;
        }
    }

    static class ConnectedPoints {

        public static double[] getPointOnPath(double[][] controlPoints, double t) {
            int numPoints = controlPoints.length;
            double[] point = new double[3]; // 假设三维空间

            // 计算每段路径的点
            int segmentCount = numPoints * (numPoints - 1) / 2; // 计算总段数
            double segmentLength = 1.0 / segmentCount; // 每段的长度

            int segmentIndex = (int) (t / segmentLength); // 当前段的索引
            double segmentT = (t % segmentLength) / segmentLength; // 当前段的进度

            // 计算起始点和结束点的索引
            if (segmentIndex < numPoints - 1) {
                double[] startPoint = controlPoints[segmentIndex];
                double[] endPoint = controlPoints[segmentIndex + 1];
                point[0] = interpolate(startPoint[0], endPoint[0], segmentT);
                point[1] = interpolate(startPoint[1], endPoint[1], segmentT);
                point[2] = interpolate(startPoint[2], endPoint[2], segmentT);
            } else {
                // 处理最后一段，确保返回终点
                point[0] = controlPoints[numPoints - 1][0];
                point[1] = controlPoints[numPoints - 1][1];
                point[2] = controlPoints[numPoints - 1][2];
            }

            return point;
        }

        // 插值函数
        private static double interpolate(double start, double end, double t) {
            return start + (end - start) * t;
        }
    }


    // Catmull-Rom 样条
    static class CatmullRomSpline3D {
        public static double[] calculateCatmullRom3D(double t, double[][] controlPoints) {
            if (controlPoints.length < 4) {
                throw new IllegalArgumentException("控制点数量不能少于4");
            }
            if (t < 0 || t > 1) {
                throw new IllegalArgumentException("t 必须在 [0, 1] 范围内");
            }

            int n = controlPoints.length - 1;
            int segmentIndex = (int) (t * n);
            segmentIndex = Math.min(segmentIndex, n - 1);
            double segmentT = (t * n) - segmentIndex;

            double[] p0 = controlPoints[segmentIndex == 0 ? 0 : segmentIndex - 1];
            double[] p1 = controlPoints[segmentIndex];
            double[] p2 = controlPoints[segmentIndex + 1];
            double[] p3 = controlPoints[segmentIndex == n - 1 ? n : segmentIndex + 2];

            double[] result = new double[3];
            for (int i = 0; i < 3; i++) {
                result[i] = 0.5 * (
                        (2 * p1[i]) +
                                (-p0[i] + p2[i]) * segmentT +
                                (2 * p0[i] - 5 * p1[i] + 4 * p2[i] - p3[i]) * segmentT * segmentT +
                                (-p0[i] + 3 * p1[i] - 3 * p2[i] + p3[i]) * segmentT * segmentT * segmentT
                );
            }

            return result;
        }
    }

    static class NatureCubicSpline {

        // 计算 Catmull-Rom 样条曲线上的点
        public static double[] calculateNaturalCubicSpline(double t, double[][] controlPoints) {
            if (controlPoints.length < 4) {
                throw new IllegalArgumentException("控制点数量必须至少为4");
            }
            if (t < 0 || t > 1) {
                throw new IllegalArgumentException("参数 t 必须在 [0, 1] 范围内");
            }

            int n = controlPoints.length - 1;
            int segmentIndex = (int) (t * n);
            segmentIndex = Math.min(segmentIndex, n - 1); // 确保不会超出边界
            double segmentT = (t * n) - segmentIndex; // 在段内的 t 值

            // 获取四个控制点
            double[] p0 = controlPoints[segmentIndex == 0 ? 0 : segmentIndex - 1];
            double[] p1 = controlPoints[segmentIndex];
            double[] p2 = controlPoints[segmentIndex + 1];
            double[] p3 = controlPoints[segmentIndex == n - 1 ? n : segmentIndex + 2];

            // 计算自然样条曲线的点
            double[] result = new double[3];
            for (int i = 0; i < 3; i++) {
                result[i] = 0.5 * (
                        (2 * p1[i]) +
                                (-p0[i] + p2[i]) * segmentT +
                                (2 * p0[i] - 5 * p1[i] + 4 * p2[i] - p3[i]) * segmentT * segmentT +
                                (-p0[i] + 3 * p1[i] - 3 * p2[i] + p3[i]) * segmentT * segmentT * segmentT
                );
            }

            return result;
        }

    }

}

package com.example.particlecomplex.particles.presents.exps;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class ElectricChargeVecExp {
    StringBuilder vecExpX;
    StringBuilder vecExpY;
    StringBuilder vecExpZ;

//    默认k=1,F=q1q2/r^2
    public ElectricChargeVecExp( double q1, List<Double> q2s, List<Vector3d> poses){
        vecExpX= new StringBuilder();
        vecExpY= new StringBuilder();
        vecExpZ= new StringBuilder();
        if(q2s.size()!=poses.size()){
            throw new RuntimeException("错误的电荷量与位置匹配");
        }
        for (int i = 0; i < q2s.size() ; i++) {
            double q2=q2s.get(i);
            Vector3d pos=poses.get(i);
            vecExpX.append("+").append(String.format("(%f * %f * (%f - x) / (sqrt((x - %f)^2 + (y - %f)^2 + (z - %f)^2))^3)", q1, q2, pos.x, pos.x, pos.y, pos.z));
            vecExpY.append("+").append(String.format("(%f*%f*(%f-y)/ (sqrt((x - %f)^2 + (y - %f)^2 + (z - %f)^2))^3)",q1,q2,pos.y,pos.x,pos.y,pos.z));
            vecExpZ.append("+").append(String.format("(%f*%f*(%f-z)/ (sqrt((x - %f)^2 + (y - %f)^2 + (z - %f)^2))^3)",q1,q2,pos.z,pos.x,pos.y,pos.z));
        }

    }
    public ElectricChargeVecExp( double q1, List<Double> q2s,int entityAmount){
        vecExpX= new StringBuilder();
        vecExpY= new StringBuilder();
        vecExpZ= new StringBuilder();
        if(q2s.size()!=entityAmount){
            throw new RuntimeException("错误的电荷量与实体匹配");
        }

        for (int i = 0; i < q2s.size() ; i++) {
            double q2=q2s.get(i);
            String entityX="e"+"x"+i;
            String entityY="e"+"y"+i;
            String entityZ="e"+"z"+i;
            vecExpX.append("+").append("(").append(q1).append(" * ").append(q2).append(" * (").append(entityX).append(" - x) / (sqrt((x - ").append(entityX).append(")^2 + (").append(entityY).append(" - y)^2 + (").append(entityZ).append(" - z)^2))^3)");
            vecExpY.append("+").append("(").append(q1).append(" * ").append(q2).append(" * (").append(entityY).append(" - y) / (sqrt((x - ").append(entityX).append(")^2 + (").append(entityY).append(" - y)^2 + (").append(entityZ).append(" - z)^2))^3)");
            vecExpZ.append("+").append("(").append(q1).append(" * ").append(q2).append(" * (").append(entityZ).append(" - z) / (sqrt((x - ").append(entityX).append(")^2 + (").append(entityY).append(" - y)^2 + (").append(entityZ).append(" - z)^2))^3)");

        }

    }

        public String[] getStrings(){
            return new String[]{vecExpX.toString(),vecExpY.toString(),vecExpZ.toString()};
        }

    public static void main(String[] args){
        List<Double> q2s=new ArrayList<>();
        q2s.add(3d);
        double[] a=new double[]{1d};
        ElectricChargeVecExp electricChargeVecExp=new ElectricChargeVecExp(3,q2s,1);
        System.out.println(electricChargeVecExp.getStrings()[0]);

    }

}

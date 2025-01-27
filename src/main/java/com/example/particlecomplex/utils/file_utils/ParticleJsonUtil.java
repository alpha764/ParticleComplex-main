package com.example.particlecomplex.utils.file_utils;

import com.example.particlecomplex.particles.base.BaseParticleType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3d;
import org.joml.Vector4i;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.example.particlecomplex.utils.file_utils.FileUtil.getPresentFolder;

public class ParticleJsonUtil {
    public static boolean writeParticleJsonDoubleValue(CommandContext<CommandSourceStack> context, String property, double value) {
        String filename = StringArgumentType.getString(context, "name");
        try {
            File file = FileUtil.getFileInPresentParticleFolder(filename);

            // 读取整个 JSON 数据
            JsonObject jsonObject;
            try (FileReader reader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            }

            // 获取 baseParticle 部分
            JsonObject baseParticle = jsonObject.getAsJsonObject("baseParticle");
            if (baseParticle == null) {
                baseParticle = new JsonObject();
                jsonObject.add("baseParticle", baseParticle);
            }

            // 更新指定属性的值
            baseParticle.addProperty(property, value);

            // 写回整个 JSON 数据
            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new Gson();
                gson.toJson(jsonObject, writer);
            }
            return true;

        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("写入" + property + "属性时发生错误"));
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeParticleJsonNestedValue(CommandContext<CommandSourceStack> context, String property, String key, double value) {
        String filename = StringArgumentType.getString(context, "name");
        try {
            File file = FileUtil.getFileInPresentParticleFolder(filename);

            // 读取整个 JSON 数据
            JsonObject jsonObject;
            try (FileReader reader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            }

            // 获取 baseParticle 部分
            JsonObject baseParticle = jsonObject.getAsJsonObject("baseParticle");
            if (baseParticle == null) {
                baseParticle = new JsonObject();
                jsonObject.add("baseParticle", baseParticle);
            }

            // 获取嵌套对象
            JsonObject nestedObject = baseParticle.getAsJsonObject(property);
            if (nestedObject == null) {
                nestedObject = new JsonObject();
                baseParticle.add(property, nestedObject);
            }

            // 更新嵌套对象中的子属性
            nestedObject.add(key, new JsonPrimitive(value));

            // 写回整个 JSON 数据
            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new Gson();
                gson.toJson(jsonObject, writer);
            }
            return true;

        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("写入" + property + "." + key + "时发生错误"));
            e.printStackTrace();
            return false;
        }
    }


    public static boolean writeParticleJsonString(CommandContext<CommandSourceStack> context, String property, String value) {
        String filename = StringArgumentType.getString(context, "name");
        try {
            File file = FileUtil.getFileInPresentParticleFolder(filename);

            // 读取整个 JSON 数据
            JsonObject jsonObject;
            try (FileReader reader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            }

            // 获取 baseParticle 部分
            JsonObject baseParticle = jsonObject.getAsJsonObject("baseParticle");
            if (baseParticle == null) {
                baseParticle = new JsonObject();
                jsonObject.add("baseParticle", baseParticle);
            }

            // 更新指定属性的字符串值
            baseParticle.addProperty(property, value);

            // 写回整个 JSON 数据
            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new Gson();
                gson.toJson(jsonObject, writer);
            }
            return true;

        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("写入" + property + "属性时发生错误"));
            e.printStackTrace();
            return false;
        }
    }

    public static BaseParticleType setAttributesFromJson(BaseParticleType baseParticle, JsonObject baseParticleObject) {
        // 提取并设置各个属性
        if (baseParticleObject.has("tps")) {
            baseParticle.setFps(baseParticleObject.get("tps").getAsFloat());
        }
        if (baseParticleObject.has("speed")) {
            JsonObject speedObject = baseParticleObject.getAsJsonObject("speed");
            double speedX = speedObject.get("x").getAsDouble();
            double speedY = speedObject.get("y").getAsDouble();
            double speedZ = speedObject.get("z").getAsDouble();
            baseParticle.setSpeed(new Vector3d(speedX, speedY, speedZ));
        }
        if (baseParticleObject.has("color")) {
            JsonObject colorObject = baseParticleObject.getAsJsonObject("color");
            int red = colorObject.get("x").getAsInt();
            int green = colorObject.get("y").getAsInt();
            int blue = colorObject.get("z").getAsInt();
            int alpha = colorObject.get("w").getAsInt();
            baseParticle.setColor(new Vector4i(red, green, blue, alpha));
        }
        if (baseParticleObject.has("diameter")) {
            baseParticle.setDiameter(baseParticleObject.get("diameter").getAsFloat());
        }
        if (baseParticleObject.has("lifetime")) {
            baseParticle.setLifetime(baseParticleObject.get("lifetime").getAsInt());
        }
        if (baseParticleObject.has("vecExpX")) {
            baseParticle.setVecExpX(baseParticleObject.get("vecExpX").getAsString());
        }
        if (baseParticleObject.has("vecExpY")) {
            baseParticle.setVecExpY(baseParticleObject.get("vecExpY").getAsString());
        }
        if (baseParticleObject.has("vecExpZ")) {
            baseParticle.setVecExpZ(baseParticleObject.get("vecExpZ").getAsString());
        }
        if (baseParticleObject.has("entitiesID")) {
            List<Integer> entitiesID = new ArrayList<>();
            for (var id : baseParticleObject.getAsJsonArray("entitiesID")) {
                entitiesID.add(id.getAsInt());
            }
            baseParticle.setEntitiesID(entitiesID);
        }
        if (baseParticleObject.has("ax")) {
            baseParticle.setAx(baseParticleObject.get("ax").getAsFloat());
        }
        if (baseParticleObject.has("ay")) {
            baseParticle.setAy(baseParticleObject.get("ay").getAsFloat());
        }
        if (baseParticleObject.has("az")) {
            baseParticle.setAz(baseParticleObject.get("az").getAsFloat());
        }
        if (baseParticleObject.has("centerX") && baseParticleObject.has("centerY") && baseParticleObject.has("centerZ")) {
            double centerX = baseParticleObject.get("centerX").getAsDouble();
            double centerY = baseParticleObject.get("centerY").getAsDouble();
            double centerZ = baseParticleObject.get("centerZ").getAsDouble();
            baseParticle.setCenter(centerX, centerY, centerZ);
        }
        if (baseParticleObject.has("dynamicExp")) {
            baseParticle.setDynamicExp(baseParticleObject.get("dynamicExp").getAsString());
        }
        if (baseParticleObject.has("rx") && baseParticleObject.has("ry") && baseParticleObject.has("rz")) {
            baseParticle.setRotation(baseParticleObject.get("rx").getAsDouble(), baseParticleObject.get("ry").getAsDouble(), baseParticleObject.get("rz").getAsDouble());
        }


        return baseParticle;
    }

    public static BaseParticleType loadParticleFromFile(String filename) {
        File file = FileUtil.getFileInPresentParticleFolder(filename);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filename);
        }

        try (FileReader reader = new FileReader(file)) {
            // 解析 JSON
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            // 读取 JSON 中的 baseParticle 对象
            JsonObject baseParticleObject = jsonObject.getAsJsonObject("baseParticle");

            JsonObject particleTypeObject = jsonObject.getAsJsonObject("particleType");
            ParticleType<?> a = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(particleTypeObject.getAsJsonPrimitive("type").getAsString()));

            // 创建一个空的 BaseParticleType 实例
            BaseParticleType baseParticle = (BaseParticleType) a;

            // 直接传递 JsonObject 给 setAttributesFromJson
            return setAttributesFromJson(baseParticle, baseParticleObject);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read particle file", e);
        }
    }

    public static List<String> getAllParticleFileName() {
        return Arrays.stream(Objects.requireNonNull(getPresentFolder().listFiles())).map(File::getName).toList();
    }


}

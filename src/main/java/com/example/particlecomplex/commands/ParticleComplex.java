package com.example.particlecomplex.commands;
import com.example.particlecomplex.ExampleMod;
import com.example.particlecomplex.utils.expression_utils.ExpressionExtendBuilder;
import com.example.particlecomplex.particles.base.ParticleAreaSpawner;
import com.example.particlecomplex.utils.file_utils.FileUtil;
import com.example.particlecomplex.utils.file_utils.ParticleJsonUtil;
import com.example.particlecomplex.particles.base.BaseParticleType;
import com.google.gson.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.objecthunter.exp4j.Expression;
import org.joml.Vector4i;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.particlecomplex.utils.file_utils.FileUtil.*;
import static com.example.particlecomplex.utils.file_utils.ParticleJsonUtil.*;
public class ParticleComplex {
    static String documentation = """
            支持的基本函数包括：
            幂和根：
              - pow(x, y) 或 x^y (幂运算)
              - sqrt(x) (平方根)
            三角函数：
              - sin(x) (正弦)
              - cos(x) (余弦)
              - tan(x) (正切)
              - asin(x) (反正弦)
              - acos(x) (反余弦)
              - atan(x) (反正切)
              - atan2(x) (JAVA的MATH.atan2)
              - sinh(x)
              - cosh(x)
              - tanh(x)
            对数函数：
              - log(x) (自然对数，底数为 e)
              - log10(x) (以 10 为底的对数)
              - logN(x, base) (任意底数对数)
            指数函数：
              - exp(x) (以 e 为底的指数函数)
            绝对值和其他数学函数：
              - abs(x) (绝对值)
              - ceil(x) (向上取整)
              - floor(x) (向下取整)
              - truncate(x) (取整)
              - random() (随机)
            贝塞尔曲线函数：
              - "BezierCurve3(y0,y1,y2,t)
              - "BezierCurve4(y0,y1,y2,y3,t)
            支持的常量：
              - pi (圆周率 π)
              - e (自然对数的底数 e)
            条件运算函数：
              - max(x, y) (返回 x 和 y 中的最大值)
              - min(x, y) (返回 x 和 y 中的最小值)
            """;
    static List<String> curveTypes = List.of("BezierCurve", "NatureCubicSpline", "CatmullRomSpline", "ConnectedLines");

    public static Set<String> getParticlesWithModNamespace(String modId) {
        Set<ResourceLocation> allParticleKeys = ForgeRegistries.PARTICLE_TYPES.getKeys();

        return allParticleKeys.stream()
                .filter(key -> key.getNamespace().equals(modId)) // 只保留指定命名空间的粒子
                .map(ResourceLocation::toString) // 转换为完整的 ResourceLocation 字符串
                .collect(Collectors.toSet());
    }

    private static void outputJsonAttributes(JsonObject jsonObject, CommandContext<CommandSourceStack> context) {
        // 遍历 JSON 对象中的所有键值对
        for (String key : jsonObject.keySet()) {
            JsonElement value = jsonObject.get(key);

            // 将每个属性和值发送到命令执行者
            context.getSource().sendSuccess(() -> Component.literal(key + ": " + value.toString()), false);

            // 如果是嵌套的对象，递归处理
            if (value.isJsonObject()) {
                outputJsonAttributes(value.getAsJsonObject(), context);
            }
        }
    }

    private static double parseRelativeCoordinate(double current, String input) {
        if (input.equals("~")) {
            return current; // "~" 表示当前坐标
        } else if (input.startsWith("~")) {
            try {
                // "~数值" 表示相对坐标
                double offset = Double.parseDouble(input.substring(1));
                return current + offset;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid relative coordinate: " + input);
            }
        } else {
            return Double.parseDouble(input); // 绝对坐标
        }
    }


    static Set<String> supportParticleTypes = getParticlesWithModNamespace(ExampleMod.MODID).stream().map(type -> "\"" + type + "\"").collect(Collectors.toSet());


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("particleComplex")
                        // 添加 add 子命令
                        .then(Commands.literal("add")
                                .then(Commands.literal("Entities")
                                        .then(Commands.literal("CurveWithEntities")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                        .then(Commands.argument("name", StringArgumentType.string())
                                                                .suggests(((commandContext, suggestionsBuilder) -> {
                                                                    getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                    return suggestionsBuilder.buildFuture();
                                                                }))
                                                                .then(Commands.argument("entities", EntityArgument.entities())
                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                        .executes(ParticleComplex::addCurveWithEntities)))))))
                                        .then(Commands.literal("ConnectedCurveWithEntities")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                        .then(Commands.argument("N", IntegerArgumentType.integer())
                                                                .then(Commands.argument("name", StringArgumentType.string())
                                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                            return suggestionsBuilder.buildFuture();
                                                                        }))
                                                                        .then(Commands.argument("entities", EntityArgument.entities())
                                                                                .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                        .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                                .executes(ParticleComplex::addConnectedCurveWithEntities))))))))

                                        .then(Commands.literal("CurveWithEntitiesAndPos")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                        .then(Commands.argument("name", StringArgumentType.string())
                                                                .suggests(((commandContext, suggestionsBuilder) -> {
                                                                    getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                    return suggestionsBuilder.buildFuture();
                                                                }))
                                                                .then(Commands.argument("entities", EntityArgument.entities())
                                                                        .then(Commands.argument("entitiesForVec", EntityArgument.entities())
                                                                                .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                        .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                                .executes(ParticleComplex::addCurveWithEntitiesAndPos))))))))
                                        .then(Commands.literal("ConnectedCurveWithEntitiesAndPos")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                        .then(Commands.argument("N", IntegerArgumentType.integer())
                                                                .then(Commands.argument("name", StringArgumentType.string())
                                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                            return suggestionsBuilder.buildFuture();
                                                                        }))
                                                                        .then(Commands.argument("entities", EntityArgument.entities())
                                                                                .then(Commands.argument("entitiesForVec", EntityArgument.entities())
                                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                                .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                                        .executes(ParticleComplex::addConnectedCurveWithEntitiesAndPos)))))))))

                                        .then(Commands.literal("equationWithEntities")
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        }))
                                                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                                                .then(Commands.argument("exp", StringArgumentType.string())
                                                                        .then(Commands.argument("entities", EntityArgument.entities())
                                                                                .then(Commands.argument("start", DoubleArgumentType.doubleArg())
                                                                                        .then(Commands.argument("end", DoubleArgumentType.doubleArg())
                                                                                                .then(Commands.argument("step", DoubleArgumentType.doubleArg())
                                                                                                        .then(Commands.argument("tolerance", DoubleArgumentType.doubleArg())
                                                                                                                .executes(ParticleComplex::addEquationWithEntities))))))))))
                                        .then(Commands.literal("polarmeterWithEntities")
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        }))
                                                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                                                .then(Commands.argument("offsetX", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("offsetY", DoubleArgumentType.doubleArg())
                                                                                .then(Commands.argument("offsetZ", DoubleArgumentType.doubleArg())
                                                                                        .then(Commands.argument("entities", EntityArgument.entities())
                                                                                                .then(Commands.argument("start", DoubleArgumentType.doubleArg())
                                                                                                        .then(Commands.argument("end", DoubleArgumentType.doubleArg())
                                                                                                                .then(Commands.argument("step", DoubleArgumentType.doubleArg())
                                                                                                                        .then(Commands.argument("expR", StringArgumentType.string())
                                                                                                                                .then(Commands.argument("expTheta", StringArgumentType.string())
                                                                                                                                        .then(Commands.argument("expPhi", StringArgumentType.string())
                                                                                                                                                .executes(ParticleComplex::addPolarmeterWithEntities))))))))))))))
                                        .then(Commands.literal("parameterWithEntities")
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        }))
                                                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                                                .then(Commands.argument("offsetX", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("offsetY", DoubleArgumentType.doubleArg())
                                                                                .then(Commands.argument("offsetZ", DoubleArgumentType.doubleArg())
                                                                                        .then(Commands.argument("entities", EntityArgument.entities())
                                                                                        .then(Commands.argument("start", DoubleArgumentType.doubleArg())
                                                                                                .then(Commands.argument("end", DoubleArgumentType.doubleArg())
                                                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg())
                                                                                                                        .then(Commands.argument("expX", StringArgumentType.string())
                                                                                                                                .then(Commands.argument("expY", StringArgumentType.string())
                                                                                                                                        .then(Commands.argument("expZ", StringArgumentType.string())
                                                                                                                                                .executes(ParticleComplex::addParameterWithEntities))))))))))))))
                                )
                                .then(Commands.literal("Common")
                                        .then(Commands.literal("Single")
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        }))
                                                        .then(Commands.argument("offsetX", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("offsetY", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("offsetZ", DoubleArgumentType.doubleArg())
                                                                                .executes(ParticleComplex::addSingle))))))

                                        .then(Commands.literal("Curve")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                        .then(Commands.argument("name", StringArgumentType.string())
                                                                .suggests(((commandContext, suggestionsBuilder) -> {
                                                                    getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                    return suggestionsBuilder.buildFuture();
                                                                }))
                                                                .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                        .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                .executes(ParticleComplex::addCurve))))))
                                        .then(Commands.literal("ConnectedCurve")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                        .then(Commands.argument("N", IntegerArgumentType.integer())
                                                                .then(Commands.argument("name", StringArgumentType.string())
                                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                            return suggestionsBuilder.buildFuture();
                                                                        }))
                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                        .executes(ParticleComplex::addCurveConnected)))))))
                                        .then(Commands.literal("ConnectedCurveWithEntityPos")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                        .then(Commands.argument("N", IntegerArgumentType.integer())
                                                                .then(Commands.argument("name", StringArgumentType.string())
                                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                            return suggestionsBuilder.buildFuture();
                                                                        }))
                                                                        .then(Commands.argument("entities", EntityArgument.entities())
                                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                                .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                                        .executes(ParticleComplex::addConnectedCurveWithEntityPos))))))))
                                        .then(Commands.literal("CurveWithEntityPos")
                                                .then(Commands.argument("curveType", StringArgumentType.string())
                                                        .suggests((commandContext, suggestionsBuilder) -> {
                                                            curveTypes.forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        })
                                                                .then(Commands.argument("name", StringArgumentType.string())
                                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                                            return suggestionsBuilder.buildFuture();
                                                                        }))
                                                                        .then(Commands.argument("entities", EntityArgument.entities())
                                                                                .then(Commands.argument("step", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                        .then(Commands.argument("positions", StringArgumentType.greedyString())
                                                                                                .executes(ParticleComplex::addCurveWithEntityPos)))))))


                                        .then(Commands.literal("equation")
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        }))
                                                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                                                .then(Commands.argument("exp", StringArgumentType.string())
                                                                        .then(Commands.argument("start", DoubleArgumentType.doubleArg())
                                                                                .then(Commands.argument("end", DoubleArgumentType.doubleArg())
                                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg())
                                                                                                .then(Commands.argument("tolerance", DoubleArgumentType.doubleArg())
                                                                                                        .executes(ParticleComplex::addEquation)))))))))


                                        .then(Commands.literal("polarmeter")
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        }))
                                                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                                                .then(Commands.argument("offsetX", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("offsetY", DoubleArgumentType.doubleArg())
                                                                                .then(Commands.argument("offsetZ", DoubleArgumentType.doubleArg())
                                                                                        .then(Commands.argument("start", DoubleArgumentType.doubleArg())
                                                                                                .then(Commands.argument("end", DoubleArgumentType.doubleArg())
                                                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg())
                                                                                                                .then(Commands.argument("expR", StringArgumentType.string())
                                                                                                                        .then(Commands.argument("expTheta", StringArgumentType.string())
                                                                                                                                .then(Commands.argument("expPhi", StringArgumentType.string())
                                                                                                                                        .executes(ParticleComplex::addPolarmeter)))))))))))))


                                        .then(Commands.literal("parameter")
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                            return suggestionsBuilder.buildFuture();
                                                        }))
                                                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                                                .then(Commands.argument("offsetX", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("offsetY", DoubleArgumentType.doubleArg())
                                                                                .then(Commands.argument("offsetZ", DoubleArgumentType.doubleArg())
                                                                                        .then(Commands.argument("start", DoubleArgumentType.doubleArg())
                                                                                                .then(Commands.argument("end", DoubleArgumentType.doubleArg())
                                                                                                        .then(Commands.argument("step", DoubleArgumentType.doubleArg())
                                                                                                                .then(Commands.argument("expX", StringArgumentType.string())
                                                                                                                        .then(Commands.argument("expY", StringArgumentType.string())
                                                                                                                                .then(Commands.argument("expZ", StringArgumentType.string())
                                                                                                                                        .executes(ParticleComplex::addParameter)))))))))))))
                                )

                        )

                        .then(Commands.literal("board")

                                .then(Commands.literal("getAllFunctionsSupported").executes(ParticleComplex::getAllFunctionsSupported))
                                .then(Commands.literal("getAllAttributeFunctionsSupported").executes(ParticleComplex::getAllAttributeFunctionsSupported))
                                .then(Commands.literal("deleteAll").executes(ParticleComplex::deleteAll))
                                .then(Commands.literal("delete")
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .suggests(((commandContext, suggestionsBuilder) -> {
                                                    getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                                    return suggestionsBuilder.buildFuture();
                                                }))
                                                .executes(ParticleComplex::deleteParticle))))
                        .then(Commands.literal("construct")
                                .then(Commands.argument("particleType", StringArgumentType.string())
                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                            supportParticleTypes.forEach(suggestionsBuilder::suggest);
                                            return suggestionsBuilder.buildFuture();
                                        }))
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .executes(ParticleComplex::constructParticle))))
                        .then(Commands.literal("setProperty")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                            getAllParticleFileName().forEach(suggestionsBuilder::suggest);
                                            return suggestionsBuilder.buildFuture();
                                        }))
                                        .then(Commands.literal("dynamicProperty")
                                                .then(Commands.argument("expression", StringArgumentType.string())
                                                        .executes(ParticleComplex::setDynamicProperty)))
                                        .then(Commands.literal("motion")
                                                .then(Commands.literal("speed")
                                                        .then(Commands.argument("vx", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("vy", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("vz", DoubleArgumentType.doubleArg())
                                                                                .executes(ParticleComplex::setMotionSpeed)))))
                                                .then(Commands.literal("rotation")
                                                        .then(Commands.argument("rx", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("ry", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("rz", DoubleArgumentType.doubleArg())
                                                                                .executes(ParticleComplex::setMotionRotation)))))
                                                .then(Commands.literal("acceleration")
                                                        .then(Commands.argument("ax", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("ay", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("az", DoubleArgumentType.doubleArg())
                                                                                .executes(ParticleComplex::setMotionAcceleration)))))
                                                .then(Commands.literal("center")
                                                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                                                .executes(ParticleComplex::setMotionCenter)))))
                                                .then(Commands.literal("speedExpression")
                                                        .then(Commands.argument("vecExpX", StringArgumentType.string())
                                                                .then(Commands.argument("vecExpY", StringArgumentType.string())
                                                                        .then(Commands.argument("vecExpZ", StringArgumentType.string())
                                                                                .executes(ParticleComplex::setSpeedExpression))))))
                                        .then(Commands.literal("vision")
                                                .then(Commands.literal("lifetime")
                                                        .then(Commands.argument("lifetime", IntegerArgumentType.integer())
                                                                .executes(ParticleComplex::setVisionLifetime)))
                                                .then(Commands.literal("diameter")
                                                        .then(Commands.argument("diameter", DoubleArgumentType.doubleArg())
                                                                .executes(ParticleComplex::setVisionDiameter)))
                                                .then(Commands.literal("color")
                                                        .then(Commands.argument("r", IntegerArgumentType.integer())
                                                                .then(Commands.argument("g", IntegerArgumentType.integer())
                                                                        .then(Commands.argument("b", IntegerArgumentType.integer())
                                                                                .then(Commands.argument("a", IntegerArgumentType.integer())
                                                                                        .executes(ParticleComplex::setVisionColor))))))
                                                .then(Commands.literal("tps")
                                                        .then(Commands.argument("tps", IntegerArgumentType.integer())
                                                                .executes(ParticleComplex::setVisionTps)))))

                        ))
        ;
    }


    private static String replaceEntityCoordinate(String input, Collection<? extends Entity> entities) {
        if (input.startsWith("e")) {
            try {
                int entityIndex = Integer.parseInt(input.substring(1, 2)); // e0x, e1y 等
                String coordinateExpression = input.substring(2); // x+1, y+1 等

                // 获取实体列表
                List<Entity> entityList = new ArrayList<>(entities);
                if (entityIndex >= entityList.size()) return input; // 如果索引超出范围，返回原值

                Entity entity = entityList.get(entityIndex);
                double entityCoordinate;

                // 根据表达式的坐标类型确定基础值
                if (coordinateExpression.startsWith("x")) {
                    entityCoordinate = entity.getX();
                } else if (coordinateExpression.startsWith("y")) {
                    entityCoordinate = entity.getY();
                } else if (coordinateExpression.startsWith("z")) {
                    entityCoordinate = entity.getZ();
                } else {
                    return input; // 非法的坐标类型，返回原值
                }

                // 处理表达式 (比如 "x+1" 或 "y+2")
                String expressionString = entityCoordinate + coordinateExpression.substring(1); // 例如 "10+1"
                Expression expression = new ExpressionExtendBuilder(expressionString).build();

                // 计算并返回结果
                return String.valueOf(expression.evaluate());

            } catch (Exception e) {
                return input; // 如果解析格式不正确，返回原值
            }
        }
        return input;
    }


    private static int addSingle(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        Vec3 pos = context.getSource().getPosition();
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        writeParticleJsonDoubleValue(context, "centerX", pos.x + offsetX);
        writeParticleJsonDoubleValue(context, "centerY", pos.y + offsetY);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z + offsetZ);
        BaseParticleType type = loadParticleFromFile(name);
        context.getSource().getLevel().sendParticles(type, pos.x(), pos.y(), pos.z(), 1, 0, 0, 0, 0);
        context.getSource().sendSuccess(() -> Component.literal("创建单个粒子"), true);
        return 1;
    }

    private static int addCurve(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        String curveType = StringArgumentType.getString(context, "curveType");
        Vec3 pos = context.getSource().getPosition();
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");
        Vec3 playerPosition = context.getSource().getPosition();
        String[] positionArray = positions.split("\\s+");
        if (positionArray.length % 3 != 0) {
            context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
            return 0;
        }

        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            double x = parseRelativeCoordinate(playerPosition.x, positionArray[i]);
            double y = parseRelativeCoordinate(playerPosition.y, positionArray[i + 1]);
            double z = parseRelativeCoordinate(playerPosition.z, positionArray[i + 2]);
            parsedPositions.add(new double[]{x, y, z});

        }
        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByCurve(step, positionArray2D, curveType);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条" + curveType + "曲线"), true);
        return 1;
    }

    private static int addCurveConnected(CommandContext<CommandSourceStack> context) {
        int N = IntegerArgumentType.getInteger(context, "N");
        String name = StringArgumentType.getString(context, "name");
        String curveType = StringArgumentType.getString(context, "curveType");
        Vec3 pos = context.getSource().getPosition();
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");
        Vec3 playerPosition = context.getSource().getPosition();
        String[] positionArray = positions.split("\\s+");
        if (positionArray.length % 3 != 0) {
            context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
            return 0;
        }

        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            double x = parseRelativeCoordinate(playerPosition.x, positionArray[i]);
            double y = parseRelativeCoordinate(playerPosition.y, positionArray[i + 1]);
            double z = parseRelativeCoordinate(playerPosition.z, positionArray[i + 2]);
            parsedPositions.add(new double[]{x, y, z});

        }
        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByConnectedCurve(step, positionArray2D, curveType, N);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条" + curveType + "曲线"), true);
        return 1;
    }

    private static int addCurveWithEntities(CommandContext<CommandSourceStack> context) {
        String curveType = StringArgumentType.getString(context, "curveType");
        String name = StringArgumentType.getString(context, "name");
        Vec3 pos = context.getSource().getPosition();
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        Collection<? extends Entity> entities;
        try {
            entities = EntityArgument.getEntities(context, "entities");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        type.setEntitiesID(entities.stream().map(Entity::getId).toList());
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");
        Vec3 playerPosition = context.getSource().getPosition();
        String[] positionArray = positions.split("\\s+");
        if (positionArray.length % 3 != 0) {
            context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
            return 0;
        }


        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            double x = parseRelativeCoordinate(playerPosition.x, positionArray[i]);
            double y = parseRelativeCoordinate(playerPosition.y, positionArray[i + 1]);
            double z = parseRelativeCoordinate(playerPosition.z, positionArray[i + 2]);
            parsedPositions.add(new double[]{x, y, z});

        }
        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByCurve(step, positionArray2D, curveType);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条"+curveType+"曲线"), true);
        return 1;
    }

    private static int addConnectedCurveWithEntities(CommandContext<CommandSourceStack> context) {
        int N = IntegerArgumentType.getInteger(context, "N");
        String curveType = StringArgumentType.getString(context, "curveType");
        String name = StringArgumentType.getString(context, "name");
        Vec3 pos = context.getSource().getPosition();
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        Collection<? extends Entity> entities;
        try {
            entities = EntityArgument.getEntities(context, "entities");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        type.setEntitiesID(entities.stream().map(Entity::getId).toList());
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");
        Vec3 playerPosition = context.getSource().getPosition();
        String[] positionArray = positions.split("\\s+");
        if (positionArray.length % 3 != 0) {
            context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
            return 0;
        }


        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            double x = parseRelativeCoordinate(playerPosition.x, positionArray[i]);
            double y = parseRelativeCoordinate(playerPosition.y, positionArray[i + 1]);
            double z = parseRelativeCoordinate(playerPosition.z, positionArray[i + 2]);
            parsedPositions.add(new double[]{x, y, z});

        }
        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByConnectedCurve(step, positionArray2D, curveType, N);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条"+curveType+"曲线"), true);
        return 1;
    }

    private static int addCurveWithEntitiesAndPos(CommandContext<CommandSourceStack> context) {
        //第一个实体参数用于确定实体位置areaSpawner,第二个参数用于确定速度VecExp
        //有vec3pos的用vec3pos作为center,没有则是context的pos作为center
        String curveType = StringArgumentType.getString(context, "curveType");
        Vec3 pos = context.getSource().getPosition();
        String name = StringArgumentType.getString(context, "name");
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");

        // 尝试获取实体，确保不会抛出异常
        Collection<? extends Entity> entities;//用于确认位置
        Collection<? extends Entity> entitiesForVec;//用于速度表达式的e
        try {
            entities = EntityArgument.getEntities(context, "entities");
            entitiesForVec = EntityArgument.getEntities(context, "entitiesForVec");
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("获取实体时出错，请检查实体选择器参数."));
            return 0;
        }
        type.setEntitiesID(entitiesForVec.stream().map(Entity::getId).toList());
        // 校验实体数量是否足够
        String[] positionArray;
        Vec3 playerPosition = context.getSource().getPosition();
        if(!positions.equals("all")){
            positionArray = positions.split("\\s+");

        if (positionArray.length % 3 != 0) {
            context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
            return 0;
        }}
        else {
            List<String> ePosT=new ArrayList<>();
            for (Entity entity : entities) {
                ePosT.add(String.valueOf(entity.getX()));
                ePosT.add(String.valueOf(entity.getY()));
                ePosT.add(String.valueOf(entity.getZ()));
            }
            positionArray= ePosT.toArray(new String[0]);
        }


        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            String xString = positionArray[i];
            String yString = positionArray[i + 1];
            String zString = positionArray[i + 2];

            // 使用 replaceEntityCoordinate 替换坐标并进行加法运算
            double x = parseRelativeCoordinate(playerPosition.x, replaceEntityCoordinate(xString, entities));
            double y = parseRelativeCoordinate(playerPosition.y, replaceEntityCoordinate(yString, entities));
            double z = parseRelativeCoordinate(playerPosition.z, replaceEntityCoordinate(zString, entities));

            parsedPositions.add(new double[]{x, y, z});
        }

        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByCurve(step, positionArray2D, curveType);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条"+curveType+"曲线"), true);
        return 1;
    }

    private static int addConnectedCurveWithEntitiesAndPos(CommandContext<CommandSourceStack> context) {
        //第一个实体参数用于确定实体位置areaSpawner,第二个参数用于确定速度VecExp
        //有vec3pos的用vec3pos作为center,没有则是context的pos作为center
        int N = IntegerArgumentType.getInteger(context, "N");
        String curveType = StringArgumentType.getString(context, "curveType");
        Vec3 pos = context.getSource().getPosition();
        String name = StringArgumentType.getString(context, "name");
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");

        Collection<? extends Entity> entities;//用于确认位置
        Collection<? extends Entity> entitiesForVec;//用于速度表达式的e
        try {
            entities = EntityArgument.getEntities(context, "entities");
            entitiesForVec = EntityArgument.getEntities(context, "entitiesForVec");
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("获取实体时出错，请检查实体选择器参数."));
            return 0;
        }
        type.setEntitiesID(entitiesForVec.stream().map(Entity::getId).toList());
        // 校验实体数量是否足够

        String[] positionArray;
        Vec3 playerPosition = context.getSource().getPosition();
        if(!positions.equals("all")){
            positionArray = positions.split("\\s+");

            if (positionArray.length % 3 != 0) {
                context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
                return 0;
            }}
        else {
            List<String> ePosT=new ArrayList<>();
            for (Entity entity : entities) {
                ePosT.add(String.valueOf(entity.getX()));
                ePosT.add(String.valueOf(entity.getY()));
                ePosT.add(String.valueOf(entity.getZ()));
            }
            positionArray= ePosT.toArray(new String[0]);
        }

        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            String xString = positionArray[i];
            String yString = positionArray[i + 1];
            String zString = positionArray[i + 2];

            // 使用 replaceEntityCoordinate 替换坐标并进行加法运算
            double x = parseRelativeCoordinate(playerPosition.x, replaceEntityCoordinate(xString, entities));
            double y = parseRelativeCoordinate(playerPosition.y, replaceEntityCoordinate(yString, entities));
            double z = parseRelativeCoordinate(playerPosition.z, replaceEntityCoordinate(zString, entities));

            parsedPositions.add(new double[]{x, y, z});
        }

        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByConnectedCurve(step, positionArray2D, curveType, N);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条"+curveType+"曲线"), true);
        return 1;
    }
    private static int addConnectedCurveWithEntityPos(CommandContext<CommandSourceStack> context) {
        int N = IntegerArgumentType.getInteger(context, "N");
        String curveType = StringArgumentType.getString(context, "curveType");
        Vec3 pos = context.getSource().getPosition();
        String name = StringArgumentType.getString(context, "name");
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");

        Collection<? extends Entity> entities;//用于确认位置
        try {
            entities = EntityArgument.getEntities(context, "entities");
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("获取实体时出错，请检查实体选择器参数."));
            return 0;
        }

        String[] positionArray;
        Vec3 playerPosition = context.getSource().getPosition();
        if(!positions.equals("all")){
            positionArray = positions.split("\\s+");

            if (positionArray.length % 3 != 0) {
                context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
                return 0;
            }}
        else {
            List<String> ePosT=new ArrayList<>();
            for (Entity entity : entities) {
                ePosT.add(String.valueOf(entity.getX()));
                ePosT.add(String.valueOf(entity.getY()));
                ePosT.add(String.valueOf(entity.getZ()));
            }
            positionArray= ePosT.toArray(new String[0]);
        }

        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            String xString = positionArray[i];
            String yString = positionArray[i + 1];
            String zString = positionArray[i + 2];

            double x = parseRelativeCoordinate(playerPosition.x, replaceEntityCoordinate(xString, entities));
            double y = parseRelativeCoordinate(playerPosition.y, replaceEntityCoordinate(yString, entities));
            double z = parseRelativeCoordinate(playerPosition.z, replaceEntityCoordinate(zString, entities));

            parsedPositions.add(new double[]{x, y, z});
        }

        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByConnectedCurve(step, positionArray2D, curveType, N);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条曲线"), true);
        return 1;
    }
    private static int addCurveWithEntityPos(CommandContext<CommandSourceStack> context) {
        //done
        String curveType = StringArgumentType.getString(context, "curveType");
        Vec3 pos = context.getSource().getPosition();
        String name = StringArgumentType.getString(context, "name");
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        double step = DoubleArgumentType.getDouble(context, "step");
        String positions = StringArgumentType.getString(context, "positions");

        Collection<? extends Entity> entities;//用于确认位置
        try {
            entities = EntityArgument.getEntities(context, "entities");
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("获取实体时出错，请检查实体选择器参数."));
            return 0;
        }

        Vec3 playerPosition = context.getSource().getPosition();
        String[] positionArray;
        if (!positions.equals("all")) {
            positionArray = positions.split("\\s+");
            if (positionArray.length % 3 != 0) {
                context.getSource().sendFailure(Component.literal("必须以(x, y, z)的方式提供点."));
                return 0;
            }
        } else {
            List<String> ePosT = new ArrayList<>();
            for (Entity entity : entities) {
                ePosT.add(String.valueOf(entity.getX()));
                ePosT.add(String.valueOf(entity.getY()));
                ePosT.add(String.valueOf(entity.getZ()));
            }
            positionArray = ePosT.toArray(new String[0]);
        }

        List<double[]> parsedPositions = new ArrayList<>();
        for (int i = 0; i < positionArray.length; i += 3) {
            String xString = positionArray[i];
            String yString = positionArray[i + 1];
            String zString = positionArray[i + 2];

            // 使用 replaceEntityCoordinate 替换坐标并进行加法运算
            double x = parseRelativeCoordinate(playerPosition.x, replaceEntityCoordinate(xString, entities));
            double y = parseRelativeCoordinate(playerPosition.y, replaceEntityCoordinate(yString, entities));
            double z = parseRelativeCoordinate(playerPosition.z, replaceEntityCoordinate(zString, entities));

            parsedPositions.add(new double[]{x, y, z});
        }


        double[][] positionArray2D = parsedPositions.toArray(new double[0][]);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, -1, 1, 0.5f);
        areaParticle.createByCurve(step, positionArray2D, curveType);
        context.getSource().sendSuccess(() -> Component.literal("成功构筑一条曲线"), true);
        return 1;
    }


    private static int addEquation(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        double start = DoubleArgumentType.getDouble(context, "start");
        double end = DoubleArgumentType.getDouble(context, "end");
        double step = DoubleArgumentType.getDouble(context, "step");
        double tolerance = DoubleArgumentType.getDouble(context, "tolerance");
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        String exp = StringArgumentType.getString(context, "exp");
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, (float) start, (float) end, (float) step);
        areaParticle.createByPositionEquation((float) x, (float) y, (float) z, exp, tolerance);
        context.getSource().sendSuccess(() -> Component.literal("成功以方程生成粒子"), true);
        return 1;
    }

    private static int addEquationWithEntities(CommandContext<CommandSourceStack> context) {
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        writeParticleJsonDoubleValue(context, "centerX", pos.x);
        writeParticleJsonDoubleValue(context, "centerY", pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", pos.z);
        String name = StringArgumentType.getString(context, "name");
        BaseParticleType type = loadParticleFromFile(name);
        Collection<? extends Entity> entities;
        try {
            entities = EntityArgument.getEntities(context, "entities");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        type.setEntitiesID(entities.stream().map(Entity::getId).toList());
        double start = DoubleArgumentType.getDouble(context, "start");
        double end = DoubleArgumentType.getDouble(context, "end");
        double step = DoubleArgumentType.getDouble(context, "step");
        String exp = StringArgumentType.getString(context, "exp");
        double tolerance = DoubleArgumentType.getDouble(context, "tolerance");
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, (float) start, (float) end, (float) step);
        areaParticle.createByPositionEquation((float) x, (float) y, (float) z, exp, tolerance);
        context.getSource().sendSuccess(() -> Component.literal("成功以方程生成粒子"), true);
        return 1;
    }

    private static int addPolarmeter(CommandContext<CommandSourceStack> context) {
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        String name = StringArgumentType.getString(context, "name");
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");  //这个部分改变了json
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        writeParticleJsonDoubleValue(context, "centerX", offsetX + pos.x);
        writeParticleJsonDoubleValue(context, "centerY", offsetY + pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", offsetZ + pos.z);
        BaseParticleType type = loadParticleFromFile(name);  // 这部分读取读取json
        double start = DoubleArgumentType.getDouble(context, "start");
        double end = DoubleArgumentType.getDouble(context, "end");
        double step = DoubleArgumentType.getDouble(context, "step");
        String expR = StringArgumentType.getString(context, "expR");
        String expTheta = StringArgumentType.getString(context, "expTheta");
        String expPhi = StringArgumentType.getString(context, "expPhi");
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, (float) start, (float) end, (float) step);
        areaParticle.setPolarPositionExpression(expR, expTheta, expPhi);
        areaParticle.createByPolarPositionExpression((float) x, (float) y, (float) z);
        context.getSource().sendSuccess(() -> Component.literal("成功生成来自极坐标系解析式的粒子"), true);
        return 1;
    }

    private static int addParameter(CommandContext<CommandSourceStack> context) {//OFFSET
        String name = StringArgumentType.getString(context, "name");
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        double start = DoubleArgumentType.getDouble(context, "start");
        double end = DoubleArgumentType.getDouble(context, "end");
        double step = DoubleArgumentType.getDouble(context, "step");
        String expX = StringArgumentType.getString(context, "expX");
        String expY = StringArgumentType.getString(context, "expY");
        String expZ = StringArgumentType.getString(context, "expZ");
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        writeParticleJsonDoubleValue(context, "centerX", offsetX + pos.x);
        writeParticleJsonDoubleValue(context, "centerY", offsetY + pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", offsetZ + pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, (float) start, (float) end, (float) step);
        areaParticle.setPositionExpression(expX, expY, expZ);
        areaParticle.createByPositionExpression((float) x, (float) y, (float) z);
        context.getSource().sendSuccess(() -> Component.literal("成功生成来自笛卡尔坐标系解析式的粒子"), true);
        return 1;
    }

    private static int addPolarmeterWithEntities(CommandContext<CommandSourceStack> context) {//速度表达式中可使用e参数
        String name = StringArgumentType.getString(context, "name");
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
        double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
        double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
        writeParticleJsonDoubleValue(context, "centerX", offsetX + pos.x);
        writeParticleJsonDoubleValue(context, "centerY", offsetY + pos.y);
        writeParticleJsonDoubleValue(context, "centerZ", offsetZ + pos.z);
        BaseParticleType type = loadParticleFromFile(name);
        Collection<? extends Entity> entities;
        try {
            entities = EntityArgument.getEntities(context, "entities");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        type.setEntitiesID(entities.stream().map(Entity::getId).toList());
        double start = DoubleArgumentType.getDouble(context, "start");
        double end = DoubleArgumentType.getDouble(context, "end");
        double step = DoubleArgumentType.getDouble(context, "step");
        String expR = StringArgumentType.getString(context, "expR");
        String expTheta = StringArgumentType.getString(context, "expTheta");
        String expPhi = StringArgumentType.getString(context, "expPhi");
        ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, (float) start, (float) end, (float) step);
        areaParticle.setPolarPositionExpression(expR, expTheta, expPhi);
        areaParticle.createByPolarPositionExpression((float) x, (float) y, (float) z);
        context.getSource().sendSuccess(() -> Component.literal("成功生成来自极坐标系解析式的粒子"), true);
        return 1;
    }

    private static int addParameterWithEntities(CommandContext<CommandSourceStack> context) {//OFFSET
        String name = StringArgumentType.getString(context, "name");
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        try {
            Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            double offsetX = DoubleArgumentType.getDouble(context, "offsetX");
            double offsetY = DoubleArgumentType.getDouble(context, "offsetY");
            double offsetZ = DoubleArgumentType.getDouble(context, "offsetZ");
            writeParticleJsonDoubleValue(context, "centerX", offsetX + pos.x);
            writeParticleJsonDoubleValue(context, "centerY", offsetY + pos.y);
            writeParticleJsonDoubleValue(context, "centerZ", offsetZ + pos.z);
            double start = DoubleArgumentType.getDouble(context, "start");
            double end = DoubleArgumentType.getDouble(context, "end");
            double step = DoubleArgumentType.getDouble(context, "step");
            String expX = StringArgumentType.getString(context, "expX");
            String expY = StringArgumentType.getString(context, "expY");
            String expZ = StringArgumentType.getString(context, "expZ");
            BaseParticleType type = loadParticleFromFile(name);
            type.setEntitiesID(entities.stream().map(Entity::getId).toList());
            ParticleAreaSpawner areaParticle = new ParticleAreaSpawner(context.getSource().getLevel(), type, (float) start, (float) end, (float) step);
            areaParticle.setPositionExpression(expX, expY, expZ);
            areaParticle.createByPositionExpression((float) x, (float) y, (float) z);
            context.getSource().sendSuccess(() -> Component.literal("成功生成来自笛卡尔坐标系解析式的粒子"), true);
            return 1;
        } catch (CommandSyntaxException e) {
            throw new RuntimeException();
        }
    }







    private static int getAllFunctionsSupported(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        // 将文档发送给玩家，分段显示
        for (String line : documentation.split("\n")) {
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return 1;
    }

    private static int getAllAttributeFunctionsSupported(CommandContext<CommandSourceStack> context) {
        String Doc = """
                对于速度表达式,可用:
                    自变量:一次随机量sRandom;存活时间t;粒子位置x,y,z;实体位置e0x,e0y,e0z;e1x,e1y,e1z;e2x.......
                对于加速度表达式,可用:
                    自变量:一次随机量sRandom;存活时间t;粒子位置x,y,z;实体位置e0x,e0y,e0z;e1x,e1y,e1z;e2x.......
                对于位置表达式,可用:
                    自变量:存活时间t;
                对于动态属性表达式,可用:
                    自变量:ax,ay,az;centerX,centerY,centerZ;vx,vy,vz;x,y,z;r,g,b,w;lifetime;t;
                    应变量:ax,ay,az;centerX,centerY,centerZ;
                    vx,vy,vz;r,g,b,w;lifetime;x!,y!,z!;t!!;
                    请使用"r(应变量)<-t*2(自变量表达式)"构建粒子
                """;
        CommandSourceStack source = context.getSource();
        // 将文档发送给玩家，分段显示
        for (String line : Doc.split("\n")) {
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return 1;
    }


    private static int deleteAll(CommandContext<CommandSourceStack> context) {
        File minecraftDirectory = getMinecraftDirectory();
        File presentParticleDir = new File(minecraftDirectory, "presentparticle");
        try {
            deleteDirectoryRecursively(presentParticleDir);
            context.getSource().sendSuccess(() -> Component.literal("已删除所有粒子文件。"), true);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("删除文件夹失败。"));
            e.printStackTrace();
        }
        return 1;
    }


    private static int deleteParticle(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        File minecraftDirectory = getMinecraftDirectory();
        File presentParticleDir = new File(minecraftDirectory, "presentparticle");
        File fileToDelete = new File(presentParticleDir, name);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                context.getSource().sendSuccess(() -> Component.literal("已删除文件：" + fileToDelete.getName()), true);
            } else {
                context.getSource().sendFailure(Component.literal("删除文件失败：" + fileToDelete.getName()));
            }
        } else {
            context.getSource().sendFailure(Component.literal("文件不存在：" + fileToDelete.getName()));
        }

        return 1;
    }

    private static int constructParticle(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        String particleType = StringArgumentType.getString(context, "particleType");

        // 确保文件名以 .json 结尾
        if (!name.endsWith(".json")) {
            name += ".json";
        }

        File file = FileUtil.getFileInPresentParticleFolder(name);

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter fileWriter = new FileWriter(file)) {
                JsonObject jsonObject = new JsonObject();

                JsonObject particleTypeObject = new JsonObject();
                particleTypeObject.addProperty("type", particleType);


                jsonObject.add("particleType", particleTypeObject);
                BaseParticleType type = new BaseParticleType();
//                type.setOrigin(source.getPosition().x,source.getPosition().y,source.getPosition().z);
                type.setColor(new Vector4i(100, 100, 100, 255));
                type.setCenter(context.getSource().getPosition().x, context.getSource().getPosition().y, context.getSource().getPosition().z);
                type.setDynamicExp("t<-t");
                jsonObject.add("baseParticle", new Gson().toJsonTree(type));


                String jsonText = new Gson().toJson(jsonObject);
                fileWriter.write(jsonText);
                fileWriter.flush(); // 确保内容被写入
                source.sendSuccess(() -> Component.literal("在" + file.getPath() + "创建了" + file.getName()), true);
            }

        } catch (IOException e) {
            source.sendFailure(Component.literal("Error writing JSON file....."));
            e.printStackTrace();
        }

        return 1;
    }


    private static int setMotionSpeed(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        double vx = DoubleArgumentType.getDouble(context, "vx");
        double vy = DoubleArgumentType.getDouble(context, "vy");
        double vz = DoubleArgumentType.getDouble(context, "vz");
        if (writeParticleJsonNestedValue(context, "speed", "x", vx)
                && writeParticleJsonNestedValue(context, "speed", "y", vy)
                && writeParticleJsonNestedValue(context, "speed", "z", vz)) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的粒子速度大小已改为(" + vx + "," + vy + "," + vz + ")"), true);
        }
        return 1;
    }

    private static int setMotionRotation(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        double rx = DoubleArgumentType.getDouble(context, "rx");
        double ry = DoubleArgumentType.getDouble(context, "ry");
        double rz = DoubleArgumentType.getDouble(context, "rz");
        if (ParticleJsonUtil.writeParticleJsonDoubleValue(context, "rx", rx)
                && ParticleJsonUtil.writeParticleJsonDoubleValue(context, "ry", ry)
                && ParticleJsonUtil.writeParticleJsonDoubleValue(context, "rz", rz)
        ) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的旋转速度已改为(" + rx + "," + ry + "," + rz + ")"), true);
        }
        return 1;
    }

    private static int setMotionAcceleration(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        double ax = DoubleArgumentType.getDouble(context, "ax");
        double ay = DoubleArgumentType.getDouble(context, "ay");
        double az = DoubleArgumentType.getDouble(context, "az");
        if (ParticleJsonUtil.writeParticleJsonDoubleValue(context, "ax", ax)
                && ParticleJsonUtil.writeParticleJsonDoubleValue(context, "ay", ay)
                && ParticleJsonUtil.writeParticleJsonDoubleValue(context, "ax", ax)
        ) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的粒子加速度已改为(" + ax + "," + ay + "," + az + ")"), true);
        }
        return 1;
    }

    private static int setMotionCenter(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        if (ParticleJsonUtil.writeParticleJsonDoubleValue(context, "centerX", x)
                && ParticleJsonUtil.writeParticleJsonDoubleValue(context, "centerY", y)
                && ParticleJsonUtil.writeParticleJsonDoubleValue(context, "centerZ", z)
        ) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的粒子相对坐标系中心点已改为(" + x + "," + y + "," + z + ")"), true);
        }
        return 1;
    }

    private static int setSpeedExpression(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        String expX = StringArgumentType.getString(context, "vecExpX");
        String expY = StringArgumentType.getString(context, "vecExpY");
        String expZ = StringArgumentType.getString(context, "vecExpZ");
        if (ParticleJsonUtil.writeParticleJsonString(context, "vecExpX", expX)
                && ParticleJsonUtil.writeParticleJsonString(context, "vecExpY", expY)
                && ParticleJsonUtil.writeParticleJsonString(context, "vecExpZ", expZ)) {
            context.getSource().sendSuccess(() -> Component.literal(name + "已设计速度表达式 " + "x:" + expX + " y:" + expY + " z:" + expZ), true);
        }
        return 1;
    }

    private static int setVisionLifetime(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        int lifetime = IntegerArgumentType.getInteger(context, "lifetime");
        if (ParticleJsonUtil.writeParticleJsonDoubleValue(context, "lifetime", lifetime)) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的粒子存活时间已改为" + lifetime), true);
        }
        return 1;
    }

    private static int setVisionDiameter(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        double diameter = DoubleArgumentType.getDouble(context, "diameter");
        if (ParticleJsonUtil.writeParticleJsonDoubleValue(context, "diameter", diameter)) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的粒子大小已改为" + diameter), true);
        }
        return 1;
    }

    private static int setVisionColor(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        int r = IntegerArgumentType.getInteger(context, "r");
        int g = IntegerArgumentType.getInteger(context, "g");
        int b = IntegerArgumentType.getInteger(context, "b");
        int a = IntegerArgumentType.getInteger(context, "a");
        if (writeParticleJsonNestedValue(context, "color", "x", r)
                && writeParticleJsonNestedValue(context, "color", "y", g)
                && writeParticleJsonNestedValue(context, "color", "z", b)
                && writeParticleJsonNestedValue(context, "color", "w", a)) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的粒子颜色已改为(" + r + "," + g + "," + b + "," + a + ")"), true);
        }
        return 1;
    }

    private static int setVisionTps(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        int tps = IntegerArgumentType.getInteger(context, "tps");
        if (ParticleJsonUtil.writeParticleJsonDoubleValue(context, "tps", tps)) {
            context.getSource().sendSuccess(() -> Component.literal(name + "文件的粒子每tick渲染次数(生长动画速度)已改为" + tps), true);
        }
        return 1;
    }

    private static int setDynamicProperty(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        String expression = StringArgumentType.getString(context, "expression");
        String[] parts = expression.split(";");
        for (String part : parts) {
            String trimmedPart = part.trim();
            // 检查格式：确保只接受分号作为分隔符，左边部分是非符号字符，右边部分为有效表达式
            String regex = "^[a-zA-Z0-9_]+<-[^;]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(trimmedPart);
            if (!matcher.matches()) {
                context.getSource().sendFailure(Component.literal("输入的 dynamicExp 格式不正确。格式应为 '左边部分<-右边表达式'，并且多个表达式应使用分号分隔。错误部分: " + trimmedPart));
                throw new IllegalArgumentException("输入的 dynamicExp 格式不正确。格式应为 '左边部分<-右边表达式'。错误部分: " + trimmedPart);
            }
        }

        if (ParticleJsonUtil.writeParticleJsonString(context, "dynamicExp", expression)) {
            context.getSource().sendSuccess(() -> Component.literal("为" + name + "文件映射动态属性, 表达式: " + expression), true);
        }
        return 1;
    }

}

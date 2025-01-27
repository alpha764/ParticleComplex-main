# ParticleComplex

**ParticleComplex** 是一个基于 Minecraft 的模组，允许通过多种方式生成粒子效果，包括贝塞尔曲线、方程式和极坐标表达式，支持通过命令生成复杂粒子效果。此外，还可以通过表达式建立参数间的映射

## 功能特性

- **粒子生成**：通过指定粒子名称生成各种粒子效果。
- **贝塞尔曲线生成**：使用指定的坐标点和步长生成粒子贝塞尔曲线。
- **方程式生成**：通过解析数学表达式生成粒子效果。
- **笛卡尔坐标表达式生成**：使用笛卡尔坐标解析表达式生成粒子。
- **极坐标表达式生成**：使用极坐标解析表达式生成粒子。
- **实体支持**：粒子生成可动态绑定实体坐标，并支持基于实体坐标的偏移和复杂表达式计算。
- **属性映射**：可建立属性映射,如r<-t*3,颜色的r会随着粒子的生命时长呈线性变化


## 命令列表概览

- **add**
  - **Entities**
    - **addBezierCurveWithEntities**
      - 生成可容纳实体自变量的贝塞尔曲线粒子效果。
    - **addEquationWithEntities**
      - 生成可容纳实体自变量的方程粒子效果。
    - **addPolarmeterWithEntities**
      - 生成可容纳实体自变量的极坐标粒子效果。
    - **addParameterWithEntities**
      - 生成可容纳实体自变量的参数粒子效果。
  - **Common**
    - **addSingle**
      - 生成单个粒子效果。
    - **addBezierCurve**
      - 生成贝塞尔曲线粒子效果。
    - **addBezierCurveWithEntities**
      - 生成带有实体的贝塞尔曲线粒子效果。
    - **addEquation**
      - 生成方程粒子效果。
    - **addPolarmeter**
      - 生成极坐标粒子效果。
    - **addParameter**
      - 生成参数粒子效果。
    - **addBezierCurveWithEntitiesPos**
      - 以实体坐标生成贝塞尔曲线
- **board**
  - **getAllConstructedParticles**
    - 获取所有已构建的粒子。
  - **getAllVelocityFunctionsSupported**
    - 获取所有支持的速度函数。
  - **getAllPositionFunctionsSupported**
    - 获取所有支持的位置函数。
  - **getAllAttributeFunctionsSupported**
    - 获取所有支持的属性函数。
  - **getAllAttributesFromJson**
    - 从 JSON 文件获取所有属性。
  - **deleteAll**
    - 删除所有粒子数据。
  - **delete**
      - 删除指定名称的粒子数据。
- **construct**
  - **construct particleType**
    - 生成指定粒子类型的粒子效果。
- **setProperty**
  - **dynamicProperty**
    - 设置动态属性。
  - **motion**-运动属性
    - **speed**
    - **acceleration**
    - **speedExpression**
    - **center**
  - **vision**- 视觉属性
    - **lifetime**
    - **diameter**
    - **color**
    - **tps**

## 教程文档
  - **使用教程 todo**
  - **开发api使用教程(未完成) todo**
  - **简要说明**
      - 用/particleComplex construct (particleType) (yourname)创建一个粒子json文件
      - /particleComplex add Common Single (yourname.json)创建单个粒子
      - /particleComplex add Common BezierCurveWithEntityPos (yourname.json) 0.003  @e[limit=3,sort=nearest,distance=..100] ~ ~ ~ e0x e0y e0z e1x e1y+20 e1z e2x e2y e2z来生成实体位置的贝塞尔曲线
      - /particleComplex setProperty (yourname.json) dynamicProperty "r<-t;g<-t*2;b<-t"构筑颜色与时间的映射
      - 先用/particleComplex setProperty (yourname.json) motion speedExpression 0 ey0-y 0构筑表达式
      - 再用/particleComplex add Entities equationWithEntities (yourname.json) ~ ~ ~ "x^2+y^2+z^2-1" @s -10 10 0.1 0.1生成一个y坐标=玩家坐标的球
      - 可用/particleComplex setProperty (yourname.json) vision tps 10 来加快粒子生长速度

## 安装

1. 下载并安装 Minecraft Forge。
2. 将本模组的 `.jar` 文件放入 Minecraft 的 `mods` 文件夹中。
3. 启动 Minecraft 并确保模组加载成功。

## 粒子文件管理   
粒子存放与minecraft的presentparticle文件夹下,以json文件存放,可以通过命令或者直接上手编辑


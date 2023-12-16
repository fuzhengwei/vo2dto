# :currency_exchange: IDEA Plugin vo2dto

> 你好，我是小傅哥，[《重学Java设计模式》](https://item.jd.com/13218336.html) 图书作者，一线互联网 Java 工程师、架构师。[:pencil2: 虫洞栈，博主](https://bugstack.cn)，[:memo: 关于我](https://bugstack.cn/md/other/guide-to-reading.html) 我做过的项目演示地址：[https://gaga.plus](https://gaga.plus)

<br/>
<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/18262-vo2dto" style="text-decoration:none"><img src="/docs/_media/logo-02.png" width="128px"></a>
</div>
<br/>
<div align="center">
	<a href="https://plugins.jetbrains.com/plugin/18262-vo2dto"><img src="/docs/_media/npm-version.svg"></a>
	<a href="https://bugstack.cn/"><img src="/docs/_media/npm-author.svg"></a>
	<a href="https://www.bilibili.com/video/BV13Y411h7fv"><img src="/docs/_media/npm-bilibili.svg"></a>
	<a href="https://plugins.jetbrains.com/plugin/18262-vo2dto"><img src="/docs/_media/npm-idea.svg"></a>
</div>

<h4 align="center">一款用于帮助使用 IDEA 编写代码的研发人员，快速生成两个对象转换过程中所需要大量的 `x.set(y.get)` 代码块的插件工具.</h4>

| `对vo2dto感兴趣的，程序员👨🏻‍💻‍，来自这些国家` |
|:---:|
| ![](/docs/_media/visits.png) |

## ⛳ 目录

-  [特性](https://github.com/fuzhengwei/vo2dto#sparkles-%E7%89%B9%E6%80%A7)
-  [使用](https://github.com/fuzhengwei/vo2dto#hammer-%E4%BD%BF%E7%94%A8)
-  [安装](https://github.com/fuzhengwei/vo2dto#hammer_and_wrench-%E5%AE%89%E8%A3%85)
   -  [1. 在线安装](https://github.com/fuzhengwei/vo2dto#1-%E5%9C%A8%E7%BA%BF%E5%AE%89%E8%A3%85)
   -  [2. 手动安装](https://github.com/fuzhengwei/vo2dto#2-%E6%89%8B%E5%8A%A8%E5%AE%89%E8%A3%85)
-  [迭代](https://github.com/fuzhengwei/vo2dto#-%E8%BF%AD%E4%BB%A3)
-  [技术栈](https://github.com/fuzhengwei/vo2dto#alembic-%E6%8A%80%E6%9C%AF%E6%A0%88)
-  [许可证](https://github.com/fuzhengwei/vo2dto#scroll-%E8%AE%B8%E5%8F%AF%E8%AF%81)  

## ✨ 特性

1. 2个对象的转换操作，通过复制 X x 对象，转换给 Y y 对象
2. 允许使用 lombok 对象转换、lombok 和普通对象转换，对于 serialVersionUID 属性过滤
3. 支持类继承类，全量的对象转换操作
4. 含记忆功能的弹窗选择映射关系，支持全量对象、支持匹配对象、也支持空转换，生成一组set但无get的对象
5. 支持对于引入不同包下的同名类处理

## 🔨 使用

- 视频：[https://www.bilibili.com/video/BV13Y411h7fv](https://www.bilibili.com/video/BV13Y411h7fv) - `视频内有完整的使用介绍和插件设计` 
- 描述：你需要复制被转换 X x = new X() 中的 X x 部分，无论它是方法入参还是实例化或者是接口回值，接下来鼠标定位到转换对象 Y y 上，可以定位到`类 大Y`、或者`属性 小y`，这样我就可以知道你要做到是X的对象的属性值，转换到Y对象的属性值上。接下来帮你快速生成全部的 `y.set(x.get)` 代码片段。

---

| IDEA Plugin vo2dto 使用演示图|
|:---:|
| ![](/docs/_media/use-demo.png) |

## 🛠️ 安装

### 1. 在线安装

| IDEA Plugin 搜索vo2dto直接在线安装即可|
|:---:|
| ![](/docs/_media/install.png) |

### 2. 手动安装

- 下载：[https://github.com/fuzhengwei/vo2dto/releases/tag/v2.5.0](https://github.com/fuzhengwei/vo2dto/releases/tag/v2.5.0)
- 安装：

| IDEA Plugin 手动安装，导入下载包|
|:---:|
| ![](/docs/_media/install02.png) |

## 🐾 迭代

- [x]【推荐】v2.5.1 修复 Objects.requireNonNull 部分 idea 不支持该方法问题 
- v2.4.9 支持 Lombok Builder 模式
- v2.4.8 静态字段过滤

<details><summary><a>👉显示更多</a></summary></br>

- v2.4.7

  - 支持转换对象，鼠标定位到属性时候的类搜索和匹配
  - 处理全局扫描访问 `GlobalSearchScope.allScope`

- v2.4.6

  - 修改搜索对象范围，支持pom引入对象搜索
  - 屏蔽 lombok 下 serialVersionUID 属性的生成

- v2.4.5

  - 完善查找多包下同名类的引入和使用

- v2.4.4

  - 支持在多个包下查找相似的名称

- v2.4.3

  - 校验复制被转换对象的上下文关系

- v2.4.2

  - 添加映射字段多选框

- v2.3.0

  - 提供转换对象映射关系的弹窗提示，有多少个对象属性在转换

- v2.4.1

  - 修复映射字段bug

- v2.4.0

  - 优化映射关系
  - 提供映射配置功能

- V2.2.2

  - 通过步长计算，支持用户把光标定位到转换对象的属性上

- v2.2.0

  - 支持Lombok生成策略
  - 支持对象空转换
  - 在兼容的Lombok中添加自定义get和set方法

- v2.1.0
  
  - 复制对象a并在对象B上生成x.set（y.get）
  - 在“对象B”属性上，也可以直接单击以生成x.set（y.get）
  - 支持生成父对象的方法

- v1.0.0

  - 初版，支持对象的复制和转换

</details>

## ⚗️ 技术栈

- Java
- IDEA Plugin SDK

## 📜 许可证

MIT 

---

> GitHub [@fuzhengwei](https://github.com/fuzhengwei) &nbsp;&middot;&nbsp;


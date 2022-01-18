# :currency_exchange: IDEA Plugin vo2dto

> 你好，我是小傅哥，[《重学Java设计模式》](https://item.jd.com/13218336.html) 图书作者，一线互联网 Java 工程师、架构师。[:pencil2: 虫洞栈，博主](https://bugstack.cn)，[:memo: 关于我](https://bugstack.cn/md/other/guide-to-reading.html) 

<br/>
<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/18262-vo2dto" style="text-decoration:none"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/logo-02.png" width="128px"></a>
</div>
<br/>
<div align="center">
	<a href="https://plugins.jetbrains.com/plugin/18262-vo2dto"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-version.svg"></a>
	<a href="https://bugstack.cn/"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-author.svg"></a>
	<a href="https://www.bilibili.com/video/BV13Y411h7fv"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-bilibili.svg"></a>
	<a href="https://plugins.jetbrains.com/plugin/18262-vo2dto"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-idea.svg"></a>
</div>

<h4 align="center">一款用于帮助使用 IDEA 编写代码的研发人员，快速生成两个对象转换过程中所需要大量的 `x.set(y.get)` 代码块的插件工具.</h4>

## ⛳ 目录

-  [特性](https://github.com/fuzhengwei/vo2dto#sparkles-%E7%89%B9%E6%80%A7)
-  [使用](https://github.com/fuzhengwei/vo2dto#hammer-%E4%BD%BF%E7%94%A8)
-  [安装](https://github.com/fuzhengwei/vo2dto#hammer_and_wrench-%E5%AE%89%E8%A3%85)
   -  [1. 在线安装](https://github.com/fuzhengwei/vo2dto#1-%E5%9C%A8%E7%BA%BF%E5%AE%89%E8%A3%85)
   -  [2. 手动安装](https://github.com/fuzhengwei/vo2dto#2-%E6%89%8B%E5%8A%A8%E5%AE%89%E8%A3%85)
-  [技术栈](https://github.com/fuzhengwei/vo2dto#alembic-%E6%8A%80%E6%9C%AF%E6%A0%88)
-  [许可证](https://github.com/fuzhengwei/vo2dto#scroll-%E8%AE%B8%E5%8F%AF%E8%AF%81)  

## :sparkles: 特性

1. 2个对象的转换操作，通过复制 X x 对象，转换给 Y y 对象
2. 允许使用 lombok 对象转换、lombok 和普通对象转换，对于 serialVersionUID 属性过滤
3. 支持类继承类，全量的对象转换操作
4. 含记忆功能的弹窗选择映射关系，支持全量对象、支持匹配对象、也支持空转换，生成一组set但无get的对象
5. 支持对于引入不同包下的同名类处理

## :hammer: 使用

- 视频：[https://www.bilibili.com/video/BV13Y411h7fv](https://www.bilibili.com/video/BV13Y411h7fv) - `视频内有完整的使用介绍和插件设计` 
- 描述：你需要复制被转换 X x = new X() 中的 X x 部分，无论它是方法入参还是实例化或者是接口回值，接下来鼠标定位到转换对象 Y y 上，可以定位到`类 大Y`、或者`属性 小y`，这样我就可以知道你要做到是X的对象的属性值，转换到Y对象的属性值上。接下来帮你快速生成全部的 `y.set(x.get)` 代码片段。

---

| IDEA Plugin vo2dto 使用演示图|
|:---:|
| ![](https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/use-demo.png) |

## :hammer_and_wrench: 安装

### 1. 在线安装

| IDEA Plugin 搜索vo2dto直接在线安装即可|
|:---:|
| ![](https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/install.png) |

### 2. 手动安装

- 下载：[https://github.com/fuzhengwei/vo2dto/releases/tag/v2.4.3](https://github.com/fuzhengwei/vo2dto/releases/tag/v2.4.3)
- 安装：

| IDEA Plugin 手动安装，导入下载包|
|:---:|
| ![](https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/install02.png) |

## :alembic: 技术栈

- Java
- IDEA Plugin SDK

## :scroll: 许可证

MIT 

---

> GitHub [@fuzhengwei](https://github.com/fuzhengwei) &nbsp;&middot;&nbsp;


# :art: IDEA Plugin vo2dto

<br/>
<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/18262-vo2dto" style="text-decoration:none"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/logo.png?raw=true" width="256px"></a>
    <div>vo2dto</div>
</div>
<br/>

<hr/>

vo2dto 是一款用于帮助使用 IDEA 编写代码的研发人员，快速生成两个对象转换过程中所需要大量的 `x.set(y.get)` 代码块的插件工具。

<br/>
<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/18262-vo2dto" style="text-decoration:none"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/use-demo.png?raw=true"></a>
</div>
<br/>

<div align="center">
	<a href="https://plugins.jetbrains.com/plugin/18262-vo2dto"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-version.svg"></a>
	<a href="#"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-author.svg"></a>
	<a href="#"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-version.svg"></a>
	<a href="#"><img src="https://github.com/fuzhengwei/vo2dto/blob/master/docs/_media/npm-idea.svg"></a>
</div>


## ⛳ 目录



安装包下载：[https://github.com/fuzhengwei/vo2dto/releases/tag/v2.2.2](https://github.com/fuzhengwei/vo2dto/releases/tag/v2.2.2)

---

选定对象批量织入“x.set(y.get)”代码，帮助开发者自动生成vo2dto转换代码。视频：<a href="https://youtu.be/HOlNi2b0oXI">https://youtu.be/HOlNi2b0oXI</a><br>
<ul>
    <li>使用方法：先复制对象后，例如：A a，再光标放到需要织入的对象上，例如：B b 选择 Generate -> vo2dto</li>
    <li>使用场景：对于一些不适合使用 MapStruct 编写映射类的又不希望大量手写转换代码的场景</li>
    <li>使用提醒：通过检测 @DATA 注解，集成 lombok 生成策略，也可以创建 get、set 方法</li>
</ul>

<h3>V2.1.0</h3>
<ul>
    <li>复制A对象，在对象B上，生成x.set(y.get)</li>
    <li>在对象B属性上也可以直接点击生成x.set(y.get)</li>
    <li>支持生成父类对象的方法</li>
</ul>

<h3>V2.2.0</h3>
<ul>
    <li>支持 lombok 生成策略</li>
    <li>支持对象空转换操作</li>
    <li>兼容 lombok 中添加自定义 get、set 方法</li>
</ul>

<h3>V2.2.2</h3>
<ul>
    <li>兼容方法修复</li>
</ul>

<h3>V2.3.0</h3>
<ul>
    <li>生成对象转换，提供UI界面，可以选择某些对象生成关系</li>
</ul>

---

| 使用演示  |
|---|
|  ![](/docs/_media/vo2dto.png)  |

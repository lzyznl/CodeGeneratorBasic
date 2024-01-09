## 定制化代码生成项目

### 第一阶段

#### == 本地代码生成器 ==

##### 项目进度

- 2024-01-05：完成框架的搭建、静态代码文件生成以及FreeMarker模板引擎的测试

- 2024-01-06：完成ACM模板代码生成（静态以及自定义参数生成）

- 2024-01-07：完成命令行开发框架PicoCli的学习以及基本使用

  ​						使用PicoCli完成命令行ACM模板生成

  PicoCli：https://github.com/remkop/picocli

  ```
  环境:Windows/Linux
  以下命令行模板生成命令以Windows为例
  /*查看帮助手册*/
  generator --help
  /*基于交互式生成ACM模板*/
  generator generate -a -o -l
  /*查看生成文件所需要的配置参数*/
  generator config
  /*查看生成文件的目录结构*/
  generator list
  /*查看子命令的帮助手册*/
  generator list --help  或者是 generator config --help
  ```


### 第二阶段

#### == 制作代码生成器生成工具（生成代码生成器的工具） ==

- 2024-01-08 完成生成工具的框架搭建，以及代码生成器部分代码的生成
- 2024-01-09 修改程序之前开发中的bug，优化部分代码结构，完成代码生成器生成工具的80%的基础开发，
             还剩余jar包以及脚本代码的生成

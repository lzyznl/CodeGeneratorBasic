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
         
- 2024-01-10 完成代码生成器生成工具的Jar包生成，修改所有模板文件中存在的bug

- 2024-01-11 完成代码生成器生成工具的执行脚本生成，至此代码生成器生成工具的基本代码均已经实现

     随后要对代码进行优化以及配置能力增强
     
- 2024-01-12 具体工作如下：

     >1： 提升程序的可移植性，将原模板文件的绝对路径修改为相对路径
     >
     >2：作为一个优秀的开源项目，应该为用户提供一个使用说明，因此新增生成README文件
     >
     >3：对程序进行空间优化，将没有必要生成的代码文件进行删除，节省存储资源
     >
     >4：提高程序健壮性，本项目最重要的健壮性体现在元信息的配置读取中，为了简化用户的使用，对元信息可能出现的空值情况
     >
     >​		进行赋默认值处理，确保程序在用户没有输入某些元信息字段时，程序依旧能够正常运行
     >
     >5：降低元信息校验代码的圈复杂度，使用**抽离方法**，**卫语句**，**第三方工具库**来降低代码圈复杂度
     >
     >​		优化过后，代码圈复杂度从原来的**平均45**到平均6.5
     >
     >![image-20240104184537744](https://lzyzxq-1310836527.cos.ap-shanghai.myqcloud.com/code-complex.png)
     >
     >6：使用**设计模式**中的**模板方法模式**提高程序的可拓展性
     >
     >==简述==：什么是模板方法模式？
     >
     >我们在一段程序中可能会发现这段程序是有一个很明显的顺序步骤结构的，程序按照一个步骤一个步骤执行，那么我们可以将每个步骤抽离为一个方法，然后将该类改为抽象类，这样别的类可以继承该类，重写方法，新增方法，提升程序可拓展性
     >
     >**举例说明:**
     >
     >有个事情的步骤是：
     >
     >吃饭
     >
     >睡觉
     >
     >打豆豆
     >
     >小王可能是：站着吃饭，躺着睡觉，边唱歌边打豆豆
     >
     >小李可能是：坐着吃饭，趴着睡觉，边看电视边打豆豆
     >
     >这种情况我们就可以用**模板方法模式**来解决这个问题
- 2024-01-13 进行配置能力增强工作，实现使用一个参数来控制某个具体文件是否生成
- 2024-01-14 进行配置能力增强工作，在原来的基础上添加实现一个参数控制多个文件的生成
- 2024-01-24 之前因为期末考试停止更新，今天完成了参数组的基本实现，没有进行测试
- 2024-01-25 完成参数组的测试，成功实现自定义一个参数组，比如自定义MySQL参数，Redis参数等等
- 2024-01-26 进行模板制作工具的开发，完成模板制作工具的基本能力
- 2024-01-27 在之前所实现的对单个文件进行挖坑的基础上，拓展更多新的功能，并且优化单个模板文件生成过程的代码逻辑，具体新功能如下:

> 1:支持某个目录下所有文件的挖坑操作
>
> 2:支持多个路径下的文件挖坑操作
>
> 3:对于没有产生任何挖坑的文件不生成模板文件

- 2024-01-28 继续新增模板制作工具的新功能以及提升模板制作工具的健壮性

> 1:实现对同一文件的多次模板制作，本次的模板制作会追加入上次的模板制作当中
>
> 2:在多次模板文件的制作过程当中可能会在元信息文件中产生多个冗余的不必要信息，因此提供每次模板文件的生成时，对即将同步写入元信息文件当中的内容进行去重

- 2024-01-29 新需求：用户可能想要指定是否开启redis，如果开启redis，那么和redis相关的代码可能分布在不同的包中，如何将所有的有关redis的代码进行挖坑呢？

> 自研文件**过滤系统**，根据指定的过滤规则对文件进行过滤（已实现）
>

-2024-01-30 对模板生成工具的代码进行重构，抽离方法，降低主方法的逻辑复杂度，圈复杂度，使得代码更为易于理解
>同时实现文件分组以及文件分组后多次模板制作的去重工作（目前还是有问题的，比如第二次模板制作可能会将之前制作的模板文件添加进去）


# Google-Caja for Shop

###背景

前端开放js方案， 本工程为开放js学习以及改进过程，主要涉及对源码和工程架构的解读  


本工程源码使用google caja 3000版本，后面改进都是以此为基础， 目前google官方已经更新到5000多版本  

####目前对google caja 3000版本号源代码修改如下

1. 修改了build.xml中， runserver target的启动参数，增加了端口和分配的内存大小

2. 创建了build.bat文件，设置了设置了ant的启动内存和xercesImpl.jar的path
    set CLASSPATH=.\third_party\java\xerces\xercesImpl.jar		

3. 由于caja所有文件按linux来的，软连接在window下不可用：

所以在window下，找到相应目录，将jetty.jar和jetty-util-6.1.15.jar删除，分别将带版本号的jar重命名为这个两个文件

###说明

工程clone 下来，直接运行build.bat jar, 启动服务运行build.bat runserver





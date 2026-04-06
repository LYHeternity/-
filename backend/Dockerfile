# 使用服务器上已有的mysql镜像作为基础
FROM mysql:latest

# 安装Java 8
RUN apt-get update && apt-get install -y openjdk-8-jdk && rm -rf /var/lib/apt/lists/*

# 设置环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin

# 设置工作目录
WORKDIR /app

# 复制JAR文件
COPY platform-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "app.jar"]

# 网络问题解决方案：
# 1. 检查网络连接
# 2. 使用Docker镜像加速器，例如：
#    - 阿里云：https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors
#    - 网易云：https://c.163.com/hub#/m/home/
# 3. 尝试使用其他OpenJDK镜像版本
# 4. 如果仍有问题，可以考虑使用本地构建或其他容器解决方案
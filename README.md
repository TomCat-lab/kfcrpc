# 分布式服务框架KFCRPC项目 

这个仓库包含了分布式服务框架KFCRPC的所有开发和迭代代码。

## 项目概览

本项目是一个简化的分布式服务框架RPC，旨在教育和演示如何设计和实现基本的分布式系统组件，包括服务提供者、服务消费者、注册中心、负载均衡、方法重载支持、以及过滤器等。

### 核心组件

- **服务提供者和消费者**：实现基本的服务注册、发现和消费机制。
- **注册中心**：支持静态注册中心和基于 ZooKeeper 的动态注册中心。
- **负载均衡**：实现简单的负载均衡策略。
- **方法重载支持**：在服务提供端支持 Java 方法重载。
- **类型转换处理**：解决由序列化和反序列化引发的类型转换问题。
- **过滤器设计**：设计过滤器机制处理请求和响应。

### 开发历程
- **第六阶段**：对项目代码进行重构，优化结构和可维护性。
- **第七阶段**：设计并实现过滤器机制（进行中）。


- **第一阶段**：实现了服务提供者的基础功能和仓库初始化（已完成）。
- **第二阶段**：完善服务提供者并实现服务消费者，完成基本的联动测试（已完成）。
- **第三阶段**：增强服务消费者端，引入方法重载支持，解决序列化相关问题（已完成）。
- **第四阶段**：引入负载均衡(轮询和随机策略)，实现静态注册中心（已完成）。
- **第五阶段**：转向基于 ZooKeeper 的动态注册中心，实现消费者订阅和拉取，服务提供者注册和取消注册（已完成）。
## 使用指南

### 环境要求

- Java JDK 17 
- Maven 3.6 或以上
- ZooKeeper (如果使用基于 ZooKeeper 的注册中心)

### 如何运行

1. 克隆仓库：
   ```
   git clone https://github.com/TomCat-lab/kfcrpc
   ```
2. 进入项目目录并安装依赖：
   ```
   cd kfcrpc
   mvn install
   ```
3. 运行服务提供者和消费者：

[TOC]

## 概述
本文将围绕一个简单的例子展开论述，这样可以更容易突出我们解决问题的思路、方法。本文想向读者展现的正是这些思路、方法。这些思路、方法更加适用于解决大规模、复杂应用中的并发问题。
1. 基础知识
2. 逻辑模型
3. 并发模型
4. 基于框架的应用解决方案
5. 结论

## 基础知识
Java语言提供了对于线程很好的支持，实现方法小巧、优雅。对于方法重入的保护，信号量（semaphore）和临界区（critical section）机制的实现都非常简洁。可以很容易的实现多线程间的同步操作从而保护关键数据的一致性。

Java中内置了对于对象并发访问的支持，每一个对象都有一个监视器（monitor,也有理解为对象锁），同时只允许一个线程持有监视器从而进行对对象的访问，那些没有获得监视器的线程必须等待直到持有监视器的线程释放监视器。对象通过synchronized关键字来声明线程必须获得监视器才能进行对自己的访问。

synchronized声明仅仅对于一些较为简单的线程间同步问题比较有效，对于哪些复杂的同步问题，比如带有条件的同步问题，Java提供了另外的解决方法，wait/notify/notifyAll。获得对象监视器的线程可以通过调用该对象的wait方法主动释放监视器，等待在该对象的线程等待队列上，此时其他线程可以得到监视器从而访问该对象，之后可以通过调用notify/notifyAll方法来唤醒先前因调用wait方法而等待的线程。一般情况下，对于wait/notify/notifyAll方法的调用都是根据一定的条件来进行的，比如：经典的生产者/消费者问题中对于队列空、满的判断。熟悉POSIX的读者会发现，使用wait/notify/notifyAll可以很容易的实现POSIX中的一个线程间的高级同步技术：**条件变量**。

## 逻辑业务
一个简单的例子，我们有一个服务提供者，它通过一个接口对外提供服务，服务内容非常简单，就是在标准输出上打印Hello World。类结构图如下：
![image](http://www.ibm.com/developerworks/cn/java/l-multithreading/images/1.gif)

#### 业务逻辑代码如下:
```java
interface Service {
    public void sayHello();
}
class ServiceImp implements Service {
    public void sayHello() {
        System.out.println("Hello World!");
    }
}
class Client {
    public Client(Service s) {
        _service = s;
    }
    public void requestService() {
        _service.sayHello();
    }
    private Service _service;
}
```

#### 需求变更:
要求该服务必须支持Client的并发访问。一种简单的方法就是在ServicImp类中的每个方法前面加上synchronized声明，来保证自己内部数据的一致性（当然对于本例来说，目前是没有必要的，因为ServiceImp没有需要保护的数据，但是随着需求的变化，以后可能会有的）。但是这样做至少会存在以下几个问题：
1. 现在要维护ServiceImp的两个版本：多线程版本和单线程版本（有些地方，比如其他项目，可能没有并发的问题），容易带来同步更新和正确选择版本的问题，给维护带来麻烦。
2. 如果多个并发的Client频繁调用该服务，由于是直接同步调用，会造成Client阻塞，降低服务质量。
3. 很难进行一些灵活的控制，比如：根据Client的优先级进行排队等等。

###### 这些问题对于大型的多线程应用服务器尤为突出，对于一些简单的应用（如本文中的例子）可能根本不用考虑。本文正是要讨论这些问题的解决方案，文中的简单的例子只是提供了一个说明问题，展示思路、方法的平台。
###### 如何才能较好的解决这些问题，有没有一个可以重用的解决方案呢？让我们先把这些问题放一放，先来谈谈和框架有关的一些问题。

## 并发模型

#### 框架概述
熟悉面向对象的你一定知道面向对象的最大的优势之一就是：软件复用。通过复用，可以减少很多的工作量，提高软件开发生产率。复用本身也是分层次的，**代码级的复用**和 **设计架构的复用**。

###### 代码级的复用:
大家可能非常熟悉C语言中的一些标准库，它们提供了一些通用的功能让你的程序使用。但是这些标准库并不能影响你的程序结构和设计思路，仅仅是提供一些机能，帮助你的程序完成工作。它们使你不必重头编写一般性的通用功能（比如printf），它们强调的是程序代码本身的复用性，而不是设计架构的复用性。

###### 设计架构的复用:
那么什么是框架呢？所谓框架，它不同于一般的标准库，是指一组紧密关联的（类）classes，强调彼此的配合以完成某种可以重复运用的设计概念。这些类之间以特定的方式合作，彼此不可或缺。它们相当程度的影响了你的程序的形貌。框架本身规划了应用程序的骨干，让程序遵循一定的流程和动线，展现一定的风貌和功能。这样就使程序员不必费力于通用性的功能的繁文缛节，集中精力于专业领域。

有一点必须要强调，放之四海而皆准的框架是不存在的，也是最没有用处的。框架往往都是针对某个特定应用领域的，是在对这个应用领域进行深刻理解的基础上，抽象出该应用的概念模型，在这些抽象的概念上搭建的一个模型，是一个有形无体的框架。不同的具体应用根据自身的特点对框架中的抽象概念进行实现，从而赋予框架生命，完成应用的功能。

基于框架的应用都有两部分构成：框架部分和特定应用部分。要想达到框架复用的目标，必须要做到框架部分和特定应用部分的隔离。使用面向对象的一个强大功能：多态，可以实现这一点。在框架中完成抽象概念之间的交互、关联，把具体的实现交给特定的应用来完成。其中一般都会大量使用了Template Method设计模式。

#### 为应用构建框架

###### 如何构建一个Java并发模型框架呢？
先回到原来的问题，先来分析一下原因。造成要维护多线程和单线程两个版本的原因是由于把**应用逻辑**和**并发逻辑**混在一起，如果能够做到把应用逻辑和并发模型进行很好的隔离，那么**应用逻辑本身就可以很好的被复用**，而且也很容易把并发逻辑添加进来而不会对应用逻辑造成任何影响。造成Client阻塞，性能降低以及无法进行额外的控制的原因是由于所有的服务调用都是同步的，解决方案很简单，改为异步调用方式，把服务的调用和服务的执行分离。

###### 首先来介绍一个概念，活动对象（Active Object）和被动对象（passive object）
1. 活动对象:我更喜欢称为主动对象.主动对象的方法的调用和执行是分离的，主动对象有自己独立的执行线程，主动对象的方法的调用是由其他线程发起的，但是方法是在自己的线程中执行的，主动对象方法的调用是异步的，非阻塞的.你调动,但是我执行.
2. 被动对象的方法的调用和执行都是在同一个线程中的，被动对象方法的调用是同步的、阻塞的，一般的对象都属于被动对象.你调用,并且你执行.

#### 为该应用设计框架的思路
本框架的核心就是使用主动对象来封装并发逻辑，然后把Client的请求转发给实际的服务提供者（应用逻辑），这样无论是Client还是实际的服务提供者都不用关心并发的存在，不用考虑并发所带来的数据一致性问题。从而实现应用逻辑和并发逻辑的隔离，服务调用和服务执行的隔离。下面给出关键的实现细节。

本框架有如下几部分构成：

1. 一个ActiveObject类，从Thread继承，封装了并发逻辑的活动对象
2. 一个ActiveQueue类，主要用来存放调用者请求
3. 一个MethodRequest接口，主要用来封装调用者的请求，Command设计模式的一种实现方式  

它们的一个简单的实现如下：

```java
//MethodRequest接口定义
interface MethodRequest {
    public void call();
}
//ActiveQueue定义，其实就是一个producer/consumer队列
class ActiveQueue {
    public ActiveQueue() {
        _queue = new Stack();
    }
    public synchronized void enqueue(MethodRequest mr) {
        while (_queue.size() > QUEUE_SIZE) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        _queue.push(mr);
        notifyAll();
        System.out.println("Leave Queue");
    }
    public synchronized MethodRequest dequeue() {
        MethodRequest mr;

        while (_queue.empty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mr = (MethodRequest)_queue.pop();
        notifyAll();

        return mr;
    }
    private Stack _queue;
    private final static int QUEUE_SIZE = 20;
}
//ActiveObject的定义
class ActiveObject extends Thread {
    public ActiveObject() {
        _queue = new ActiveQueue();
        start();
    }
    public void enqueue(MethodRequest mr) {
        _queue.enqueue(mr);
    }
    public void run() {
        while (true) {
            MethodRequest mr = _queue.dequeue();
            mr.call();
        }
    }
    private ActiveQueue _queue;
}
```

通过上面的代码可以看出正是这些类相互合作完成了对并发逻辑的封装。开发者只需要根据需要实现MethodRequest接口，另外再定义一个服务代理类提供给使用者，在服务代理者类中把服务调用者的请求转化为MethodRequest实现，交给活动对象即可。  

使用该框架，可以较好的做到应用逻辑和并发模型的分离，从而使开发者集中精力于应用领域，然后平滑的和并发模型结合起来，并且可以针对ActiveQueue定制排队机制，比如基于优先级等。


## 基于框架的应用解决方案
本小节将使用上述的框架重新实现前面的例子，提供对于并发的支持。

- 第一步:先完成对于MethodRequest的实现，对于我们的例子来说实现如下：

```java
class SayHello implements MethodRequest {
    public SayHello(Service s) {
        _service = s;
    }
    public void call() {
        _service.sayHello();
    }
    private Service _service;
}
```

- 第二步:接下来定义一个服务代理类，来完成请求的封装、排队功能，当然为了做到对Client透明，该类必须实现Service接口。定义如下：

```java
class ServiceProxy implements Service
{
    public ServiceProxy() {
        _service = new ServiceImp();
        _active_object = new ActiveObject();
    }
    
    public void sayHello() {
        MethodRequest mr = new SayHello(_service);
        _active_object.enqueue(mr);
    }
    private Service _service;
    private ActiveObject _active_object;
}
```

- 第三步:其他的类和接口定义不变，下面对比一下并发逻辑增加前后的服务调用的变化.
- 并发逻辑增加前，对于sayHello服务的调用方法：

```java
Service s = new ServiceImp();
Client c = new Client(s);
c.requestService();
```

- 并发逻辑增加后，对于sayHello服务的调用方法：

```java
Service s = new  ServiceProxy();
Client c = new Client(s);
c.requestService();
```

- 可以看出并发逻辑增加前后对于Client的ServiceImp都无需作任何改变，使用方式也非常一致，ServiceImp也能够独立的进行重用。类结构图如下：
![image](http://www.ibm.com/developerworks/cn/java/l-multithreading/images/2.gif)



## 结论
本文围绕一个简单的例子论述了如何构架一个Java并发模型框架，其中使用了一些构建框架的常用技术，当然所构建的框架和一些成熟的商用框架相比，显得非常稚嫩，比如没有考虑服务调用有返回值的情况，但是其思想方法是一致的，希望读者能够深加领会，这样无论对于构建自己的框架还是理解一些其他的框架都是很有帮助的。读者可以对本文中的框架进行扩充，直接应用到自己的工作中。参考文献〔1〕中对于构建并发模型框架中的很多细节问题进行了深入的论述，有兴趣的读者可以自行研究。下面列出本框架的优缺点：

#### 优点:
1. 增强了应用的并发性，简化了同步控制的复杂性
2. 服务的请求和服务的执行分离，使得可以对服务请求排队，进行灵活的控制
3. 应用逻辑和并发模型分离，使得程序结构清晰，易于维护、重用
4. 可以使开发者集中精力于应用领域

#### 缺点：
1. 由于框架所需类的存在，在一定程度上增加了程序的复杂性
2. 如果应用需要过多的活动对象，由于线程切换开销会造成性能下降
3. 可能会造成调试困难


## 参考链接
1. [构建Java并发模型框架](http://www.ibm.com/developerworks/cn/java/l-multithreading/)
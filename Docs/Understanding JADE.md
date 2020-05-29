# Java Agent Development Framework

## Terminology

AP = Agent Platform

RMA = Remote Monitoring Agent

AMS = Agent Management System

DF = Directory Facilitator

IIOP = Internet Inter Orb Protocol

ACL = Agent Communications Languague

## Agent tasks - The behaviour class

Agent: An agent is a computational process that inhabits an agent platfrom (AP) and typically offers one or more computational services that can be published as a service description. An agent must have at least one owner and must support at least one notion of identity which can be described using the FIPA Agent Identifier (AID) that labels an agent so that it may be distinguished unambiguously. An agent may be registered at a number of transport addresses at which it can be contacted.

An agent can execute several behaviours concurrently. However it is important to notice that scheduling of behaviours in an agent is not pre-emptive (as for Java threads) but cooperative. **This means that when a behaviour is scheduled for execution its action() method is called and runs until it returns.** Therefore it is the programmer who defines when an agent switches from the execution of a behaviour to the execution of the next one.

Though requiring a small additional effort to programmers, this approach has several advantages.

- Allows having a single Java thread per agent (that is quite important especially in environments with limited resources such as cell phones).

- Provides better performances since behaviour switch is extremely faster than Java thread switch.

- Eliminates all synchronization issues between concurrent behaviours accessing the same resources (this speed-up performances too) since all behaviours are executed by the same Java thread.

- When a behaviour switch occurs the status of an agent does not include any stack information and is therefore possible to take a &quot;snapshot&quot; of it. This makes it possible to implement important advanced features e.g. saving the status of an agent on a persistent storage (agent persistency) or transferring it to another container for remote execution (agent mobility).

![](RackMultipart20200529-4-1ph82ey_html_527e4242cb7f32bd.png)

![](RackMultipart20200529-4-1ph82ey_html_ab5e825fd3182fd7.png)

![](RackMultipart20200529-4-1ph82ey_html_22c6cf8221600f68.png)

Types of behaviours:

- &#39;One-shot&#39; behaviours are designed to complete in one execution phase; their action() method is thus executed only once.
- &#39;Cyclic&#39; behaviours are designed to never complete; their action() method executes the same operations each time it is called.
- Generic behaviours embed a status trigger and execute different operations depending on the status value. They complete when a given condition is met.

## Agent communication - The ACLMessage class

The communication paradigm adopted is the asynchronous message passing. Each agent has a sort of mailbox (the agent message queue) where the JADE runtime posts messages sent by other agents. Whenever a message is posted in the message queue the receiving agent is notified. If and when the agent actually picks up the message from the message queue to process it is completely up to the programmer.

![](RackMultipart20200529-4-1ph82ey_html_52f2efafb56225a6.png)

We can use the block() method of the Behaviour class. This method marks the behaviour as &quot;blocked&quot; so that the agent does not schedule it for execution anymore. When a new message is inserted in the agent&#39;s message queue all blocked behaviours becomes available for execution again so that they have a chance to process the received message.

We can use this principle to block the replication of files while a file on some node is being accessed.

## The Yellow Pages Service – The DFService class

This principle is used to make agents dynamically discover other agents available at a given point in time. A &quot;yellow pages&quot; service allows agents to publish one or more services they provide so that other agents can find and exploit them as seen in the figure below.

![](RackMultipart20200529-4-1ph82ey_html_536d4680c660c01d.png)

The yellow pages service is provided by an agent called DF (Directory Facilitator).

## Compiling the software and launching the platform

After downloading JADE, some environment variables have to be set first. These have to be added in the &#39;CLASSPATH&#39; environment variable, if this isn&#39;t present you can add it manually. You can look for the files in your installed JADE folder, copy their file path to the environment variable. Add the following:

- &#39;jade.jar&#39;, contains all the JADE packages except add-ons, MTPs and graphical tools.
- &#39;commons-codec\commons-codec-1.3.jar&#39; contains the Base64 codec used by JADE.
- &#39;jadeExamples.jar&#39; contains the example projects (only required if you want to run the examples).

Alternatively, you can add them by typing the following in the command prompt:

For Windows:

prompt\&gt; set JADE\_HOME=c:\jade

prompt\&gt; set CLASSPATH=%JADE\_HOME%\lib\jade.jar; %JADE\_HOME%\lib\commons-codec\commons-codec-1.3.jar; %JADE\_HOME%\classes

For UNIX:

prompt\&gt; export JADE\_LIB=&quot;/home/pi/agent/jade/lib&quot;

prompt\&gt;

You can now run the main container with an overview GUI using the command: &#39;java jade.Boot -gui&#39;. You can add a note with the following command: &#39;java jade.Boot -container -host {hostname}&#39;. Replace {hostname} with the host name or address of the main container. To specify an agent enter the following: &#39;java jade.Boot -host {hostname} -container {agent name}:{class name}&#39;

The GUI looks as follows:

![](RackMultipart20200529-4-1ph82ey_html_364bfb0cebf6e612.png)

## JAR agents

Jar Agents are agents whose code, as the name indicates, is packaged inside a JAR file. Those jar files must not be in the classpath, they must be placed in a special folder which can be specified as follows:

java jade.Boot -jade\_core\_management\_AgentManagementService\_agentspath {c:\tmp\jarsfolder}

p10 Administration guide

## Configuring the Node Failure Monitoring system

Within the JADE platform a main container is constantly monitoring the availability of all its peripheral containers. If a container suddenly crashes the main container recognizes this and automatically removes the container from the platform.

## The Log Manager Agent

P46 JADE book

## Agent Mobility

P132 JADE book, 140 programming a mobile agent

INTER-PLATFORM MOBILITY SERVICE p123 book

This concept will be used for the Failure agent.

A mobile agent, consists of three parts: code, state and data.

![](RackMultipart20200529-4-1ph82ey_html_d63308a298a2c1d3.png)

The code will be executed when the mobile agent is transferred to another platform. The state keeps the data execution environment of the agent, including the program counter and the execution stack. The data consists of the variables used by the agents such as file identifiers etc.

There are 2 types of mobility for agents. The first being intra-platform mobility, meaning the agent will move between containers on the same platfrom. The second is inter-platform mobility that will allow the agent to move between platforms, so between different nodes. This is what we will be needing for our failure agents. However, this requires the Inter-Platform Mobility Service (IPMS) add-on, and is not built-in with the standard agent mobility service.

## Support for inter-platform messaging with plug-in Message Transport Protocols

This will be required for the failure and sync agent to be able to communicate through for example our own REST api.

The FIPA 2000 specification proposes a number of different Message Transport Protocols (MTPs for short) over which ACL messages can be delivered in a compliant way. JADE comprises a framework to write and deploy multiple MTPs in a flexible way. An implementation of a FIPA compliant MTP can be compiled separately and put in a JAR file of its own; the code will be dynamically loaded when an endpoint of that MTP is activated. Moreover, every JADE container can have any number of active MTPs, so that the platform administrator can choose whatever topology he or she wishes.

JADE performs message routing for both incoming and outgoing messages, using a singlehop routing table that requires direct visibility among containers. When a new MTP is activated on a container, the JADE platform gains a new address that is added to the list in the platform profile (that can be obtained from the AMS using the action get-description). Moreover, the new address is added to all the ams-agentdescription objects contained within the AMS knowledge base.

When a JADE container is started, it is possible to activate one or more MTPs on it, using suitable command line options. The –mtps option activates a new inter-platform communication endpoint on the starting container. Its value must be the fully qualified name of a class that provides the MTP functionality i.e. implements the jade.mtp.MTP interface.

## Hands-On Tutorial

Install ant, java and jade and set all environment variables. For windows it will look something like this:

![](RackMultipart20200529-4-1ph82ey_html_ce0431ec83517a88.png)

![](RackMultipart20200529-4-1ph82ey_html_443b83614fca579d.png)

![](RackMultipart20200529-4-1ph82ey_html_489372bdae98638e.png)

The &#39;Path&#39; variable should contain the following:

![](RackMultipart20200529-4-1ph82ey_html_d1733989be8a56a8.png)

And also add the &#39;CLASSPATH&#39; variable with the following arguments:

&#39;%JADE\_HOME%\lib\jade.jar;%JADE\_HOME%\lib\jadeExamples.jar;%JADE\_HOME%\lib\commons-codec\commons-codec-1.3.jar;%JADE\_HOME%\classes;&#39;

- You can add .java code for your own agent in the &#39;src&#39; folder
- Compiling all code for agent with ant from the &#39;src&#39; folder:
  - Ant jade
    - You will end up with all JADE classes in classes subdirectory.
  - Ant lib
    - This will jar the content of the classes directory and create the jade.jar file in the lib directory.
  - Ant clean
    - Will remove all generated files from the source tree.

- Starting the main container with gui:
  - Java jade.Boot -gui
- Add a compiled agent:
  - java jade.Boot -container {agent name}:{package}.{classname}
  - see the administration tutorial for more options
- Run a command with arguments, for the book trading example:
  - java jade.Boot -container &quot;Buyer1:examples.bookTrading.BookBuyerAgent(test)&quot;

## STARTING JADE FROM AN EXTERNAL JAVA APPLICATION

P111 of the JADE book.

Until now it has always been assumed that the JADE run-time is started from the command-line

In many cases, however, it is necessary to start one or more JADE agents from an external application, and therefore to create the JADE run-time to host them. To support this requirement, since JADE v2.3, an in-process interface has been implemented that allows JADE to be used as a kind of library to allow the run-time to be launched from within external programs. The JADE run-time is implemented by the jade.core.Runtime class. According to the singleton pattern, a single instance of this class exists in a JVM and can be retrieved by means of the instance() static method. The singleton Runtime instance provides two methods: createMainContainer() to create a JADE main container and createAgentContainer() to create a JADE peripheral container (i.e. a container that joins an existing main container running somewhere in the network).

## Running the failure agent at shutdown

We will put the script that initiates the failure agent in the directory below, which is handled by the systemd-halt.service.

![](RackMultipart20200529-4-1ph82ey_html_665333a5bf4e807d.png)

[https://unix.stackexchange.com/questions/39226/how-to-run-a-script-with-systemd-right-before-shutdown](https://unix.stackexchange.com/questions/39226/how-to-run-a-script-with-systemd-right-before-shutdown)
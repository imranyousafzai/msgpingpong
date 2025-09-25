# MsgPingPong
MsgPingPong is a Java-based messaging game that simulates a “ping-pong” style message exchange between players (client-server). It demonstrates concepts like socket communication, mediator pattern, and modular architecture for real-time message handling.

# Features

# Client-Server Communication:
Supports message exchange over sockets or in-memory communication.
# Mediator Pattern:
Manages communication between different players and components.
# Modular Design:
Core, communication, utility, and model packages for easy maintenance and extension.
# Cross-Platform Execution:
Run scripts for Windows (.bat/PowerShell) and Linux/Mac (.sh)

# 🗂️ Project Structure
msgpingpong/
├─ pom.xml                  # Maven project descriptor (dependencies & build)
├─ run.sh                   # Unix/Linux startup script
├─ run.bat                  # Windows batch script
├─ run.ps1                  # PowerShell startup script
├─ src/                      # (if present) Java source files
├─ target/classes/           # Compiled .class files
│  └─ org/msgpingpong/...
├─ .idea/                    # IntelliJ IDEA configuration files
└─ README.txt                # Original notes

# 🛠️ Requirements
Java 11+ (Java 17 recommended)
Apache Maven 3.6+

# ⚡ Build & Run
# 1️⃣ Build the Project

From the project root (msgpingpong/):
mvn clean install

# 2️⃣ Run the Game

Choose the appropriate script based on your OS:
# Linux/Mac:
sh run.sh

# Windows (CMD):
run.bat

# Windows (PowerShell):
.\run.ps1

# 🧩 Key Components
# Package	                  Purpose
org.msgpingpong.core	      Core game logic (GameController, PlayerServer, PlayerClient).
org.msgpingpong.comm	      Communication strategies (SocketCommunication, InMemoryCommunication, Mediator).
org.msgpingpong.model	      Data models for messages and players.
org.msgpingpong.util	      Utility classes (logging, constants).
org.msgpingpong.exception	  Custom exceptions for communication and game logic.

# ⚙️ Configuration
Adjust configuration parameters (such as host/port) inside Constants or relevant config files if needed.
Scripts (run.sh, run.bat, run.ps1) can be modified to pass custom arguments.
# Autonomous Treasure Hunt

This project is an **A*-algorithm-based pathfinding and exploration game** developed using **Java Swing**. In the game, the character uses the **A* algorithm to find the shortest path to collect treasures on the map**, and if no treasure is available, it performs **random exploration**. The game includes **static and moving obstacles**, and the character’s field of view is limited by a **fog-of-war mechanic**.

## Features

- **Shortest path calculation** using the **A* Algorithm**
- **Randomly generated dynamic map**
- **Treasure collection mechanic**
- **Field of view limitation** with a **fog-of-war system**
- **Static and moving obstacles** (trees, mountains, walls, rocks, moving birds, and bees)
- **Autonomously moving character**

## Installation and Setup

### 1. Clone the Repository

```sh
git clone https://github.com/Esentrn/Autonomous-Treasure-Hunt.git
cd otonom-hazine
```

### 2. Make Sure Java Is Installed

Ensure that **JDK 8 or later** is installed on your system.

### 3. Compile and Run the Project

- **Using an IDE (NetBeans, IntelliJ, Eclipse):**  
  Run the `OtonomHazine.java` file.

- **Using the command line:**

```sh
javac -d bin -sourcepath src src/ddd/OtonomHazine.java
java -cp bin ddd.OtonomHazine
```

## Game Mechanics

### Start

- The game starts by clicking the **"Start Game"** button.
- The map size is taken from the user, and a random map is generated.

### Character Movement

- The character **finds the nearest treasure and moves by calculating the shortest path using the A* algorithm**.
- If there is no treasure available, the character **explores randomly**.
- As the character moves, the **fog clears**, and explored areas become visible.

### Obstacles

- **Static Obstacles:** Tree, Rock, Wall, Mountain
- **Moving Obstacles:** **Bird** (moves vertically), **Bee** (moves horizontally)

### Treasure Collection

- When the character reaches a treasure, it collects it and **adds it to the collected treasures list**.
- The treasure collection process is displayed on the game screen.

## Screenshots

![Game Screen](https://github.com/Esentrn/Autonomous-Treasure-Hunt/blob/f82d3ee42d9dcbb9635a01d863f738b97109e850/Autonomous-Treasure-Hunt1.png)

![Game Screen](https://github.com/Esentrn/Autonomous-Treasure-Hunt/blob/f82d3ee42d9dcbb9635a01d863f738b97109e850/Autonomous-Treasure-Hunt2.png)

## Game Video

![Video Preview](https://github.com/Esentrn/Autonomous-Treasure-Hunt/blob/f82d3ee42d9dcbb9635a01d863f738b97109e850/Autonomous-Treasure-Hunt.gif)

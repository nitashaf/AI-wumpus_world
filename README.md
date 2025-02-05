# AI-wumpus_world
In this project, we implemented a first-order reasoning system to solve the Wumpus World problem. We generated first-order rules (not propositional!) to guide the agent on how to behave. The agent can only see the current cell, can smell adjacent cells, and can feel the wind from adjacent cells. It can also hear a scream from anywhere in the cave, so if an arrow hits its target, the agent will know. These rules were created in clause form.

After building the Wumpus worlds, we used the rule set with our reasoning system and had the explorer explore each cave. We kept track of the following statistics:

Number of times the gold is found
Number of Wumpus killed
Number of times the explorer falls into a pit
Number of times the Wumpus kills the explorer
Total number of cells explored
We also created a simple reactive explorer that does not use the reasoning system. Instead, it makes a decision on which cell to enter at random, based on whether or not it believes the neighboring cell is safe. It selects first from safe neighboring cells, and then from unsafe neighboring cells. Additionally, we had to track the frontier, which helps in random exploration.


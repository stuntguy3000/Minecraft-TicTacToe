# TicTacToe
A simple 1v1 TicTacToe Spigot/Bukkit plugin for Minecraft, using ItemFrames.

Built for 1.16.4, but I would expect many versions to work with it (older and newer).

Compiled for Java 8.

Download here:
https://github.com/stuntguy3000/Minecraft-TicTacToe/releases

### Please note: I will not be actively maintaining this plugin, but if you find a bug or want to request a feature, leave an issue or contact me on Discord (stuntguy3000#1337)

## Key Features:
 - 1v1 Mode
 - Block/Environment Protection to prevent accidental damage to the board
 - Simple and intuitive game user interface for quick operation
 - Plugin configuration settings
 - Configurable Walk-away & disconnection protection (remove the player from a game if they physically walk away from the board, or quit the server)
 

## Pictures
![Command Help](https://i.imgur.com/WndxIEu.jpg)

![Join Game Display](https://i.imgur.com/SBCnf1m.jpg)

![Block Selection Menu](https://i.imgur.com/tLQSQzm.jpg)

![Ingame Grid Example](https://i.imgur.com/RIyIxDh.jpg)

![Win Animation](https://i.imgur.com/3XPYlAg.gif)

## Commands
**All Players:**

``/tictactoe board`` - Access board specific commands

``/tictactoe leave`` - Leave the current game

``/tictactoe version`` - View plugin info

``/tictactoe help`` - View plugin commands

**Admin Only (Permission: ``tictactoe.admin``)**

``/tictactoe board`` - Access board specific commands

``/tictactoe board list`` - View a list of known boards

``/tictactoe board remove [id]`` - Removes a board (either at location or by specifying an id)

``/tictactoe board create`` - Creates a board

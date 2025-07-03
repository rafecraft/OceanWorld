# Note: This will be updated soon! if you find some error please report

# OceanWorld Plugin

OceanWorld is a Minecraft plugin that generates infinite custom ocean-themed worlds with configurable biomes and features. It provides commands for creating, teleporting, and reloading ocean worlds, along with a variety of configuration options to tailor the experience.

## Features
- Generate infinite ocean worlds with customizable biomes.
- Teleport to any world with ease.
- Reload plugin configurations dynamically.
- Fully configurable settings for biomes, water levels, and seafloor composition.

## Commands

### `/oceanworldcreate <world-name>`
- **Description**: Create a new ocean world.
- **Usage**: `/oceanworldcreate <world-name>`
- **Permission**: `oceanworld.create`
- **Permission Message**: "You don't have permission to create ocean worlds!"

### `/oceantp <world-name>`
- **Description**: Teleport to any world.
- **Usage**: `/oceantp <world-name>`
- **Permission**: `oceanworld.teleport`
- **Permission Message**: "You don't have permission to teleport to worlds!"

### `/oceanworldreload`
- **Description**: Reload the OceanWorld plugin configuration.
- **Usage**: `/oceanworldreload`
- **Permission**: `oceanworld.reload`
- **Permission Message**: "You don't have permission to reload the plugin!"
- **Aliases**: `owreload`, `oceanreload`

## Permissions

### `oceanworld.create`
- **Description**: Allows creating ocean worlds.
- **Default**: OP

### `oceanworld.teleport`
- **Description**: Allows teleporting to any world.
- **Default**: OP

### `oceanworld.reload`
- **Description**: Allows reloading the plugin configuration.
- **Default**: OP

## Configuration
The `config.yml` file allows you to customize various aspects of the plugin:

### Prefix
- **Enabled**: Toggle the plugin prefix on or off.
- **Text**: Customize the prefix text (supports color codes with `&`).

### Included Biomes
Define which ocean biomes to include in world generation. Available options:
- `warm_ocean`
- `lukewarm_ocean`
- `ocean`
- `cold_ocean`
- `frozen_ocean`
- `deep_lukewarm_ocean`
- `deep_ocean`
- `deep_cold_ocean`
- `deep_frozen_ocean`

### Water Level
- **Enabled**: Toggle custom water levels.
- **Level**: Set the water height (default: 62).

### World Limits
- **Min World Y**: Minimum Y level for world generation (e.g., `-64`).
- **Max World Y**: Maximum Y level for world generation (e.g., `320`).

### Seafloor
- **Enabled**: Toggle custom seafloor generation.
- **Min Y**: Minimum Y level for the seafloor.
- **Max Y**: Maximum Y level for the seafloor.
- **Materials**: Define materials for the seafloor (e.g., `sand`, `gravel`, `dirt`).

## Installation
1. Download the latest version of the plugin.
2. Place the `.jar` file in your server's `plugins` folder.
3. Restart your server to generate the default configuration files.
4. Customize the `config.yml` file as needed.
5. Use the commands to create and manage ocean worlds.

## Support
For issues or feature requests, please open an issue on the [GitHub repository](https://github.com/your-repo-link).

## License
This plugin is licensed under the MIT License. See the LICENSE file for details.

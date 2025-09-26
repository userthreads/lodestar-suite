
<p align="center">
<img src="https://raw.githubusercontent.com/copiuum/lodestar-client/master/src/main/resources/assets/lodestar-client/icon.png" alt="lodestar-client-logo" width="15%"/>
</p>

<h1 align="center">Lodestar</h1>
<p align="center">The Fabric Minecraft mod with quality of life modules and tools, for everyone.</p>

<div align="center">
    <img src="https://img.shields.io/github/last-commit/copiuum/lodestar-client" alt="GitHub last commit"/>
    <img src="https://img.shields.io/github/languages/code-size/copiuum/lodestar-client" alt="GitHub code size in bytes"/>
    <img src="https://img.shields.io/endpoint?url=https://ghloc.vercel.app/api/copiuum/lodestar-client/badge?filter=.java$&label=lines%20of%20code&color=blue" alt="GitHub lines of code"/>
</div>

## Usage

### Building
- Clone this repository
- Run `./gradlew build`

## Credits
The [Fabric Team](https://github.com/FabricMC) for [Fabric](https://github.com/FabricMC/fabric-loader) and [Yarn](https://github.com/FabricMC/yarn)

## Important notice: There might be leftover code from Meteor client unfair advantage modules, but you are free to review if it is fair play compatible or not.

## Releases

### Latest Release: v1.0.0
- **Download**: [Latest Release](https://github.com/copiuum/lodestar-client/releases/latest)
- **File Format**: `lodestar-client-{minecraft_version}-{build_type}-{random_suffix}.jar`
- **Example**: `lodestar-client-1.21.8-local-073ym.jar`
- **Compatible with**: Minecraft 1.21.6, 1.21.7, 1.21.8
- **Requires**: Fabric Loader 0.16.0+
- **Note**: Each build generates a unique random 5-character suffix for file identification

### Installation
1. Download the latest JAR file from the [Releases](https://github.com/copiuum/lodestar-client/releases) page
2. Place the JAR file in your Minecraft `mods` folder
3. Launch Minecraft with Fabric Loader

### Dynamic File Naming
Each build automatically generates a unique filename with the following format:
- **Pattern**: `lodestar-client-{minecraft_version}-{build_type}-{random_suffix}.jar`
- **Random Suffix**: 5 characters using letters (a-z) and numbers (0-9)
- **Purpose**: Ensures each build has a unique identifier for tracking and distribution
- **Example**: `lodestar-client-1.21.8-local-073ym.jar`

## Features
- **Player**: AutoClicker
- **Render**: BetterTab, BetterTooltips, BlockSelection, Blur, BossStack, BreakIndicators, FreeLook, Fullbright, HandView, ItemHighlight, ItemPhysics, NoRender, TimeChanger, Zoom
- **World**: Ambience
- **Misc**: AutoReconnect, BetterBeacons, BetterChat, DiscordPresence, ServerSpoof

## Licensing
This project is licensed under the [BSD 2-Clause License](LICENSE).


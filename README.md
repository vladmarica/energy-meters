# Energy Meters

[![](https://img.shields.io/badge/CurseForge-1.12.x-red.svg?style=flat-square&logo=conda-forge)](https://www.curseforge.com/minecraft/mc-mods/energy-meters)


This is a mod for Minecraft **1.12** (and **1.15** soon) that adds energy meters for measuring and limiting the throughput of energy through various cable types.

![](https://i.imgur.com/xzstT81.png)

![](https://i.imgur.com/LgrCW9R.png)
---
## Meter Types

There are currently 3 different meters available in the 1.12 version.

### **FE Meter**
Works with (almost) all implementations of Forge Energy:

- Redstone Flux (RF) - *from various mods, including the [Thermal series](https://www.curseforge.com/minecraft/mc-mods/thermal-foundation)*
- Micro Infinities (μI) - *from [Ender IO](https://www.curseforge.com/minecraft/mc-mods/ender-io)*
- *and likely many more*

Currently does **NOT** work properly with *Mekanism Universal Cables* and *Immersive Engineering Flux* - the numbers on the meter appear incorrectly.

![](https://i.imgur.com/oS9SxdN.png)

### **MJ Meter**
Works with:

- Minecraft Joule (MJ) - *from [BuildCraft](https://www.curseforge.com/minecraft/mc-mods/buildcraft)*

![](https://i.imgur.com/UqiBAYQ.png)

Only available if BuildCraft is installed, of course.

### **EU Meter**
Works with:

- Energy Unit (EU) - *from [IndustrialCraft 2](https://www.curseforge.com/minecraft/mc-mods/industrial-craft)*

![](https://i.imgur.com/ZXWTUb2.png)

Only available if IC2 is installed, of course.

Due to the way the IC2 EU system works, you cannot set a limit on EU meters.

---

## ComputerCraft and OpenComputers Integration
Energy meters expose a Lua API that can be used by ComputerCraft and OpenComputers. For the full API documentation, see [this wiki page](https://github.com/vladmarica/energy-meters/wiki/Lua-API).

---

## Issues and Suggestions
If you find any bugs or you have a good suggestions, please [open an issue](https://github.com/vladmarica/energy-meters/issues) here on GitHub. Please do not open an issue to ask about porting this mod to other versions of Minecraft. A 1.15+ port will come soon and back-port to older version isn't going to happen.
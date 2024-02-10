# Toolchain guide  
These files can be used by others to learn how can be such Minecraft toolchain created.
It's not directly meant on how to use Amber toolchain.

## Prerequisites
Common sense included.  
You should be pretty advanced in Java and even better, have some experience with modding Minecraft.
Get familiar with Mappings, MCP and some history behind all of this. 

## Toolchain
The toolchain is an essential part of any modding environment, it's a set of tools which allow you to edit/partially edit
the game files, compile the code, and run the game with your changes. It should be one of the first things you should
create when starting a new mod loader.  
Toolchain mainly does the following:
- **Obtaining the game files** - It's the first step of the toolchain. I mean, it's pretty obvious, since you want to mod
  something right?
- **Deobfuscation** - Process where the obfuscated code is converted into a human-readable form. This is where mappings
  come in hand, as they are used as a reference to convert the obfuscated names into human-readable ones.
- **Decompilation** - It's the process of converting the compiled code into a human-readable form. It's essential for
  modding, as you can't edit the compiled code directly. This step is primarily targeted for the developers maintaining
  their API, and is not necessary for the mod developers, as better tools such as Mixins already exist!
- **Creating source patches** - Since Minecraft is closed-source, you can't publish the edited code, but you can publish
  the patches which can be applied to the decompiled code to get the same result. This is the step where you create them.
- **Compiling and running the code** - Compile, re-obfuscate and run the game with your changes. This is the final step of
  the toolchain. Note that not all mappings support recompilation, and you might need to use a different set of mappings than you wanted!

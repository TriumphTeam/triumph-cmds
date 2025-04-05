import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    id("cmds.base-conventions")
    id("cmds.bukkit-example")
}

dependencies {
    api(projects.triumphCmdBukkit)
}

bukkitPluginYaml {
    main.set("dev.triumphteam.bukkit.example.ExamplePlugin")
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("Matt")
    apiVersion = "1.21"
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
    }
}

plugins {
    `cpp-library`
    id("me.spencernold.janus.binding")
}

library {
    linkage.set(listOf(Linkage.SHARED))
    targetMachines.add(machines.linux.x86_64)
    targetMachines.add(machines.windows.x86_64)
    targetMachines.add(machines.macOS.architecture("arm64"))
    targetMachines.add(machines.macOS.architecture("x86_64"))
}